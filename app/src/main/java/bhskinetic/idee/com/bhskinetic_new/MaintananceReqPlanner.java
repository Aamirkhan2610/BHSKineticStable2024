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

import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Model.ModelLeaveHistory;
import Model.ModelLeaveType;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 05/09/2019.
 */
public class MaintananceReqPlanner extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_startdate, btn_lorry, btn_res_list, btn_company_list, btn_service_type, btn_service_list, btn_worksite, btn_appointment_date,btn_submit;
    public static ArrayList<ModelLeaveType> arrayLeaveHistory;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ArrayList<ModelLeaveType> arrayPrementive;
    public static ArrayList<ModelLeaveType> arrayLorry;
    public static ArrayList<ModelLeaveType> arrayCompany;
    public static ArrayList<ModelLeaveType> arrayResList;
    public static ArrayList<ModelLeaveType> arrayServiceType;
    public static ArrayList<ModelLeaveType> arrayServiceList;
    public static ArrayList<ModelLeaveType> arrayWorkSite;
    public static ModelLeaveHistory modelleaveHistory;
    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String requestType = "";
    public static String PhotoJSON = "";
    public static String AttachedType = "";
    public static ArrayAdapter<String> adapterleave;
    public static String resType = "";
    public static int tapPosition = -1;
    private static int _date = 0;
    private static int _year = 0;
    private static int _month = 0;
    private static int _hh = 0;
    private static int _mm = 0;
    private static int _ss = 0;
    public static String selectedDate="";
    public static String bookingStatus="Appointment";
    public static String seqNumber="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maint_req_planner);
        Init();
    }

    private void Init() {
        seqNumber="0";
        resType = "";
        tapPosition = -1;
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

        mContext = MaintananceReqPlanner.this;
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

        activity = MaintananceReqPlanner.this;
        arrayLeaveHistory = new ArrayList<>();
        btn_startdate = (Button) findViewById(R.id.btn_startdate);
        btn_lorry = (Button) findViewById(R.id.btn_lorry);
        btn_company_list = (Button) findViewById(R.id.btn_company_list);
        btn_service_type = (Button) findViewById(R.id.btn_service_type);
        btn_service_list = (Button) findViewById(R.id.btn_service_list);
        btn_worksite = (Button) findViewById(R.id.btn_worksite);
        btn_res_list = (Button) findViewById(R.id.btn_res_list);
        btn_appointment_date = (Button) findViewById(R.id.btn_appointment_date);
        btn_submit= (Button) findViewById(R.id.btn_submit);

        lv_request_status = (ListView) findViewById(R.id.lv_request_status);


        btn_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayPrementive, btn_startdate);
            }
        });

        btn_lorry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayLorry, btn_lorry);
            }
        });

        btn_company_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayCompany, btn_company_list);
            }
        });

        btn_res_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayResList, btn_res_list);
            }
        });

        btn_service_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayServiceType, btn_service_type);
            }
        });

        btn_service_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayServiceList, btn_service_list);
            }
        });

        btn_worksite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogueWithList(arrayWorkSite, btn_worksite);
            }
        });


        btn_appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetDateTime();
                InitDatePicker(0);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btn_startdate.getText().toString().trim().equalsIgnoreCase("CATEGORY")){
                        Utils.Alert("SELECT CATEGORY",mContext);
                    return;
                }

                if(btn_lorry.getText().toString().trim().equalsIgnoreCase("RESOURCE TYPE")){
                    Utils.Alert("SELECT RESOURCE TYPE",mContext);
                    return;
                }

                if(btn_company_list.getText().toString().trim().equalsIgnoreCase("SUPPLIER NAME")){
                    Utils.Alert("SELECT SUPPLIER NAME",mContext);
                    return;
                }

                if(btn_res_list.getText().toString().trim().equalsIgnoreCase("RESOURCE NAME")){
                    Utils.Alert("SELECT RESOURCE NAME",mContext);
                    return;
                }

                if(btn_service_type.getText().toString().trim().equalsIgnoreCase("MAINTANANCE TYPE")){
                    Utils.Alert("SELECT MAINTANANCE TYPE",mContext);
                    return;
                }

                if(btn_service_list.getText().toString().trim().equalsIgnoreCase("SERVICE TYPE")){
                    Utils.Alert("SELECT SERVICE TYPE",mContext);
                    return;
                }

                if(btn_worksite.getText().toString().trim().equalsIgnoreCase("WORKSHOP NAME")){
                    Utils.Alert("SELECT WORKSHOP NAME",mContext);
                    return;
                }

                if(btn_appointment_date.getText().toString().trim().equalsIgnoreCase("APPOINTMENT DATE")){
                   Utils.Alert("ENTER APPOINTMENT DATE",mContext);
                   return;
               }

                seqNumber="0";
                bookingStatus="Appointment";
                submitMaintRequest("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON");
            }
        });

        arrayPrementive = new ArrayList<>();
        arrayLorry = new ArrayList<>();
        arrayCompany = new ArrayList<>();
        arrayResList = new ArrayList<>();
        arrayServiceType = new ArrayList<>();
        arrayServiceList = new ArrayList<>();
        arrayWorkSite = new ArrayList<>();
        leaveTypeID = "";
        AttachedType = "";
        PhotoJSON = "";
    }

    public static void ResetDateTime() {
        _date = 0;
        _year = 0;
        _month = 0;
        _hh = 0;
        _mm = 0;
        _ss = 0;
    }

    public static void InitDatePicker(final int flag) {
        Calendar now = Calendar.getInstance();
       /* DatePickerDialog dpd = DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Calendar fivedayBeforeCalendar = Calendar.getInstance();
        fivedayBeforeCalendar.add(Calendar.DAY_OF_YEAR, -3);

        dpd.setMinDate(fivedayBeforeCalendar);
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");

            dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                _year = year;
                _month = monthOfYear+1;
                _date = dayOfMonth;
                InitTimePicker(flag);

            }
        });*/

        Calendar fivedayBeforeCalendar = Calendar.getInstance();
        fivedayBeforeCalendar.add(Calendar.DAY_OF_YEAR, -3);
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,R.style.dateTimeStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        _year = year;
                        _month = monthOfYear+1;
                        _date = dayOfMonth;
                        InitTimePicker(flag);


                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();




    }

    private static void InitTimePicker(final int flag) {
        Calendar now = Calendar.getInstance();
      /*  TimePickerDialog tpd = TimePickerDialog.newInstance(
                null,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });

        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                String secondString = second < 10 ? "0" + second : "" + second;
                String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
                Log.i("TIME-->", time);

                _hh = Integer.parseInt(hourString);
                _mm = Integer.parseInt(minuteString);
                _ss = Integer.parseInt(secondString);

                String selectedDateTime = _year + "-" + _month + "-" + _date + " " + _hh + ":" + _mm + ":" + _ss;
                Log.i("SELECTED DATE TIME", selectedDateTime);

                selectedDate=selectedDateTime;
                if(flag==0) {
                    btn_appointment_date.setText(_year + "-" + _month + "-" + _date + " " + _hh + ":" + _mm);
                    bookingStatus="Appointment";
                }else{
                    if(flag==1){
                        bookingStatus="Re-Appointment";
                    }else{
                        bookingStatus="Extend";
                    }
                    submitMaintRequest("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON");
                }
            }
        });

        tpd.show(activity.getFragmentManager(), "TimePickerDialoge");*/

        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString = "0";
                        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
                        Log.i("TIME-->", time);

                        _hh = Integer.parseInt(hourString);
                        _mm = Integer.parseInt(minuteString);
                        _ss = Integer.parseInt(secondString);

                        String selectedDateTime = _year + "-" + _month + "-" + _date + " " + _hh + ":" + _mm + ":" + _ss;
                        Log.i("SELECTED DATE TIME", selectedDateTime);

                        selectedDate = selectedDateTime;
                        if (flag == 0) {
                            btn_appointment_date.setText(_year + "-" + _month + "-" + _date + " " + _hh + ":" + _mm);
                            bookingStatus = "Appointment";
                        } else {
                            if (flag == 1) {
                                bookingStatus = "Re-Appointment";
                            } else {
                                bookingStatus = "Extend";
                            }
                            submitMaintRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");
                        }


                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);

        if (_date == now.get(Calendar.DAY_OF_MONTH)) {
            // tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);

        }
        timePickerDialog.show();


    }


    @Override
    protected void onResume() {
        super.onResume();
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "PlannerMaintananceAddress");

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


    public void DialogueWithList(final ArrayList<ModelLeaveType> leaveTypes, final Button btn) {
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
                dialog.dismiss();
                leaveTypeID = leaveTypesImplemented.get(position).getListID();
                btn.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());

                if (btn.getId() == R.id.btn_startdate) {
                    tapPosition = 0;
                    getButton4Value();
                } else if (btn.getId() == R.id.btn_lorry) {
                    tapPosition = 1;
                    getButton4Value();
                } else if (btn.getId() == R.id.btn_company_list) {
                    tapPosition = 2;
                    getButton4Value();
                } else if (btn.getId() == R.id.btn_res_list) {
                    tapPosition = 3;
                    getButton4Value();
                } else if (btn.getId() == R.id.btn_service_type) {
                    tapPosition = 4;
                    getButton5Value();
                } else if (btn.getId() == R.id.btn_service_list) {
                    tapPosition = 5;
                    getButton6Value();
                } else if (btn.getId() == R.id.btn_worksite) {
                    tapPosition = 6;
                    getButton7Value();
                }



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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutPlannerMaintanance");
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
                    Intent logoutIntent = new Intent(mContext, MaintananceReqPlanner.class);
                    //logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    MaintananceReqPlanner.activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("SubmitMaintRequest")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                    if(btn_service_list.getText().toString().trim().equalsIgnoreCase("GOOGLE FORM") && seqNumber.equalsIgnoreCase("0")){
                        Intent googleIntent = new Intent(mContext, WebViewActivitiy.class);
                        googleIntent.putExtra("DocumentURL", "https://docs.google.com/forms/d/e/1FAIpQLSd4FAjSs8Qf9en8QAocJRWKL-SQEAF-3pqh4EioXtys9mlojg/viewform?usp=sf_link");
                        googleIntent.putExtra("DocumentTitle",tv_header.getText().toString().trim());
                        googleIntent.putExtra("isGoogleDrive","0");
                        mContext.startActivity(googleIntent);
                    }else {
                        Intent refreshIntent = new Intent(mContext, MaintananceReqPlanner.class);
                        refreshIntent.putExtra("title", tv_header.getText().toString().trim());
                        mContext.startActivity(refreshIntent);
                        MaintananceReqPlanner.activity.finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("logoutPlannerMaintanance")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    MaintananceReqPlanner.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("PlannerMaintananceAddress")) {
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
                getMaintananceDetails(address, lat, lng, GPSStatus);
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
        } else if (redirectionKey.equalsIgnoreCase("MaintList")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (resType.equalsIgnoreCase("4")) {
                    arrayResList.clear();
                } else if (resType.equalsIgnoreCase("5")) {
                    arrayServiceType.clear();
                } else if (resType.equalsIgnoreCase("6")) {
                    arrayServiceList.clear();
                } else if (resType.equalsIgnoreCase("7")) {
                    arrayWorkSite.clear();
                }
                JSONArray Data = jobj.optJSONArray("Data");
                for (int i = 0; i < Data.length(); i++) {
                    JSONObject jsonObject = Data.optJSONObject(i);
                    modelLeaveType = new ModelLeaveType();
                    modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("Res_Name"));
                    modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Res_Name"));
                    modelLeaveType.setListID(jsonObject.optString("SeqNo"));
                    if (resType.equalsIgnoreCase("4")) {
                        arrayResList.add(modelLeaveType);
                    } else if (resType.equalsIgnoreCase("5")) {
                        arrayServiceType.add(modelLeaveType);
                    } else if (resType.equalsIgnoreCase("6")) {
                        arrayServiceList.add(modelLeaveType);
                    } else if (resType.equalsIgnoreCase("7")) {
                        arrayWorkSite.add(modelLeaveType);
                    }
                }

                if(tapPosition==0){
                    btn_lorry.setText("RESOURCE TYPE");
                    btn_company_list.setText("SUPPLIER NAME");
                }

                if(tapPosition==1){
                    btn_company_list.setText("SUPPLIER NAME");
                }


                if (resType.equalsIgnoreCase("4")) {
                    if (arrayResList.size() > 0 && tapPosition != 3) {
                        btn_res_list.setText("RESOURCE NAME");
                    }
                    getButton5Value();
                } else if (resType.equalsIgnoreCase("5")) {
                    if (arrayServiceType.size() > 0 && tapPosition != 4) {
                        btn_service_type.setText("MAINTENANCE TYPE");
                    }
                    getButton6Value();
                } else if (resType.equalsIgnoreCase("6")) {
                    if (arrayServiceList.size() > 0 && tapPosition != 5) {
                        btn_service_list.setText("SERVICE TYPE");
                    }
                    getButton7Value();
                } else if (resType.equalsIgnoreCase("7")) {
                    if (arrayWorkSite.size() > 0 && tapPosition != 6) {
                        btn_worksite.setText("WORKSHOP NAME");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("PlannerMaintData")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayPrementive.clear();

                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("ListValue"));
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("ListValue"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayPrementive.add(modelLeaveType);

                    }

                    /*if (arrayPrementive.size() > 0) {
                        btn_startdate.setText(arrayPrementive.get(0).getListValueWithoutIndex());
                    }*/

                    arrayLorry.clear();
                    JSONArray list1 = jobj.optJSONArray("list1");
                    for (int i = 0; i < list1.length(); i++) {
                        JSONObject jsonObject = list1.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("ListValue"));
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("ListValue"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayLorry.add(modelLeaveType);

                    }

                    /*if (arrayLorry.size() > 0) {
                        btn_lorry.setText(arrayLorry.get(0).getListValueWithoutIndex());
                    }*/

                    arrayCompany.clear();
                    JSONArray list2 = jobj.optJSONArray("list2");
                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject jsonObject = list2.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("ListValue"));
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("ListValue"));
                        modelLeaveType.setListID(jsonObject.optString("ListID"));
                        arrayCompany.add(modelLeaveType);
                    }

                    /*if (arrayCompany.size() > 0) {
                        btn_company_list.setText(arrayCompany.get(0).getListValueWithoutIndex());
                    }*/
                    arrayLeaveHistory.clear();
                    JSONArray list3 = jobj.optJSONArray("list3");
                    for (int i = 0; i < list3.length(); i++) {
                        JSONObject jsonObject = list3.optJSONObject(i);
                        modelLeaveType = new ModelLeaveType();
                        modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Req_Display"));
                        modelLeaveType.setL_Bal(jsonObject.optString("Req_Name"));
                        modelLeaveType.setReq_Status(jsonObject.optString("Req_Status"));
                        modelLeaveType.setListID(jsonObject.optString("Req_StartDate")+"_"+jsonObject.optString("Req_SeqNo"));
                        arrayLeaveHistory.add(modelLeaveType);
                    }

                    adapter = new ListElementAdapter(mContext);
                    lv_request_status.setAdapter(adapter);

                    //getButton4Value();
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

    private void getButton4Value() {
        resType = "4";
        getMaintList("NA", "" + gps.getLongitude(), "" + gps.getLongitude(), "ON", "Resource List", btn_lorry.getText().toString().trim(), btn_company_list.getText().toString().trim());
    }

    private void getButton5Value() {
        resType = "5";
        getMaintList("NA", "" + gps.getLongitude(), "" + gps.getLongitude(), "ON", "Service Type", btn_startdate.getText().toString().trim(), btn_lorry.getText().toString().trim());
    }

    private void getButton6Value() {
        resType = "6";
        getMaintList("NA", "" + gps.getLongitude(), "" + gps.getLongitude(), "ON", "Service list", btn_lorry.getText().toString().trim(), btn_service_type.getText().toString().trim());
    }

    private void getButton7Value() {
        resType = "7";
        getMaintList("NA", "" + gps.getLongitude(), "" + gps.getLongitude(), "ON", "Workshop", btn_service_list.getText().toString().trim(), btn_company_list.getText().toString().trim());
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
                h.tv_request_name.setText(arrayLeaveHistory.get(position).getReq_Status());
                final String[] array=arrayLeaveHistory.get(position).getListID().split("_");
                h.tv_datetime.setText(array[0]);

                h.btn_approve.setText("Re-Appt Date");
                h.btn_reject.setText("Extend Date");

                h.btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seqNumber=array[1];
                        InitDatePicker(1);
                    }
                });

                h.btn_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seqNumber=array[1];
                        InitDatePicker(2);
                    }
                });

                if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Pending")){
                    h.btn_approve.setVisibility(View.VISIBLE);
                    h.btn_reject.setVisibility(View.VISIBLE);
                }else{
                    h.btn_approve.setVisibility(View.GONE);
                    h.btn_reject.setVisibility(View.GONE);
                }

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_drivername.setText(arrayLeaveHistory.get(position).getListValueWithoutIndex());
                h.tv_request_name.setText(arrayLeaveHistory.get(position).getReq_Status());
                final String[] array=arrayLeaveHistory.get(position).getListID().split("_");
                h.tv_datetime.setText(array[0]);
                h.btn_approve.setText("Re-Appt Date");
                h.btn_reject.setText("Extend Date");
                h.btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seqNumber=array[1];
                        InitDatePicker(1);
                    }
                });

                h.btn_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seqNumber=array[1];
                        InitDatePicker(2);
                    }
                });

                if(arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Pending")){
                    h.btn_approve.setVisibility(View.VISIBLE);
                    h.btn_reject.setVisibility(View.VISIBLE);
                }else{
                    h.btn_approve.setVisibility(View.GONE);
                    h.btn_reject.setVisibility(View.GONE);
                }
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

    public static void ConfirmCancelSubmition(final String vseqNumber, final String miscType, final String miscStatus) {
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

        builder.setMessage("Are you sure to cancel this reuqest?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void submitMaintRequest(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Maint_Req_Cre.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_Lat="+lat+"&Str_Long="+lng+"&Str_DriverID="+driverId+"&Service_Type="+btn_startdate.getText().toString().trim()+"&Resource_Type="+btn_lorry.getText().toString().trim()+"&Supplier_Name="+btn_company_list.getText().toString().trim()+"&Resource_Name="+btn_res_list.getText().toString().trim()+"&Service_Name="+btn_service_type.getText().toString().trim()+"&Service_Item="+btn_service_list.getText().toString().trim()+"&WorkShop_Name="+btn_worksite.getText().toString().trim()+"&Appointment_Date="+selectedDate+"&Booking_Status="+bookingStatus+"&Str_SeqNo="+seqNumber;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Submit Maintainance Request", URL, "SubmitMaintRequest");
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

    private void getMaintananceDetails(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=PlannerMaintenances";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "PlannerMaintData");
    }

    private void getMaintList(String address, String lat, String lng, String gpsStatus, String Str_ListType, String Str_SEL1, String Str_SEL2) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Maint_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_DriverID=" + driverId + "&Str_ListType=" + Str_ListType + "&Str_SEL1=" + Str_SEL1 + "&Str_SEL2=" + Str_SEL2 + "&Str_Lat=" + lat + "&Str_Long=" + lng;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "MaintList");
    }
}
