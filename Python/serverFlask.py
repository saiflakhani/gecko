from flask import Flask,request
from firebase import firebase
import datetime
import requests
import csv
import pandas as pd
import math
import csv as csv
import numpy as np
from sklearn.ensemble import AdaBoostClassifier
from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn import metrics
from sklearn.preprocessing import Imputer
from statistics import mode
from flask import send_file
from statistics import mean
#import matplotlib.pyplot as plt
#import pylab

firebase = firebase.FirebaseApplication('https://gecko-e9929.firebaseio.com/', None)



app = Flask(__name__)
x_est_last = 13;
p = 0;
p_last = 0;
x_est = 13
#NOISE    
q = 0.162;
r = 0.417;
a=1;
efficiency_values = []


@app.route('/getdriverscores')
def getScores():
    data = request.values.to_dict()
    if 'eml' in data.keys():
        driverScore = performDriverRating(data['eml'])
        comfortScore = performComfortRating(data['eml'])
        
        return '{"driverScore":'+str(driverScore)+',"comfortScore":'+str(comfortScore)+'}'
    else:
        return "Please add eml as an argument"
        
        
def calculateNewValue(noisy_reading,x_est_last,p_last,q,r):
	x_temp_est   =   x_est_last
	p_temp       =   p_last+q
	k = p_temp * (1.0/(p_temp + r));
	z_measured = noisy_reading;
	x_est = x_temp_est + k * (z_measured - x_temp_est); 
	p = (1- k) * p_temp;
	p_last = p
	x_est_last = x_est
	efficiency_values.append(x_est_last)
    
@app.route('/getKalman')    
def doKalmanFiltering():
    arguments = request.values.to_dict()
    if 'eml' in arguments.keys():
        nameStr = arguments['eml']
        result = firebase.get('/Users/'+nameStr+'/', None)
        data=[]
        for a in result.values():
            if type(a) is float or type(a) is int:
                continue
            for d in a.values():            
                if 'kff1203' in d.keys():
                    temp = float(d['kff1203'])
                    data.append(temp)
        x_est_last = Average(data)
        for instantVal in data:
            calculateNewValue(instantVal,x_est_last,p_last,q,r)
        plt.xlabel('Time')
        plt.ylabel('Efficency')
        plt.plot(data[:200])
        plt.plot(efficiency_values[:200])
        plt.savefig('kalman.png')
        return send_file('kalman.png')
    return('error');
    
    
    
    
    
        

    
    
def performComfortRating(nameStr):
    result = firebase.get('/Users/'+nameStr+'/', None)
    data = []
    throttle_data = []
    accel_values = []
    comfortScore = 100
    upperThreshold = 0.3
    for a in result.values():
        if type(a) is float or type(a) is int:
            continue
        #try:
        for d in a.values():
                #print(d)            
                if 'kff1223' in d.keys():
                    temp = float(d['kff1223'])
                    data.append(temp)
                if 'k11' in d.keys():
                    temp = float(d['k11'])
                    throttle_data.append(temp)
        #except:
            #print('some error')
    
    initial = 0.02
    cur_score = 0 
    score_arr = []
    for acc in data:
        if abs(float(acc)-initial) > upperThreshold:
            cur_score = cur_score-10
        else:
            cur_score = cur_score+1
        initial = acc
            
            
    initial = 20
    throt_score = 0
    for pos in throttle_data:
        if abs(float(pos)-float(initial)) > 60:
            throt_score = throt_score-1
        else:
            throt_score = throt_score+1
        initial = pos
            
    
    print("Score = "+str(throt_score))
    print("count = "+str(len(throttle_data)))
    
    throttleScore = 0
    finalScore = 0
    calcScore = 0
    if len(throttle_data) is not 0:
        throttleScore = throt_score/float((len(throttle_data)))*100
        print("throttleScore = "+str(throttleScore))
    
    if len(data) is not 0:
        finalScore = cur_score/float((len(data)))*100
        print("finalScore = "+str(finalScore))
        
    if len(throttle_data)>=(len(data)/2):
        calcScore = (throttleScore*0.70) + (finalScore*0.30)
    else:
        calcScore = finalScore
        
    result = firebase.put('/Users/'+nameStr+'/','comfort_score',finalScore)
    print("COMFY == "+str(calcScore))
    return calcScore
    
    
