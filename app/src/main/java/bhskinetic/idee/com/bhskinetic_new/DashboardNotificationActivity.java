package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelDashNotification;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 12/11/2017.
 */

public class DashboardNotificationActivity extends Activity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ArrayList<ModelDashNotification> arrayList;
    public static ListView list_notification;
    public static ListElementAdapter adapter;
    public static boolean ScrolledToBottomOnce=false;
    public static String Str_SeqNo="";
    public static ViewTreeObserver observer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashnotification);
        activity = DashboardNotificationActivity.this;
        mContext = DashboardNotificationActivity.this;
        gps = new TrackGPS(mContext);
        Init();
    }

    private void Init() {
        ScrolledToBottomOnce=false;
        Str_SeqNo="";
        arrayList=new ArrayList<>();
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_logout.setVisibility(View.GONE);
        img_refresh.setImageResource(R.drawable.ic_back);
        Bundle b = getIntent().getExtras();
        tv_header.setText("Notification");
        list_notification=findViewById(R.id.list_notification);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + "", "DashboardRefresh1");
            }
        });

        list_notification.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = list_notification.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        //Toast.makeText(mContext,"TOP",Toast.LENGTH_SHORT).show();
                        // reached the top:
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  list_notification.getChildAt(totalItemCount-1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        UpdateReadStatus();
                        return;
                    }
                }
            }
        });





        getNotificationData("","","","");
    }

    private void UpdateReadStatus() {
        if(!ScrolledToBottomOnce) {
            // reached the bottom:
            ScrolledToBottomOnce=true;
            Str_SeqNo="";
            for(int i=0;i<arrayList.size();i++){
                if(Str_SeqNo.trim().length()==0){
                    Str_SeqNo=arrayList.get(i).getSeqNo();
                }else{
                    Str_SeqNo=Str_SeqNo+","+arrayList.get(i).getSeqNo();
                }
            }
            //Toast.makeText(mContext, "BOTTOM", Toast.LENGTH_SHORT).show();
            notificationReadStatus("",""+gps.getLatitude(),""+gps.getLatitude(),"");
        }
    }

    boolean willMyListScroll() {
        int pos = list_notification.getLastVisiblePosition();
        if (list_notification.getChildAt(pos).getBottom() > list_notification.getHeight()) {
            return true;
        } else {
            return false;
        }
    }


    private void getNotificationData(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Notification List", "Notify_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_MsgType=NOTIFY", "DashNotificationList");

    }

    private void notificationReadStatus(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Notification Read Status", "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_SeqNo="+Str_SeqNo+"&Str_Misc_Type=NOTIFICATION&Str_Misc_Status=Read&Str_JobFor=BHS", "NotificationReadUpdate");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //ListView Adapter Class
    public static class ListElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public ListElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder h = null;

            if (v == null) {
                v = layoutInflater.inflate(R.layout.raw_dashboard_notification, null);
                h = new ViewHolder();
                h.webnotification=v.findViewById(R.id.webnotification);
                h.webnotification.getSettings().setJavaScriptEnabled(true);
                h.webnotification.loadData(arrayList.get(position).getRemarks(), "text/html; charset=utf-8", "UTF-8");
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.webnotification.getSettings().setJavaScriptEnabled(true);
                h.webnotification.loadData(arrayList.get(position).getRemarks(), "text/html; charset=utf-8", "UTF-8");

            }
            return v;
        }

        private class ViewHolder {
            LinearLayout layout_delete;
            WebView webnotification;
        }
    }


    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("DashNotificationList")) {
            arrayList.clear();
            try {
                JSONObject jobj = new JSONObject(response);
                JSONArray jsonArray= jobj.optJSONArray("Data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject dataObject=jsonArray.optJSONObject(i);
                    ModelDashNotification modelDashNotification=new ModelDashNotification();
                    modelDashNotification.setRemarks(dataObject.optString("Remarks"));
                    modelDashNotification.setSeqNo(dataObject.optString("SeqNo"));
                    arrayList.add(modelDashNotification);
                }

                if(arrayList.size()>0){
                    adapter = new ListElementAdapter(mContext);
                    list_notification.setAdapter(adapter);
                    observer = list_notification.getViewTreeObserver();
                    observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (!willMyListScroll()) {
                                // Do something
                                UpdateReadStatus();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("NotificationReadUpdate")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if(jobj.optString("recived").equalsIgnoreCase("1")){
                   // Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("DashboardRefresh1")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Status").equalsIgnoreCase("0")) {
                    //Error
                    Utils.Alert(mContext.getResources().getString(R.string.alert_login_failed), mContext);
                } else {
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Menu_Button_Counter), jsonObject.optString("Menu_Button_Counter"), mContext);
                    DashboardActivity.UpdateNotificationCounter();
                    activity.finish();
                    //Redirect to another activity
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

