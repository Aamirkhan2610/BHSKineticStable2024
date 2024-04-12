package activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Classes.DirectionsJSONParser;
import Classes.RecyclerAdapter;
import bhskinetic.idee.com.bhskinetic_new.R;

import static Classes.UtilFunctions.PLAY_SERVICES_RESOLUTION_REQUEST;


public class LiveTrackActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private double lat = 0;
    private double lng = 0;
    double latitude, longitude;
    String url;
    // Location updates intervals in sec
    public static ArrayList<LatLng> markerPoints;
    private List<Marker> listMarkers;
    String tag_json_obj = "json_obj_req";
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private boolean zoomed = false;
    public static String MapETA = "0";
    public static String DrivingDistance = "0";
    private boolean firstPass = true;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private Location mLastLocation;
    TextView tvVehNum, tvStatus, tvSpeed, tvLoc, tvDateTime, tvDistance,tvLoad;
    ImageView btnGetDirection;
    GoogleMap map;
    public static LatLng bgOrigin, bgDestination;
    ImageView ivBack;
    public static Handler GPSNetworkHandler;
    public static Runnable myRunnable;
    LatLng point1, point2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_track);
        init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        tvVehNum = (TextView) findViewById(R.id.tvVehNum);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvLoc = (TextView) findViewById(R.id.tvLoc);
        tvLoad=(TextView)findViewById(R.id.tvLoad);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        btnGetDirection = (ImageView) findViewById(R.id.btnGetDirection);
        //tvSpeedAt = findViewById(R.id.tvSpeedAt);
        tvDistance = (TextView) findViewById(R.id.tvDateTime);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // LiveTrackActivity.super.onBackPressed();
                startActivity(new Intent(LiveTrackActivity.this, HomeActivity.class));
                finish();
            }
        });
        getSupportActionBar().hide();
        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=23.0350,72.5293&daddr=22.3072,73.1812"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);*/
                // First we need to check availability of play services
                /*if (checkPlayServices()) {

                    // Building the GoogleApi client
                    buildGoogleApiClient();*/
                if (checkPlayServices()) {
                    //tvSpeedAt.setText("ETA");
                    tvSpeed.setText("----");
                    tvSpeed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, 0, 0);
                    tvDistance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_distance, 0, 0, 0);
                    tvLoc.setText("Distance");
                    tvLoc.setVisibility(View.GONE);
                    tvDistance.setText("----");
                    tvDistance.setVisibility(View.VISIBLE);
                    btnGetDirection.setVisibility(View.GONE);

                    createLocationRequest();
                    displayLocation();
                } else {
                    Toast.makeText(LiveTrackActivity.this, "Your Gps seems to be off, kindly turn it on", Toast.LENGTH_SHORT).show();
                }
                //togglePeriodicLocationUpdates();
                //startActivity(new Intent(LiveTrackActivity.this, DirectionActivity.class));
                //}
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LiveTrackActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(false);
        Bundle b = getIntent().getExtras();

        if (b != null) {

            lat = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LAT));
            lng = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LONG));
            String vehNum = b.getString(RecyclerAdapter.KEY_VEH_NUM);
            String speed = b.getString(RecyclerAdapter.KEY_SPEED);
            String vehLoc = b.getString(RecyclerAdapter.KEY_LOC);
            String vehLoad = b.getString(RecyclerAdapter.KEY_VEH_LOAD);
            String dateTime = b.getString(RecyclerAdapter.KEY_DATE_TIME);
            String color = b.getString(RecyclerAdapter.KEY_ASSET_COLOR);

            LatLng latLng = new LatLng(lat, lng);

            tvVehNum.setText(vehNum);
            tvSpeed.setText(speed);
            tvLoc.setText(vehLoc);
            tvLoad.setText(vehLoad);
            tvDateTime.setText(dateTime);

            if (color.trim().equalsIgnoreCase("black")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_black_small, 0, 0, 0);
                tvStatus.setText("is inactive");
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_black_small)));

            } else if (color.trim().equalsIgnoreCase("blue")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_blue_small, 0, 0, 0);
                tvStatus.setText("is idling");
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_blue_small)));

            } else if (color.trim().equalsIgnoreCase("green")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_green_small, 0, 0, 0);
                tvStatus.setText("is running");
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green_small)));

            } else if (color.trim().equalsIgnoreCase("red")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_red_small, 0, 0, 0);
                tvStatus.setText("is stopped");
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_red_small)));
            }

            CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        /*mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();*/

        // Displaying the new location on UI
        //displayLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Once connected with google api, get the location
        //displayLocation();
        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection failed", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            /*final LatLng[] point1 = new LatLng[1];
            final LatLng[] point2 = new LatLng[1];*/
            //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
            /*mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    map = googleMap;
                    DrawGoogleMapPath();
                    *//*
                    Bundle b = getIntent().getExtras();
                    if (b != null) {
                        lat = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LAT));
                        lng = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LONG));
                        String vehNum = b.getString(RecyclerAdapter.KEY_VEH_NUM);
                        String speed = b.getString(RecyclerAdapter.KEY_SPEED);
                        String vehLoc = b.getString(RecyclerAdapter.KEY_LOC);
                        String dateTime = b.getString(RecyclerAdapter.KEY_DATE_TIME);
                        String color = b.getString(RecyclerAdapter.KEY_ASSET_COLOR);

                        LatLng latLng = new LatLng(lat, lng);

                        tvVehNum.setText(vehNum);
                        tvSpeed.setText(speed);
                        tvLoc.setText(vehLoc);
                        tvDateTime.setText(dateTime);*//*

                        *//*Marker markerVehicle = null, markerCurrent = null;

                        if (color.trim().equalsIgnoreCase("black")) {

                            tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_black, 0, 0, 0);
                            tvStatus.setText("is inactive");
                            markerVehicle = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));

                        } else if (color.trim().equalsIgnoreCase("blue")) {

                            tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_black, 0, 0, 0);
                            tvStatus.setText("is idling");
                            markerVehicle = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_blue)));

                        } else if (color.trim().equalsIgnoreCase("green")) {

                            tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_green, 0, 0, 0);
                            tvStatus.setText("is running");
                            markerVehicle = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_green)));

                        } else if (color.trim().equalsIgnoreCase("red")) {

                            tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_red, 0, 0, 0);
                            tvStatus.setText("is stopped");
                            markerVehicle = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_red)));
                        }*//*
                    if (ActivityCompat.checkSelfPermission(LiveTrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveTrackActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                        *//*LatLng latLng1 = new LatLng(latitude, longitude);
                        markerCurrent = googleMap.addMarker(new MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.defaultMarker()));
                        //CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(16).build();
                        listMarkers = new ArrayList<>();
                        listMarkers.add(markerCurrent);
                        listMarkers.add(markerVehicle);
                        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                        for (Marker m : listMarkers) {
                            latLngBuilder.include(m.getPosition());
                        }
                        LatLngBounds bounds = latLngBuilder.build();
                        PolylineOptions po = new PolylineOptions();
                        po.add(latLng);
                        po.add(latLng1);
                        po.width(10).color(Color.CYAN).geodesic(true);
                        googleMap.addPolyline(po);
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                        int padding = 40;
                        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                googleMap.animateCamera(cameraUpdate);
                            }
                        });
                }*//*
                }
            });*/
            DrawGoogleMapPath();

        } else

        {
            Log.e("Location get failed", "Couldn't get the location. Make sure location is enabled on the device");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPlayServices())
            buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);*/

    }

    protected void stopLocationUpdates() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            /*btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");*/

        } else {
            // Changing the button text
            /*btnStartLocationUpdates
                    .setText(getString(R.string.btn_start_location_updates));

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");*/
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    // Fetches data from url passed
    /*private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }*/

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */

    private void UpdateDistanceETA() {
        GPSNetworkHandler = new Handler();
        //final int gpsNetworkDelay = 300000; //Checking GPS Status and Updating GPS Status on Server every 20Sec
        final int gpsNetworkDelay = 15000; //Checking GPS Status and Updating GPS Status on Server every 5Sec
        myRunnable = new Runnable() {
            public void run() {
                /*lat = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LAT));
                lng = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LONG));*/
                try {
                    bgOrigin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    GPSNetworkHandler.postDelayed(this, gpsNetworkDelay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        GPSNetworkHandler.postDelayed(myRunnable, gpsNetworkDelay);

    }
   /* private void UpdateJobStatus(String status) {
        String driverUD= UtilFunctions.getPref(mContext.getResources().getString(R.string.str_driverID),mContext);
        String IMEINumber=UtilFunctions.getPref(mContext.getResources().getString(R.string.str_IMEINumber), mContext);
        //Get Location and Shift List for a driv

        APIUtils.sendRequest(LiveTrackActivity.this, "Order Update Map", "Order_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID1=" + driverUD + "&Str_DriverID2=" + driverUD + "&Str_VehicleNo=" + vehicleNumber + "&JobID=" + JobID + "&Str_Sts=" + status + "&ETA=" + MapETA + "&ETC=" + ETCVal, "order_update_map");
    }
*/

    private void DrawGoogleMapPath() {
        String color = null;
        if (map != null) {

            // Enable MyLocation Button in the Map
            if (ActivityCompat.checkSelfPermission(LiveTrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveTrackActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            map.clear();
            map.setMyLocationEnabled(true);
            Bundle b = getIntent().getExtras();
            if (b != null) {
                lat = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LAT));
                lng = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LONG));
                String vehNum = b.getString(RecyclerAdapter.KEY_VEH_NUM);
                String speed = b.getString(RecyclerAdapter.KEY_SPEED);
                String vehLoc = b.getString(RecyclerAdapter.KEY_LOC);
                String dateTime = b.getString(RecyclerAdapter.KEY_DATE_TIME);
                color = b.getString(RecyclerAdapter.KEY_ASSET_COLOR);

                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                point1 = new LatLng(latitude, longitude);
                point2 = new LatLng(lat, lng);
            }

            //Drawing Path between current location and
            // Already two locations
            markerPoints = new ArrayList<>();
            if (markerPoints.size() > 1) {
                markerPoints.clear();
                map.clear();
            }

            // Adding new item to the ArrayList
            markerPoints.add(point1);
            markerPoints.add(point2);

            // Creating MarkerOptions
            MarkerOptions options_start = new MarkerOptions();
            MarkerOptions options_end = new MarkerOptions();

            // Setting the position of the marker
            options_start.position(point1);
            options_end.position(point2);

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            options_start.icon(null);
            if (color.trim().equalsIgnoreCase("black")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_black_small, 0, 0, 0);
                tvStatus.setText("is inactive");
                //markerVehicle = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                options_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_black_small));

            } else if (color.trim().equalsIgnoreCase("blue")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_blue_small, 0, 0, 0);
                tvStatus.setText("is idling");
                //markerVehicle = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_blue)));
                options_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_blue_small));

            } else if (color.trim().equalsIgnoreCase("green")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_green_small, 0, 0, 0);
                tvStatus.setText("is running");
                //markerVehicle = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_green)));
                options_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green_small));

            } else if (color.trim().equalsIgnoreCase("red")) {

                tvVehNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.car_red_small, 0, 0, 0);
                tvStatus.setText("is stopped");
                //markerVehicle = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_red)));
                options_end.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_red_small));
            }


            // Add new marker to the Google Map Android API V2
            map.addMarker(options_start);
            map.addMarker(options_end);

            // Checks, whether start and end locations are captured
            if (markerPoints.size() >= 2) {
                LatLng origin = markerPoints.get(0);
                LatLng dest = markerPoints.get(1);
                bgDestination = dest;
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }

            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(point1);
            builder.include(point2);
            final LatLngBounds bounds = builder.build();
            //.bearing(30).tilt(45)
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60), new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(map.getCameraPosition()).zoom(map.getCameraPosition().zoom).bearing(180).tilt(60).build()))
                            ;
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    //map.setLatLngBoundsForCameraTarget(bounds);
                }
            });
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(Color.BLUE);

                /*tv_eta.setText("ETA: " + MapETA.toString() + " Mins");
                tv_driving_distance.setText("DISTANCE: " + DrivingDistance.toString() + " KM");*/
                Log.e("ETA", MapETA.toString());
                Log.e("DISTANCE", DrivingDistance.toString());
                sendRequest();
                UpdateDistanceETA();
                //Updating Order Status or Adding Dynamic Buttons based on permission
                /*if (updateOrder) {
                    UpdateJobStatus("CONFIRMED");
                } else {
                    AddDynamicButtons();
                }*/

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null)
                map.addPolyline(lineOptions);
        }
    }

    private void sendRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseObject = new JSONObject(response.toString());
                    JSONArray routes = responseObject.optJSONArray("routes");
                    JSONObject routesObj = routes.optJSONObject(0);
                    JSONArray legs = routesObj.optJSONArray("legs");
                    JSONObject legsObj = legs.optJSONObject(0);
                    JSONObject distance = legsObj.optJSONObject("distance");
                    JSONObject duration = legsObj.optJSONObject("duration");

                    //.replace("mins", "")
                    MapETA = duration.optString("text").trim();
                    //.replace("km", "")
                    DrivingDistance = distance.optString("text").trim();

                    /*MapActivity.tv_eta.setText("ETA: " + MapETA.toString() + " Mins");
                    MapActivity.tv_driving_distance.setText("DISTANCE: "+DrivingDistance.toString() + " KM");*/
                    Log.e("Direction response", response.toString());
                    Log.e("ETA", MapETA.toString());
                    Log.e("DISTANCE", DrivingDistance.toString());

                    //tvSpeedAt.setText("ETA");
                    //+ "  Mins"
                    tvSpeed.setText(MapETA.toString());
                    tvLoc.setText("Distance");
                    tvLoc.setVisibility(View.GONE);
                    //+ "  KM"
                    tvDistance.setText(DrivingDistance.toString());
                } catch (Exception e) {
                    //e.fillInStackTrace();
                    Log.e("Exception when parsing", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(getClass().getSimpleName() + "\n", "Error: " + error.getMessage());
                Log.e("Error when parsing", error.toString());
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);
    }

    /*private void UpdateJobStatus(String status) {
        String driverUD = UtilFunctions.getPref(LiveTrackActivity.this.getResources().getString(R.string.str_driverID), LiveTrackActivity.this);
        String IMEINumber = UtilFunctions.getPref(LiveTrackActivity.this.getResources().getString(R.string.str_IMEINumber), LiveTrackActivity.this);
        //Get Location and Shift List for a drive

        APIUtils.sendRequest(mContext, "Order Update Map", "Order_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID1=" + driverUD + "&Str_DriverID2=" + driverUD + "&Str_VehicleNo=" + vehicleNumber + "&JobID=" + JobID + "&Str_Sts=" + status + "&ETA=" + MapETA + "&ETC=" + ETCVal, "order_update_map");
    }*/

    public void showResponse(String response) {
        //if(redirectionKey.equalsIgnoreCase("order_update_map")){
        MapETA = "0";
        //ETCVal="0";
        DrivingDistance = "0";
        //try{
                /*if(responseObject.optString("recived").equalsIgnoreCase("1")){
                    //ButtonCap = responseObject.optString("ButtonCap");
                    //Utils.setPref(mContext.getResources().getString(R.string.str_job_status),responseObject.optString("Status"),mContext);

                    String Ack_Msg=responseObject.optString("Ack_Msg");
                    // String Ack_Msg="N|N|N|0000";
                    String[] ackArray=Ack_Msg.split("\\|");

                    boolean isPhoto=false;
                    boolean isSign=false;
                    //Checking if customer has permission to upload photo
                    if(ackArray[0].equalsIgnoreCase("P")){
                        isPhoto=true;
                    }else{
                        isPhoto=false;
                    }

                    //Checking if customer has permission to upload signature
                    if(ackArray[1].equalsIgnoreCase("S")){
                        isSign=true;
                    }else{
                        isSign=false;
                    }

                    boolean isNoPhoto=true;
                    Intent intent;

                    *//*if(isPhoto) {
                        intent = new Intent(LiveTrackActivity.this, PhotoUploadActivity.class);
                        intent.putExtra("isSign",isSign);
                        intent.putExtra("isNoPhoto",isNoPhoto);
                        startActivity(intent);
                    }*//*

                    //AddDynamicButtons();
                }else{
                    Utils.Alert(getResources().getString(R.string.alert_title),getResources().getString(R.string.alert_error_order_update),mContext);
                }
            }catch (Exception e){
                e.fillInStackTrace();
            }
        }else if(redirectionKey.equalsIgnoreCase("update_distance_eta")){*/
        try {
            JSONObject responseObject = new JSONObject(response);

            JSONArray routes = responseObject.optJSONArray("routes");
            JSONObject routesObj = routes.optJSONObject(0);
            JSONArray legs = routesObj.optJSONArray("legs");
            JSONObject legsObj = legs.optJSONObject(0);
            JSONObject distance = legsObj.optJSONObject("distance");
            JSONObject duration = legsObj.optJSONObject("duration");

            MapETA = duration.optString("text").replace("mins", "").trim();
            DrivingDistance = distance.optString("text").replace("km", "").trim();

                /*tv_eta.setText("ETA: " + MapETA.toString() + " Mins");
                tv_driving_distance.setText("DISTANCE: "+DrivingDistance.toString() + " KM");*/

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
    //}


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (checkPlayServices()) {
                        //tvSpeedAt.setText("ETA");
                        tvSpeed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, 0, 0);
                        tvDistance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_distance, 0, 0, 0);
                        tvSpeed.setText("----");
                        tvLoc.setText("Distance");
                        tvLoc.setVisibility(View.GONE);
                        tvDistance.setText("----");
                        tvDistance.setVisibility(View.VISIBLE);
                        btnGetDirection.setVisibility(View.GONE);

                        createLocationRequest();
                        displayLocation();
                        return;
                    }
                }
        }
    }
}
