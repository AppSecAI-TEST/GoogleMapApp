package com.btech.googlemapapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.btech.googlemapapp.Util.LongPressLocationSource;
import com.btech.googlemapapp.Util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class HomeActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener,
        ResultCallback {

    //variables
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final String LATITUD = "latitud";
    static final String LONGITUD = "longitud";

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    GoogleMap mMap;
    LongPressLocationSource mLocationSource;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    Marker destinationMarker;

    //Custom functions

    /**
     * This function will add a marker to the current location
     * and move the camere
     */
    private void saveLocation(){
        Util.saveOnSharedPreferences(LATITUD, String.valueOf(mLastLocation.getLatitude()), getApplicationContext());
        Util.saveOnSharedPreferences(LONGITUD, String.valueOf(mLastLocation.getLongitude()), getApplicationContext());
    }

    private void saveLocation(LatLng point){
        Util.saveOnSharedPreferences(LATITUD, String.valueOf(point.latitude), getApplicationContext());
        Util.saveOnSharedPreferences(LONGITUD, String.valueOf(point.longitude), getApplicationContext());
    }

    private boolean verifyLocation(){
        String latitud = Util.getFromSharedPreferences(LATITUD, getApplicationContext());
        String longitud = Util.getFromSharedPreferences(LONGITUD, getApplicationContext());
        if (latitud != null && !latitud.isEmpty() &&
                longitud != null && !longitud.isEmpty())
            return true;
        return false;
    }

    private LatLng getStoredLocation(){
        String latitud = Util.getFromSharedPreferences(LATITUD, getApplicationContext());
        String longitud = Util.getFromSharedPreferences(LONGITUD, getApplicationContext());
        return new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));
    }

    private void goCurrent(){
        LatLng point = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(point).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        saveLocation();
    }

    private void goCurrent(LatLng point){
        Toast.makeText(getApplicationContext(), "Showing your possible location", Toast.LENGTH_SHORT);
        mMap.addMarker(new MarkerOptions().position(point).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    /**
     * This function will request and get the location and try to go there...
     */
    private void goLocation(){
        locationManager = (LocationManager) getSystemService(HomeActivity.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, HomeActivity.this);
        mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null)
            goCurrent();
    }

    /**
     * Initialization of Google API Client
     */
    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
        }
    }

    /**
     * Initialization of the map
     */
    private void initMap() {
        mFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
    }

    /**
     * Initialization of Floating buttons
     */
    private void initializeFabs(){
        FloatingActionButton goFab = (FloatingActionButton) findViewById(R.id.goFab);
        goFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //trace route
            }
        });

        FloatingActionButton myLocation = (FloatingActionButton) findViewById(R.id.myLocationFab);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient != null)
                    mGoogleApiClient.disconnect();
                buildGoogleApiClient();
            }
        });
    }

    /**
     * Initialization of search box
     */
    private void initSearch(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);

        searchIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_black_24dp, getTheme()));

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Toast.makeText(getApplicationContext(), place.getAddress().toString(), Toast.LENGTH_LONG);
                LatLng point = place.getLatLng();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point).title("Current location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    /**
     * Initialization of navigation drawer menu
     */
    private void initNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        initializeFabs();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initSearch();

    }

    /**
     * Global Initialization
     */
    private void init() {
        initNavigationDrawer();
        initMap();
    }

    // Activity Life cycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationSource != null)
            mLocationSource.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationSource != null)
            mLocationSource.onPause();
    }

    //Drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void verifyPermissions(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Dialog ....
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }else{
            //Access granted
            improveMap();
            goLocation();
        }
    }

    //Permissions callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    improveMap();
                    goLocation();
                }
                return;
            }
        }
    }

    //Maps
    private void improveMap() {
        mMap.setMyLocationEnabled(true);
        mLocationSource = new LongPressLocationSource(mMap, destinationMarker);
        mMap.setLocationSource(mLocationSource);
        mMap.setOnMapLongClickListener(mLocationSource);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        verifyPermissions(HomeActivity.this);
        if (verifyLocation())
            goCurrent(getStoredLocation());
    }

    //GoogleAPIClient listeners
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    //Location listeners
    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation != null)
            goCurrent();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1000:
                Toast.makeText(HomeActivity.this, "Gonna move, access granted", Toast.LENGTH_SHORT);
                goLocation();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                //I can get the current location
                Toast.makeText(HomeActivity.this, "Gonna move, had access", Toast.LENGTH_SHORT);
                goLocation();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            HomeActivity.this, 1000);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                break;
        }
        mGoogleApiClient.disconnect();
    }
}