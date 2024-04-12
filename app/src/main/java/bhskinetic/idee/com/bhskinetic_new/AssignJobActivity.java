package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.concurrent.TimeUnit;

import Model.ModelException;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/18/2017.
 */
public class AssignJobActivity extends Activity {
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
    public static Button btn_enddate;
    public static Button btn_vehicle;
    public static Button btn_submit;
    public static Button btn_customer_list;
    public static int FLAG_FROM_LOCATION = 0;
    public static int FLAG_TO_LOCATION = 1;
    public static int FLAG_SELECT_VEHICLE = 2;
    public static int FLAG_SUBMIT = 3;
    public static int FLAG_SELECT_DURATION = 4;
    public static int FLAG_SELECT_CUSTOMER = 5;
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
        setContentView(R.layout.layout_assign_job);
        mContext = AssignJobActivity.this;
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        tv_tracking_number = (TextView) findViewById(R.id.tv_tracking_number);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);

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
        btn_vehicle = (Button) findViewById(R.id.btn_vehicle);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_customer_list = (Button) findViewById(R.id.btn_customer_list);
        driverType = "";

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
            if (b.getString("isPlanner").equalsIgnoreCase("1")) {
                driverType = "Planner";
            } else {
                driverType = "OTHER";
            }
        }

        if (driverType.equalsIgnoreCase("Planner")) {
            btn_vehicle.setText("SELECT CONTAINER");
            btn_enddate.setText("SELECT DURATION");
            linear_no_document.setVisibility(View.VISIBLE);
            btn_customer_list.setVisibility(View.VISIBLE);
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

                getLocation("NA", ""+lat, ""+lng, "");

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

                getLocation("NA", ""+lat, ""+lng, "");


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
                ResetDateTime();
                InitDatePicker(0);
            }
        });

        btn_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_startdate.getText().toString().trim().equalsIgnoreCase("PICKUP DATE")) {
                    Utils.Alert("SELECT PICKUP DATE", mContext);
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
                } else {
                    // ResetDateTime();
                    InitDatePicker(1);
                }
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

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = FLAG_SELECT_VEHICLE;
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                getVehicle("", ""+lat, ""+lng, "");

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

                if (btn_vehicle.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_select_vehicle))) {
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

        activity = AssignJobActivity.this;

        nameVehicle = "";
        namefromLocation = "";
        nametoLocation = "";
        arrayException = new ArrayList<>();

        linear_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (driverType.equalsIgnoreCase("Planner")) {
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

                    if (btn_enddate.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }
                    if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
                        return;
                    }

                    if (btn_vehicle.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
                        Utils.Alert("SELECT CONTAINER", mContext);
                        return;
                    }

                    if (btn_customer_list.getText().toString().trim().equalsIgnoreCase("SELECT CUSTOMER")) {
                        Utils.Alert("SELECT CUSTOMER", mContext);
                        return;
                    }

                    redirectionType = 0;
                    checkAvailblity("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");
                } else {
                    RedirectToNextPage(0);
                }
            }
        });


        linear_no_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (driverType.equalsIgnoreCase("Planner")) {
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

                    if (btn_enddate.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }
                    if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
                        return;
                    }

                    if (btn_vehicle.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
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
            }
        });

        linear_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (driverType.equalsIgnoreCase("Planner")) {
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

                    if (btn_enddate.getText().toString().trim().equalsIgnoreCase("SELECT DURATION")) {
                        Utils.Alert("SELECT DURATION", mContext);
                        return;
                    }

                    if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
                        return;
                    }

                    if (btn_vehicle.getText().toString().trim().equalsIgnoreCase("SELECT CONTAINER")) {
                        Utils.Alert("SELECT CONTAINER", mContext);
                        return;
                    }

                    if (btn_customer_list.getText().toString().trim().equalsIgnoreCase("SELECT CUSTOMER")) {
                        Utils.Alert("SELECT CUSTOMER", mContext);
                        return;
                    }

                    redirectionType = 1;
                    checkAvailblity("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");
                } else {
                    RedirectToNextPage(1);
                }
            }
        });

        if (Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext).trim().length() > 0) {
            btn_vehicle.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext));
            nameVehicle=Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext);
        }

    }


    public static void RedirectToNextPage(int type) {
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

        if (edt_numberof_trip.getText().toString().trim().equalsIgnoreCase("0") || edt_numberof_trip.getText().toString().trim().length() == 0) {
            Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_num_of_trip), mContext);
            return;
        }

        if (btn_vehicle.getText().toString().trim().equalsIgnoreCase(mContext.getResources().getString(R.string.btn_select_vehicle))) {
            Utils.Alert(mContext.getResources().getString(R.string.alert_addjob_select_vehicle), mContext);
            return;
        }
        if (type == 0) {
            Intent intent = new Intent(mContext, ScanActivity.class);
            intent.putExtra("title", "Scan");
            intent.putExtra("isSign", false);
            intent.putExtra("isAssignJob", "true");
            intent.putExtra("isViaAssign", "1");
            activity.startActivity(intent);
            activity.finish();
        } else if (type == 1) {
            Intent intent = new Intent(mContext, PhotoUploadActivity.class);
            intent.putExtra("title", "Snap");
            intent.putExtra("isSign", false);
            intent.putExtra("isViaAssign", "1");
            intent.putExtra("isAssignJob", "true");
            intent.putExtra("Str_Sts", "Snap");
            intent.putExtra("Str_Event", "Snap");
            intent.putExtra("snap", "true");
            activity.startActivity(intent);
            activity.finish();
        } else {
            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
            // String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
            String tripNumber = "";
            String URL = "";
            if (driverType.equalsIgnoreCase("Planner")) {
                String endDateStr = AssignJobActivity.btn_enddate.getText().toString().trim();
                if (endDateStr.contains("Min")) {
                    endDateStr = endDateStr.replaceAll("Min", "").trim();
                }
                String startDateStr = AssignJobActivity.btn_startdate.getText().toString().trim();
                DateFormat myformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                try {
                    Date startDate = myformat.parse(startDateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(cal.MINUTE, Integer.parseInt(endDateStr));
                    String EnddateValue = myformat.format(cal.getTime());
                    URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SCAN&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + "" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Loc=" + "NA" + "&Str_GPS=" + "OFF" + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + EnddateValue + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.btn_vehicle.getText().toString().trim() + "&Str_JobStatus=Un-Assigned"+"&Str_Customer=" + btn_customer_list.getText().toString().trim();
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
        /*Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

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

        dpd.setMinDate(now);

        dpd.show(getFragmentManager(), "Datepickerdialog");

        dpd.setOnDateSetListener(new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                Log.i("DATE-->", date);
                _year = year;
                _month = monthOfYear;
                _date = dayOfMonth;
                InitTimePicker(flag);
            }
        });*/

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
                        InitTimePicker(flag);
                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        //datePickerDialog.getDatePicker().setDisplayedValues( new String[] { "25-5-2010" ,"25-5-2011", "25-5-2012", "25-5-2013","25-5-2014","25-5-2015" } );

        datePickerDialog.show();

    }


    private void InitTimePicker(final int flag) {

        /*final Calendar now = Calendar.getInstance();


        TimePickerDialog tpd = TimePickerDialog.newInstance(
                null,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

      //  Toast.makeText(mContext,""+ _date+"=="+now.get(Calendar.DAY_OF_MONTH),Toast.LENGTH_SHORT).show();

        if(_date==now.get(Calendar.DAY_OF_MONTH)) {
            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
        }
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
                // tv_datetime.setText(UiUtils.changeDateFormateNOUTCObs(selectedDateTime));

                if (flag == 0) {

                    btn_startdate.setText(selectedDateTime);


                } else {
                    btn_enddate.setText(selectedDateTime);
                }
            }

        });



        tpd.show(getFragmentManager(), "TimePickerDialoge");*/

        final Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString = "00";
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
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
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
                    btn_vehicle.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
                } else if (FLAG == FLAG_SELECT_DURATION) {
                    btn_enddate.setText(arrayListImplemented.get(position).getListValue());
                } else if (FLAG == FLAG_SELECT_CUSTOMER) {
                    btn_customer_list.setText(arrayListImplemented.get(position).getListValueWithoutIndex());
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
                        btn_vehicle.setText(etSearch.getText().toString().trim());
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
                    AssignJobActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

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
        } else if (redirectionKey.equalsIgnoreCase("AddressAssignJob"))

        {
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


        } else if (redirectionKey.equalsIgnoreCase("locationlist1"))

        {
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
        } else if (redirectionKey.equalsIgnoreCase("customerList"))

        {
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
        } else if (redirectionKey.equalsIgnoreCase("createJob"))

        {
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
                    btn_vehicle.setText(mContext.getResources().getString(R.string.btn_select_vehicle));
                    tv_tracking_number.setText("");
                } else {
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
        APIUtils.sendRequest(mContext, "Location List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Loc", "locationlist1");
    }

    private void checkAvailblity(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);


        String endDateStr = btn_enddate.getText().toString().trim();
        if (endDateStr.contains("Min")) {
            endDateStr = endDateStr.replaceAll("Min", "").trim();
        }
        String startDateStr = btn_startdate.getText().toString().trim();
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
        }

    }

    private void getVehicle(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ResType = "Vehicle";
        if (driverType.equalsIgnoreCase("Planner")) {
            Str_ResType = "TrailerType";
        }

        APIUtils.sendRequest(mContext, "Location List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_ResType=" + Str_ResType, "locationlist1");

    }

    private void getCustomer(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ResType = "Customer";

        APIUtils.sendRequest(mContext, "Location List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_ResType=" + Str_ResType, "customerList");

    }


}
