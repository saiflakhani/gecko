
# gecko

It sounds like a lizard, but in reality, GECKO helps you with your driving. It analyses driver patterns and computes comfort scores, driver rating and theft probability

## GPS Based Encapsulation of Car Keyprint using OBD

This project has three parts :

* The torque app : Torque App [Torque](https://play.google.com/store/apps/details?id=org.prowl.torque&hl=en_IN)
* The GECKO app : GECKO App [GECKO](/Android)
* The flask server script : [Script](/Python)

## What it does

It allows you to compute the following, from driver data :

Comfort Score : How the passengers feel while sitting in the car
Driver Rating : How well you are driving, based on mileage and fault values
Theft Probability : The probability that you're not the one driving the car (requires extensive data)


## How to install

#### Steps :

1. Get an OBD Scanner : [OBD Tool](https://www.amazon.in/GadgetGuru-Bluetooth-OBD-II-Diagnostic/dp/B00XL9HKQO/ref=sr_1_6?ie=UTF8&qid=1552595067&sr=8-6&keywords=obd)
2. Change the python script value of `app.run(hostname,port)` to your server's hostname and port. If running on local, it will be `127.0.0.1,5000`
3. Run the python script. This requires you to install python-firebase and pandas via pip
4. Open the torque app and go to settings. Set "data upload to webserver" to "ON"
5. Set the link of the webserver to `http://(your-hostname):(your-port)/test`
6. Set the user email address to your first name. `(Bug : @ in the email will not work)`
7. Attach the OBD Tool to your car and drive!
8. Let the data be gathered
9. Open the GECKO app and type in the name in step 6.
10. Enjoy!


## How it works

- Driver Identification : Random Forests
- Driver Rating : AdaBoost Algorithm
- Mileage : Kalman Filters
- Theft Probability : Random Forests
- Comfort Score : Linear Regression


## Credits
#### Development 
1. Pranav Patil
2. Niharika Sachdev
3. Shubham Kokate
4. Deptii Chaudhari

#### Ideation
1. Shivom Thakkar
2. Saloni Goel
3. Vishva Iyer
4. Sabarinath S
5. Ravleen Kaur
