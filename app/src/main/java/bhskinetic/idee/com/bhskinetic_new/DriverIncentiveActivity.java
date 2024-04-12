package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelLeaveHistory;
import Model.ModelLeaveType;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by pathanaa on 25-04-2017.
 */
public class DriverIncentiveActivity extends Activity {
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static TextView tv_header;
    public static Context mContext;
    public static Activity activity;
    public static TrackGPS gps;
    public static String Str_TripNo="";
    public static String Str_JobNo="";
    public static Button btn_cost_for;
    public static Button btn_submit;
    public static EditText edt_cost;
    public static ArrayList<ModelLeaveType> arrayLeaveType;
    public static ArrayList<ModelLeaveType> arrayLeaveTypeSelection;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ModelLeaveType modelLeaveType;
    public static ModelLeaveHistory modelleaveHistory;
    public static ListElementAdapter adapter;
    public static ListView lv_driver_incentive;
    public static String PhotoJSON="";
    public static String eventSelection="";
    public static String listvalue="";
    public static String sublistvalue="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_driver_incentive);
        activity = DriverIncentiveActivity.this;
        mContext = DriverIncentiveActivity.this;
        Init();
    }

    private void Init() {
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        lv_driver_incentive=(ListView)findViewById(R.id.lv_driver_incentive);
        btn_cost_for=(Button)findViewById(R.id.btn_cost_for);
        btn_cost_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listvalue="";
                DialogueWithList(arrayLeaveType);
            }
        });

        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("SUB LIST",sublistvalue);

                 if(btn_cost_for.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_cost_for))){
                     Utils.Alert(mContext.getResources().getString(R.string.alert_select_bill_for),mContext);
                     return;
                 }

                if(sublistvalue.trim().length()==0){
                    Utils.Alert(mContext.getResources().getString(R.string.alert_select_bill_for_sub),mContext);
                    return;
                }

                if(edt_cost.getText().toString().trim().length()==0){
                    Utils.Alert(mContext.getResources().getString(R.string.alert_enter_cost),mContext);
                    return;
                }

                 if(edt_cost.getText().toString().trim().length()==0){
                     Utils.Alert(mContext.getResources().getString(R.string.alert_enter_cost),mContext);
                     return;
                 }


                APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "AddressBillSubmit");

            }
        });

        img_logout = (ImageView) findViewById(R.id.img_logout);
        tv_header = (TextView) findViewById(R.id.tv_header);
        gps=new TrackGPS(mContext);
        tv_header.setText(mContext.getResources().getString(R.string.str_driver_incentive));
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        Bundle b=getIntent().getExtras();
        if(b!=null){
            Str_TripNo=b.getString("Str_TripNo");
            Str_JobNo=b.getString("Str_JobNo");;
        }

        edt_cost=(EditText)findViewById(R.id.edt_cost);

        arrayLeaveType = new ArrayList<>();
        arrayLeaveTypeSelection= new ArrayList<>();
        arrayLeaveHistory = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "DriverIncentiveAddress");
    }

    private void UpdateBill(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
       // Str_TripNo="NA";
        String URL="Add_TC.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_TripNo="+Str_TripNo+"&Str_JobNo="+Str_JobNo+"&Str_Event=TripCost&Str_BillFor="+listvalue+"&Str_BillFor1="+sublistvalue+"&Str_TC="+DriverIncentiveActivity.edt_cost.getText().toString().trim();
        if(URL.contains(" ")){
            URL=URL.replaceAll(" ","%20");
        }
        APIUtils.sendRequest(mContext, "Update Bill",URL, "UpdateIncentiveBill");
    }

    public void DialogueWithList(final ArrayList<ModelLeaveType> leaveTypes) {
       listvalue="";
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle(mContext.getResources().getString(R.string.btn_cost_for));
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[leaveTypes.size()];
        for (int i = 0; i < leaveTypes.size(); i++) {
            values[i] = (i+1)+"."+leaveTypes.get(i).getListValue();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapter);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                btn_cost_for.setText(arrayLeaveType.get(position).getListValue());
                listvalue=arrayLeaveType.get(position).getListValue();
                eventSelection=arrayLeaveType.get(position).getListValue();
                APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "DriverIncentiveAddressSelection");
                // leaveTypeID = arrayLeaveType.get(position).getListID();
            }
        });
        dialog.show();
    }


    public void DialogueWithListSelection(final ArrayList<ModelLeaveType> leaveTypes) {
        sublistvalue="";
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle(mContext.getResources().getString(R.string.btn_cost_for));
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[leaveTypes.size()];
        for (int i = 0; i < leaveTypes.size(); i++) {
            values[i] = (i+1)+"."+leaveTypes.get(i).getListValue();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapter);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                btn_cost_for.setText(arrayLeaveTypeSelection.get(position).getListValue());
                sublistvalue=arrayLeaveTypeSelection.get(position).getListValue();
                //eventSelection=arrayLeaveType.get(position).getListValue();
                //APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "DriverIncentiveAddressSelection");
                //leaveTypeID = arrayLeaveType.get(position).getListID();
            }
        });
        dialog.show();
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
            return arrayLeaveHistory.size();
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
                v = layoutInflater.inflate(R.layout.raw_leave_log, null);
                h = new ViewHolder();
                h.tv_leavetitle = (TextView) v.findViewById(R.id.tv_leavetitle);
                h.img_status = (ImageView) v.findViewById(R.id.img_status);
                h.tv_index= (TextView) v.findViewById(R.id.tv_index);
                h.tv_index.setText((position+1)+".");
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());

                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_approved);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_rejected);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_transist_pending);
                }


                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());
                h.tv_index.setText((position+1)+".");
                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_approved);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_rejected);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_transist_pending);
                }
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_leavetitle;
            private TextView tv_index;
            private ImageView img_status;
        }
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutDriverIncentive");
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

    private void getIncentiveDetail(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);


        String URL = "Misc_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_Event=Incentive&Str_TripNo="+Str_TripNo+"&Str_JobNo="+Str_JobNo+"&Str_Event1=";
        //String URL="Misc_List.jsp?Str_iMeiNo=11&Str_Model=Android&Str_ID=10&Str_Lat=1.35&Str_Long=103.85&Str_Loc=India&Str_GPS=ON&Str_DriverID=1245&Str_Event=TripCost&Str_TripNo=NA&Str_JobNo=0";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "IncentiveData");
    }

    private void getIncentiveDetailSelection(String address, String lat, String lng, String gpsStatus,String selection) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);


        String URL = "Misc_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_Event=Incentive&Str_TripNo="+Str_TripNo+"&Str_JobNo="+Str_JobNo+"&Str_Event1="+selection;
        //String URL="Misc_List.jsp?Str_iMeiNo=11&Str_Model=Android&Str_ID=10&Str_Lat=1.35&Str_Long=103.85&Str_Loc=India&Str_GPS=ON&Str_DriverID=1245&Str_Event=TripCost&Str_TripNo=NA&Str_JobNo=0";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "IncentiveDataSelection");
    }

    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutDriverIncentive")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    DriverIncentiveActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("UpdateIncentiveBill")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {

                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();

                    String Ack_Msg = jobj.optString("POD");
                    // String Ack_Msg="N|N|N|0000";
                    String[] ackArray = Ack_Msg.split("\\|");
                    DriverIncentiveActivity.edt_cost.setText("");
                    boolean isPhoto = false;
                    boolean isSign = false;
                    //Checking if customer has permission to upload photo
                    if (ackArray[0].equalsIgnoreCase("P")) {
                        isPhoto = true;
                    } else {
                        isPhoto = false;
                    }

                    //Checking if customer has permission to upload signature
                    if (ackArray[1].equalsIgnoreCase("S")) {
                        isSign = true;
                    } else {
                        isSign = false;
                    }

                    Intent intent;

                    if (isPhoto) {
                        intent = new Intent(mContext, PhotoUploadActivity.class);
                        intent.putExtra("isSign", isSign);
                        intent.putExtra("Str_JobNo", Str_JobNo);
                       // intent.putExtra("PhotoJSON",PhotoJSON);
                        intent.putExtra("title", "Incentive Photo");
                        intent.putExtra("Str_Event", "TripCost");
                        intent.putExtra("Str_Sts", "BillUpdate");
                        intent.putExtra("Str_TripNo", Str_TripNo);
                        mContext.startActivity(intent);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("DriverIncentiveAddress")) {
            try {
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
                getIncentiveDetail(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("DriverIncentiveAddressSelection")) {
            try {
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
                getIncentiveDetailSelection(address, lat, lng, GPSStatus,eventSelection);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }  else if (redirectionKey.equalsIgnoreCase("AddressBillSubmit")) {
            try {
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
                UpdateBill(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("IncentiveDataSelection")) {
            try {
                arrayLeaveTypeSelection.clear();
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    JSONArray list = jobj.optJSONArray("list1");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue(jsonObject.optString("ListValue"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayLeaveTypeSelection.add(modelLeaveType);
                    }

                    DialogueWithListSelection(arrayLeaveTypeSelection);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if (redirectionKey.equalsIgnoreCase("IncentiveData")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveType.clear();
                    arrayLeaveHistory.clear();
                    PhotoJSON="";
                    PhotoJSON = jobj.optJSONArray("list").toString();
                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue(jsonObject.optString("ListValue"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayLeaveType.add(modelLeaveType);
                    }

                    JSONArray list2 = jobj.optJSONArray("list2");
                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject jsonObject = list2.optJSONObject(i);
                        modelleaveHistory = new ModelLeaveHistory();
                        modelleaveHistory.setReq_Name(jsonObject.optString("Req_Name"));
                        modelleaveHistory.setReq_Status(jsonObject.optString("Req_Status"));
                        Log.i("Req_Status============>", jsonObject.optString("Req_Name") + "\n" + jsonObject.optString("Req_Status"));
                        arrayLeaveHistory.add(modelleaveHistory);
                    }

                    adapter = new ListElementAdapter(mContext);
                    lv_driver_incentive.setAdapter(adapter);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("ApplyLeave")) {

        }
    }
}
