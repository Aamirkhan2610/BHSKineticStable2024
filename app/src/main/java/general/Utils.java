package general;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.regex.Pattern;

import bhskinetic.idee.com.bhskinetic_new.R;

/**
 * Created by Aamir on 2/18/2017.
 */
public class Utils {
    public static String MyPREFERENCES="prfs";
    public static String jobCodeSeperator=",";
    public static String ScanResult="";
    public static boolean isFromMultipleSelection=false;
    public static void Showdropdown(final Context mContext, String heading, final ArrayAdapter<String> arrayAdapter, final String key){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setCancelable(false);
        builderSingle.setTitle(heading);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
            }
        });

        builderSingle.show();

    }

    public static boolean isGPSON(Context mContext){
        final LocationManager manager = (LocationManager)mContext.getSystemService    (Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false;
        }else{
            return true;
        }
    }

    public static boolean checkServiceRunning(Context mContext){
        ActivityManager manager = (ActivityManager)mContext.getSystemService(mContext.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (mContext.getApplicationContext().getPackageName()
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    //Validate the user with alert dialogue
    public static void Alert(String alertMessage, Context mContext){
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle("");
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public static void clearPref(Context mContext) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.edit().remove(mContext.getString(R.string.pref_driverinfo)).commit();
        sharedpreferences.edit().remove(mContext.getString(R.string.pref_IMEINumber)).commit();
        sharedpreferences.edit().remove(mContext.getString(R.string.pref_driverID)).commit();
        sharedpreferences.edit().remove(mContext.getString(R.string.pref_drivertype)).commit();
    }

    public static void clearTimerPreferences(Context mContext) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.edit().remove("lock_job_id").commit();
        sharedpreferences.edit().remove("lock_time").commit();
        sharedpreferences.edit().remove("lock_timestamp").commit();

    }

    //Set Preference
    public static void setPref(String key, String value, Context mContext){
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //Log For the information
    public static void LogI(String key, String value, Context mContext){

        Log.i(mContext.getClass().getSimpleName() + ": " + key, value);
    }

    //Get Preference
    public static String getPref(String key, Context mContext){
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "0");
    }

    public static boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }catch (Exception e){
            Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }
    //Validate the user with alert dialogue
    public static void Alert(String alertTitle, String alertMessage, Context mContext){
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }
}
