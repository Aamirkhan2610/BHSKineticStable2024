package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Model.ModelLeaveHistory;
import Model.ModelLeaveType;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 04/06/2019.
 */
public class SAMPLE_DROPDOWN_LIST extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_startdate;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ArrayList<ModelLeaveType> arrayLeaveType;

    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String requestType = "";
    public static String PhotoJSON = "";
    public static String AttachedType = "";
    public static ArrayAdapter<String> adapterleave;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_dropdown_list);
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        mContext = SAMPLE_DROPDOWN_LIST.this;
        gps = new TrackGPS(mContext);
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

        activity = SAMPLE_DROPDOWN_LIST.this;
        arrayLeaveHistory=new ArrayList<>();
        btn_startdate = (Button) findViewById(R.id.btn_startdate);
        lv_request_status = (ListView) findViewById(R.id.lv_request_status);


        arrayLeaveType = new ArrayList<>();
        leaveTypeID = "";
        AttachedType = "";
        PhotoJSON = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void DialogueWithListDayType(final ArrayList<String> _DayType) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);

        final ArrayList<String> dayType = new ArrayList<>();
        dayType.addAll(_DayType);


        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.GONE);
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);

        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[dayType.size()];
        for (int i = 0; i < dayType.size(); i++) {
            values[i] = dayType.get(i);
        }
        adapterleave = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterleave);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public void DialogueWithList(final ArrayList<ModelLeaveType> leaveTypes) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);

        if (requestType.equalsIgnoreCase("Maintenances")) {
            dialog.setTitle("SERVICE TYPE");
        } else {
            dialog.setTitle(mContext.getResources().getString(R.string.btn_request_name));
        }

        final ArrayList<ModelLeaveType> leaveTypesFiltered = new ArrayList<>();
        final ArrayList<ModelLeaveType> leaveTypesImplemented = new ArrayList<>();
        leaveTypesImplemented.addAll(leaveTypes);

        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);

        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[leaveTypesImplemented.size()];
        for (int i = 0; i < leaveTypesImplemented.size(); i++) {
            if (requestType.equalsIgnoreCase("Leave")) {
                values[i] = leaveTypesImplemented.get(i).getListValue() + " (" + leaveTypesImplemented.get(i).getL_Bal() + ") ";
            } else {
                values[i] = leaveTypesImplemented.get(i).getListValue();
            }
        }
        adapterleave = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterleave);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                leaveTypesFiltered.clear();
                if (charText.length() == 0) {
                    leaveTypesFiltered.addAll(leaveTypes);
                } else {
                    for (ModelLeaveType wp : leaveTypes) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            leaveTypesFiltered.add(wp);
                        }

                    }

                }

                leaveTypesImplemented.clear();
                leaveTypesImplemented.addAll(leaveTypesFiltered);


                final String[] values = new String[leaveTypesImplemented.size()];
                for (int i = 0; i < leaveTypesImplemented.size(); i++) {

                    if (requestType.equalsIgnoreCase("Leave")) {
                        values[i] = leaveTypesImplemented.get(i).getListValue() + " (" + leaveTypesImplemented.get(i).getL_Bal() + ") ";
                    } else {
                        values[i] = leaveTypesImplemented.get(i).getListValue();
                    }
                }
                adapterleave = new ArrayAdapter<String>(mContext, R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterleave);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                leaveTypeID = leaveTypesImplemented.get(position).getListID();
            }
        });
        dialog.show();
    }

    public void DialogueWithListAttachedType(final ArrayList<String> leaveTypes) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle("SELECT VEHICLE NUMBER");
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
            values[i] = (i + 1) + "." + leaveTypes.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapter);


        dialog.show();
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutLeaverequest");
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

        if (redirectionKey.equalsIgnoreCase("LeaveCancel")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, SAMPLE_DROPDOWN_LIST.class);
                    //logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    SAMPLE_DROPDOWN_LIST.activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (redirectionKey.equalsIgnoreCase("logoutLeaverequest")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    SAMPLE_DROPDOWN_LIST.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("LeaveRequestAddress")) {
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
                getLeaveDetail(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("LeaveSubmitAddress")) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("LeaveData")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveType.clear();

                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("ListValue"));
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("ListValue"));
                        modelLeaveType.setL_Bal(jsonObject.optString("L_Bal"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayLeaveType.add(modelLeaveType);

                    }
                    adapter = new ListElementAdapter(mContext);
                    lv_request_status.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("ApplyLeave")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();


                    String Ack_Msg = jobj.optString("POD");
                    // String Ack_Msg="N|N|N|0000";
                    String[] ackArray = Ack_Msg.split("\\|");

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
                        intent.putExtra("Str_Sts", jobj.optString("Str_Sts"));
                        intent.putExtra("Str_JobNo", jobj.optString("Str_JobNo"));
                        intent.putExtra("Str_Event", "Snap");
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        mContext.startActivity(intent);
                    }

                    //   RequestLeave.activity.finish();
                } else {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                h.tv_leavedetail = (TextView) v.findViewById(R.id.tv_leavedetail);
                h.img_status = (ImageView) v.findViewById(R.id.img_status);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);
                h.tv_cancel = (TextView) v.findViewById(R.id.tv_cancel);
                h.tv_index.setText((position + 1) + ".");
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());

                boolean isCancel=false;
                //2018-12-06 08:00:00.0
                String appliedDateString=arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if(!appliedDate.before(currentDate)){
                        isCancel=true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                    if(isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    }else{
                        h.tv_cancel.setVisibility(View.GONE);
                    }

                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                    h.tv_cancel.setVisibility(View.GONE);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                    h.tv_cancel.setVisibility(View.GONE);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                    h.tv_cancel.setVisibility(View.VISIBLE);
                }




                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(),"Maintenances","Cancelled");
                        }else{
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(),"Leave","Cancelled");

                        }
                    }
                });

                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());
                h.tv_index.setText((position + 1) + ".");
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());


                boolean isCancel=false;
                //2018-12-06 08:00:00.0
                String appliedDateString=arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if(!appliedDate.before(currentDate)){
                        isCancel=true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                    if(isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    }else{
                        h.tv_cancel.setVisibility(View.GONE);
                    }
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                    h.tv_cancel.setVisibility(View.GONE);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                    h.tv_cancel.setVisibility(View.GONE);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                    h.tv_cancel.setVisibility(View.VISIBLE);
                }

                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(),"Maintenances","Cancelled");
                        }else{
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(),"Leave","Cancelled");
                        }
                    }
                });

                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_leavetitle;
            private TextView tv_index;
            private TextView tv_leavedetail;
            private TextView tv_cancel;
            private ImageView img_status;
        }
    }

    public static void ConfirmCancelSubmition(final String vseqNumber, final String miscType, final String miscStatus) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        submitCancelRequest("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON",vseqNumber,miscType,miscStatus);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder.setMessage("Are you sure to cancel this reuqest?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void submitCancelRequest(String address, String lat, String lng, String gpsStatus,String seqNumber,String miscType,String miscStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_SeqNo="+seqNumber+"&Str_Misc_Type="+miscType+"&Str_Misc_Status="+miscStatus+"&Str_JobFor=BHS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "LeaveCancel");
    }

    private void getLeaveDetail(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + requestType;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "LeaveData");
    }
}
