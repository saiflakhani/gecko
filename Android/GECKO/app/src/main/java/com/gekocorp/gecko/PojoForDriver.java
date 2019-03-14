package com.gekocorp.gecko;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PojoForDriver {
    String tripDate;
    String avgKmpl;
    String driveDuration;
    String driverRating;
    String comfortScore;
    String latitude;
    ArrayList<LatLng> route = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    String longitude;

    public PojoForDriver(String tripDate, String avgKmpl, ArrayList<LatLng> route,String name) {
        this.tripDate = tripDate;
        this.avgKmpl = avgKmpl;
        this.route = route;
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getAvgKmpl() {
        return avgKmpl;
    }

    public void setAvgKmpl(String avgKmpl) {
        this.avgKmpl = avgKmpl;
    }

    public String getDriveDuration() {
        return driveDuration;
    }

    public void setDriveDuration(String driveDuration) {
        this.driveDuration = driveDuration;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getComfortScore() {
        return comfortScore;
    }

    public void setComfortScore(String comfortScore) {
        this.comfortScore = comfortScore;
    }

    public String getTheftPossibility() {
        return theftPossibility;
    }

    public void setTheftPossibility(String theftPossibility) {
        this.theftPossibility = theftPossibility;
    }

    String theftPossibility;
}
