package com.gekocorp.gecko;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class LogPage extends AppCompatActivity {

    String myName = AppGlobalData.driverName;
    DriverAdapter adapter;
    String myDriverScore = "1";
    String myComfortScore = "0";

    public static ArrayList<PojoForDriver> driverData = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    DatabaseReference userRef = myRef.child(myName);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_log_page);

            generateSimpleList();
            getRatings();
        }

        private void getRatings(){
            new GetUrlContentTask().execute();
            Button btnRatings = findViewById(R.id.btnMyRatings);
            btnRatings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(LogPage.this)
                            .setTitle("Your ratings")
                            .setMessage("Your Comfort Rating is : "+AppGlobalData.comfortScore+"\nYour Driver Rating is : "+AppGlobalData.driverRating)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .show();
                }
            });
        }

        private void generateSimpleList() {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    driverData.clear();
                    Log.e("Count " ,""+snapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String date = postSnapshot.getKey();
                        if(date!=null && date.equalsIgnoreCase("Driver_score"  )){
                            myDriverScore = String.valueOf(postSnapshot.getValue());
                            continue;
                        }else if(date!=null && date.equalsIgnoreCase("comfort_score")){
                            myComfortScore = String.valueOf(postSnapshot.getValue());
                            continue;
                        }
                        double sumOFKmpl=0;
                        long count=0;
                        ArrayList<LatLng> currentLatlng = new ArrayList<>();
                        Log.e("Count " ,""+postSnapshot.getChildrenCount());
                        for(DataSnapshot curSnapshot1:postSnapshot.getChildren()){
                            try {
                                //Log.d("LATITUDE",String.valueOf(curSnapshot1.child("kff1005").getValue()));
                                currentLatlng.add(new LatLng(Double.parseDouble(String.valueOf(curSnapshot1.child("kff1006").getValue())), Double.parseDouble(String.valueOf(curSnapshot1.child("kff1005").getValue()))));
                                sumOFKmpl+=Double.parseDouble(String.valueOf(curSnapshot1.child("kff1203").getValue()));
                                count++;
                            }catch (NumberFormatException e){
                                Log.e("LOCATION","The Latitude is " + String.valueOf(curSnapshot1.child("kff1005").getValue()));
                            }
                        }
                        double avgKMPL = 0;
                        if(count!=0){avgKMPL = sumOFKmpl/count;}
                        PojoForDriver currentSession = new PojoForDriver(date,String.valueOf(avgKMPL),currentLatlng,myName);
                        driverData.add(currentSession);
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            adapter = new DriverAdapter(this,R.layout.activity_log_page,driverData);

            ListView myRecyclerView = (ListView) findViewById(R.id.rV);
            myRecyclerView.setAdapter(adapter);
            //TextView tVEmpty = findViewById(R.id.tVempty);
            RelativeLayout rl = findViewById(R.id.rLempty);
            myRecyclerView.setEmptyView(rl);
            myRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(LogPage.this,MapsActivity.class);
                    i.putExtra("position",position);
                    startActivity(i);
                }
            });
        }
}

