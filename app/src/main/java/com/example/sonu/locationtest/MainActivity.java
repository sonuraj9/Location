package com.example.sonu.locationtest;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity implements FusedLocationListener.LocationListener {


    @InjectView(R.id.loc)
    TextView textView;
    private FusedLocationListener locationService;
    private ConnectivityHelper connectivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        connectivityHelper = new ConnectivityHelper(MainActivity.this);
        if (!connectivityHelper.isConnectingToInternet()) {
            connectivityHelper.showAlert("Connection Error!", "Check Your Internet Connection", false);
        }
        locationService = new FusedLocationListener(this, this);

    }

    @OnClick(R.id.update)
    public void onClick(View view){
        locationService = new FusedLocationListener(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onReceiveLocation(Location location) {
        textView.setText("Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude());

    }

    @Override
    public void onLocationDisabled(String msg) {
        connectivityHelper = new ConnectivityHelper(MainActivity.this);
        connectivityHelper.showAlert("Location Disabled", msg, true);

    }

    @Override
    public void onConnectionFailed(String msg) {

        connectivityHelper = new ConnectivityHelper(MainActivity.this);
        connectivityHelper.showAlert("Can't Get location", msg, false);
    }
}
