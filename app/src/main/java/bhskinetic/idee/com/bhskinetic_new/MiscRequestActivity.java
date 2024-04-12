package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
public class MiscRequestActivity extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_misc_type,btn_misc_items,btn_standby,btn_reported_date,btn_submit;
    public static int index=0;
    public static EditText edt_opt1,edt_opt2,edt_remark;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ArrayList<ModelLeaveType> arrayLeaveType;
    public static ModelLeaveHistory modelleaveHistory;
    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String standByValue1 = "",standByValue2 = "";
    public static String requestType = "MISCREQ";
    public static String PhotoJSON = "";
    public static String AttachedType = "";
    public static String SeqNo="";
    public static ArrayAdapter<String> adapterleave;
    private static int _date = 0;
    private static int _year = 0;
    private static int _month = 0;
    private static int _hh = 0;
    private static int _mm = 0;
    private static int _ss = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc_req);
        Init();
    }

    private void Init() {
        standByValue1 = "";
        standByValue2 = "";
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        edt_opt1 =  findViewById(R.id.edt_opt1);
        edt_opt2 = findViewById(R.id.edt_opt2);
        edt_remark =findViewById(R.id.edt_remark);
        index=0;
        SeqNo="";
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        mContext = MiscRequestActivity.this;
        gps = new TrackGPS(mContext);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));

        }
        img_logout.setVisibility(View.GONE);


        activity = MiscRequestActivity.this;
        arrayLeaveHistory=new ArrayList<>();
        arrayLeaveType = new ArrayList<>();
        btn_misc_type = (Button) findViewById(R.id.btn_misc_type);
        btn_misc_items = (Button) findViewById(R.id.btn_misc_items);
        btn_submit=findViewById(R.id.btn_submit);
        btn_standby=findViewById(R.id.btn_standby);
        btn_reported_date=findViewById(R.id.btn_reported_date);
        lv_request_status = (ListView) findViewById(R.id.lv_request_status);

        btn_misc_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_standby.setVisibility(View.GONE);
                btn_standby.setText("STAND BY");
                index=0;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"","NA","NA","Misc Type");
            }
        });

        btn_misc_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_misc_type.getText().toString().trim().equalsIgnoreCase("SELECT MISC TYPE")){
                    Utils.Alert("SELECT MISC TYPE",mContext);
                    return;
                }
                btn_standby.setVisibility(View.GONE);
                btn_standby.setText("STAND BY");
                index=1;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"",standByValue1,btn_misc_type.getText().toString().trim(),"Misc Type");
            }
        });

        btn_standby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_misc_items.getText().toString().trim().equalsIgnoreCase("SELECT MISC ITEM")){
                    Utils.Alert("SELECT MISC ITEM",mContext);
                    return;
                }
                index=2;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"",standByValue2,btn_misc_items.getText().toString().trim(),"Misc Type");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_misc_type.getText().toString().trim().equalsIgnoreCase("SELECT MISC TYPE")){
                    Utils.Alert("SELECT MISC TYPE",mContext);
                    return;
                }

                if(btn_misc_items.getText().toString().trim().contains("SELECT")){
                    Utils.Alert(btn_misc_items.getText().toString().trim(),mContext);
                    return;
                }

                if(btn_standby.getVisibility()==View.VISIBLE && btn_standby.getText().toString().trim().equalsIgnoreCase("STAND BY")){
                    Utils.Alert("SELECT STAND BY",mContext);
                    return;
                }

                if(btn_reported_date.getText().toString().trim().contains("REPORTED DATE")){
                    Utils.Alert("SELECT REPORTED DATE",mContext);
                    return;
                }

                if(edt_opt1.getVisibility()==View.VISIBLE && edt_opt1.getText().toString().trim().length()==0){
                    Utils.Alert(edt_opt1.getHint().toString(),mContext);
                    return;
                }

                if(edt_opt2.getVisibility()==View.VISIBLE && edt_opt2.getText().toString().trim().length()==0){
                    Utils.Alert(edt_opt2.getHint().toString(),mContext);
                    return;
                }

                if(edt_remark.getText().toString().trim().length()==0){
                    Utils.Alert("Enter Remark",mContext);
                    return;
                }


                submitMisc("",""+gps.getLatitude(),""+gps.getLongitude(),"");



            }
        });


        btn_reported_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetDateTime();
                InitDatePicker(0);
            }
        });

        getMISCList("",""+gps.getLatitude(),""+gps.getLongitude(),"");
    }

    private void getMISCList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + requestType;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Misc Data", URL, "MiscHistory");
    }

    public static void ResetDateTime() {
        _date = 0;
        _year = 0;
        _month = 0;
        _hh = 0;
        _mm = 0;
        _ss = 0;
    }

    private void InitDatePicker(final int flag) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.dateTimeStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                        Log.i("DATE-->", date);
                        _year = year;
                        _month = monthOfYear;
                        _date = dayOfMonth;

                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            InitTimePicker(flag);
                        } else {
                            String selectedDateTime = _year + "-" + _month + "-" + _date;
                            InitTimePicker(flag);
                        }

                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        Calendar fivedayBeforeCalendar = Calendar.getInstance();
       // fivedayBeforeCalendar.add(Calendar.DAY_OF_YEAR, -3);

        datePickerDialog.getDatePicker().setMinDate(fivedayBeforeCalendar.getTimeInMillis());
        datePickerDialog.show();
    }


    private void InitTimePicker(final int flag) {

        Calendar now = Calendar.getInstance();


        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString ="0";
                        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
                        Log.i("TIME-->", time);

                        _hh = Integer.parseInt(hourString);
                        _mm = Integer.parseInt(minuteString);
                        _ss = Integer.parseInt(secondString);

                        String selectedDateTime = _year + "-" + _month + "-" + _date + " " + _hh + ":" + _mm + ":" + _ss;
                        btn_reported_date.setText(selectedDateTime);
                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);


        timePickerDialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
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

         /*modelLeaveType.setListID(jsonObject.optString("TextBox1")+"-"+jsonObject.optString("TextBox2"));
        modelLeaveType.setReq_Status(jsonObject.optString("Stand_By"));*/

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();

                if(index==0) {
                    String textbox[] = leaveTypesImplemented.get(position).getListID().split("-");
                    leaveTypeID = textbox[0];
                    SeqNo=leaveTypeID;
                    standByValue1=leaveTypesImplemented.get(position).getReq_Status();
                    if (textbox[1].equalsIgnoreCase("YES")) {
                        edt_opt1.setVisibility(View.VISIBLE);
                    } else {
                        edt_opt1.setVisibility(View.GONE);
                    }

                    if (textbox[2].equalsIgnoreCase("YES")) {
                        edt_opt2.setVisibility(View.VISIBLE);
                    } else {
                        edt_opt2.setVisibility(View.GONE);
                    }

                    String selection=leaveTypesImplemented.get(position).getListValueWithoutIndex();
                    if(selection.equalsIgnoreCase("CLAIMS")){
                        edt_opt1.setHint("ENTER AMOUNT");
                    }else  if(selection.equalsIgnoreCase("PURCHASE")){
                        edt_opt1.setHint("ENTER AMOUNT");
                    }else{
                        edt_opt1.setHint("ENTER ODOMETER VALUE");
                        edt_opt2.setHint("ENTER TOPUP VALUE");
                    }

                }else if(index==1){
                    standByValue2=leaveTypesImplemented.get(position).getReq_Status();
                    if(leaveTypesImplemented.get(position).getReq_Status().equalsIgnoreCase("YES")){
                        btn_standby.setVisibility(View.VISIBLE);
                    }else{
                        btn_standby.setVisibility(View.GONE);
                    }
                }

                if(index==0){
                    btn_misc_type.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                    btn_misc_items.setText("SELECT "+leaveTypesImplemented.get(position).getListValueWithoutIndex());

                }else if(index==1){
                    btn_misc_items.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }else{
                    btn_standby.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }
            }
        });
        dialog.show();
    }

    public void showResponse(String response, String redirectionKey) {

         if (redirectionKey.equalsIgnoreCase("MiscHistory")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveHistory.clear();

                    JSONArray list2 = jobj.optJSONArray("list");
                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject jsonObject = list2.optJSONObject(i);
                        modelleaveHistory = new ModelLeaveHistory();
                        modelleaveHistory.setReq_Name(jsonObject.optString("Req_Name"));
                        modelleaveHistory.setReq_Status(jsonObject.optString("Req_Status"));
                        modelleaveHistory.setReq_Display(jsonObject.optString("Req_Display"));
                        modelleaveHistory.setListID(jsonObject.optString("Req_SeqNo"));
                        modelleaveHistory.setReq_StartDate(jsonObject.optString("Req_StartDate"));
                        arrayLeaveHistory.add(modelleaveHistory);
                    }
                    adapter = new ListElementAdapter(mContext);
                    lv_request_status.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (redirectionKey.equalsIgnoreCase("MiscType")) {
            try {

                JSONObject jobj = new JSONObject(response);
                arrayLeaveType.clear();

                JSONArray list = jobj.optJSONArray("Data");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject jsonObject = list.optJSONObject(i);
                    modelLeaveType = new ModelLeaveType();
                    modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("Res_Name"));
                    modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Res_Name"));
                    modelLeaveType.setListID(jsonObject.optString("SeqNo")+"-"+jsonObject.optString("TextBox1")+"-"+jsonObject.optString("TextBox2"));
                    modelLeaveType.setReq_Status(jsonObject.optString("Stand_By"));
                    arrayLeaveType.add(modelLeaveType);
                }

                if(arrayLeaveType.size()>0){
                    DialogueWithList(arrayLeaveType);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

         }else if (redirectionKey.equalsIgnoreCase("SubmitMISCData")) {
             try {


                 JSONObject jobj = new JSONObject(response);
                 Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                 String Ack_Msg="N|N|N|0000";

                 if(jobj.has("POD")){
                     Ack_Msg = jobj.optString("POD");
                 }
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
                     if (requestType.equalsIgnoreCase("Maintenances")) {
                         intent.putExtra("isPONO", "1");
                     }
                     intent.putExtra("Str_JobNo", jobj.optString("Str_JobNo"));
                     intent.putExtra("Str_Event", btn_misc_type.getText().toString().trim());
                     intent.putExtra("PhotoJSON", PhotoJSON);
                     mContext.startActivity(intent);
                 }else{
                     activity.finish();
                 }




             } catch (JSONException e) {
                 e.printStackTrace();
             }

         }else if (redirectionKey.equalsIgnoreCase("LeaveCancelMisc")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, MiscRequestActivity.class);
                    logoutIntent.putExtra("title", tv_header.getText().toString().trim());
                    mContext.startActivity(logoutIntent);
                    MiscRequestActivity.activity.finish();
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
                    if (isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    } else {
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

                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                }

                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "MISCREQ", "Cancelled", "Are you sure to cancel this request?");
                    }
                });

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
                    if (isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    } else {
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


                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                }

                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "MISCREQ", "Cancelled", "Are you sure to cancel this request?");
                    }
                });
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

    public static void ConfirmCancelSubmition(final String vseqNumber, final String miscType, final String miscStatus, String errorMsg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        submitCancelRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON", vseqNumber, miscType, miscStatus);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder.setMessage(errorMsg).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void submitCancelRequest(String address, String lat, String lng, String gpsStatus, String seqNumber, String miscType, String miscStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_SeqNo=" + seqNumber + "&Str_Misc_Type=" + miscType + "&Str_Misc_Status=" + miscStatus + "&Str_JobFor=BHS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "LeaveCancelMisc");
    }


    private static void getMaintTypes(String address, String lat, String lng, String gpsStatus, String Str_SEL1, String Str_SEL2,String Str_ListType) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Maint_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_DriverID="+driverId+"&Str_ListType="+Str_ListType+"&Str_SEL1="+Str_SEL1+"&Str_SEL2="+Str_SEL2+"&Str_Lat="+lat+"&Str_Long="+lng;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Misc Type", URL, "MiscType");
    }

    private void submitMisc(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String Stand_By="NA",TextBox_Value1="NA",TextBox_Value2="NA";

        if(btn_standby.getVisibility()==View.VISIBLE){
            Stand_By=btn_standby.getText().toString().trim();
        }

        if(edt_opt1.getVisibility()==View.VISIBLE){
            TextBox_Value1=edt_opt1.getText().toString().trim();
        }

        if(edt_opt2.getVisibility()==View.VISIBLE){
            TextBox_Value2=edt_opt2.getText().toString().trim();
        }

        String URL = "Misc_Req_Cre.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_Lat="+lat+"&Str_Long="+lng+"&Str_DriverID="+driverId+"&ReqType=Misc Req&Service_Date="+btn_reported_date.getText().toString().trim()+"&Misc_Type="+btn_misc_type.getText().toString().trim()+"&Misc_Items="+btn_misc_items.getText().toString().trim()+"&Stand_By="+Stand_By+"&TextBox_Value1="+TextBox_Value1+"&TextBox_Value2="+TextBox_Value2+"&Req_Rmks="+edt_remark.getText().toString().trim()+"&Str_SeqNo="+SeqNo;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "SubmitMISCData");
    }
}
