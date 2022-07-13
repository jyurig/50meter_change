package com.yuri.locationchangetracker;

public class DistanceCalculator {

    public static double getDistanceBetween(double firstLat, double firstLon, double secondLat, double secondLon, String unit) {
        if ((firstLat == secondLat) && (firstLon == secondLon)) {
            return 0;
        }
        else {
            double theta = firstLon - secondLon;
            double dist = Math.sin(Math.toRadians(firstLat)) * Math.sin(Math.toRadians(secondLat)) + Math.cos(Math.toRadians(firstLat)) * Math.cos(Math.toRadians(secondLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return dist;
        }
    }

}
