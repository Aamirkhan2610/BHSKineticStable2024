package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 12/11/2017.
 */

public class SAMPLE_ACTIVITY extends Activity{
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sample);
        activity=SAMPLE_ACTIVITY.this;
        mContext = SAMPLE_ACTIVITY.this;
        gps = new TrackGPS(mContext);
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);

        img_refresh.setImageResource(R.drawable.ic_back);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
        }
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "ParkingAddress");
    }



    @Override
    protected void onResume() {
        super.onResume();

    }



    //Ask User to choose Yes/No
    public static void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext,R.style.AlertDialogStyle);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutParking");
            }
        });

        builderInner.setNegativeButton(mContext.getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("logoutParking")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    //WoodCuttingActivity.activity.finish();
                    Utils.clearPref(mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

