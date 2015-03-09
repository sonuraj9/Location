package com.example.sonu.locationtest;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by sonu on 29/1/15.
 */


public class FusedLocationListener implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final long INTERVAL = 1000 * 30;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final long ONE_MIN = 1000 * 60;
    private static final long REFRESH_TIME = ONE_MIN * 5;
    private static final float MINIMUM_ACCURACY = 50.0f;
    private final LocationManager lm;
    private final boolean gps_enabled;
    private final boolean network_enabled;
    Context locationActivity;
    LocationListener locationListener;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    public FusedLocationListener(Activity locationActivity, LocationListener locationListener) {

        locationRequest = LocationRequest.create();
        this.locationListener = locationListener;
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.locationActivity = locationActivity;
        lm = (LocationManager) locationActivity.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps_enabled || !network_enabled)
            locationListener.onLocationDisabled("Please turn on location to high accuracy!");
        googleApiClient = new GoogleApiClient.Builder(locationActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
        if (currentLocation != null && currentLocation.getTime() > REFRESH_TIME) {
            location = currentLocation;
            locationListener.onReceiveLocation(location);
        } else {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // Schedule a Thread to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    fusedLocationProviderApi.removeLocationUpdates(googleApiClient,
                            FusedLocationListener.this);
                }
            }, ONE_MIN, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //if the existing location is empty or
        //the current location accuracy is greater than existing accuracy
        //then store the current location
        locationListener.onReceiveLocation(location);
        if (null == this.location || location.getAccuracy() < this.location.getAccuracy()) {
            this.location = location;
            //if the accuracy is not better, remove all location updates for this listener
            if (this.location.getAccuracy() < MINIMUM_ACCURACY) {
                fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationListener.onConnectionFailed("Connection Failed to Google play service! : " +
                connectionResult.getErrorCode() + " : " + connectionResult.toString());
    }



    public interface LocationListener {

        public void onReceiveLocation(Location location);

        public void onLocationDisabled(String msg);

        public void onConnectionFailed(String msg);
    }
}