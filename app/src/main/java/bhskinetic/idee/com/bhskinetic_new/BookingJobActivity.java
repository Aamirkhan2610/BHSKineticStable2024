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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Model.ModelException;
import co.ceryle.fitgridview.FitGridView;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/18/2017.
 */
public class BookingJobActivity extends Activity {
    public static TextView tv_header, tv_tracking_number;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ArrayAdapter<String> adapterAssignJob;
    public static Button btn_from_location;
    public static Button btn_to_location;
    public static ImageView img_barecode;
    public static EditText edt_tracking_number;
    public static EditText edt_numberof_trip;
    public static Button btn_startdate;
    public static String selectedDat = "";
    public static Button btn_enddate;
    public static Button btn_container;
    public static Button btn_submit;
    public static Button btn_duration;
    public static String timeSlotSelection = "";
    public static Button btn_work_group;
    public static Button btn_customer_list;
    public static int FLAG_FROM_LOCATION = 0;
    public static int FLAG_TO_LOCATION = 1;
    public static int FLAG_SELECT_VEHICLE = 2;
    public static int FLAG_SUBMIT = 3;
    public static int FLAG_SELECT_DURATION = 4;
    public static int FLAG_SELECT_CUSTOMER = 5;
    public static int FLAG_SELECT_WORKGROUP = 6;
    public static int FLAG = FLAG_FROM_LOCATION;
    public static ModelException modelException;
    public static ArrayList<ModelException> arrayException;
    public static String nameVehicle = "";
    public static String namefromLocation = "";
    public static String nametoLocation = "";
    public static String requestType = "";
    private static int _date = 0;
    private static int _year = 0;
    private static int _month = 0;
    private static int _hh = 0;
    private static int _mm = 0;
    private static int _ss = 0;
    public static LinearLayout linear_scan, linear_snap, linear_no_document;
    public static int redirectionType = 0;
    public static String driverType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_booking_job);
        mContext = BookingJobActivity.this;
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        tv_tracking_number = (TextView) findViewById(R.id.tv_tracking_number);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        timeSlotSelection = "";
        selectedDat = "";
        linear_scan = (LinearLayout) findViewById(R.id.linear_scan);
        linear_snap = (LinearLayout) findViewById(R.id.linear_snap);
        linear_no_document = (LinearLayout) findViewById(R.id.linear_no_document);
        redirectionType = 0;
        FLAG = FLAG_FROM_LOCATION;
        btn_from_location = (Button) findViewById(R.id.btn_from_location);
        btn_to_location = (Button) findViewById(R.id.btn_to_location);
        img_barecode = (ImageView) findViewById(R.id.img_barecode);
        edt_tracking_number = (EditText) findViewById(R.id.edt_tracking_number);
        edt_numberof_trip = (EditText) findViewById(R.id.edt_numberof_trip);
        btn_startdate = (Button) findViewById(R.id.btn_startdate);
        btn_enddate = (Button) findViewById(R.id.btn_enddate);
        btn_duration = (Button) findViewById(R.id.btn_duration);
        btn_container = (Button) findViewById(R.id.btn_vehicle);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_work_group = (Button) findViewById(R.id.btn_work_group);
        btn_customer_list = (Button) findViewById(R.id.btn_customer_list);


        driverType = "";

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));

            btn_work_group.setText("SELECT WORKGROUP");
            driverType = Utils.getPref(getString(R.string.pref_drivertype), mContext);
            if (driverType.equalsIgnoreCase("Customer")) {
                btn_container.setText("SELECT CONTAINER");
                btn_enddate.setText("SELECT DURATION");
                btn_enddate.setVisibility(View.GONE);
                btn_duration.setVisibility(View.VISIBLE);

                btn_work_group.setVisibility(View.GONE);
            } else {
                btn_work_group.setVisibility(View.VISIBLE);
            }

            if (b.getString("isPlanner").equalsIgnoreCase("1")) {
                driverType = "Planner";
            } else {
                driverType = "OTHER";
            }
        }


        btn_from_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = FLAG_FROM_LOCATION;
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressAssignJob");

            }
        });

        btn_to_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = FLAG_TO_LOCATION;
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressAssignJob");
            }
        });

        img_barecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra("title", "SCAN");
                startActivity(intent);
            }
        });

        btn_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* ResetDateTime();
                InitDatePicker(0);*/

                if (btn_duration.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                    Utils.Alert("SELECT DURATION", mContext);
                    return;
                }

                getTimeSlot("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "", getCurrentDateTime(), "NA", "1", "getAvailableDates");

            }
        });


        btn_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_startdate.getText().toString().trim().equalsIgnoreCase("PICKUP DATE")) {
                    Utils.Alert("SELECT PICKUP DATE", mContext);
                    return;
                }

                //getTimeSlot("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "");

                ResetDateTime();


            }
        });

        btn_customer_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GPSStatus = "ON";
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                FLAG = FLAG_SELECT_CUSTOMER;
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                    GPSStatus = "OFF";
                }

                getCustomer("NA", "" + lat, "" + lng, GPSStatus);
            }
        });


        btn_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_from_location.getText().toString().trim().equalsIgnoreCase("PICKUP LOCATION")) {
                    Utils.Alert("SELECT PICKUP LOCATION", mContext);
                    return;
                }

                if (driverType.equalsIgnoreCase("Planner")) {

                    ArrayList<ModelException> arrayListTimeSlot = new ArrayList<>();
                    ModelException modelException = new ModelException();
                    modelException.setListValue("120 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("180 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("240 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("300 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("360 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("420 Min");
                    arrayListTimeSlot.add(modelException);
                    modelException = new ModelException();
                    modelException.setListValue("480 Min");
                    arrayListTimeSlot.add(modelException);
                    FLAG = FLAG_SELECT_DURATION;
                    DialogueWithList(arrayListTimeSlot);
                }

            }
        });

        btn_work_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GPSStatus = "ON";
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                FLAG = FLAG_SELECT_WORKGROUP;
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                    GPSStatus = "OFF";
                }

                getWorkGroup("NA", "" + lat, "" + lng, GPSStatus);
            }
        });

        btn_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FLAG = FLAG_SELECT_VEHICLE;
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressAssignJob");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_from_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_from_location))) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikuplocation), mContext);
                    return;
                }

                if (btn_startdate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_pickup_date))) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikupdate), mContext);
                    return;
                }

                if (btn_to_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_to_location))) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_dellocation), mContext);
                    return;
                }


                if (btn_enddate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_del_date))) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_deldate), mContext);
                    return;
                }


                String StartDate = btn_startdate.getText().toString().trim();
                String EndDate = btn_enddate.getText().toString().trim();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                try {
                    Date pickupdate = format.parse(StartDate);
                    Date dropdate = format.parse(EndDate);
                    Calendar pickupcal = Calendar.getInstance();
                    Calendar dropcal = Calendar.getInstance();
                    pickupcal.setTime(pickupdate);
                    dropcal.setTime(dropdate);
                    if (pickupcal.getTimeInMillis() > dropcal.getTimeInMillis()) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_start_end_delivery_date), mContext);
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*if (tv_tracking_number.getText().toString().trim().length() == 0) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_tracking_number), mContext);
                    return;
                }*/

                if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
                    return;
                }

                if (btn_container.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_select_vehicle))) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_select_vehicle), mContext);
                    return;
                }


                FLAG = FLAG_SUBMIT;
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressAssignJob");
            }
        });

        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        gps = new TrackGPS(mContext);


        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        activity = BookingJobActivity.this;

        nameVehicle = "";
        namefromLocation = "";
        nametoLocation = "";
        arrayException = new ArrayList<>();

        linear_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (btn_from_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_from_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikuplocation), mContext);
                        return;
                    }

                    if (btn_duration.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }

                    if (btn_startdate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_pickup_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikupdate), mContext);
                        return;
                    }

                    if (btn_to_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_to_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_dellocation), mContext);
                        return;
                    }

                    if (btn_work_group.getVisibility() == View.VISIBLE) {
                        if (btn_work_group.getText().toString().trim().equalsIgnoreCase("SELECT WORKGROUP")) {
                            Utils.Alert("SELECT WORKGROUP", mContext);
                            return;
                        }
                    }

                    if (btn_container.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
                        Utils.Alert("SELECT CONTAINER", mContext);
                        return;
                    }

                    if (btn_customer_list.getText().toString().trim().equalsIgnoreCase("SELECT CUSTOMER")) {
                        Utils.Alert("SELECT CUSTOMER", mContext);
                        return;
                    }

                    redirectionType = 0;
                    checkAvailblity("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");

            }
        });


        linear_no_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (btn_from_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_from_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikuplocation), mContext);
                        return;
                    }


                    if (btn_duration.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }

                    if (btn_startdate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_pickup_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikupdate), mContext);
                        return;
                    }

                    if (btn_to_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_to_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_dellocation), mContext);
                        return;
                    }

                    if (btn_work_group.getVisibility() == View.VISIBLE) {
                        if (btn_work_group.getText().toString().trim().equalsIgnoreCase("SELECT WORKGROUP")) {
                            Utils.Alert("SELECT WORKGROUP", mContext);
                            return;
                        }
                    }

                    if (btn_container.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
                        Utils.Alert("SELECT CONTAINER", mContext);
                        return;
                    }

                    if (btn_customer_list.getText().toString().trim().equalsIgnoreCase("SELECT CUSTOMER")) {
                        Utils.Alert("SELECT CUSTOMER", mContext);
                        return;
                    }



                    redirectionType = 2;
                    checkAvailblity("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");

            }
        });

        linear_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (btn_from_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_from_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikuplocation), mContext);
                        return;
                    }

                    if (btn_duration.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }

                    if (btn_startdate.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_pickup_date))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_pikupdate), mContext);
                        return;
                    }

                    if (btn_to_location.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_to_location))) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_dellocation), mContext);
                        return;
                    }

                    if (btn_work_group.getVisibility() == View.VISIBLE) {
                        if (btn_work_group.getText().toString().trim().equalsIgnoreCase("SELECT WORKGROUP")) {
                            Utils.Alert("SELECT WORKGROUP", mContext);
                            return;
                        }
                    }

                    if (btn_container.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
                        Utils.Alert("SELECT CONTAINER", mContext);
                        return;
                    }

                    if (btn_customer_list.getText().toString().trim().equalsIgnoreCase("SELECT CUSTOMER")) {
                        Utils.Alert("SELECT CUSTOMER", mContext);
                        return;
                    }



                    redirectionType = 1;
                    checkAvailblity("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");

            }
        });

        if (Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext).trim().length() > 0) {
            btn_container.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext));
        }

        String[] data = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};


    }

    private void getCustomer(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ResType = "Customer";

        APIUtils.sendRequest(mContext, "Location List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_ResType=" + Str_ResType, "customerListBook");

    }

    public static String getCurrentDateTime() {
        Calendar cc = Calendar.getInstance();
        _year = cc.get(Calendar.YEAR);
        String month = "" + (cc.get(Calendar.MONTH) + 1);
        if (month.trim().length() == 1) {
            month = "0" + month;
        }
        String date = "" + cc.get(Calendar.DAY_OF_MONTH);
        if (date.trim().length() == 1) {
            date = "0" + date;
        }

        return _year + "-" + month + "-" + date;
    }


    public static void RedirectToNextPage(int type) {



        String StartDate = selectedDat + " 00:00:00";
        String EndDate = btn_duration.getText().toString().trim();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Date pickupdate = format.parse(StartDate);
            Date dropdate = format.parse(StartDate);
            Calendar pickupcal = Calendar.getInstance();
            Calendar dropcal = Calendar.getInstance();
            pickupcal.setTime(pickupdate);
            dropcal.setTime(dropdate);
            if (pickupcal.getTimeInMillis() > dropcal.getTimeInMillis()) {
                Utils.Alert(mContext.getResources().getString(R.string.alert_start_end_delivery_date), mContext);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
            Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
            return;
        }

      /*  if (btn_vehicle.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_select_vehicle))) {
            Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_select_vehicle), mContext);
            return;
        }*/

        String workGroup = "NA";
        if (btn_work_group.getVisibility() == View.VISIBLE) {
            workGroup = btn_work_group.getText().toString().trim();
        }

        if (type == 0) {
            Intent intent = new Intent(mContext, ScanActivity.class);
            intent.putExtra("title", "Scan");
            intent.putExtra("isSign", false);
            intent.putExtra("workGroup", workGroup);
            intent.putExtra("isAssignJob", "true");
            activity.startActivity(intent);
            activity.finish();
        } else if (type == 1) {
            Intent intent = new Intent(mContext, PhotoUploadActivity.class);
            intent.putExtra("title", "Snap");
            intent.putExtra("isSign", false);
            intent.putExtra("isAssignJob", "true");
            intent.putExtra("Str_Sts", "Snap");
            intent.putExtra("Str_Event", "Snap");
            intent.putExtra("workGroup", workGroup);
            intent.putExtra("snap", "true");
            activity.startActivity(intent);
            activity.finish();
        } else {

            String timeslot1 = timeSlotSelection.split("-")[0];
            String timeslot2 = timeSlotSelection.split("-")[1];

            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

            // String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
            String tripNumber = "";
            String URL = "";
            if (driverType.equalsIgnoreCase("Planner")) {
                String endDateStr = BookingJobActivity.btn_duration.getText().toString().trim();
                if (endDateStr.contains("Min")) {
                    endDateStr = endDateStr.replaceAll("Min", "").trim();
                }
                String startDateStr = BookingJobActivity.selectedDat + " " + timeslot1 + ":00";
                DateFormat myformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                try {
                    Date startDate = myformat.parse(startDateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(cal.MINUTE, Integer.parseInt(endDateStr));
                    String EnddateValue = myformat.format(cal.getTime());
                    URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SCAN&Str_BarCode=" + "NODOCUMENTS" + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + "" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Loc=" + "NA" + "&Str_GPS=" + "OFF" + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + startDateStr + "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat + " " + timeslot2 + ":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim() + "&Str_JobStatus=Un-Assigned" + "&Str_Customer=" + btn_customer_list.getText().toString().trim() + "&Str_WorkGroup=" + workGroup;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (URL.contains(" ")) {
                URL = URL.replace(" ", "%20");
            }
            APIUtils.sendRequest(mContext, "Create Job", URL, "createJobAsign");

        }
    }


    private void InitDatePicker(final int flag) {
        Calendar now = Calendar.getInstance();
        if (flag == 1) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = null;
            try {
                date = format.parse(btn_startdate.getText().toString().trim());
                now.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.dateTimeStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                        Log.i("DATE-->", date);
                        _year = year;
                        _month = monthOfYear;
                        _date = dayOfMonth;
                        //InitTimePicker(flag);
                        String selectedDateTime = _year + "-" + _month + "-" + _date;
                        btn_startdate.setText(selectedDateTime);
                        getTimeSlot("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "", timeSlotSelection, btn_duration.getText().toString().trim().replaceAll("Min", ""), "2", "bookingTimeSlot");

                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        //datePickerDialog.getDatePicker().setDisplayedValues( new String[] { "25-5-2010" ,"25-5-2011", "25-5-2012", "25-5-2013","25-5-2014","25-5-2015" } );

        datePickerDialog.show();


    }


    private void InitTimePicker(final int flag) {

        final Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
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
                        // tv_datetime.setText(UiUtils.changeDateFormateNOUTCObs(selectedDateTime));

                        if (flag == 0) {

                            btn_startdate.setText(selectedDateTime);


                        } else {
                            btn_enddate.setText(selectedDateTime);
                        }
                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);

        if (_date == now.get(Calendar.DAY_OF_MONTH)) {
            // tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);

        }
        timePickerDialog.show();

    }

    public static void ResetDateTime() {
        _date = 0;
        _year = 0;
        _month = 0;
        _hh = 0;
        _mm = 0;
        _ss = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tv_tracking_number != null)
            tv_tracking_number.setText(Utils.ScanResult);
    }

    //Ask User to choose Yes/No
    public static void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutAssignJob");
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

    public void DialogueWithList(final ArrayList<ModelException> arrayList) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist_addjob);
        dialog.setCancelable(false);

        if (FLAG == FLAG_FROM_LOCATION) {
            dialog.setTitle("PICKUP LOCATION");
        } else if (FLAG == FLAG_TO_LOCATION) {
            dialog.setTitle("DELIVERY LOCATION");
        } else if (FLAG == FLAG_SELECT_VEHICLE) {
            dialog.setTitle(mContext.getResources().getString(R.string.alert_select_resources_vehicle));
        } else if (FLAG == FLAG_SELECT_DURATION) {
            dialog.setTitle("SELECT DURATION (MINUTE)");
        } else if (FLAG == FLAG_SELECT_CUSTOMER) {
            dialog.setTitle("SELECT CUSTOMER");
        }else if (FLAG == FLAG_SELECT_WORKGROUP) {
            dialog.setTitle("SELECT WORKGROUP");
        }

        final ArrayList<ModelException> arrayListFiltered = new ArrayList<>();
        final ArrayList<ModelException> arrayListImplemented = new ArrayList<>();
        arrayListImplemented.addAll(arrayList);

        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final String[] values = new String[arrayListImplemented.size()];
        for (int i = 0; i < arrayListImplemented.size(); i++) {
            values[i] = arrayListImplemented.get(i).getListValue();
        }
        adapterAssignJob = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterAssignJob);
        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (FLAG == FLAG_FROM_LOCATION) {
                    namefromLocation = arrayListImplemented.get(position).getListValueWithoutIndex();
                    btn_from_location.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                } else if (FLAG == FLAG_TO_LOCATION) {
                    nametoLocation = arrayListImplemented.get(position).getListValueWithoutIndex();
                    btn_to_location.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                } else if (FLAG == FLAG_SELECT_VEHICLE) {
                    nameVehicle = arrayListImplemented.get(position).getListValueWithoutIndex();
                    btn_container.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                } else if (FLAG == FLAG_SELECT_DURATION) {
                    btn_duration.setText(arrayListImplemented.get(position).getListValue());
                } else if (FLAG == FLAG_SELECT_CUSTOMER) {
                    btn_customer_list.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                }else if (FLAG == FLAG_SELECT_WORKGROUP) {
                    btn_work_group.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                }
                dialog.dismiss();
            }
        });


        final EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSearch.getText().toString().trim().length() > 0) {
                    if (FLAG == FLAG_FROM_LOCATION) {
                        namefromLocation = etSearch.getText().toString().trim();
                        btn_from_location.setText(etSearch.getText().toString().trim());
                    } else if (FLAG == FLAG_TO_LOCATION) {
                        nametoLocation = etSearch.getText().toString().trim();
                        btn_to_location.setText(etSearch.getText().toString().trim());
                    } else if (FLAG == FLAG_SELECT_VEHICLE) {
                        nameVehicle = etSearch.getText().toString().trim();
                        btn_container.setText(etSearch.getText().toString().trim());
                    }
                    dialog.dismiss();
                }
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                arrayListFiltered.clear();
                if (charText.length() == 0) {
                    arrayListFiltered.addAll(arrayList);
                } else {
                    for (ModelException wp : arrayList) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault()))) || (wp.getpCode().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            arrayListFiltered.add(wp);
                        }

                    }

                }

                arrayListImplemented.clear();
                arrayListImplemented.addAll(arrayListFiltered);

                final String[] values = new String[arrayListImplemented.size()];
                for (int i = 0; i < arrayListImplemented.size(); i++) {
                    values[i] = arrayListImplemented.get(i).getListValue();
                }
                adapterAssignJob = new ArrayAdapter<String>(mContext,
                        R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterAssignJob);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        dialog.show();
    }


    public void showResponse(String response, String redirectionKey) {

        if (redirectionKey.equalsIgnoreCase("logoutAssignJob")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    BookingJobActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("customerListBook")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListID("0");
                        modelException.setListValue((i + 1) + "." + value.optString("ResName"));
                        modelException.setListValueWithoutIndex(value.optString("ResName"));

                        arrayException.add(modelException);
                    }

                    DialogueWithList(arrayException);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("bookingTimeSlot")) {
            try {
                JSONObject jobj = new JSONObject(response);
                arrayException.clear();
                JSONArray list = jobj.optJSONArray("Data");

                if (list.length() > 0) {
                    JSONObject value = list.optJSONObject(0);
                    if (!value.has("Res_Date")) {
                        Utils.Alert("NO DATA AVAILABLE, PLEASE SELECT ANOTHER RECORD", mContext);
                        return;
                    }

                }

                for (int i = 0; i < list.length(); i++) {
                    JSONObject value = list.optJSONObject(i);
                    modelException = new ModelException();
                    String[] resDate = value.optString("Res_Date").split(" ");
                    modelException.setListID(resDate[0]);
                    modelException.setListValue((i + 1) + "." + value.optString("Res_TimeSlot"));
                    modelException.setListValueWithoutIndex(value.optString("Res_TimeSlot"));
                    arrayException.add(modelException);
                }
                //Opening Time Slot Dialogue
                showTimeSlotDialogue(0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("getAvailableDates")) {
            try {
                JSONObject jobj = new JSONObject(response);
                arrayException.clear();
                JSONArray list = jobj.optJSONArray("Data");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject value = list.optJSONObject(i);
                    modelException = new ModelException();
                    modelException.setListID(value.optString("Res_Count"));
                    modelException.setListValueWithoutIndex(value.optString("Res_Date"));
                    arrayException.add(modelException);
                }
                //Opening Time Slot Dialogue
                //showTimeSlotDialogue(1);
                showTimeSlotDialogueGrid(1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("CheckAvailblity")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    if (jobj.optString("Availbilty").equalsIgnoreCase("NO")) {
                        Toast.makeText(mContext, jobj.optString("MSG"), Toast.LENGTH_SHORT).show();
                    } else {
                        RedirectToNextPage(redirectionType);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("createJobAsign")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {

                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();

                    String Ack_Msg = jsonObject.optString("POD");
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
                    intent = new Intent(mContext, OrderList.class);
                    intent.putExtra("Str_JobStatus", "Un-Assigned");
                    intent.putExtra("title", "ORDER LIST");
                    activity.startActivity(intent);
                    activity.finish();


                } else {
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("AddressAssignJob")) {
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


                if (FLAG == FLAG_FROM_LOCATION || FLAG == FLAG_TO_LOCATION) {
                    getLocation(address, lat, lng, GPSStatus);
                }

                if (FLAG == FLAG_SELECT_VEHICLE) {
                    getVehicle(address, lat, lng, GPSStatus);
                }

                if (FLAG == FLAG_SUBMIT) {
                    createJob(address, lat, lng, GPSStatus);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("locationlist")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();

                        if (FLAG == FLAG_SELECT_VEHICLE) {
                            modelException.setListID("0");
                            modelException.setListValue((i + 1) + "." + value.optString("ResName"));
                            modelException.setListValueWithoutIndex(value.optString("ResName"));
                        } else {
                            modelException.setListID(value.optString("ListID"));
                            modelException.setListValue((i + 1) + "." + value.optString("ListValue"));
                            modelException.setListValueWithoutIndex(value.optString("ListValue"));
                            modelException.setpCode(value.optString("pCode"));
                        }
                        arrayException.add(modelException);
                    }

                    DialogueWithList(arrayException);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("customerList")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListID("0");
                        modelException.setListValue((i + 1) + "." + value.optString("ResName"));
                        modelException.setListValueWithoutIndex(value.optString("ResName"));

                        arrayException.add(modelException);
                    }

                    DialogueWithList(arrayException);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("createJob")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {

                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();

                    String Ack_Msg = jsonObject.optString("POD");
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
                        intent.putExtra("Str_Event", "Photos");
                        intent.putExtra("Str_Remark", "");
                        intent.putExtra("Str_Sts", "Create Job");
                        mContext.startActivity(intent);
                    }

                    //Reseting all fields
                    btn_from_location.setText(mContext.getResources().getString(R.string.btn_from_location));
                    btn_startdate.setText(mContext.getResources().getString(R.string.btn_pickup_date));
                    btn_to_location.setText(mContext.getResources().getString(R.string.btn_to_location));
                    btn_enddate.setText(mContext.getResources().getString(R.string.btn_del_date));
                    edt_tracking_number.setText("1");
                    edt_numberof_trip.setHint(mContext.getResources().getString(R.string.edt_hint_trip_number));
                    btn_container.setText(mContext.getResources().getString(R.string.btn_select_vehicle));
                    tv_tracking_number.setText("");
                } else {
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void showTimeSlotDialogueGrid(int flag) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialogue_grid);
        dialog.setCancelable(false);
        final ArrayList<ModelException> arrayListImplemented = new ArrayList<>();
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        FitGridView fitGridView = dialog.findViewById(R.id.gridView);
        final String[] values = new String[arrayListImplemented.size()];
        for (int i = 0; i < arrayListImplemented.size(); i++) {
            values[i] = arrayListImplemented.get(i).getListValueWithoutIndex();
        }

        final ListCheckAdapterGrid listCheckAdapter = new ListCheckAdapterGrid(mContext, flag);
        timeSlotSelection = "";
        fitGridView.setAdapter(listCheckAdapter);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //  dialog.dismiss();
            }
        });
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (timeSlotSelection.trim().length() == 0) {
                    Utils.Alert("Select Time Slot", mContext);
                    return;
                }

                if (flag == 0) {
                 //   String[] selectedRecords = selectedDat.split("/");
                 //   selectedDat = selectedRecords[2] + "-" + selectedRecords[1] + "-" + selectedRecords[0];
                    btn_startdate.setText(selectedDat + " " + timeSlotSelection);
                } else {
                    String[] selectedRecords = timeSlotSelection.split("/");

                    getTimeSlot("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "", selectedRecords[2] + "-" + selectedRecords[1] + "-" + selectedRecords[0], btn_duration.getText().toString().trim().replaceAll("Min", ""), "2", "bookingTimeSlot");
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public static void showTimeSlotDialogue(int flag) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialogue_timeslot);
        dialog.setCancelable(false);
        final ArrayList<ModelException> arrayListImplemented = new ArrayList<>();
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        final String[] values = new String[arrayListImplemented.size()];
        for (int i = 0; i < arrayListImplemented.size(); i++) {
            values[i] = arrayListImplemented.get(i).getListValueWithoutIndex();
        }

        final ListCheckAdapter listCheckAdapter = new ListCheckAdapter(mContext, flag);
        timeSlotSelection = "";
        lv_resource.setAdapter(listCheckAdapter);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //  dialog.dismiss();
            }
        });
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (timeSlotSelection.trim().length() == 0) {
                    Utils.Alert("Select Time Slot", mContext);
                    return;
                }

                if (flag == 0) {

                    btn_startdate.setText(selectedDat + " " + timeSlotSelection);
                } else {
                    String[] selectedRecords = timeSlotSelection.split("/");
                    selectedDat = timeSlotSelection;
                    getTimeSlot("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "", selectedRecords[2] + "-" + selectedRecords[1] + "-" + selectedRecords[0], btn_duration.getText().toString().trim().replaceAll("Min", ""), "2", "bookingTimeSlot");
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public static class ListCheckAdapterGrid extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        int flag = 0;

        public ListCheckAdapterGrid(Context _context, int _flag) {
            super();
            this.context = _context;
            this.flag = _flag;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayException.size();
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
            ListCheckAdapterGrid.ViewHolder h = null;
            boolean isVisible = false;
            if (v == null) {
                v = layoutInflater.inflate(R.layout.raw_dynamic_grid_booking, null);
                h = new ListCheckAdapterGrid.ViewHolder();
                h.tv_title = (TextView) v.findViewById(R.id.tv_title);
                h.radio_timeslot = v.findViewById(R.id.radio_range);

                h.tv_title.setText(arrayException.get(position).getListValueWithoutIndex());
                if (position != 0) {
                    if (arrayException.get(position).getListID().equalsIgnoreCase(arrayException.get(position - 1).getListID())) {
                        isVisible = false;
                    } else {
                        isVisible = true;
                    }
                } else {
                    isVisible = true;
                }


                h.radio_timeslot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < arrayException.size(); i++) {
                            arrayException.get(i).setSelected(false);
                        }
                        arrayException.get(position).setSelected(true);
                        timeSlotSelection = arrayException.get(position).getListValueWithoutIndex();
                        notifyDataSetChanged();
                    }
                });

                if (arrayException.get(position).isSelected()) {
                    h.radio_timeslot.setBackgroundResource(R.drawable.shape_rect_grid_selected);
                } else {
                    h.radio_timeslot.setBackgroundResource(R.drawable.shape_rect_grid);
                }

                v.setTag(h);
            } else {
                h = (ListCheckAdapterGrid.ViewHolder) v.getTag();
                h.tv_title.setText(arrayException.get(position).getListValueWithoutIndex());
                if (position != 0) {
                    if (arrayException.get(position).getListID().equalsIgnoreCase(arrayException.get(position - 1).getListID())) {
                        isVisible = false;
                    } else {
                        isVisible = true;
                    }
                } else {
                    isVisible = true;
                }


                h.radio_timeslot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < arrayException.size(); i++) {
                            arrayException.get(i).setSelected(false);
                        }
                        arrayException.get(position).setSelected(true);

                        timeSlotSelection = arrayException.get(position).getListValueWithoutIndex();
                        notifyDataSetChanged();
                    }
                });

                if (arrayException.get(position).isSelected()) {
                    h.radio_timeslot.setBackgroundResource(R.drawable.shape_rect_grid_selected);
                } else {
                    h.radio_timeslot.setBackgroundResource(R.drawable.shape_rect_grid);
                }
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_title, tv_time;
            private FrameLayout radio_timeslot;
            private LinearLayout linear_div, linear_div1;
        }
    }

    public static class ListCheckAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        int flag = 0;

        public ListCheckAdapter(Context _context, int _flag) {
            super();
            this.context = _context;
            this.flag = _flag;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayException.size();
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
            ListCheckAdapter.ViewHolder h = null;
            boolean isVisible = false;
            if (v == null) {
                v = layoutInflater.inflate(R.layout.listtext_timeslot, null);
                h = new ListCheckAdapter.ViewHolder();
                h.tv_title = (TextView) v.findViewById(R.id.tv_title);
                h.linear_div = (LinearLayout) v.findViewById(R.id.linear_div);
                h.tv_time = (TextView) v.findViewById(R.id.tv_time);
                h.radio_timeslot = (ImageView) v.findViewById(R.id.radio_range);
                h.linear_div1 = (LinearLayout) v.findViewById(R.id.linear_div1);
                h.tv_title.setText(arrayException.get(position).getListID());
                h.tv_time.setText(arrayException.get(position).getListValueWithoutIndex());

                if (position != 0) {
                    if (arrayException.get(position).getListID().equalsIgnoreCase(arrayException.get(position - 1).getListID())) {
                        isVisible = false;
                    } else {
                        isVisible = true;
                    }
                } else {
                    isVisible = true;
                }

                if (isVisible) {
                    h.tv_title.setVisibility(View.VISIBLE);
                    h.linear_div.setVisibility(View.VISIBLE);
                    h.linear_div1.setVisibility(View.VISIBLE);
                } else {
                    h.tv_title.setVisibility(View.GONE);
                    h.linear_div.setVisibility(View.GONE);
                    h.linear_div1.setVisibility(View.GONE);
                }
                h.radio_timeslot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < arrayException.size(); i++) {
                            arrayException.get(i).setSelected(false);
                        }
                        arrayException.get(position).setSelected(true);
                        selectedDat=arrayException.get(position).getListID();

                        timeSlotSelection = arrayException.get(position).getListValueWithoutIndex();
                        notifyDataSetChanged();
                    }
                });

                if (arrayException.get(position).isSelected()) {
                    h.radio_timeslot.setImageResource(R.drawable.ic_selected);
                } else {
                    h.radio_timeslot.setImageResource(R.drawable.ic_unselected);
                }

                if (flag == 1) {
                    h.linear_div1.setVisibility(View.GONE);
                    h.linear_div.setVisibility(View.GONE);
                    h.tv_title.setVisibility(View.GONE);
                }

                v.setTag(h);
            } else {
                h = (ListCheckAdapter.ViewHolder) v.getTag();
                h.tv_title.setText(arrayException.get(position).getListID());
                h.tv_time.setText(arrayException.get(position).getListValueWithoutIndex());
                if (position != 0) {
                    if (arrayException.get(position).getListID().equalsIgnoreCase(arrayException.get(position - 1).getListID())) {
                        isVisible = false;
                    } else {
                        isVisible = true;
                    }
                } else {
                    isVisible = true;
                }

                if (isVisible) {
                    h.tv_title.setVisibility(View.VISIBLE);
                    h.linear_div.setVisibility(View.VISIBLE);
                    h.linear_div1.setVisibility(View.VISIBLE);
                } else {
                    h.tv_title.setVisibility(View.GONE);
                    h.linear_div.setVisibility(View.GONE);
                    h.linear_div1.setVisibility(View.GONE);
                }


                h.radio_timeslot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < arrayException.size(); i++) {
                            arrayException.get(i).setSelected(false);
                        }
                        arrayException.get(position).setSelected(true);
                        selectedDat=arrayException.get(position).getListID();

                        timeSlotSelection = arrayException.get(position).getListValueWithoutIndex();
                        notifyDataSetChanged();
                    }
                });

                if (arrayException.get(position).isSelected()) {
                    h.radio_timeslot.setImageResource(R.drawable.ic_selected);
                } else {
                    h.radio_timeslot.setImageResource(R.drawable.ic_unselected);
                }

                if (flag == 1) {
                    h.linear_div1.setVisibility(View.GONE);
                    h.linear_div.setVisibility(View.GONE);
                    h.tv_title.setVisibility(View.GONE);
                }
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_title, tv_time;
            private ImageView radio_timeslot;
            private LinearLayout linear_div, linear_div1;
        }
    }

    private static void getTimeSlot(String address, String lat, String lng, String gpsStatus, String selectedDate, String deliveryDate, String Str_Opt, String redirectKey) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        //String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + namefromLocation + "&Str_PickUP_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + nametoLocation + "&Str_Delivery_Dt=" + btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + tv_tracking_number.getText().toString().trim() + "&Str_NoofTrip=" + edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + nameVehicle;
        String URL = "Chk_Res_Availability.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + btn_from_location.getText().toString().trim() + "&Str_PickUP_Dt=" + selectedDate + "&Str_Delivery_Loc=NA&Str_Delivery_Dt=" + deliveryDate + "&Str_Work_Group=NA&Str_Opt=" + Str_Opt;
        if (URL.contains(" ")) {
            URL = URL.replace(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Time Slot", URL, redirectKey);
    }

    private void createJob(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        //String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + namefromLocation + "&Str_PickUP_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + nametoLocation + "&Str_Delivery_Dt=" + btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + tv_tracking_number.getText().toString().trim() + "&Str_NoofTrip=" + edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + nameVehicle;
        String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + namefromLocation + "&Str_PickUP_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + nametoLocation + "&Str_Delivery_Dt=" + btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + nameVehicle;
        if (URL.contains(" ")) {
            URL = URL.replace(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Create Job", URL, "createJob");
    }


    private void getLocation(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Location List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Loc", "locationlist");
    }

    private void checkAvailblity(String address, String lat, String lng, String gpsStatus) {
        /*String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);


        String endDateStr = btn_duration.getText().toString().trim();
        if (endDateStr.contains("Min")) {
            endDateStr = endDateStr.replaceAll("Min", "").trim();
        }
        String startDateStr = selectedDat+" 00:00:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        try {
            Date startDate = format.parse(startDateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(cal.MINUTE, Integer.parseInt(endDateStr));
            String EnddateValue = format.format(cal.getTime());
            String APIUrl = "Chk_Availability.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Loc=NA&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + namefromLocation + "&Str_PickUP_Dt=" + btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + nametoLocation + "&Str_Delivery_Dt=" + EnddateValue + "&Str_ContainerNo=" + nameVehicle;
            if (APIUrl.contains(" ")) {
                APIUrl = APIUrl.replaceAll(" ", "%20");
            }
            APIUtils.sendRequest(mContext, "Check Availblity", APIUrl, "CheckAvailblity");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        RedirectToNextPage(redirectionType);


    }

    private void getVehicle(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ResType = "TrailerType";


        APIUtils.sendRequest(mContext, "Location List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_ResType=" + Str_ResType, "locationlist");

    }

    private void getWorkGroup(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ResType = "Work Group";
        APIUtils.sendRequest(mContext, "Location List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_ResType=" + "Work Group", "customerList");

    }


}
