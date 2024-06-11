package com.example.zoomagymmanagement.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.service.LocationService;
import com.example.zoomagymmanagement.stepcounter.receiver.MyReceiver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class StepCountsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private LocationService.LocationServiceBinder locationService;
    private GoogleMap mMap;
    private TextView distanceText;
    private TextView avgSpeedText;
    private TextView durationText;
    private TextView stepsText;
    private Button playButton;
    private Button stopButton;
    private static final int PERMISSION_GPS_CODE = 1;
    FusedLocationProviderClient fusedLocationClient;
    private Polyline polyline;

    private Handler postBack = new Handler();

    // Define a ServiceConnection to interact with LocationService
    private ServiceConnection lsc = new ServiceConnection() {
        // Called when the service is connected
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Cast the IBinder to LocationServiceBinder
            locationService = (LocationService.LocationServiceBinder) iBinder;

            // Initialize buttons based on tracking status
            initButtons();

            // Start a new thread to continuously update UI with journey statistics
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Continue updating UI while locationService is available
                    while (locationService != null) {
                        // Retrieve journey statistics from LocationService
                        float d = (float) locationService.getDuration();
                        long duration = (long) d;  // in seconds
                        float distance = locationService.getDistance();
                        double counter = locationService.getCounter();

                        // Calculate duration in hours, minutes, and seconds
                        long hours = duration / 3600;
                        long minutes = (duration % 3600) / 60;
                        long seconds = duration % 60;

                        // Calculate average speed
                        float avgSpeed = 0;
                        if (d != 0) {
                            avgSpeed = distance / (d / 3600);
                        }

                        // Update UI with journey statistics
                        final String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        final String dist = String.format("%.2f KM", distance / 1000);
                        final String avgs = String.format("%.2f KM/H", avgSpeed);

                        // Post changes to UI thread
                        postBack.post(new Runnable() {
                            @Override
                            public void run() {
                                durationText.setText(time);
                                avgSpeedText.setText(avgs);
                                distanceText.setText(dist);
                                int steps = (int) (distance / 0.6);
                                stepsText.setText(steps + "");
                            }
                        });

                        try {
                            // Sleep for 500 milliseconds before updating UI again
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        // Called when the service is disconnected
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Set locationService to null
            locationService = null;
        }
    };


    // whenever activity is reloaded while still tracking a journey (if back button is clicked for example)
    private void initButtons() {
        // no permissions means no buttons
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            return;
        }

        // if currently tracking then enable stopButton and disable startButton
        if (locationService != null && locationService.currentlyTracking()) {
            stopButton.setEnabled(true);
            playButton.setEnabled(false);

        } else {
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counts);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);

        distanceText = findViewById(R.id.distanceText);
        durationText = findViewById(R.id.durationText);
        stepsText = findViewById(R.id.counterText);
        avgSpeedText = findViewById(R.id.avgSpeedText);

        playButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // connect to service to see if currently tracking before enabling a button
        stopButton.setEnabled(false);
        playButton.setEnabled(false);

        // register broadcast receiver to receive low battery broadcasts
        try {
            MyReceiver receiver = new MyReceiver();
            registerReceiver(receiver, new IntentFilter(
                    Intent.ACTION_BATTERY_LOW));
        } catch (IllegalArgumentException e) {
        }


        handlePermissions();

        // start the service so that it persists outside of the lifetime of this activity
        // and also bind to it to gain control over the service
        startService(new Intent(this, LocationService.class));
        bindService(
                new Intent(this, LocationService.class), lsc, Context.BIND_AUTO_CREATE);
    }

    public void onClickPlay(View view) {

        // start the timer and tracking GPS locations
        locationService.playJourney();
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void onClickStop(View view) {
        // save the current journey to the database
        float distance = locationService.getDistance();
        double counter = locationService.getCounter();
        locationService.saveJourney();

        playButton.setEnabled(false);
        stopButton.setEnabled(false);


        DialogFragment modal = FinishedTrackingDialogue.newInstance(String.format("%.2f KM", distance));
        modal.show(getSupportFragmentManager(), "Finished");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // un-register this receiver since we only need it while recording GPS
        try {
            MyReceiver receiver = new MyReceiver();
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
        }

        // unbind to the service (if we are the only binding activity then the service will be destroyed)
        if (lsc != null) {
            unbindService(lsc);
            lsc = null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng firstLoc = null;
        LatLng lastLoc = null;


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mMap.setMyLocationEnabled(true);
                            float zoom = 50.0f;
                            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
                            polyline = mMap.addPolyline(new PolylineOptions().color(Color.GREEN).width(5f).clickable(false));


                        }
                    }
                });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    List<LatLng> points = polyline.getPoints();
                    points.add(currentLocation);
                    polyline.setPoints(points);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "location changed", Toast.LENGTH_SHORT).show();

    }

    public static class FinishedTrackingDialogue extends DialogFragment {
        public static FinishedTrackingDialogue newInstance(String distance) {
            Bundle savedInstanceState = new Bundle();
            savedInstanceState.putString("distance", distance);
            FinishedTrackingDialogue frag = new FinishedTrackingDialogue();
            frag.setArguments(savedInstanceState);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Thanks for using the service.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // go back to home screen
                            getActivity().finish();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    // PERMISSION THINGS

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(reqCode, permissions, results);
        switch (reqCode) {
            case PERMISSION_GPS_CODE:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    initButtons();
                    if (locationService != null) {
                        locationService.notifyGPSEnabled();
                    }
                } else {
                    // permission denied, disable GPS tracking buttons
                    stopButton.setEnabled(false);
                    playButton.setEnabled(false);
                }
                return;

        }
    }


    public static class NoPermissionDialogue extends DialogFragment {
        public static NoPermissionDialogue newInstance() {
            NoPermissionDialogue frag = new NoPermissionDialogue();
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("GPS is required to track your journey!")
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // user agreed to enable GPS
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dialogue was cancelled
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void handlePermissions() {
        // if don't have GPS permissions then request this permission from the user.
        // if not granted the permission disable the start button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // the user has already declined request to allow GPS
                // give them a pop up explaining why its needed and re-ask
                DialogFragment modal = NoPermissionDialogue.newInstance();
                modal.show(getSupportFragmentManager(), "Permissions");
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
            }

        }
    }
}