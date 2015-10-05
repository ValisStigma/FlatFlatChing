
package com.flatflatching.flatflatching.helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
 
import android.util.Log;

import java.io.IOException;

public class GpsTracker extends Service implements LocationListener {
 
    private Context context;
 
    // flag for GPS status
    private boolean gpsEnabled;
 
    // flag for network status
    private boolean networkEnabled;
 
    // flag for GPS status
    private transient boolean hasLocation;
 
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DIST_CHANGE = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_CHANGE = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    private LocationManager locationManager;
 
    /** This class is used to locate the users phone.
     * @param context the context of the activity calling
     */
    public GpsTracker(final Context context) {
        super();
        setContext(context);
        //getLocation();
    }
 
    /** Gets the current location of the user.
     * @return the current Location
     */
    /*public final Location getLocation() {
        try {
            setLocationManager((LocationManager) getContext()
                    .getSystemService(LOCATION_SERVICE));
 
            // getting GPS status
            setGpsEnabled(getLocationManager()
                    .isProviderEnabled(LocationManager.GPS_PROVIDER));
 
            // getting network status
            setNetworkEnabled(getLocationManager()
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
            
            if (isNetworkEnabled()) {
                setHasLocation(true);
                getLocationManager().requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_CHANGE,
                        MIN_DIST_CHANGE, this);
                if (getLocationManager() != null) {
                    setLocation(locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                    setCoordinates();
                }
            }
            if (isGpsEnabled()) {
                setHasLocation(true);
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_CHANGE,
                        MIN_DIST_CHANGE, this);
            }
        } catch (IOException e) {
            Log.d(ACCOUNT_SERVICE, e.getMessage());
        }
 
        return location;
    }*/
    
    private void setCoordinates() throws IOException {
        if (location == null) {
            throw new IOException();
        } else {
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        }
    }
     
    /**
     * Function to get latitude.
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude.
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
     
    /**
     * Function to check GPS/wifi enabled.
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.hasLocation;
    }
 
    @Override
    public void onLocationChanged(final Location location) {
        setLongitude(location.getLongitude());
        setLatitude(location.getLatitude());
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
        //Not needed in this implementation

    }
 
    @Override
    public void onProviderEnabled(final String provider) {
        //Not needed in this implementation

    }
 
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        //Not needed in this implementation

    }
 
    @Override
    public IBinder onBind(final Intent arg0) {
        return null;
    }

    private Context getContext() {
        return context;
    }

    private void setContext(final Context context) {
        this.context = context;
    }

    private boolean isGpsEnabled() {
        return gpsEnabled;
    }

    private void setGpsEnabled(final boolean isGpsEnabled) {
        this.gpsEnabled = isGpsEnabled;
    }

    private boolean isNetworkEnabled() {
        return networkEnabled;
    }

    private void setNetworkEnabled(final boolean isNetworkEnabled) {
        this.networkEnabled = isNetworkEnabled;
    }

    private void setHasLocation(final boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    private LocationManager getLocationManager() {
        return locationManager;
    }

    private void setLocationManager(final LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    private void setLocation(final Location location) {
        this.location = location;
    }

    private void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(final double longitude) {
        this.longitude = longitude;
    }
 
}