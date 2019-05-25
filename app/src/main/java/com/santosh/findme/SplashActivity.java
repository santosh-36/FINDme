package com.santosh.findme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    final String TAG ="splash_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        permissions_();
        locationCheck();
        time();
    }

    private boolean locationCheck() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            //
        }

        if(!gps_enabled && !network_enabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_enabled)
                return true;
            else
                return false;
        }else{
            return true;
        }
    }

    private boolean permissions_() {
        final int PERMISSION_ALL = 1;
        String[] Permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,};
        if (!Permission.hasPermissions(getApplicationContext(), Permissions)) {
            ActivityCompat.requestPermissions(SplashActivity.this, Permissions, PERMISSION_ALL);
            permissions_();
        }else{
            return true;
        }
        return true;
    }
    private boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }
    static int sec = 0;
    public void time(){
        sec++;
        int timems = 700*sec;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(isInternetAvailable() && locationCheck() && permissions_()){
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
                    String a = sp.getString("name",null);
                    if(a != null)
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    SplashActivity.this.finish();
                }else{
                    Snackbar.make(findViewById(R.id.splash_cl),"No Internet or Location or permission available.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new RetryListener())
                            .show();
                }
            }
        },timems);
    }

    private class RetryListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            time();
        }
    }
}
