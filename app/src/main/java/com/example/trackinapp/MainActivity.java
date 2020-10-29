package com.example.trackinapp;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    int RC_CAMERA_AND_LOCATION = 11;
    Handler handler = new Handler();
    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            SmartLocation.with(MainActivity.this).location().start(locationUpdatedListener);
        }
    };
    OnLocationUpdatedListener locationUpdatedListener = new OnLocationUpdatedListener() {
        @Override
        public void onLocationUpdated(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            UserLocation loc = new UserLocation(lat, lng);
            mDatabase.getReference("userLocations").child("userId").setValue(loc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e("LocationAddedDebug: ", "True");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LocationAddedDebug: ", "false");
                    Log.e("LocationAddedDebug: ", e.getMessage());
                }
            });
            Log.e("locationDebug: ", "latitude: " + (lat) + "  longitude: " + lng);
            handler.postDelayed(locationRunnable, 3000);
        }
    };

    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStop() {
        SmartLocation.with(this).location().stop();
        super.onStop();
    }

    public void getLocation(View view) {
        Toast.makeText(this, "button", Toast.LENGTH_SHORT).show();
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            SmartLocation.with(this).location().start(locationUpdatedListener);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }
}