def performDriverRating(nameStr):
    ratings=[]
    result = firebase.get('/Users/'+nameStr+'/', None)
    #result = firebase.get('/Users/saiflakhani/', None)
    print(result.keys())
    del result['Driver_score']
    del result['comfort_score']
    for a in result.values():
        for d in a.values():

            ##GET DATA

            if 'k4' in d.keys() and 'kd' in d.keys() and 'kc' in d.keys():
               temp = [float(d['k4']), float(d['kd']), float(d['kc'])]
               ratings.append(temp)
    vs=[]
    es=[]

    ##FIND MEAN OF VEHICLE RATIO AND ENGINE LOAD

    for r in range(len(ratings)):
        if ratings[r][1]!=0.0 and ratings[r][2]!=0.0 :
            vs.append((ratings[r][1]/220)/(ratings[r][2]/8000))
        if ratings[r][0] != 0.0 :
            es.append(ratings[r][0])

    vs_mean = mean(vs)
    es_mean = mean(es)

    print(ratings)
    print(es_mean)
    print(vs_mean)
    for r in range(len(ratings)):
        engine_load = ratings[r][0]
        if ratings[r][0] == 0:
            ratings[r][0] = es_mean

        ##CASE CHECK FOR LAST ROW RATIO

        if ratings[r] == ratings[-1]:
            ratings[r][1] = abs(ratings[r][1] - ratings[int(r / 2)][1])
            ratings[r][2] = abs(ratings[r][2] - ratings[int(r / 2)][2])
            if ratings[r][1] !=  0:
                vehicle_ratio = ((ratings[r][1] / 220) / (ratings[r][2] / 8000))
            else :
                vehicle_ratio = vs_mean
            ratings[r].append(vehicle_ratio)
            if (vehicle_ratio >= 0.9 and vehicle_ratio< 1.3) and (engine_load >= 18 and engine_load <= 55):
                label = 1.0
            else:
                label = 0.0
            #print(label)
            ratings[r].append(label)
            continue
        ratings[r][1] = abs(ratings[r][1] - ratings[r + 1][1])
        ratings[r][2] = abs(ratings[r][2] - ratings[r + 1][2])

        ##CALCULATE THE VEHICLE RATIO FOR NORMAL ROWS
        ######TO BE MADE CHANGES##########
        if ratings[r][2] != 0 :
            vehicle_ratio = ((ratings[r][1] / 220) / (ratings[r][2] / 8000))
        else :
            vehicle_ratio = vs_mean
        if ratings[r][1] == 0:
            vehicle_ratio = vs_mean
        ratings[r].append(vehicle_ratio)
        #print(ratings[r][1])

        ##ASSIGN LABEL TO NORMAL ROWS

        if (ratings[r][1] >= 0.9 and ratings[r][1] < 1.3) and (engine_load >= 20 and engine_load <= 50):
            label = 1.0
        else:
            label = 0.0

        ratings[r].append(label)

    ##CREATE DATAFRAME
    df = pd.DataFrame(ratings, columns=['EngineLoad', 'VehicleSpeed', 'EngineSpeed','VehicleSpeedRatio', 'Label'])
    print(df)
    df = df.drop(['VehicleSpeed','EngineSpeed'],axis=1)

    ##PERFORM DATABOOST

    array = df.values
    X = array[:, 0:2]
    Y = array[:, 2]
    #print(X)
    #print(Y)
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.33)
    abc = AdaBoostClassifier(n_estimators=50,learning_rate=1)
    model = abc.fit(X_train, y_train)
    y_pred = model.predict(X_test)
    print("Accuracy:", metrics.accuracy_score(y_test, y_pred))
    #print(mode(y_pred))
    y_pred = int((mean(y_pred)*6)*75)
    #if y_pred<25:
        #y_pred=25
    #if y_pred>100 :
        #y_pred = 92

    print(y_pred)


    result = firebase.put('/Users/'+nameStr+'/','Driver_score',y_pred)
    ####PRINT THE PREDICTION#####

@app.route('/test',methods=['GET', 'POST'])
def hello_world():
   data = request.values.to_dict()
   date_object = datetime.date.today()
   location = '/Users/'+str(data['eml'])+'/'+str(date_object)+'/'
   result = firebase.post(location, data, params={'print': 'pretty'})
   #write_to_csv(data)
   #wrtetocsv([eml][speed obd][rpm][engineload][lat][lon][accel][])
   return('OK!')
   
  
def Average(lst): 
    return sum(lst) / len(lst) 
     
def write_to_csv(dataDict):
    with open('workFile.csv','r+b') as myfile:
        header = next(csv.reader(myfile))
        dict_writer = csv.DictWriter(f, header, 0)
        dict_writer.writerow(dataDict)
        
        
if __name__ == '__main__':
   app.run('188.166.247.94',50109)
