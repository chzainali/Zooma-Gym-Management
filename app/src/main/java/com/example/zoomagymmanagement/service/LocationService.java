package com.example.zoomagymmanagement.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.admin.StepCountsActivity;
import com.example.zoomagymmanagement.stepcounter.utils.JourneyProviderContract;
import com.example.zoomagymmanagement.stepcounter.utils.MyLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationService extends Service {
    private LocationManager locationManager;
    public MyLocationListener locationListener;
    private final IBinder binder = new LocationServiceBinder();

    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 001;
    private long startTime = 0;
    private long stopTime = 0;
    final int TIME_INTERVAL = 3000;
    final int DIST_INTERVAL = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("mdp", "Location Service created");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationListener.recordLocations = false;


        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, TIME_INTERVAL, DIST_INTERVAL, locationListener);
        } catch (SecurityException e) {
            // don't have the permission to access GPS
            Log.d("mdp", "No Permissions for GPS");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // broadcast receiver may send message about low battery in which bundle containing battery will exist
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null && b.getBoolean("battery")) {
                // slow down GPS request frequency
                changeGPSRequestFrequency(TIME_INTERVAL * 3, DIST_INTERVAL * 3);
            }
        }

        return START_NOT_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tracking Journey";
            String description = "Keep Running!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, StepCountsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Tracking Journey")
                .setContentText("Keep Running!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // user has closed the application so cancel the current journey and stop tracking GPS
        locationManager.removeUpdates(locationListener);
        locationListener = null;
        locationManager = null;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        Log.d("mdp", "Location Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected float getDistance() {
        return locationListener.getDistanceOfJourney();
    }

    protected double getCounter() {
        return locationListener.getStepsFromDistance();
    }

    /* Display notification and start recording GPS locations for a new, also start timer */
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void playJourney() {
        addNotification();
        locationListener.newJourney();
        locationListener.recordLocations = true;
        startTime = SystemClock.elapsedRealtime();
        stopTime = 0;
    }

    /* Get the duration of the current journey */
    protected double getDuration() {
        if (startTime == 0) {
            return 0.0;
        }

        long endTime = SystemClock.elapsedRealtime();

        if (stopTime != 0) {
            // saveJourney has been called, until playJourney is called again display constant time
            endTime = stopTime;
        }

        long elapsedMilliSeconds = endTime - startTime;
        return elapsedMilliSeconds / 1000.0;
    }

    protected boolean currentlyTracking() {
        return startTime != 0;
    }

    /* Save journey to the database and stop saving GPS locations, also removes the notification */
    protected void saveJourney() {
        // save journey to database using content provider
        ContentValues journeyData = new ContentValues();
        journeyData.put(JourneyProviderContract.J_distance, getDistance());
        journeyData.put(JourneyProviderContract.J_DURATION, (long) getDuration());
        journeyData.put(JourneyProviderContract.J_DATE, getDateTime());

        long journeyID = Long.parseLong(getContentResolver().insert(JourneyProviderContract.JOURNEY_URI, journeyData).getLastPathSegment());

        // for each location belonging to this journey save it to the location table linked to this journey
        for (Location location : locationListener.getLocations()) {
            ContentValues locationData = new ContentValues();
            locationData.put(JourneyProviderContract.L_JID, journeyID);
            locationData.put(JourneyProviderContract.L_ALTITUDE, location.getAltitude());
            locationData.put(JourneyProviderContract.L_LATITUDE, location.getLatitude());
            locationData.put(JourneyProviderContract.L_LONGITUDE, location.getLongitude());

            getContentResolver().insert(JourneyProviderContract.LOCATION_URI, locationData);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        // reset state by clearing locations, stop recording, reset startTime
        locationListener.recordLocations = false;
        stopTime = SystemClock.elapsedRealtime();
        startTime = 0;
        locationListener.newJourney();

        Log.d("comp3018", "Journey saved with id = " + journeyID);
    }

    protected void changeGPSRequestFrequency(int time, int dist) {
        // can be used ot change GPS request frequency for battery conservation
        try {
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, time, dist, locationListener);
            Log.d("comp3018", "New min time = " + time + ", min dist = " + dist);
        } catch (SecurityException e) {
            // don't have the permission to access GPS
            Log.d("comp3018", "No Permissions for GPS");
        }
    }


    protected void notifyGPSEnabled() {
        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3, 3, locationListener);
        } catch (SecurityException e) {
            // don't have the permission to access GPS
            Log.d("comp3018", "No Permissions for GPS");
        }
    }

    private String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public class LocationServiceBinder extends Binder {
        // would like to get the distance in km for activity
        // the activity will keep track of the duration using chronometer
        public float getDistance() {
            return LocationService.this.getDistance();
        }

        public double getCounter() {
            return LocationService.this.getCounter();
        }

        public double getDuration() {
            // get duration in seconds
            return LocationService.this.getDuration();
        }

        public boolean currentlyTracking() {
            return LocationService.this.currentlyTracking();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void playJourney() {
            LocationService.this.playJourney();
        }

        public void saveJourney() {
            LocationService.this.saveJourney();
        }

        public void notifyGPSEnabled() {
            LocationService.this.notifyGPSEnabled();
        }

        public void changeGPSRequestFrequency(int time, int dist) {
            LocationService.this.changeGPSRequestFrequency(time, dist);
        }
    }

}
