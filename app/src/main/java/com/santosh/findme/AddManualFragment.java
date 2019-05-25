package com.santosh.findme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddManualFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    LatLng loc;
    LocationManager locationManager;
    ConstraintLayout cl;
    EditText et;
    Button b;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Manual Location");
        return inflater.inflate(R.layout.fragment_add_manual, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        cl = view.findViewById(R.id.manual_cl);
        et = view.findViewById(R.id.add_manual_title);
        b = view.findViewById(R.id.add_manual_save);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_manual);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void insertToDb(LatLng loc, String address, String title, String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newtitle = new HashMap<>();
        newtitle.put("address", address);
        newtitle.put("latitude", loc.latitude);
        newtitle.put("longitude", loc.longitude);
        newtitle.put("title", title);
        newtitle.put("uid", uid);

        // Add a new document with a generated ID
        db.collection("Places")
                .add(newtitle)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("db", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(),"Uploaded successfully.",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error adding document", e);
                    }
                });
    }
    private String getAddress(LatLng loc){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(loc.latitude,loc.longitude, 1);
            return addresses.get(0).getAddressLine(0);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            return null;
        }
    }

    private void getTitle() {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et.getText())){
                    Toast.makeText(getContext(), "Title is must!!!", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences sp = getContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                    String uid = sp.getString("uid", null);
                    insertToDb(loc, getAddress(loc), et.getText().toString(), uid);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng arg0) {
                Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());

                adb.setTitle("Are you sure to add this location ?");
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loc = new LatLng(arg0.latitude ,arg0.longitude);
                        map.addMarker(new MarkerOptions().position(loc).title("Your Manual Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
                        cl.setVisibility(View.VISIBLE);
                        getTitle();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });
                adb.show();

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());
            //map.addMarker(new MarkerOptions().position(loc).title("Your Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
