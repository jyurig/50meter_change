package com.yuri.locationchangetracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {


    private LocationCallback locationCallBack;
    private Location location;
    private Location startLocation;
    private LocationRequest locationRequest;
    private boolean firstLocation = true;


    @Override
    public void onCreate() {
        super.onCreate();
        locationCallBack = new LocationCallback() {

            //event that is triggered whenever the update interval is met
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // get username from shared preferences
                // if the location is available
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    //handle location result
                    location = locationResult.getLastLocation();
                    if (firstLocation) {
                        startLocation = location;
                        firstLocation = false;
                    } else if (DistanceCalculator.getDistanceBetween(startLocation.getLatitude(), startLocation.getLongitude(), location.getLatitude(), location.getLongitude(), "K") >= 0.05) {
                        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(getApplicationContext(), Constants.CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_baseline_directions_walk_24)
                                .setContentTitle("Movement observed")
                                .setContentText("A movement of more than 50 meters was observed")
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(11, builder2.build());

                        stopLocationService();
                        MainActivity.setToggle(false);
                    }
                }
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")//I ask for promotion in MainActivity
    private void startLocationService() {
        firstLocation = true;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class); // will take to the mainActivity when notification is clicked
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.REQUEST_CODE, notificationIntent,
                    PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), Constants.REQUEST_CODE, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constants.CHANNEL_ID);
        configNotification(builder, pendingIntent);

        if (notificationManager != null && notificationManager.getNotificationChannel(Constants.CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME
                    , NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        locationRequest = locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        configLocationRequest();


        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService()
    {
        firstLocation = true;
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallBack);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        startLocationService();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        stopLocationService();
    }

    @Override
    public boolean stopService(Intent name) {
        stopLocationService();
        return super.stopService(name);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void configNotification(NotificationCompat.Builder builder, PendingIntent pendingIntent)
    {
        builder.setSmallIcon(R.drawable.ic_baseline);
        builder.setContentTitle("Tracking Location");
        builder.setContentText("Tracking your location");
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle()); // make notification expandable
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public void configLocationRequest()
    {
        locationRequest.setInterval(Constants.DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }
}
