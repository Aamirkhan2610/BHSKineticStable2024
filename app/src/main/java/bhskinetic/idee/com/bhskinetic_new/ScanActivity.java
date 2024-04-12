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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

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
import Model.ModelScanStatus;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Aamir on 4/19/2017.
 */

public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {
    public static ZXingScannerView mScannerView;
    public static TextView tv_header;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ListView list_scan_data;
    public static ListElementAdapter adapter;
    public static ArrayList<String> arrayScanResult;
    public static ModelScanStatus modelScanStatus;
    public static ArrayList<ModelScanStatus> arrayListScanStatus;
    public static Button btn_status;
    public static Button btn_exception;
    public static ModelException modelException;
    public static ArrayList<ModelException> arrayException;
    ArrayAdapter<String> adapterException;
    public static String ExceptionName = "NA";
    public static String StatusName = "NA";
    public static String PhotoJSON = "";
    public static ArrayAdapter<String> adapterstatus = null;
    public static boolean isAssignJob = false;
    public static String TripNumber = "", JobNo = "";
    public static String ScanCode="";
    public static ImageView imgFlash;
    public static boolean isFlashOn=false;
    public static String workgroup="";
    public static String isViaAssign="0";
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_scannerview);
        Init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(mContext, DashboardActivity.class);
        activity.startActivity(intent);
        activity.finish();
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, DashboardActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }


    private void Init() {
        isAssignJob = false;
        isFlashOn=false;
        mScannerView = (ZXingScannerView) findViewById(R.id.mScannerView);
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        btn_status = (Button) findViewById(R.id.btn_status);
        imgFlash=findViewById(R.id.img_flash);
        imgFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    isFlashOn=false;
                    imgFlash.setImageResource(R.drawable.ic_flash_off);
                    mScannerView.setFlash(false);
                }else{
                    isFlashOn=true;
                    imgFlash.setImageResource(R.drawable.ic_flash_on);
                    mScannerView.setFlash(true);
                }
            }
        });
        TripNumber = JobNo = "";
        ScanCode="";
        btn_exception = (Button) findViewById(R.id.btn_exception);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DashboardActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        mContext = ScanActivity.this;
        gps = new TrackGPS(mContext);
        activity = ScanActivity.this;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
            if (b.getString("isAssignJob") != null) {
                isAssignJob = Boolean.parseBoolean(b.getString("isAssignJob"));
            }

            if (b.getString("isViaAssign") != null) {
                isViaAssign = b.getString("isViaAssign");
            }

            if (b.getString("TripNumber") != null) {
                TripNumber = b.getString("TripNumber");
            }

            if (b.getString("JobNo") != null) {
                JobNo = b.getString("JobNo");
            }

            if (b.getString("workGroup") != null) {
                workgroup = b.getString("workGroup");
            }
        }
        //  TripNumber,JobNo

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });
        list_scan_data = (ListView) findViewById(R.id.list_scan_data);
        arrayScanResult = new ArrayList<>();


        //TEST RECORDS
        /*
         arrayScanResult.add("123456");
         arrayScanResult.add("123456");
         arrayScanResult.add("123456");*/

       //arrayScanResult.add("201210-853");

        adapter = new ListElementAdapter(mContext);
        list_scan_data.setAdapter(adapter);


        arrayListScanStatus = new ArrayList<>();
        arrayException = new ArrayList<>();

        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayScanResult.size() > 0) {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }

                    getStatusList("", ""+lat, ""+lng,"");
                    //APIUtils.getAddressFromLatLong(mContext, lat, lng, "ScanAddress");
                } else {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_scan_container), mContext);
                }
            }
        });

        btn_exception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayScanResult.size() == 0) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_scan_container), mContext);
                    return;
                }

                if (TripNumber.trim().length() > 0) {
                    submitOrderDetails("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");
                } else {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }
