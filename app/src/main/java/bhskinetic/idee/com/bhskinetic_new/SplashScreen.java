package bhskinetic.idee.com.bhskinetic_new;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
/*
import com.splunk.mint.Mint;
*/

import java.io.File;
import java.util.Calendar;

import general.Utils;

/**
 * Created by Aamir on 2/24/2017.
 */
public class SplashScreen extends Activity {
    // Splash screen timer
    private int SPLASH_TIME_OUT = 3000;
    private int SCREEN_TIMEOUT = 900000;
    private Context mContext;
    private TextView tvAppVersion;
    private static String _date = "0";
    private static int _year = 0;
    public static String Str_Date="";
    private static String _month = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
       // Mint.initAndStartSession(this.getApplication(), "4cb2b69b");
        File camFile = new File(Environment.getExternalStorageDirectory(),getResources().getString(R.string.app_name));
        if(!camFile.isDirectory()){
            camFile.mkdir();
        }

        tvAppVersion=(TextView)findViewById(R.id.tv_app_version);
        tvAppVersion.setText("Application Version: "+BuildConfig.VERSION_NAME);

        mContext=SplashScreen.this;
        Log.i("FCM Token","=>"+ FirebaseInstanceId.getInstance().getToken());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                boolean isAppLock = false;
                if (Utils.getPref("app_lock", mContext) == null) {
                    isAppLock = false;
                }else if (Utils.getPref("app_lock", mContext).equalsIgnoreCase("1")) {
                    isAppLock = true;
                }else{
                    isAppLock = false;
                }

                if(isAppLock){
                    Intent intent=new Intent(mContext, OrderDetail.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return;
                }

                Intent i = null;
                if(!(Utils.getPref(getResources().getString(R.string.pref_driverID),mContext).equalsIgnoreCase("0"))){
                    i=new Intent(SplashScreen.this,DashboardActivity.class);
                }else{
                    i=new Intent(SplashScreen.this,LoginActivity.class);
                }
                startActivity(i);
                //close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

        getCurrentDateTime();

    }

    private void getCurrentDateTime() {
        Calendar cc = Calendar.getInstance();
        _year=cc.get(Calendar.YEAR);
        _month=""+(cc.get(Calendar.MONTH)+1);
        if(_month.trim().length()==1){
            _month="0"+_month;
        }
        _date = ""+cc.get(Calendar.DAY_OF_MONTH);
        if(_date.trim().length()==1){
            _date="0"+_date;
        }

        Str_Date=_date+"-"+_month+"-"+_year;
        Utils.setPref(mContext.getResources().getString(R.string.pref_datetime),Str_Date,mContext);
    }

}
