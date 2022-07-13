package com.yuri.locationchangetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        ContextCompat.startForegroundService(this, intent);
        setContentView(R.layout.activity_main);
        getPermissions();
        toggleButton = (ToggleButton) findViewById(R.id.startLocationToggle);

        if(isLocationServiceRunning())
        {
            toggleButton.setChecked(true);
        }
        else {

            toggleButton.setChecked(false);
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    // check for required permissions
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        // request fine location permission
                        toggleButton.setChecked(false);
                        getPermissions();
                    }
                    else
                    {
                        startLocationService();
                    }
                }
                else
                {
                    stopLocationService();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        // if permission is granted
        if(requestCode == Constants.FINE_LOCATION_PERMISSION && grantResults.length> 0 )
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startLocationService();
            }
            else
            {
                // didn't get permission, notify user
                Toast.makeText(this, "Please enable these permissions to to use this app", Toast.LENGTH_SHORT);
            }
        }
    }

    // returns is the location service running (as foreground)
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
        {
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE))
            {
                if(LocationService.class.getName().equals(service.service.getClassName()))
                {
                    if(service.foreground)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService()
    {
        if(!isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            ContextCompat.startForegroundService(this, intent);
            Toast.makeText(this, "Location Service has started",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService()
    {
        if(isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            stopService(intent);
            Toast.makeText(this, "Location Service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    public static void setToggle(boolean status) {
        toggleButton.setChecked(status);
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.FINE_LOCATION_PERMISSION);
    }

}