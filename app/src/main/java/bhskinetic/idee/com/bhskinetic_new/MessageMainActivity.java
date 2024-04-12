package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelMessageMain;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/13/2017.
 */

public class MessageMainActivity extends Activity {
    public static TextView tv_header;
    public static Context mContext;
    public static ListElementAdapter adapter;
    public static ListView list_message_main;
    public static ModelMessageMain modelMessageMain;
    public static ArrayList<ModelMessageMain> modelMessageMainArrayList;
    public static TrackGPS gps;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Activity activity;
    public static ImageView img_newmessage;
    public static  ProgressDialog pDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_message_main_list);
        mContext = MessageMainActivity.this;
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent=new Intent(v.getContext(),DashboardActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
            }
        });
        list_message_main=(ListView)findViewById(R.id.list_message_main);

        img_newmessage = (ImageView) findViewById(R.id.img_newmessage);
        gps = new TrackGPS(mContext);


        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        pDialog = new ProgressDialog(mContext,R.style.ProgressDialogStyle);
        pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));

        activity = MessageMainActivity.this;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
        }

        modelMessageMainArrayList=new ArrayList<>();

        list_message_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MessageNotificationTab.class);
                intent.putExtra("Str_MsgID",modelMessageMainArrayList.get(position).getStr_MsgID());
                intent.putExtra("Str_SentTo",modelMessageMainArrayList.get(position).getStr_SentTo());
                intent.putExtra("Str_TripNo",modelMessageMainArrayList.get(position).getStr_Trip_No());
                intent.putExtra("Str_JobNo",modelMessageMainArrayList.get(position).getStr_Job_No());
                intent.putExtra("CurrentTab", 1);
                intent.putExtra("Stry_Msg_Type","SUB");
                startActivity(intent);

            }
        });

        img_newmessage.setVisibility(View.VISIBLE);
        img_newmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserListActivity.class);
                intent.putExtra("title","New Message");
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pDialog!=null){
            if(!pDialog.isShowing()){
                pDialog.show();
            }
        }
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "MessageMainListAddress");
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutMainMessageList");
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
        try {
            if (redirectionKey.equalsIgnoreCase("MessageMainListAddress")) {
                if(pDialog!=null){
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                String lat = "0";
                String lng = "0";
                String GPSStatus = "OFF";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }

                    lat = "" + gps.getLatitude();
                    lng = "" + gps.getLongitude();
                    GPSStatus = "ON";
                }

                getMessageMainList(address, lat, lng, GPSStatus);
            } else if (redirectionKey.equalsIgnoreCase("logoutMainMessageList")) {

                try {
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("Status").equalsIgnoreCase("1")) {
                        Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(logoutIntent);
                        MessageMainActivity.activity.finish();
                        Utils.clearPref(mContext);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (redirectionKey.equalsIgnoreCase("MessageMainList")) {
                try {
                    modelMessageMainArrayList.clear();
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("recived").equalsIgnoreCase("1")) {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject innerObj = jsonArray.getJSONObject(i);
                        modelMessageMain=new ModelMessageMain();
                        modelMessageMain.setStr_MsgID(innerObj.optString("Str_MsgID"));
                        modelMessageMain.setStr_CreatedBy(innerObj.optString("Str_CreatedBy"));
                        String Str_SentTo=innerObj.optString("Str_SentTo");
                        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                        if(Str_SentTo.equalsIgnoreCase(DriverID)){
                            modelMessageMain.setStr_SentTo(innerObj.optString("Str_CreatedBy"));
                        }else {
                            modelMessageMain.setStr_SentTo(innerObj.optString("Str_SentTo"));
                        }
                        modelMessageMain.setStr_Message(innerObj.optString("Str_Message"));
                        modelMessageMain.setStr_Msd_Dt(innerObj.optString("Str_Msd_Dt"));
                        modelMessageMain.setStr_Read_At(innerObj.optString("Str_Read_At"));
                        modelMessageMain.setStr_Read_Sts(innerObj.optString("Str_Read_Sts"));
                        modelMessageMain.setStr_Trip_No(innerObj.optString("Str_Trip_No"));
                        modelMessageMain.setStr_Job_No(innerObj.optString("Str_Job_No"));
                        modelMessageMainArrayList.add(modelMessageMain);
                    }


                    adapter = new ListElementAdapter(mContext);
                    list_message_main.setAdapter(adapter);

                    }else{
                        Utils.Alert(mContext.getResources().getString(R.string.alert_error_getting_messagelist),mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getMessageMainList(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Message Main List","Message_Log.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_Msg_Type=MAIN&Str_DriverID="+DriverID+"&Str_Message=NA&Str_MsgID=0&Str_TripNo=NA&Str_JobNo=0", "MessageMainList");
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
            return modelMessageMainArrayList.size();
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
                v = layoutInflater.inflate(R.layout.raw_messagemainlist, null);
                h = new ViewHolder();

                h.tv_display = (TextView) v.findViewById(R.id.tv_display);
                h.tv_message = (TextView) v.findViewById(R.id.tv_message);
                h.tv_datetime = (TextView) v.findViewById(R.id.tv_datetime);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);

                h.tv_display.setText("To: "+modelMessageMainArrayList.get(position).getStr_SentTo());
                h.tv_message.setText(modelMessageMainArrayList.get(position).getStr_Message());
                h.tv_datetime.setText(modelMessageMainArrayList.get(position).getStr_Msd_Dt());
                h.tv_index.setText((position + 1) + ".");
                h.tv_index.setVisibility(View.GONE);
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_display.setText("To: "+modelMessageMainArrayList.get(position).getStr_SentTo());
                h.tv_message.setText(modelMessageMainArrayList.get(position).getStr_Message());
                h.tv_datetime.setText(modelMessageMainArrayList.get(position).getStr_Msd_Dt());
                h.tv_index.setText((position + 1) + ".");
                h.tv_index.setVisibility(View.GONE);
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_display;
            private TextView tv_index;
            private TextView tv_message;
            private TextView tv_datetime;
        }
    }
}
