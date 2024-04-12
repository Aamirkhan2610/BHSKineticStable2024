package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.gms.maps.model.TileOverlayOptions;

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
 * Created by Aamir on 4/18/2017.
 */
public class RequestLeave extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_request_name;
    public static Button btn_startdate,btn_startdate_maint;
    public static Button btn_submit;
    public static Button btn_enddate;
    public static Button btn_standby;
    public static Button btn_action_type;
    public static Button btn_day_type;
    public static EditText edt_remark;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ModelLeaveHistory modelleaveHistory;
    public static ArrayList<ModelLeaveType> arrayLeaveType;
    public static ArrayList<String> arrayDayType;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String requestType = "";
    private static int _date = 0;
    private static int _year = 0;
    private static int _month = 0;
    private static int _hh = 0;
    private static int _mm = 0;
    private static int _ss = 0;
    public static String PhotoJSON = "";
    public static String AttachedVehicle = "";
    public static String AttachedContainer = "";
    public static ArrayList<String> arrayListAttachedType;
    public static String AttachedType = "";
    public static ArrayAdapter<String> adapterleave;
    public static String Res_Name_1="",Stand_By_1="",Res_Name_2="",Stand_By_2="",Str_SeqNo="";
    public static int ReqIndex=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_request_leave);
        Init();
    }

    private void Init() {
        Res_Name_1=Stand_By_1=Res_Name_2=Str_SeqNo=Stand_By_2="";
        ReqIndex=0;
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
        arrayListAttachedType = new ArrayList<>();

        mContext = RequestLeave.this;
        gps = new TrackGPS(mContext);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
            requestType = b.getString("requestType");
            AttachedVehicle = b.getString("AttachedVehicle");
            AttachedContainer = b.getString("AttachedContainer");
        }

        if (AttachedVehicle != null) {
            if (AttachedVehicle.trim().length() > 0) {
                arrayListAttachedType.add(AttachedVehicle);
            }

            if (AttachedContainer.trim().length() > 0) {
                arrayListAttachedType.add(AttachedContainer);
            }
        }

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        activity = RequestLeave.this;

        btn_request_name = (Button) findViewById(R.id.btn_request_name);


        btn_startdate = (Button) findViewById(R.id.btn_startdate);
        btn_startdate_maint= findViewById(R.id.btn_startdate_maint);
        btn_day_type = (Button) findViewById(R.id.btn_day_type);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_enddate = (Button) findViewById(R.id.btn_enddate);
        btn_standby = findViewById(R.id.btn_standby);
        btn_action_type = findViewById(R.id.btn_actiontype);


        if (requestType.equalsIgnoreCase("Maintenances")) {
            btn_startdate_maint.setText(mContext.getResources().getString(R.string.btn_service_date));
            btn_startdate.setVisibility(View.GONE);
            btn_startdate_maint.setVisibility(View.VISIBLE);
            btn_request_name.setText(mContext.getResources().getString(R.string.btn_request_name_service));
            btn_day_type.setVisibility(View.VISIBLE);
            btn_standby.setVisibility(View.GONE);
            btn_action_type.setVisibility(View.VISIBLE);
            btn_day_type.setText("Fault Type");
        } else if (requestType.equalsIgnoreCase("OT")) {
            btn_request_name.setVisibility(View.VISIBLE);
            btn_request_name.setText("OT TYPE");
            btn_day_type.setVisibility(View.GONE);
        } else {
            btn_day_type.setVisibility(View.VISIBLE);
        }



        edt_remark = (EditText) findViewById(R.id.edt_remark);
        lv_request_status = (ListView) findViewById(R.id.lv_request_status);

        lv_request_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    arrayLeaveHistory.get(position).setVisibleDetailItem(-1);
                } else {
                    for (int i = 0; i < arrayLeaveHistory.size(); i++) {
                        arrayLeaveHistory.get(i).setVisibleDetailItem(-1);
                    }
                    arrayLeaveHistory.get(position).setVisibleDetailItem(position);
                }
                adapter.notifyDataSetChanged();
            }
        });

        btn_request_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestType.equalsIgnoreCase("Maintenances")) {
                    ReqIndex=0;
                    getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"","NA","NA");
                }else{
                    DialogueWithList(arrayLeaveType);
                }
            }
        });

        btn_standby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReqIndex=2;

                if(btn_day_type.getText().toString().trim().equalsIgnoreCase("FAULT TYPE")){
                    Utils.Alert("Select Fault Type",mContext);
                    return;
                }

                getMaintTypes("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", Stand_By_2, Res_Name_2);
            }
        });

        btn_action_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReqIndex=3;
                getMaintTypes("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", "Action Type", "NA");
            }
        });
        btn_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetDateTime();
                InitDatePicker(0);
            }
        });

        btn_startdate_maint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetDateTime();
                InitDatePicker(0);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestType.equalsIgnoreCase("Leave")) {
                    if (btn_request_name.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_request_name))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_request_name), mContext);
                        return;
                    }

                } else if (requestType.equalsIgnoreCase("OT")) {
                    if (btn_request_name.getText().toString().trim().equalsIgnoreCase("OT TYPE")) {
                        Utils.Alert("SELECT OT TYPE", mContext);
                        return;
                    }

                } else {
                    if (btn_request_name.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_request_name_service))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_service_name), mContext);
                        return;
                    }

                }


                if (requestType.equalsIgnoreCase("Leave")) {
                    if (btn_day_type.getText().toString().trim().equalsIgnoreCase("DAY TYPE")) {
                        Utils.Alert("SELECT DAY TYPE", mContext);
                        return;
                    }

                }else  if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (btn_day_type.getText().toString().trim().equalsIgnoreCase("FAULT TYPE")) {
                        Utils.Alert("SELECT FAULT TYPE", mContext);
                        return;
                    }
                }

                if (requestType.equalsIgnoreCase("Leave") || requestType.equalsIgnoreCase("OT")) {
                    if (btn_startdate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_start_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_start_date), mContext);
                        return;
                    }
                } else {
                    if (btn_startdate_maint.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_service_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_service_date), mContext);
                        return;
                    }
                }

                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if(btn_standby.getVisibility()==View.VISIBLE && btn_standby.getText().toString().trim().equalsIgnoreCase("STAND BY")){
                        Utils.Alert("SELECT STAND BY VALUE", mContext);
                        return;
                    }
                }

                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (btn_enddate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_vehiclenumber))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_attch_type), mContext);
                        return;
                    }

                }

                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (btn_action_type.getText().toString().trim().equalsIgnoreCase("ACTION TYPE")) {
                        Utils.Alert("SELECT ACTION TYPE", mContext);
                        return;
                    }

                }

                if (requestType.equalsIgnoreCase("Leave") || requestType.equalsIgnoreCase("OT")) {
                    if (btn_enddate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_end_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_select_end_date), mContext);
                        return;
                    }
                }

                String StartDate = btn_startdate.getText().toString().trim();
                String EndDate = btn_enddate.getText().toString().trim();


                if (requestType.equalsIgnoreCase("Leave")) {
                    StartDate = btn_startdate.getText().toString().trim() + " 00:00:00";
                    EndDate = btn_enddate.getText().toString().trim() + " 00:00:00";
                }


                //2017-4-26 18:30:0
                String[] stdArray = StartDate.split(" ");
                String[] endArray = EndDate.split(" ");

                if (stdArray[0].equalsIgnoreCase(endArray[0])) {
                    String[] stt = stdArray[1].split(":");
                    String[] ent = endArray[1].split(":");

                    int starthour = Integer.parseInt(stt[0]);
                    int endhour = Integer.parseInt(ent[0]);

                    if (starthour > endhour) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_start_end_date), mContext);
                        return;
                    }
                }

                APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "LeaveSubmitAddress");

            }
        });

        btn_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (arrayListAttachedType.size() > 1) {

                        DialogueWithListAttachedType(arrayListAttachedType);
                    }
                } else {
                    ResetDateTime();
                    InitDatePicker(1);
                }
            }
        });

        arrayLeaveType = new ArrayList<>();
        arrayLeaveHistory = new ArrayList<>();
        arrayDayType = new ArrayList<>();

        arrayDayType.add("HALF DAY AM");
        arrayDayType.add("HALF DAY PM");
        arrayDayType.add("FULL DAY");

        leaveTypeID = "";
        AttachedType = "";

        if (requestType.equalsIgnoreCase("Maintenances")) {
            if (arrayListAttachedType.size() > 1) {
                btn_enddate.setText(mContext.getResources().getString(R.string.btn_vehiclenumber));
            } else {
                btn_enddate.setEnabled(false);
                if (arrayListAttachedType.size() > 1) {
                    btn_enddate.setText(arrayListAttachedType.get(0));
                    AttachedType = arrayListAttachedType.get(0);
                }
            }
        }

        btn_day_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestType.equalsIgnoreCase("Maintenances")) {
                    if(btn_request_name.getText().toString().trim().equalsIgnoreCase("SERVICE TYPE")){
                        Utils.Alert("Select Service Type",mContext);
                        return;
                    }

                    ReqIndex = 1;
                    getMaintTypes("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", Stand_By_1, Res_Name_1);
                }else{
                    DialogueWithListDayType(arrayDayType);
                }
            }
        });

        PhotoJSON = "";
        ResetDateTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "LeaveRequestAddress");

        if (btn_request_name != null) {

            if (requestType.equalsIgnoreCase("Leave")) {
                btn_request_name.setText(mContext.getResources().getString(R.string.btn_request_name));
                btn_enddate.setText(mContext.getResources().getString(R.string.btn_end_date));
                btn_startdate.setText(mContext.getResources().getString(R.string.btn_start_date));
            } else if (requestType.equalsIgnoreCase("OT")) {
                btn_request_name.setVisibility(View.VISIBLE);
                btn_request_name.setText("OT TYPE");
                btn_day_type.setVisibility(View.GONE);
            } else {
                btn_startdate.setText(mContext.getResources().getString(R.string.btn_service_date));
                btn_request_name.setText(mContext.getResources().getString(R.string.btn_request_name_service));
                if (arrayListAttachedType.size() > 1) {
                    btn_enddate.setText(mContext.getResources().getString(R.string.btn_vehiclenumber));
                } else {
                    btn_enddate.setEnabled(false);
                    if (arrayListAttachedType.size() > 0) {
                        btn_enddate.setText(arrayListAttachedType.get(0));
                        AttachedType = arrayListAttachedType.get(0);
                    }
                }
            }

            edt_remark.setText("");
        }
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
                            if (!requestType.equalsIgnoreCase("OT")) {
                                if (flag == 0) {
                                    //
                                    btn_startdate.setText(selectedDateTime);
                                    btn_startdate_maint.setText(selectedDateTime);
                                } else {
                                    btn_enddate.setText(selectedDateTime);
                                }
                            } else {
                                InitTimePicker(flag);
                            }
                        }

                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        Calendar fivedayBeforeCalendar = Calendar.getInstance();
        fivedayBeforeCalendar.add(Calendar.DAY_OF_YEAR, -3);

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
                        Log.i("SELECTED DATE TIME", selectedDateTime);
                        // tv_datetime.setText(UiUtils.changeDateFormateNOUTCObs(selectedDateTime));

                        if (flag == 0) {
                            //
                            btn_startdate.setText(selectedDateTime);
                            btn_startdate_maint.setText(selectedDateTime);
                        } else {
                            btn_enddate.setText(selectedDateTime);
                        }
                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);


        timePickerDialog.show();

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

                btn_day_type.setText(arrayDayType.get(position));

            }
        });
        dialog.show();
    }


    public void DialogueWithListActionType(final ArrayList<ModelLeaveType> leaveTypes) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist_addjob);
        dialog.setCancelable(false);
        final String[] selectedValue = {""};

        final ArrayList<ModelLeaveType> leaveTypesFiltered = new ArrayList<>();
        final ArrayList<ModelLeaveType> leaveTypesImplemented = new ArrayList<>();
        leaveTypesImplemented.addAll(leaveTypes);

        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        TextView tvSelection=dialog.findViewById(R.id.tv_selection);
        tvSelection.setVisibility(View.VISIBLE);
        Button btn_submit=dialog.findViewById(R.id.btn_submit);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.setHint("Enter Remark");
        etSearch.setHintTextColor(Color.BLACK);
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



        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(etSearch.getText().toString().trim().length()==0){
                    Utils.Alert("Enter Remark",mContext);
                    return;
                }


                selectedValue[0] =leaveTypesImplemented.get(position).getListValueWithoutIndex()+"~"+edt_remark.getText().toString().trim();
                tvSelection.setText(selectedValue[0]+" Selected");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.getText().toString().trim().length()==0){
                    Utils.Alert("Enter Remark",mContext);
                    return;
                }

                if(selectedValue[0].toString().trim().length()==0){
                    Utils.Alert("Select Value",mContext);
                    return;
                }

                dialog.dismiss();
                submitCloseRequest("",""+gps.getLatitude(),""+gps.getLongitude(),"",Str_SeqNo,selectedValue[0]);
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

                if(requestType.equalsIgnoreCase("Maintenances")) {

                    if(ReqIndex==0){
                        Res_Name_1 = leaveTypesImplemented.get(position).getListValueWithoutIndex();
                        Stand_By_1 = leaveTypesImplemented.get(position).getReq_Status();
                        btn_request_name.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                    }else if(ReqIndex==1){
                        Res_Name_2 = leaveTypesImplemented.get(position).getListValueWithoutIndex();
                        Stand_By_2 = leaveTypesImplemented.get(position).getReq_Status();
                        btn_day_type.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                        if(leaveTypesImplemented.get(position).getReq_Status().equalsIgnoreCase("NA")){
                            btn_standby.setVisibility(View.GONE);
                        }else{
                            btn_standby.setVisibility(View.VISIBLE);
                        }
                    }else if(ReqIndex==2){
                        btn_standby.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                    }else if(ReqIndex==3){
                        btn_action_type.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                    }

                }else {
                    btn_request_name.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }

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

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                btn_enddate.setText(leaveTypes.get(position));
                AttachedType = leaveTypes.get(position);
            }
        });
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
                    Intent logoutIntent = new Intent(mContext, RequestLeave.class);
                    logoutIntent.putExtra("title", tv_header.getText().toString().trim());
                    logoutIntent.putExtra("requestType", requestType);
                    //logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    RequestLeave.activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("MaintPopupSubmit")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
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
                    RequestLeave.activity.finish();
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
                ApplyLeave(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("LeaveData")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveType.clear();
                    arrayLeaveHistory.clear();

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

                    if (jobj.has("list1")) {
                        PhotoJSON = jobj.optJSONArray("list1").toString();
                    }

                    JSONArray list2 = jobj.optJSONArray("list2");
                    for (int i = 0; i < list2.length(); i++) {
                            JSONObject jsonObject = list2.optJSONObject(i);
                            modelleaveHistory = new ModelLeaveHistory();
                            modelleaveHistory.setReq_Name(jsonObject.optString("Req_Name"));
                            modelleaveHistory.setReq_Status(jsonObject.optString("Req_Status"));
                            modelleaveHistory.setReq_Display(jsonObject.optString("Req_Display"));
                            modelleaveHistory.setListID(jsonObject.optString("Req_SeqNo"));
                            modelleaveHistory.setComplete_Status(jsonObject.optString("Complete_Status"));
                            modelleaveHistory.setReq_StartDate(jsonObject.optString("Req_StartDate"));
                            Log.i("Req_Status============>", jsonObject.optString("Req_Name") + "\n" + jsonObject.optString("Req_Status"));
                            arrayLeaveHistory.add(modelleaveHistory);
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
                        intent.putExtra("Str_Event", "Snap");
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        mContext.startActivity(intent);
                    }else{
                        activity.finish();
                    }

                } else {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ServiceTypeMaint")) {
            try {

                JSONObject jobj = new JSONObject(response);
                    arrayLeaveType.clear();

                    JSONArray list = jobj.optJSONArray("Data");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("Res_Name"));
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Res_Name"));
                        modelLeaveType.setListID(jsonObject.optString("SeqNo"));
                        modelLeaveType.setReq_Status(jsonObject.optString("Stand_By"));
                        arrayLeaveType.add(modelLeaveType);
                    }

                    if(arrayLeaveType.size()>0){
                        if(ReqIndex==4){
                            DialogueWithListActionType(arrayLeaveType);
                        }else {
                            DialogueWithList(arrayLeaveType);
                        }
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void ApplyLeave(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String URL = "";
        if (requestType.equalsIgnoreCase("Maintenances")) {
            //URL = "Misc_Req.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_St_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Ed_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Req_Type=" + requestType + "&Str_Req_Name=" + btn_request_name.getText().toString().trim() + "&Str_Req_Txt=" + edt_remark.getText().toString().trim() + "&Str_Veh_Type=" + AttachedType + "&Str_Leave_Type=" + "NA";
                String standBy="NA";
                if(btn_standby.getVisibility()==View.VISIBLE){
                    standBy=btn_standby.getText().toString().trim();
                }
                URL="Adhoc_Maint_Req_Cre.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_Lat="+lat+"&Str_Long="+lng+"&Str_DriverID="+driverId+"&ReqType=Maintenances&Service_Date="+btn_startdate_maint.getText().toString().trim()+"&Service_Type="+btn_request_name.getText().toString().trim()+"&Faulty_Type="+btn_request_name.getText().toString().trim()+"&Stand_By="+standBy+"&Vehicle_No="+btn_enddate.getText().toString().trim()+"&Action_Type="+btn_action_type.getText().toString().trim()+"&Workshop_Name=NA&Location_Name=NA&Contact_Person=NA&Req_Rmks="+edt_remark.getText().toString().trim()+"&Str_SeqNo=0";
        } else if (requestType.equalsIgnoreCase("OT")) {
            URL = "Misc_Req.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Req_Type=" + requestType + "&Str_Req_Name=" + btn_request_name.getText().toString().trim() + "&Str_Req_Txt=" + edt_remark.getText().toString().trim() + "&Str_St_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Ed_Dt=" + btn_enddate.getText().toString().trim() + "&Str_Leave_Type=" + "NA" + "&Str_Veh_Type=NA";
        } else {
            URL = "Misc_Req.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Req_Type=" + requestType + "&Str_Req_Name=" + btn_request_name.getText().toString().trim() + "&Str_Req_Txt=" + edt_remark.getText().toString().trim() + "&Str_St_Dt=" + btn_startdate.getText().toString().trim() + " 00:00:00" + "&Str_Ed_Dt=" + btn_enddate.getText().toString().trim() + " 00:00:00" + "&Str_Leave_Type=" + btn_day_type.getText().toString().trim() + "&Str_Veh_Type=NA";
        }

        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Apply Leave", URL, "ApplyLeave");
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
                h.tv_cancel = (TextView) v.findViewById(R.id.tv_cancel);
                h.tv_complete=v.findViewById(R.id.tv_complete);
                h.tv_leavetitle.setText(" " + (position + 1) + "." + arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());
                boolean isCancel = false;
                //2018-12-06 08:00:00.0
                String appliedDateString = arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if (!appliedDate.before(currentDate)) {
                        isCancel = true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                    if (!requestType.equalsIgnoreCase("OT")) {
                        if (!requestType.equalsIgnoreCase("Maintenances")) {
                            if (isCancel) {
                                h.tv_cancel.setVisibility(View.VISIBLE);
                            } else {
                                h.tv_cancel.setVisibility(View.GONE);
                            }
                        }else{
                            if(arrayLeaveHistory.get(position).getReq_Name().equalsIgnoreCase("SCHEDULED MAINTENANCE")) {
                                if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("approved")){
                                    h.tv_cancel.setVisibility(View.VISIBLE);
                                    h.tv_cancel.setText("COMPLETE");
                                }else{
                                    h.tv_cancel.setVisibility(View.GONE);

                                    if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Completed")){
                                        h.img_status.setImageResource(R.drawable.ic_leave_approve);
                                    }
                                }

                            }


                        }

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

                    if (requestType.equalsIgnoreCase("Maintenances")) {

                        if (arrayLeaveHistory.get(position).getReq_Name().equalsIgnoreCase("SCHEDULED MAINTENANCE")) {
                            if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("approved")){
                                h.tv_cancel.setVisibility(View.VISIBLE);
                                h.tv_cancel.setText("COMPLETE");
                            }else{
                                h.tv_cancel.setVisibility(View.GONE);
                                if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Completed")){
                                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                                }
                            }

                        } else {

                            h.tv_cancel.setText("CANCEL");
                        }
                    }
                }


                final ViewHolder finalH = h;
                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            if (finalH.tv_cancel.getText().toString().trim().equalsIgnoreCase("CANCEL")) {
                                ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Maintenances", "Cancelled", "Are you sure to cancel this request?");
                            } else {
                                ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "PlannerMaintenances", "Completed", "Are you sure to complete this request?");
                            }

                        } else if (requestType.equalsIgnoreCase("OT")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "OT", "Cancelled", "Are you sure to cancel this request?");
                        } else {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Leave", "Cancelled", "Are you sure to cancel this request?");
                        }
                    }
                });

               /* if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }
*/
                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (arrayLeaveHistory.get(position).getComplete_Status().equalsIgnoreCase("1")) {
                        h.tv_complete.setVisibility(View.VISIBLE);
                    } else {
                        h.tv_complete.setVisibility(View.GONE);
                    }
                }

                h.tv_complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReqIndex=4;
                        Str_SeqNo=arrayLeaveHistory.get(position).getListID();
                        getMaintTypes("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", "Action Status", "NA");
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_leavetitle.setText(" " + (position + 1) + "." + arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());


                boolean isCancel = false;
                //2018-12-06 08:00:00.0
                String appliedDateString = arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if (!appliedDate.before(currentDate)) {
                        isCancel = true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }


                final ViewHolder finalH1 = h;
                final ViewHolder finalH2 = h;
                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            if (finalH2.tv_cancel.getText().toString().trim().equalsIgnoreCase("CANCEL")) {
                                ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Maintenances", "Cancelled", "Are you sure to cancel this request?");
                            } else {
                                ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "PlannerMaintenances", "Completed", "Are you sure to complete this request?");
                            }

                        } else if (requestType.equalsIgnoreCase("OT")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "OT", "Cancelled", "Are you sure to cancel this request?");
                        } else {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Leave", "Cancelled", "Are you sure to cancel this request?");
                        }
                    }
                });

               /* if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }
*/


                if (requestType.equalsIgnoreCase("Maintenances")) {
                    if (arrayLeaveHistory.get(position).getComplete_Status().equalsIgnoreCase("1")) {
                        h.tv_complete.setVisibility(View.VISIBLE);
                    } else {
                        h.tv_complete.setVisibility(View.GONE);
                    }
                }

                h.tv_complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReqIndex=4;
                        Str_SeqNo=arrayLeaveHistory.get(position).getListID();
                        getMaintTypes("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", "Action Status", "NA");
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_leavetitle;
            private TextView tv_leavedetail;
            private TextView tv_cancel;
            private TextView tv_complete;
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
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "LeaveCancel");
    }

    public static void submitCloseRequest(String address, String lat, String lng, String gpsStatus, String seqNumber,String Str_Misc_Status) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc=NA&Str_GPS=ON&Str_DriverID="+driverId+"&Str_SeqNo="+seqNumber+"&Str_Misc_Type=ACTION%20TYPE&Str_Misc_Status="+Str_Misc_Status+"&Str_JobFor=BHS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "MaintPopupSubmit");
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

    private static void getMaintTypes(String address, String lat, String lng, String gpsStatus, String Str_SEL1, String Str_SEL2) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Maint_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_DriverID="+driverId+"&Str_ListType=Service%20Type&Str_SEL1="+Str_SEL1+"&Str_SEL2="+Str_SEL2+"&Str_Lat="+lat+"&Str_Long="+lng;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "ServiceTypeMaint");
    }
}