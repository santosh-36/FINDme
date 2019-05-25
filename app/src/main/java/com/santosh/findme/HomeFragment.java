package com.santosh.findme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements LocationListener , SearchView.OnQueryTextListener{

    private static final String TAG = "Homefragment";
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Places> placesList;
    private PlacesAdapter placesAdapter;
    LatLng loc;
    LocationManager locationManager;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        doJob(view);
    }
    ProgressDialog pd;
    private void doJob(View view) {
        recyclerView = view.findViewById(R.id.home_recycler_view);
        placesList = new ArrayList<>();
        placesAdapter = new PlacesAdapter(placesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setAdapter(placesAdapter);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();
        pd.setCancelable(false);
        new CountDownTimer(4000, 1000) {
            int c = 0;
            public void onTick(long millisUntilFinished) {
                if(c == 2)
                    pd.setMessage("Retrieving titles...");
                /*else if(c == 3)
                    pd.setMessage("Calculating distance...");*/
                c++;
            }

            @Override
            public void onFinish() {
            }
        }.start();

    }
    public static String distance(double lat1,double lat2, double lon1,double lon2){
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;
        // calculate the result
        Double temp =(c * r);
        String pattern = "";
        if (temp > 99)
            pattern = "#";
        else
            pattern = "#.0";
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(temp);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());
            firestore.collection("Places")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "Error: " + e.getMessage());
                            }
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    if (doc.getDocument().getDouble("latitude") == null){
                                        continue;
                                    }
                                    Places t = new Places();
                                    t.setAddress(doc.getDocument().getString("address"));
                                    t.setTitle(doc.getDocument().getString("title"));
                                    t.setUid(doc.getDocument().getString("uid"));
                                    t.setLatitude(doc.getDocument().getDouble("latitude"));
                                    t.setLongitude(doc.getDocument().getDouble("longitude"));
                                    Log.e(TAG,doc.getDocument().getString("address")+
                                            doc.getDocument().getString("title")+
                                            doc.getDocument().getDouble("latitude")+doc.getDocument().getDouble("longitude"));
                                    t.setDist("5.5");//distance(loc.latitude, doc.getDocument().getDouble("latitude"), loc.longitude, doc.getDocument().getDouble("longitude")));
                                    //Places places = doc.getDocument().toObject(Places.class);
                                    placesList.add(t);
                                    placesAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
            locationManager.removeUpdates(this);
            locationManager = null;
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String ip = newText.toLowerCase().trim();
        List<Places> newList = new ArrayList<>();
        for(Places i : placesList){
            if(i.getTitle().toLowerCase().trim().contains(ip))
                newList.add(i);
        }
        placesAdapter.updateList(newList);
        return true;
    }
}
