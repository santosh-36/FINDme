package com.santosh.findme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.santosh.findme.MapsHelper.FetchURL;
import com.santosh.findme.MapsHelper.TaskLoadedCallback;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,TaskLoadedCallback,LocationListener {

    private GoogleMap mMap;
    LatLng loc;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapActivity";
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        LatLng dest = new LatLng(getIntent().getDoubleExtra("latitude",0f),getIntent().getDoubleExtra("longitude",0f));
        place2 = new MarkerOptions().position(dest).title("Your Destination");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        permissions_();

        manager = (LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);

    }


    private LocationManager manager;

    private void permissions_() {
        final int PERMISSION_ALL = 1;
        String[] Permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,};
        if (!Permission.hasPermissions(getApplicationContext(), Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, PERMISSION_ALL);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeviceLocation();
        //mMap.setMyLocationEnabled(true);
        mMap.addMarker(place2);
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(manager.isProviderEnabled(GPS_PROVIDER)){
            showRoute();
        }else{
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),403);
        }
    }

    private void showRoute() {
        try {
            @SuppressLint("MissingPermission") Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location current_location = (Location) task.getResult();
                        loc = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                        place1 = new MarkerOptions().position(loc).title("Your Location");
                        mMap.addMarker(place1);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,DEFAULT_ZOOM));

                        if(place1!=null && place2!=null)
                            new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                        else
                            Log.e(TAG,place1.getTitle()+"---"+place2.getTitle());
                    }else{
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(GPS_PROVIDER, 3000, 0, MapsActivity.this);
                    }
                }
            });
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d(TAG,url);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onLocationChanged(Location location) {
        loc = new LatLng(location.getLatitude(), location.getLongitude());
        place1 = new MarkerOptions().position(loc).title("Your Location");
        mMap.addMarker(place1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,DEFAULT_ZOOM));

        if(place1!=null && place2!=null)
            new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}