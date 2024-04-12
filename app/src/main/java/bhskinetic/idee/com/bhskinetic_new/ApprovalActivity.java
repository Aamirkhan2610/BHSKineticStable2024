package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Model.ModelLeaveType;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 04/06/2019.
 */
public class ApprovalActivity extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_startdate;
    public static ArrayList<ModelLeaveType> arrayLeaveHistory;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ArrayList<ModelLeaveType> arrayLeaveType;

    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String requestType = "";
    public static String PhotoJSON = "";
    public static String AttachedType = "";
    public static ArrayAdapter<String> adapterleave;
    public static TextView tv_select_job_date;

    public static String Str_Date = "";
    private static String _date = "0";
    private static int _year = 0;
    private static String _month = "0";
    public static boolean isButtonTextToAdd=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_approval);
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

        tv_select_job_date = (TextView) findViewById(R.id.tv_select_job_date);
        tv_select_job_date.setVisibility(View.VISIBLE);

        tv_select_job_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitDatePicker();
            }
        });

        mContext = ApprovalActivity.this;
        gps = new TrackGPS(mContext);
        Bundle b = getIntent().getExtras();
        btn_startdate = (Button) findViewById(R.id.btn_startdate);
        if (b != null) {
            tv_header.setText(b.getString("title"));
            if(b.getString("btnText")!=null){
                btn_startdate.setText(b.getString("btnText"));
                isButtonTextToAdd=false;
            }else{
                isButtonTextToAdd=true;
            }
        }

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        activity = ApprovalActivity.this;
        arrayLeaveHistory = new ArrayList<>();

        lv_request_status = (ListView) findViewById(R.id.lv_request_status);

        btn_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayLeaveType);
            }
        });

        arrayLeaveType = new ArrayList<>();
        leaveTypeID = "";
        AttachedType = "";
        PhotoJSON = "";

        getCurrentDateTime();
    }

    public static String getCurrentDateTime() {
        Str_Date = Utils.getPref(mContext.getResources().getString(R.string.pref_datetime), mContext);
        tv_select_job_date.setText(Str_Date);
        return Str_Date;
    }

    private void InitDatePicker() {
        Calendar now = Calendar.getInstance();
       /* DatePickerDialog dpd = DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        //dpd.setMinDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                Log.i("DATE-->", date);
                _year = year;
                _month = "" + monthOfYear;
                if (_month.trim().length() == 1) {
                    _month = "0" + _month;
                }
                _date = "" + dayOfMonth;

                if (_date.trim().length() == 1) {
                    _date = "0" + _date;
                }


                Str_Date = _date + "-" + _month + "-" + _year;
                tv_select_job_date.setText(Str_Date);
                getLeaveDetail("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");

            }
        });*/


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.dateTimeStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                        Log.i("DATE-->", date);
                        _year = year;
                        _month = "" + monthOfYear;
                        if (_month.trim().length() == 1) {
                            _month = "0" + _month;
                        }
                        _date = "" + dayOfMonth;

                        if (_date.trim().length() == 1) {
                            _date = "0" + _date;
                        }


                        Str_Date = _date + "-" + _month + "-" + _year;
                        tv_select_job_date.setText(Str_Date);
                        getLeaveDetail("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");


                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "ApprovalTypeAddress");

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
                btn_startdate.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                getLeaveDetail("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");

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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutApprovalrequest");
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

        if (redirectionKey.equalsIgnoreCase("ApprovalRequest")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                    Intent logoutIntent = new Intent(mContext, ApprovalActivity.class);
                    logoutIntent.putExtra("title",tv_header.getText().toString().trim());
                    logoutIntent.putExtra("btnText",btn_startdate.getText().toString().trim());
                    mContext.startActivity(logoutIntent);
                    ApprovalActivity.activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("logoutApprovalrequest")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    ApprovalActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ApprovalTypeAddress")) {
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
                getApprovalList(address, lat, lng, GPSStatus);
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


        } else if (redirectionKey.equalsIgnoreCase("ApprovalData")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveType.clear();

                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("ListValue")+" ("+jsonObject.optString("Req_Count")+")");
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("ListValue"));
                        modelLeaveType.setL_Bal(jsonObject.optString("L_Bal"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayLeaveType.add(modelLeaveType);

                    }

                    if (arrayLeaveType.size() > 0) {
                        if(isButtonTextToAdd) {
                            btn_startdate.setText(arrayLeaveType.get(0).getListValueWithoutIndex());
                        }
                    }


                    getLeaveDetail("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("getApprovalDetail")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveHistory.clear();

                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Driv_Name"));
                        modelLeaveType.setL_Bal(jsonObject.optString("Veh_No"));
                        modelLeaveType.setListID(jsonObject.optString("Remarks")+"_"+jsonObject.optString("SeqNo"));
                        arrayLeaveHistory.add(modelLeaveType);

                    }


                    adapter = new ListElementAdapter(mContext);
                    lv_request_status.setAdapter(adapter);
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
                v = layoutInflater.inflate(R.layout.raw_approval_log, null);
                h = new ViewHolder();
                h.tv_drivername = (TextView) v.findViewById(R.id.tv_driver_name);
                h.tv_request_name=(TextView)v.findViewById(R.id.tv_request_name);
                h.tv_datetime=(TextView)v.findViewById(R.id.tv_datetime);
                h.btn_approve=(Button) v.findViewById(R.id.btn_approve);
                h.btn_reject=(Button) v.findViewById(R.id.btn_reject);

                h.tv_drivername.setText(arrayLeaveHistory.get(position).getListValueWithoutIndex());
                h.tv_request_name.setText(arrayLeaveHistory.get(position).getL_Bal());
                final String[] array=arrayLeaveHistory.get(position).getListID().split("_");
                h.tv_datetime.setText(array[0]);

                h.btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON", array[1], btn_startdate.getText().toString().trim(), "Approved");

                    }
                });

                h.btn_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmRequestSubmittion(array[1],btn_startdate.getText().toString().trim(),"Rejected");
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_drivername.setText(arrayLeaveHistory.get(position).getListValueWithoutIndex());
                h.tv_request_name.setText(arrayLeaveHistory.get(position).getL_Bal());
                final String[] array=arrayLeaveHistory.get(position).getListID().split("_");
                h.tv_datetime.setText(array[0]);

                h.btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON", array[1], btn_startdate.getText().toString().trim(), "Approved");
                    }
                });

                h.btn_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmRequestSubmittion(array[1],btn_startdate.getText().toString().trim(),"Rejected");
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_drivername;
            private TextView tv_request_name;
            private TextView tv_datetime;
            private Button btn_approve;
            private Button btn_reject;

        }
    }

    public static void ConfirmRequestSubmittion(final String vseqNumber, final String miscType, final String miscStatus) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        submitRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON", vseqNumber, miscType, miscStatus);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder.setMessage("Are you sure to reject this reuqest?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void submitRequest(String address, String lat, String lng, String gpsStatus, String seqNumber, String miscType, String miscStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc=NA&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_SeqNo="+seqNumber+"&Str_Misc_Type="+miscType+"&Str_Misc_Status="+miscStatus+"&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_Client_Name),mContext);
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "ApprovalRequest");
    }

    private void getApprovalList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=APPROVALS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "ApprovalData");
    }

    private void getLeaveDetail(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Rpt_Data_List.jsp?Str_ID=" + ClientID + "&Str_Model=Android&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_DriverID=" + driverId + "&Str_Event=" + btn_startdate.getText().toString().trim() + "&Str_Filter=NA&Str_Date=" + Str_Date;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "getApprovalDetail");
    }
}
