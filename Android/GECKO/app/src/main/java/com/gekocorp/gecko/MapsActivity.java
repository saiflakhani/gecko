package com.gekocorp.gecko;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView tVRating = findViewById(R.id.tVRating);
        TextView tVComfort = findViewById(R.id.tVcomfort);
        //tVRating.setText("Driver Rating : " + AppGlobalData.driverRating);
        tVComfort.setText("Comfort Score : "+AppGlobalData.comfortScore+"%");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<LatLng> routeToPlot = LogPage.driverData.get(getIntent().getIntExtra("position",0)).route;
        // Add a marker in Sydney and move the camera

        if (routeToPlot.size() > 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routeToPlot.get(0),18));
            PolylineOptions opts = new PolylineOptions().addAll(routeToPlot).color(Color.BLACK).width(5);
            mMap.addPolyline(opts);
        }
    }
}
