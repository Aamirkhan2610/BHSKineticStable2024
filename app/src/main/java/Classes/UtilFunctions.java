package Classes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import Interface.RetroInterface;
import bhskinetic.idee.com.bhskinetic_new.R;
import dmax.dialog.SpotsDialog;

import static android.content.Context.LOCATION_SERVICE;

public class UtilFunctions {

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static Location mLastLocation;
    public static LocationManager locationManager;
    public static GoogleApiClient mGoogleApiClient;
    public static AlertDialog mProgressDialog;
    public static String MyPREFERENCES = "prfs";
   // public static String BASE_URL = "http://www.ideetracker.com/bhstrack/";
     public static String BASE_URL = "http://203.125.153.221/tt_tv1/";
    public static RetroInterface retroInterface = RetrofitClient.getRetrofitClient(BASE_URL).create(RetroInterface.class);
  //tiffany
    public static Context mContext;
    public static double latitude = 0.0, longitude = 0.0;

    public UtilFunctions(Context mContext, GoogleApiClient mGoogleApiClient) {
        this.mContext = mContext;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public UtilFunctions(Context mContext) {
        this.mContext = mContext;
    }

    public static void getLocation() {
        if (checkPlayServices(mContext)) {
            getLatLong();
        }
    }

    public static void getLatLong() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
        }
    }

    public static void showDialog(Context mContext) {
        mProgressDialog = new SpotsDialog(mContext, R.style.ProgressDialogStyle);
        mProgressDialog.setCancelable(false);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public static void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public static boolean checkPlayServices(Context mContext) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog((Activity) mContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                Toast.makeText(mContext, "This Device is not Supported!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable(Context mContext) {
        /*ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();*/

        NetworkClass nc = new NetworkClass(mContext);
        Thread t = new Thread(nc);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t.stop();
        } catch (Exception e) {

        }
        return nc.getNetworkStat();
    }

    public static void showSnackBar(Context mContext, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
        TextView tvSnackBar = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        tvSnackBar.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        snackbar.show();
    }

    public static void Alert(String alertTitle, String alertMessage, Context mContext) {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public static void LogI(String key, String value, Context mContext) {

        Log.i(mContext.getClass().getSimpleName() + ": " + key, value);
    }

    //Get Preference
    public static String getPref(String key, Context mContext) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "0");
    }

    public static void showAlert(Context mContext, String message) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        TextView tvExit = (TextView) dialog.findViewById(R.id.tvExit);
        tvExit.setText(message);
        Button btnNeg = (Button) dialog.findViewById(R.id.btnNegative);
        btnNeg.setText("Ok");
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnPos = (Button) dialog.findViewById(R.id.btnPositive);
                /*btnPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        BreakDownServiceActivity.super.onBackPressed();
                    }
                });*/
        btnPos.setVisibility(View.GONE);
        dialog.show();
    }
}