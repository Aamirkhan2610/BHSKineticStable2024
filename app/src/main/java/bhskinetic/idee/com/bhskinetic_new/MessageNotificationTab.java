package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/20/2017.
 */

public class MessageNotificationTab extends TabActivity {
    public static TextView tv_header;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Context mContext;
    public static Activity activity;
    public static TrackGPS gps;
    public static TabHost tabHost;
    public static String Str_TripNo="NA";
    public static String Str_JobNo="0";
    public static String Str_MsgID="0";
    public static String Str_SentTo="0";
    public static ImageView img_newmessage;
    public static String Stry_Msg_Type="";
    public static String NewMessageSub="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);
        mContext=MessageNotificationTab.this;
        Utils.setPref(getString(R.string.pref_mesibo_pin), "1", mContext);

        /*Intent intent = new Intent(mContext, StartUpActivity.class);
        intent.putExtra("skipTour",true);
        startActivity(intent);*/

        gps=new TrackGPS(mContext);

        Str_TripNo="NA";
        Str_JobNo="0";
        img_newmessage = (ImageView) findViewById(R.id.img_newmessage);
        tabHost = getTabHost();
        TabHost.TabSpec notificationspec = tabHost.newTabSpec(mContext.getResources().getString(R.string.title_notification));
        notificationspec.setIndicator(mContext.getResources().getString(R.string.title_notification), getResources().getDrawable(R.drawable.ic_add));
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationspec.setContent(notificationIntent);
        tabHost.addTab(notificationspec);

        TabHost.TabSpec messagespec = tabHost.newTabSpec(mContext.getResources().getString(R.string.title_message));
        messagespec.setIndicator(mContext.getResources().getString(R.string.title_message), getResources().getDrawable(R.drawable.ic_add));
        Intent messageIntent = new Intent(this, MessageSubActivity.class);
        messagespec.setContent(messageIntent);
        tabHost.addTab(messagespec);

        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));

            if(b.getInt("CurrentTab")!=0){

                tabHost.setCurrentTab(1);

            }

            if(b.getString("Str_TripNo")!=null){
                Str_TripNo=b.getString("Str_TripNo");
                Str_JobNo=b.getString("Str_JobNo");
            }

            if(b.getString("Str_MsgID")!=null){
                Str_MsgID=b.getString("Str_MsgID");
                Str_SentTo=b.getString("Str_SentTo");
            }

            if(b.getString("Stry_Msg_Type")!=null){
                Stry_Msg_Type=b.getString("Stry_Msg_Type");
            }

            if(b.getString("NewMessageSub")!=null){
                NewMessageSub=b.getString("NewMessageSub");
            }

        }

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutMessage");
                dialog.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        mContext.registerReceiver(mMessageReceiver, new IntentFilter("refresh_message_push"));
    }
    @Override
    protected void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mMessageReceiver);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Refereshing Activity on receiving push notification
            Bundle b=intent.getExtras();
            String Type="";
            if(b!=null){
                Type=b.getString("Type");
            }

            if(Type.trim().equalsIgnoreCase("PUSH") && tabHost.getCurrentTab()==0) {
                Intent refreshIntent=new Intent(context,MessageNotificationTab.class);
                mContext.startActivity(refreshIntent);
                finish();
            }else if(Type.trim().equalsIgnoreCase("MSG") && tabHost.getCurrentTab()==1) {
                /*Intent refreshIntent=new Intent(context,MessageNotificationTab.class);
                refreshIntent.putExtra("CurrentTab",1);
                mContext.startActivity(refreshIntent);
                finish();*/
                MessageSubActivity.getLatestMessage();
            }

        }
    };

    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutMessage")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    finish();
                    Utils.clearPref(mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
