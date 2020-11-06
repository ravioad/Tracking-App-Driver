package com.example.trackinapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.trackinapp.R;
import com.example.trackinapp.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase mDatabase;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mDatabase.getReference("userLocations").child("userId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (marker != null) {
                    marker.remove();
                }
                UserLocation userLocation = snapshot.getValue(UserLocation.class);
                Log.e("userLocationDebug: ", "latitude:  " + userLocation.latitude + "   longitude: " + userLocation.longitude);
                LatLng markLocation = new LatLng(userLocation.latitude, userLocation.longitude);
                marker = mMap.addMarker(new MarkerOptions().position(markLocation).title("Driver is here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(markLocation));
//                animateMarker(marker, markLocation, false);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markLocation, 18));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError: ", "Failed to read value.", error.toException());
            }
        });
    }
}