//                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "ScanAddressEx");

                    if (!isAssignJob) {
                        getExceptionList("", ""+lat, ""+lng, "");
                    } else {
                        if(isViaAssign.equalsIgnoreCase("1")){
                            createJobViaAssign("", ""+lat, ""+lng, "");
                        }else{
                            createJob("", ""+lat, ""+lng, "");
                        }

                    }
                }
            }
        });
        PhotoJSON = "";
        if (isAssignJob) {
            btn_exception.setText("SUBMIT");
            btn_status.setVisibility(View.GONE);
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutScan");
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
            return arrayScanResult.size();
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
                v = layoutInflater.inflate(R.layout.raw_scan_result, null);
                h = new ViewHolder();
                h.tv_scan_result = (TextView) v.findViewById(R.id.tv_scan_result);
                h.tv_scan_result.setText(arrayScanResult.get(position));
                h.layout_delete = (LinearLayout) v.findViewById(R.id.layout_delete);
                h.img_delete = (ImageView) v.findViewById(R.id.img_delete);
                h.layout_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayScanResult.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_scan_result.setText(arrayScanResult.get(position));
                h.layout_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayScanResult.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_scan_result;
            private ImageView img_delete;
            private LinearLayout layout_delete;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(ScanActivity.this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(); // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera(); // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.i("SCAN_RESULT", rawResult.getText()); // Prints scan results
        Log.i("SCAN_RESULT", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        if (!arrayScanResult.contains(rawResult.getText())) {
            ScanCode=rawResult.getText();
            checkBarecodeValidation("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF",rawResult.getText());
        }
        ScanActivity.mScannerView.resumeCameraPreview(this);
    }


    private void checkBarecodeValidation(String address, String lat, String lng, String gpsStatus,String barecodeNumber) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Scan Validation", "Chk_Barcode.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_JobNo="+JobNo+"&Str_Barcode="+barecodeNumber+"&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_worksite),mContext), "ValidateBarecode");
    }
    private void getStatusList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Scan Status List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Scan", "ScanStatus");
    }

    private void getExceptionList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Exception List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lng + "&Str_Long=" + lat + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Exception", "exceptionlistStatus");
    }

    private void submitScan(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String tripNumber = "";
        for (int i = 0; i < arrayScanResult.size(); i++) {
            if (tripNumber.trim().length() > 0) {
                tripNumber = tripNumber + "," + arrayScanResult.get(i);
            } else {
                tripNumber = arrayScanResult.get(i);
            }
        }


        APIUtils.sendRequest(mContext, "Submit Scan", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=ON&Str_DriverID=" + driverId + "&Str_JobView=MANIFEST&Str_JobStatus=" + StatusName + "&Str_TripNo=" + tripNumber + "&Str_JobNo=0&Str_JobExe=" + ExceptionName+"&Str_JobDate=" + DashboardActivity.tv_select_job_date.getText().toString().trim(), "SubmitScan");
    }

    private void submitOrderDetails(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String tripNumber = "";
        for (int i = 0; i < arrayScanResult.size(); i++) {
            if (tripNumber.trim().length() > 0) {
                tripNumber = tripNumber + "," + arrayScanResult.get(i);
            } else {
                tripNumber = arrayScanResult.get(i);
            }
        }


        APIUtils.sendRequest(mContext, "Submit Order Details", "Order_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_TripNo=" + tripNumber + "&Str_JobNo=" + JobNo + "&Str_Qty=" + arrayScanResult.size() + "&Str_Qty_Details=" + tripNumber + "&Str_JobFor=" + Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext), "SubmitOrderDetails");
    }


    private void createJobViaAssign(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
        String tripNumber = "";

        for (int i = 0; i < arrayScanResult.size(); i++) {
            if (tripNumber.trim().length() > 0) {
                tripNumber = tripNumber + "," + arrayScanResult.get(i);
            } else {
                tripNumber = arrayScanResult.get(i);
            }
        }


        if (URL.contains(" ")) {
            URL = URL.replace(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Create Job", URL, "createJob");
    }


    private void createJob(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        // String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
        String tripNumber = "";
        for (int i = 0; i < arrayScanResult.size(); i++) {
            if (tripNumber.trim().length() > 0) {
                tripNumber = tripNumber + "," + arrayScanResult.get(i);
            } else {
                tripNumber = arrayScanResult.get(i);
            }
        }

        String timeslot1= BookingJobActivity.timeSlotSelection.split("-")[0];
        String timeslot2= BookingJobActivity.timeSlotSelection.split("-")[1];

        String startDateStr = BookingJobActivity.selectedDat+" "+timeslot1+":00";

        String driverType = Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext);
        String URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SCAN&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + startDateStr+ "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat+" "+timeslot2+":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim()+ "&Str_WorkGroup=" + workgroup;

        if (driverType.equalsIgnoreCase("Planner") || driverType.equalsIgnoreCase("CS User")) {
            String endDateStr = BookingJobActivity.btn_duration.getText().toString().trim();
            if (endDateStr.contains("Min")) {
                endDateStr = endDateStr.replaceAll("Min", "").trim();
            }
            String _startDateStr = BookingJobActivity.selectedDat+" "+timeslot1+":00";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
            try {
                Date startDate = format.parse(_startDateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(cal.MINUTE, Integer.parseInt(endDateStr));
                String EnddateValue = format.format(cal.getTime());

                URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SCAN&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Customer=" + BookingJobActivity.btn_customer_list.getText().toString().trim() + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + _startDateStr + "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat+" "+timeslot2+":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim() + "&Str_JobStatus=Un-Assigned"+ "&Str_WorkGroup=" + workgroup;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (URL.contains(" ")) {
            URL = URL.replace(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Create Job", URL, "createJob");
    }


    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutScan")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    ScanActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
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
                        intent.putExtra("Str_Sts", "Create Job");
                        mContext.startActivity(intent);
                    } else {
                        if (isAssignJob) {
                            intent = new Intent(mContext, OrderList.class);
                            String driverType = Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext);
                            if (driverType.equalsIgnoreCase("Planner")) {
                                intent.putExtra("Str_JobStatus", "Un-Assigned");
                            } else {
                                intent.putExtra("Str_JobStatus", "Pending");
                            }
                            intent.putExtra("title", "ORDER LIST");
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                } else {
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ValidateBarecode")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                PhotoJSON = "";
                if (jsonObject.optString("MSG").equalsIgnoreCase("Barcode is not Valid")) {
                    Toast.makeText(mContext,jsonObject.optString("MSG"),Toast.LENGTH_SHORT).show();

                }else{
                    ScanActivity.arrayScanResult.add(ScanActivity.ScanCode);
                    ScanActivity.adapter.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ScanAddress")) {
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

                getStatusList(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ScanAddressSubmit")) {
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

                submitScan(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ScanAddressEx")) {
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

                if (!isAssignJob) {
                    getExceptionList(address, lat, lng, GPSStatus);
                } else {
                    createJob(address, lat, lng, GPSStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("exceptionlistStatus")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                PhotoJSON = "";
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");

                    PhotoJSON = exceptionlist.optJSONArray("list1").toString();

                    for (int i = 0; i < list.length(); i++) {

                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListID(value.optString("ListID"));
                        modelException.setListValue(((i + 1) + ".") + value.optString("ListValue"));
                        modelException.setListValueWithoutIndex(value.optString("ListValue"));
                        modelException.setTypeOfException(value.optString("TypeOfException"));
                        arrayException.add(modelException);
                    }

                    DialogueWithListException(arrayException);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("ScanStatus")) {
            PhotoJSON = "";
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayListScanStatus.clear();
                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject statusObject = list.getJSONObject(i);
                        modelScanStatus = new ModelScanStatus();
                        modelScanStatus.setListValue(((i + 1) + ".") + statusObject.optString("ListValue"));
                        modelScanStatus.setListValueWithoutIndex(statusObject.optString("ListValue"));
                        modelScanStatus.setListID(statusObject.optString("ListID"));
                        arrayListScanStatus.add(modelScanStatus);
                    }

                    DialogWithList(arrayListScanStatus);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("SubmitScan")) {
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
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        String driverType = Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext);
                        if (driverType.equalsIgnoreCase("Planner")) {
                            intent.putExtra("title", "UNASSIGNED");
                        } else {
                            intent.putExtra("title", StatusName);
                        }

                        intent.putExtra("Str_Event", "Photos");
                        intent.putExtra("Str_Sts", "Scan");
                        mContext.startActivity(intent);
                    } else {
                        if (isAssignJob) {
                            intent = new Intent(mContext, OrderList.class);
                            intent.putExtra("Str_JobStatus", "Pending");
                            intent.putExtra("title", "ORDER LIST");
                            activity.startActivity(intent);
                            activity.finish();
                        }else{
                            //Redirect To Movie Seat
                            intent = new Intent(mContext, MovieSeat.class);
                            intent.putExtra("Str_ResName", DashboardActivity.tv_container.getText().toString().trim());
                            intent.putExtra("StatusName", StatusName);
                            intent.putExtra("Str_JobNo", jobj.optString("JobNo"));
                            Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jobj.optString("Load"), mContext);
                            intent.putExtra("Str_SeqNo", arrayScanResult.get(0));
                            intent.putExtra("isFromOrderDetail", "1");
                            activity.startActivity(intent);
                        }
                    }


                    arrayScanResult.clear();
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("SubmitOrderDetails")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void DialogueWithListException(final ArrayList<ModelException> exceptionlist) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_exception_title));

        final ArrayList<ModelException> exceptionlistFiltered = new ArrayList<>();
        final ArrayList<ModelException> exceptionlistImplemented = new ArrayList<>();
        exceptionlistImplemented.addAll(exceptionlist);
        final ListView lv_resource = (ListView) dialoglist.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialoglist.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });

        EditText etSearch = (EditText) dialoglist.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                exceptionlistFiltered.clear();
                if (charText.length() == 0) {
                    exceptionlistFiltered.addAll(exceptionlist);
                } else {
                    for (ModelException wp : exceptionlist) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            exceptionlistFiltered.add(wp);
                        }

                    }

                }

                exceptionlistImplemented.clear();
                exceptionlistImplemented.addAll(exceptionlistFiltered);

                final String[] values = new String[exceptionlistImplemented.size()];
                for (int i = 0; i < exceptionlistImplemented.size(); i++) {
                    values[i] = exceptionlistImplemented.get(i).getListValue();
                }
                adapterException = new ArrayAdapter<String>(mContext,
                        R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterException);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final String[] values = new String[exceptionlistImplemented.size()];
        for (int i = 0; i < exceptionlistImplemented.size(); i++) {
            values[i] = exceptionlistImplemented.get(i).getListValue();
        }


        adapterException = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterException);



        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                builderInner.setMessage("DO YOU WANT TO ADD THIS EXCEPTION?");
                builderInner.setTitle("");
                builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialoglist.dismiss();
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }

                        ExceptionName = exceptionlistImplemented.get(position).getListValueWithoutIndex();
                        StatusName = "NA";

                       // submitScan("NA", ""+gps.getLatitude(),""+gps.getLongitude(),"NA");
                       APIUtils.getAddressFromLatLong(mContext, lat, lng, "ScanAddressSubmit");

                    }
                });


                builderInner.setNegativeButton(mContext.getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                if (exceptionlistImplemented.get(position).getTypeOfException().equalsIgnoreCase("0")) {
                    builderInner.show();
                } else {
                    RemarkDialogue(exceptionlistImplemented.get(position).getListValueWithoutIndex());
                    dialoglist.dismiss();
                }

            }
        });
        dialoglist.show();
    }

    public void RemarkDialogue(final String remark) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.layout_remark);
        dialoglist.setCancelable(false);
        final EditText edtRemark = (EditText) dialoglist.findViewById(R.id.edt_remark);
        Button btnSubmit = (Button) dialoglist.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                ExceptionName = remark + "~" + edtRemark.getText().toString().trim();
                StatusName = "NA";
                dialoglist.dismiss();
                APIUtils.getAddressFromLatLong(mContext, lat, lng, "ScanAddressSubmit");

            }
        });

        dialoglist.show();
    }

    public void DialogWithList(final ArrayList<ModelScanStatus> statuslist) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_status_title));

        final ListView lv_resource = (ListView) dialoglist.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialoglist.findViewById(R.id.img_close);
        EditText etSearch = (EditText) dialoglist.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        final ArrayList<ModelScanStatus> statuslistFiltered = new ArrayList<>();
        final ArrayList<ModelScanStatus> statuslistFilteredImplemented = new ArrayList<>();
        statuslistFilteredImplemented.addAll(statuslist);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                statuslistFiltered.clear();
                if (charText.length() == 0) {
                    statuslistFiltered.addAll(statuslist);
                } else {
                    for (ModelScanStatus wp : statuslist) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            statuslistFiltered.add(wp);
                        }

                    }

                }

                statuslistFilteredImplemented.clear();
                statuslistFilteredImplemented.addAll(statuslistFiltered);


                final String[] values = new String[statuslistFilteredImplemented.size()];
                for (int i = 0; i < statuslistFilteredImplemented.size(); i++) {
                    values[i] = statuslistFilteredImplemented.get(i).getListValue();
                }
                adapterstatus = new ArrayAdapter<String>(mContext, R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterstatus);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });


        final String[] values = new String[statuslistFilteredImplemented.size()];
        for (int i = 0; i < statuslistFilteredImplemented.size(); i++) {
            values[i] = statuslistFilteredImplemented.get(i).getListValue();
        }

        adapterstatus = new ArrayAdapter<String>(mContext, R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterstatus);
        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                builderInner.setMessage("DO YOU WANT TO UPDATE THIS STATUS?");
                builderInner.setTitle("");
                builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialoglist.dismiss();
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }

                        ExceptionName = "NA";
                        StatusName = statuslistFilteredImplemented.get(position).getListValueWithoutIndex();
                        APIUtils.getAddressFromLatLong(mContext, lat, lng, "ScanAddressSubmit");

                      //  submitScan("NA", ""+gps.getLatitude(),""+gps.getLongitude(),"NA");

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
        });

        dialoglist.show();
    }

}