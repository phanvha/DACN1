package com.utt.utils;

import com.google.android.gms.maps.model.LatLng;

public class DistanceBetweenTwoPoints {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6378137;
    public static double getDistance(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (double) AVERAGE_RADIUS_OF_EARTH_KM * c;
    }
}

