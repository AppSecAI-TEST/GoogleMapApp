package com.btech.googlemapapp.Util;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by danielromero on 23/5/17.
 */

public class LongPressLocationSource
        implements
        LocationSource,
        GoogleMap.OnMapLongClickListener {

    private OnLocationChangedListener mListener;
    private boolean mPaused;
    private GoogleMap mMap;
    private Marker destinationMarker;

    public LongPressLocationSource(GoogleMap mMap, Marker destinationMarker) {
        this.mMap = mMap;
        this.destinationMarker = destinationMarker;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        if (mListener != null && !mPaused) {
            mMap.clear();
            Location location = new Location("LongPressLocationProvider");
            location.setLatitude(point.latitude);
            location.setLongitude(point.longitude);
            location.setAccuracy(100);
            mListener.onLocationChanged(location);
            if (destinationMarker!= null) destinationMarker.remove();
            mMap.addMarker(new MarkerOptions().position(point).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }
    }

    public void onPause() {
        mPaused = true;
    }

    public void onResume() {
        mPaused = false;
    }
}
