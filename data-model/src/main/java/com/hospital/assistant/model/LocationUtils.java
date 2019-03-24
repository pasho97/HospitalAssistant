package com.hospital.assistant.model;

public class LocationUtils {

  public static double getDistance(Location first, Location second) {
    double lat1 = first.getLatitude();
    double lon1 = first.getLongitude();

    double lat2 = second.getLatitude();
    double lon2 = second.getLongitude();
    // haversine great circle distance approximation, returns meters
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
        + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
        * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60; // 60 nautical miles per degree of seperation
    dist = dist * 1852; // 1852 meters per nautical mile
    return (dist);
  }

  private static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private static double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }
}
