package com.yuri.locationchangetracker;

public class Constants {
    public final static int FINE_LOCATION_PERMISSION = 1;
    public static final int LOCATION_SERVICE_ID = 175;
    public static final String CHANNEL_ID = "location_notification_channel"; // notification channel id
    public static final String CHANNEL_NAME = "Location Service"; // The user visible name of the notification channel
    public static final String CHANNEL_DESCRIPTION = "This channel is used by location service"; // notification channel description
    public static final int DEFAULT_INTERVAL = 4000; // default location update interval
    public static final int FASTEST_INTERVAL = 2000; // fastest location update interval
    public static final int REQUEST_CODE = 0;
}
