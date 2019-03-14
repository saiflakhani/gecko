package com.gekocorp.gecko;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {
     protected String doInBackground(String... urls) {
         try {
             URL url = new URL("http://188.166.247.94:50109/getdriverscores?eml="+AppGlobalData.driverName);
             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("GET");
             connection.setConnectTimeout(5000);
             connection.setReadTimeout(10000);
             connection.connect();
             int status = connection.getResponseCode();
             Log.e("STATUS",""+status);
             BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             String content = "", line;
             while ((line = rd.readLine()) != null) {
                 content += line + "\n";
             }
             return content;
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }

     protected void onProgressUpdate(Integer... progress) {
     }

     protected void onPostExecute(String result) {
         // this is executed on the main thread after the process is over
         // update your UI here
         if(result!=null) {
             Log.d("Result", result);
             try {
                 JSONObject ob = new JSONObject(result);
                 AppGlobalData.comfortScore = ob.getString("comfortScore");
                 AppGlobalData.driverRating = ob.getString("driverScore");
             } catch (JSONException e) {
                 e.printStackTrace();
             }

         }

     }
 }