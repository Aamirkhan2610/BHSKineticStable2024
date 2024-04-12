package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import Model.ModelOrderList;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/13/2017.
 */

public class UserListActivity extends Activity {
    public static TextView tv_header;
    public static Context mContext;
    public static ListView list_order;
    public static ListElementAdapter adapter;
    public static ModelOrderList modelOrderList;
    public static ArrayList<ModelOrderList> ArrayOrderList;
    public static ArrayList<ModelOrderList> ArrayOrderListFiltered;
    public static ArrayList<ModelOrderList> ArrayOrderListImplemented;
    public static TrackGPS gps;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Activity activity;
    public static String Str_JobView = "View";
    public static String Str_JobStatus = "";
    public static EditText edt_search_order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orderlist);
        mContext = UserListActivity.this;
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        list_order = (ListView) findViewById(R.id.list_order);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        ArrayOrderList = new ArrayList<>();
        ArrayOrderListFiltered = new ArrayList<>();
        ArrayOrderListImplemented= new ArrayList<>();

        gps = new TrackGPS(mContext);

        list_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MessageNotificationTab.class);
                intent.putExtra("Str_MsgID","0");
                intent.putExtra("CurrentTab", 1);
                intent.putExtra("NewMessageSub","1");
                intent.putExtra("Str_SentTo",ArrayOrderListImplemented.get(position).getDisplay());
                startActivity(intent);
                finish();
            }
        });

        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });
        img_logout.setVisibility(View.GONE);
        activity = UserListActivity.this;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
        }

        edt_search_order=(EditText)findViewById(R.id.edt_search_order);
        edt_search_order.setHint("Search...");
        edt_search_order.setVisibility(View.VISIBLE);
        edt_search_order.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(""+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ArrayOrderListFiltered.clear();
        if (charText.length() == 0) {
            ArrayOrderListFiltered.addAll(ArrayOrderList);
        }
        else
        {
            for (ModelOrderList wp : ArrayOrderList)
            {
                if ((wp.getDisplay().toLowerCase(Locale.getDefault()).contains(charText))||(wp.getTrip_No().toLowerCase(Locale.getDefault()).contains(charText))){
                    ArrayOrderListFiltered.add(wp);
                }

            }

        }

        ArrayOrderListImplemented.clear();
        ArrayOrderListImplemented.addAll(ArrayOrderListFiltered);

        if(adapter!=null)
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(edt_search_order!=null){
            edt_search_order.setText("");
        }
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "MessageListAddress");
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutUserList");
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
            if (redirectionKey.equalsIgnoreCase("MessageListAddress")) {
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

                getMessageList(address, lat, lng, GPSStatus);
            } else if (redirectionKey.equalsIgnoreCase("logoutUserList")) {

                try {
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("Status").equalsIgnoreCase("1")) {
                        Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(logoutIntent);
                        UserListActivity.activity.finish();
                        Utils.clearPref(mContext);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (redirectionKey.equalsIgnoreCase("UserList")) {
                try {
                    ArrayOrderList.clear();
                    JSONObject jobj = new JSONObject(response);
                    // if (jobj.optString("recived").equalsIgnoreCase("1")) {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject innerObj = jsonArray.getJSONObject(i);
                        modelOrderList = new ModelOrderList();
                        modelOrderList.setDisplay(innerObj.optString("ListValue"));
                        ArrayOrderList.add(modelOrderList);
                    }

                    ArrayOrderListImplemented.clear();
                    ArrayOrderListImplemented.addAll(ArrayOrderList);
                    Log.i("ORDER ARRAY", "==============" + ArrayOrderList.size());
                    adapter = new ListElementAdapter(mContext);
                    list_order.setAdapter(adapter);

                   /* }else{
                        Utils.Alert(mContext.getResources().getString(R.string.alert_error_getting_orderlist),mContext);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getMessageList(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Message List","Misc_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_Event=User", "UserList");
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
            return ArrayOrderListImplemented.size();
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
                v = layoutInflater.inflate(R.layout.raw_userlist, null);
                h = new ViewHolder();

                h.tv_display = (TextView) v.findViewById(R.id.tv_display);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);
                h.tv_display.setText(ArrayOrderListImplemented.get(position).getDisplay());
                h.tv_index.setText((position + 1) + ".");
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_display.setText(ArrayOrderListImplemented.get(position).getDisplay());
                h.tv_index.setText((position + 1) + ".");

            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_display;
            private TextView tv_index;
        }
    }
}
