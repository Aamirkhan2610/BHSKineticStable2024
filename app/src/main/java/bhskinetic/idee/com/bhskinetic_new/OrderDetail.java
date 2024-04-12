package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Model.ModelAttachment;
import Model.ModelException;
import Model.ModelOrderListInner;
import general.APIUtils;
import general.DatabaseHandler;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/13/2017.
 */

public class OrderDetail extends Activity {
    public static TextView tv_header;
    public static Context mContext;
    public static ListView list_order;
    public static ListElementAdapter adapter;
    public static ModelOrderListInner modelOrderListInner;
    public static ArrayList<ModelOrderListInner> ArrayOrderListInner;
    public static TrackGPS gps;
    public static ImageView img_refresh;
    public static String OrderInner = "";
    public static Bundle b;
    public static JSONArray ArrayInner;
    public static ImageView img_logout;
    public static Activity activity;
    public static String jobNumber = "";
    public static String TripNumber = "";
    public static CountDownTimer countDownTimer;
    public static ModelAttachment modelAttachment;
    public static ArrayList<ModelAttachment> ArrayAttachment;
    public static ModelException modelException;
    public static ArrayList<ModelException> arrayException;
    public static String Str_JobNo = "";
    public static String Str_JobStatus = "";
    public static String Str_JobStatusToCheck = "";
    public static String Str_JobExe = "";
    public static String Str_JobFor = "";
    public static String order_type = "";
    public static int selectedPos = 0;
    public static String ERP_No = "";
    public static String Str_OC = "";
    public static String Str_TC = "";
    public static String Viewed = "1";
    public static boolean isLockedJob = false;
    public static String Str_PickedUp_Box = "";
    public static String Str_Box_Detail = "";
    public static String PhotoJSON = "";
    public static ArrayAdapter<String> adapterException;
    ArrayAdapter<String> adapterAttachment;
    public static String StatusName = "";
    public static LinearLayout linearPIN;
    public static EditText edt1, edt2, edt3, edt4;
    public static Button btnSubmit;
    public static String PINNumber="";
    public static boolean isPhotoG=false, isSignG =false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orderlist);
        mContext = OrderDetail.this;
        StatusName = "";
        Init();
    }


    private void Init() {
        PhotoJSON = "";
        Viewed = "1";
        PINNumber="";
        isPhotoG=false;
        isSignG =false;
        tv_header = (TextView) findViewById(R.id.tv_header);
        tv_header.setText("ORDER DETAIL");
        list_order = (ListView) findViewById(R.id.list_order);


        OrderInner = "";
        ArrayOrderListInner = new ArrayList<>();
        gps = new TrackGPS(mContext);


        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppLocked()){
                    return;
                }
                redirectToOrderList();
            }
        });

        b = getIntent().getExtras();
        if (b != null) {
            OrderInner = b.getString("OrderInner");
            TripNumber = b.getString("Trip_No");
            Str_JobStatusToCheck = b.getString("Str_JobStatus");
            ERP_No = b.getString("ERP_No");
            Viewed = b.getString("Viewed");
        }
        try {

            JSONArray jsonArray = new JSONArray(OrderInner);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                modelOrderListInner = new ModelOrderListInner();
                modelOrderListInner.setJobNo(jsonObject.optString("JobNo"));
                modelOrderListInner.setDelivery_Loc(jsonObject.optString("Delivery_Loc"));
                modelOrderListInner.setQty(jsonObject.optString("qty"));
                modelOrderListInner.setStatus(jsonObject.optString("status"));
                modelOrderListInner.setButton(jsonObject.optString("Button"));
                modelOrderListInner.setName(jsonObject.optString("name"));
                modelOrderListInner.setvLat(jsonObject.optString("vLat"));
                modelOrderListInner.setvLon(jsonObject.optString("vLon"));
                modelOrderListInner.setpLat(jsonObject.optString("pLat"));
                modelOrderListInner.setpLon(jsonObject.optString("pLon"));
                modelOrderListInner.setMovie_Seats(jsonObject.optString("Movie_Seats"));
                modelOrderListInner.setMovie_Milestone(jsonObject.optString("Movie_Milestone"));
                modelOrderListInner.setdLat(jsonObject.optString("dLat"));
                modelOrderListInner.setdLon(jsonObject.optString("dLon"));
                modelOrderListInner.setvGPS(jsonObject.optString("vGPS"));

                modelOrderListInner.setOrd_Type(jsonObject.optString("Ord_Type"));
                modelOrderListInner.setAttch(jsonObject.optString("Attch"));
                modelOrderListInner.setvDlvry_Dt(jsonObject.optString("vDlvry_Dt"));
                if (jsonObject.optString("vDlvry_Tm").contains("_")) {
                    modelOrderListInner.setvTrip_Tm(jsonObject.optString("vDlvry_Tm").split("_")[1]);
                } else {
                    modelOrderListInner.setvTrip_Tm(jsonObject.optString("vDlvry_Tm"));
                }
                modelOrderListInner.setvContact_No(jsonObject.optString("vContact_No"));
                modelOrderListInner.setTrip_Cost(jsonObject.optString("Trip_Cost"));
                modelOrderListInner.setvAdd_Info(jsonObject.optString("vAdd_Info"));
                modelOrderListInner.setOther_Cost(jsonObject.optString("Other_Cost"));
                modelOrderListInner.setJobFor(jsonObject.optString("JobFor"));
                modelOrderListInner.setvRmks(jsonObject.optString("vRmks"));
                ArrayOrderListInner.add(modelOrderListInner);
            }

            adapter = new ListElementAdapter(mContext);
            list_order.setAdapter(adapter);

            if(b.getString("Lock_Timer")!=null){
                isLockedJob = true;
               StartCountDown();
            }else{
                isLockedJob = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        activity = OrderDetail.this;

        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppLocked()){
                    return;
                }

                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        ArrayAttachment = new ArrayList<>();
        arrayException = new ArrayList<>();


        Str_JobNo = "";
        Str_JobStatus = "";
        Str_JobExe = "";
        order_type = "";

        Str_TC = "0.0";
        Str_OC = "0.0";

        Str_PickedUp_Box = "";
        Str_Box_Detail = "";

        if (Viewed.equalsIgnoreCase("0")) {

            if (ArrayOrderListInner.size() > 0) {
                Str_JobNo = ArrayOrderListInner.get(0).getJobNo();
            }

            UpdateViewStatus("NA", "0", "0", "OFF");
        }


        linearPIN = findViewById(R.id.linear_pin);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);
        btnSubmit = findViewById(R.id.btn_submit);

        edt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt2.requestFocus();
            }
        });

        edt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt3.requestFocus();
            }
        });

        edt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt4.requestFocus();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppLocked()){
                    return;
                }

                if (edt1.getText().toString().trim().length() == 0 ||
                        edt2.getText().toString().trim().length() == 0 ||
                        edt3.getText().toString().trim().length() == 0 ||
                        edt4.getText().toString().trim().length() == 0
                ) {
                    Utils.Alert(getString(R.string.enter_pin),mContext);
                    return;
                }

                String enteredPIN=edt1.getText().toString().trim()+edt2.getText().toString().trim()+edt3.getText().toString().trim()
                        +edt4.getText().toString().trim();

                if(!enteredPIN.equalsIgnoreCase(PINNumber)){
                    Utils.Alert(getString(R.string.error_pin),mContext);
                    edt1.setText("");
                    edt2.setText("");
                    edt3.setText("");
                    edt4.setText("");
                    edt1.requestFocus();
                    return;
                }

                //Redirecting user to next screen after PIN verified
                Toast.makeText(mContext, getString(R.string.pin_veryfied), Toast.LENGTH_SHORT).show();
                Intent intent;
                if (isPhotoG) {
                    intent = new Intent(mContext, PhotoUploadActivity.class);
                    intent.putExtra("isSign", isSignG);
                    intent.putExtra("Str_JobNo", Str_JobNo);
                    intent.putExtra("Str_Sts", Str_JobStatus);
                    intent.putExtra("PhotoJSON", PhotoJSON);
                    intent.putExtra("Str_Event", "Photos");
                    intent.putExtra("Str_TripNo", TripNumber);

                    mContext.startActivity(intent);
                    OrderDetail.activity.finish();
                } else {
                    if (ArrayOrderListInner.get(selectedPos).getButton().equalsIgnoreCase("COMPLETED")) {
                        OrderDetail.activity.finish();
                    }
                }


            }
        });

        if(b==null){
            getAppLockData("NA", "0", "0", "OFF");
        }
    }

    public static void redirectToOrderList() {
        Intent intent = new Intent(activity, OrderList.class);
        intent.putExtra("Str_JobStatus", "Pending");
        intent.putExtra("title", "ORDER LIST");
        activity.startActivity(intent);
        activity.finish();
    }

    public static void redirectToDashboard() {
        Utils.clearTimerPreferences(mContext);
        Utils.setPref("app_lock","0",mContext);
        Intent intent=new Intent(mContext, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void StartCountDown() {
        int lock_time_minutes = Integer.parseInt(Utils.getPref("lock_time", mContext));
        long lock_timestamp = Long.parseLong(Utils.getPref("lock_timestamp", mContext));
        long current_timestamp = System.currentTimeMillis();

        long difftime = current_timestamp - lock_timestamp;  // Calculate difference directly in milliseconds
        long diffSeconds = difftime / 1000;  // Convert difference to seconds

        int remainingSeconds = (lock_time_minutes * 60) - (int)diffSeconds;  // Calculate remaining seconds

        //Toast.makeText(mContext,"Remaining Sec: "+remainingSeconds,Toast.LENGTH_SHORT).show();

      //  if(remainingSeconds<=0){
      //      redirectToOrderList();
      //      Utils.setPref("app_lock","0",mContext);
      //      Utils.clearTimerPreferences(mContext);
      //  }else {
            CountDown(remainingSeconds);
     //   }
    }

    public static void CountDown(int Seconds){
        countDownTimer = new CountDownTimer(Seconds* 1000+1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                Log.i("TIME : " , String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                tv_header.setText("Order Detail \n"+"App Locked: "+String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }
            public void onFinish() {
                Log.i("TIME : " ,"Finished");
               // if(!isAppIsInBackground(mContext)){
                    AlertYesNOTimer("TimeUp","Your allocated time for this job is finished, Do you want more time?",mContext);
              //  }else{
              //      Utils.setPref("app_lock","0",mContext);
              //      Utils.clearTimerPreferences(mContext);
              //      redirectToOrderList();
             //  }
            }
        };
        countDownTimer.start();
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void AlertYesNOTimer(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setCancelable(false);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.setPref("app_lock","1",mContext);
                Utils.setPref("lock_timestamp",""+System.currentTimeMillis(),mContext);
                StartCountDown();
            }
        });

        builderInner.setNegativeButton(mContext.getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_header.setText("Order Detail");
                Utils.setPref("app_lock","0",mContext);
                Utils.clearTimerPreferences(mContext);
                dialog.dismiss();
            }
        });
        builderInner.show();
    }


      public String getCurrentDateTime() {
        Calendar cc = Calendar.getInstance();
        String _year=""+cc.get(Calendar.YEAR);
        String _month=""+(cc.get(Calendar.MONTH)+1);
        if(_month.trim().length()==1){
            _month="0"+_month;
        }
        String _date = ""+cc.get(Calendar.DAY_OF_MONTH);
        if(_date.trim().length()==1){
            _date="0"+_date;
        }

        String Str_Date=_date+"-"+_month+"-"+_year;
        return Str_Date;
    }

    private void getAppLockData(String address, String lat, String lng, String gpsStatus) {
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String lock_job_id = Utils.getPref("lock_job_id", mContext);

         APIUtils.sendRequest(mContext, "App Lock", "Order_ListView.jsp?Str_ID="+ClientID+"&Str_DriverID="+DriverID+"&Str_JobView=View&Str_JobStatus=Pending&Str_TripNo=NA&Str_JobNo="+lock_job_id+"&Str_JobDate="+getCurrentDateTime(), "App_Lock");
    }

    private void UpdateViewStatus(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Update View Status", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + DriverID + "&Str_JobView=" + "Update" + "&Str_JobStatus=" + "Viewed" + "&Str_TripNo=NA&Str_JobNo=" + Str_JobNo + "&Str_JobFor=BHS", "JobViewUpdate");
    }


    public void showResponse(String response, String redirectionKey) {
        try {
            if (redirectionKey.equalsIgnoreCase("logoutOrderInner")) {

                try {
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("Status").equalsIgnoreCase("1")) {
                        Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(logoutIntent);
                        OrderDetail.activity.finish();
                        Utils.clearPref(mContext);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (redirectionKey.equalsIgnoreCase("JobViewUpdate")) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("recived").equalsIgnoreCase("1")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (redirectionKey.equalsIgnoreCase("OrderInnerAddress")) {
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

                getAttachmentList(address, lat, lng, GPSStatus);

            } else if (redirectionKey.equalsIgnoreCase("App_Lock")) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                     try {
                         JSONArray jsonArray = jsonObject.getJSONArray("list");

                         if(jsonArray.length()==0){
                             Toast.makeText(mContext,"No Data Available for this job id",Toast.LENGTH_SHORT).show();
                             redirectToDashboard();
                             return;
                         }

                         JSONObject innerObj = jsonArray.getJSONObject(0);
                         Intent intent = new Intent(mContext, OrderDetail.class);
                         intent.putExtra("OrderInner", "" + innerObj.optJSONArray("sublist"));
                         intent.putExtra("Trip_No", "" + innerObj.optString("Trip_No"));
                         intent.putExtra("Trip_No", "" + innerObj.optString("Trip_No"));
                         intent.putExtra("Lock_Timer", "1");
                         intent.putExtra("ERP_No", "" + innerObj.optString("ERP_No"));
                         intent.putExtra("Viewed", "" + innerObj.optString("Viewed"));
                         intent.putExtra("Str_JobStatus", "Pending");
                         activity.startActivity(intent);

                     }catch (Exception e){
                         e.printStackTrace();
                     }
                }



            }else if (redirectionKey.equalsIgnoreCase("AddressQTY")) {
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
                UpdaeQTY(address, lat, lng, GPSStatus);
            } else if (redirectionKey.equalsIgnoreCase("OrderListUpdateAddress")) {
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

                UpdateOrderPrice(address, lat, lng, GPSStatus);


            } else if (redirectionKey.equalsIgnoreCase("AddressUpdateStatus")) {
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

                UpdateStatus(address, lat, lng, GPSStatus);


            } else if (redirectionKey.equalsIgnoreCase("OrderInnerAddressException")) {
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

                if (order_type.equalsIgnoreCase("EX")) {
                    getExceptionList(address, lat, lng, GPSStatus);
                } else {
                    Str_JobExe = "";
                    UpdateStatus(address, lat, lng, GPSStatus);
                }


            } else if (redirectionKey.equalsIgnoreCase("attachmentlist")) {
                try {
                    ArrayAttachment.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray Data = jsonObject.optJSONArray("Data");
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject ojb0 = Data.getJSONObject(i);
                        JSONArray sublist = ojb0.optJSONArray("sublist");

                        for (int j = 0; j < sublist.length(); j++) {
                            JSONObject sublistObj = sublist.optJSONObject(j);
                            modelAttachment = new ModelAttachment();
                            modelAttachment.setFileName((j + 1) + "." + sublistObj.optString("FileName"));
                            modelAttachment.setFileNameWithoutIndexing(sublistObj.optString("FileName"));
                            modelAttachment.setStr_URL(sublistObj.optString("Str_URL"));
                            modelAttachment.setType(sublistObj.optString("Type"));
                            ArrayAttachment.add(modelAttachment);
                        }
                    }

                    DialogueWithList(ArrayAttachment);
                    Log.i("ATTACHMENT ARRAY SIZE", "==>" + ArrayAttachment.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (redirectionKey.equalsIgnoreCase("exceptionlist")) {
                PhotoJSON = "";
                JSONObject exceptionlist = new JSONObject(response);
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

            } else if (redirectionKey.equalsIgnoreCase("PMTRList")) {
                PhotoJSON = "";
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    if (exceptionlist.has("Ack_Msg")) {
                        Toast.makeText(mContext, exceptionlist.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray list = exceptionlist.optJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject value = list.optJSONObject(i);
                            modelException = new ModelException();
                            modelException.setListValue(((i + 1) + ".") + value.optString("Resource_Name"));
                            modelException.setListValueWithoutIndex(value.optString("Resource_Name"));
                            arrayException.add(modelException);
                        }
                        DialogueWithListException(arrayException);
                    }
                }

            } else if (redirectionKey.equalsIgnoreCase("UpdateJobStatus")) {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    ArrayOrderListInner.get(selectedPos).setStatus(jobj.optString("Status"));
                    ArrayOrderListInner.get(selectedPos).setButton(jobj.optString("Button"));
                    adapter.notifyDataSetChanged();

                    //StatusName="UNLOADING";

                  //  Toast.makeText(mContext,">>>>"+StatusName,Toast.LENGTH_SHORT).show();


                        if (ArrayOrderListInner.get(selectedPos).getMovie_Seats().equalsIgnoreCase("1") &&
                           ArrayOrderListInner.get(selectedPos).getMovie_Milestone().contains(StatusName)) {
                            Intent intent = new Intent(mContext, MovieSeat.class);
                            intent.putExtra("Str_ResName", DashboardActivity.tv_container.getText().toString().trim());
                            intent.putExtra("StatusName", StatusName);
                            intent.putExtra("Str_JobNo", Str_JobNo);
                            Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jobj.optString("Load"), mContext);
                            intent.putExtra("Str_SeqNo", jobNumber);
                            intent.putExtra("isFromOrderDetail", "1");
                            activity.startActivity(intent);

                            return;

                    }

                    if (StatusName.equalsIgnoreCase("PICKED UP") || StatusName.equalsIgnoreCase("DELIVERED")) {
                       /* boolean isAppLock = false;
                        if (Utils.getPref("app_lock", mContext) == null) {
                            isAppLock = false;
                        }else if (Utils.getPref("app_lock", mContext).equalsIgnoreCase("1")) {
                            isAppLock = true;
                        }else{
                            isAppLock = false;
                        }

                        if(isAppLock) {
                            redirectToDashboard();
                            return;
                        }*/

                        if(isLockedJob) {

                            if(countDownTimer!=null){
                                countDownTimer.cancel();
                            }

                            redirectToDashboard();
                            return;
                        }
                    }

                    String Ack_Msg = jobj.optString("POD");
                    // "POD": "P|S|N|N|0000"   3698
                    String[] ackArray = Ack_Msg.split("\\|");

                    boolean isPhoto = false;
                    boolean isSign = false;
                    boolean isPIN = false;
                    String serverPINNumber = "";
                    //Checking if customer has permission to upload photo
                    if (ackArray[0].equalsIgnoreCase("P")) {
                        isPhoto = true;
                    } else {
                        isPhoto = false;
                    }
                    //1452
                    //Checking if customer has permission to upload signature
                    if (ackArray[1].equalsIgnoreCase("S")) {
                        isSign = true;
                    } else {
                        isSign = false;
                    }

                    //Checking if customer has permission to enter 4 digit PIN
                    if (ackArray[3].equalsIgnoreCase("P")) {
                        isPIN = true;
                        serverPINNumber = ackArray[4];
                    } else {
                        isPIN = false;
                    }

                    if (!isPIN) {
                        Intent intent;
                        if (isPhoto) {
                            intent = new Intent(mContext, PhotoUploadActivity.class);
                            intent.putExtra("isSign", isSign);
                            intent.putExtra("Str_JobNo", Str_JobNo);
                            intent.putExtra("Str_Sts", Str_JobStatus);
                            intent.putExtra("PhotoJSON", PhotoJSON);
                            intent.putExtra("Str_Event", "Photos");
                            intent.putExtra("Str_TripNo", TripNumber);
                            mContext.startActivity(intent);
                            OrderDetail.activity.finish();
                        } else {
                            if (ArrayOrderListInner.get(selectedPos).getButton().equalsIgnoreCase("COMPLETED")) {
                                OrderDetail.activity.finish();
                            }
                        }
                    } else {
                        PINNumber= serverPINNumber;
                        isPhotoG=isPhoto;
                        isSignG =isSign;
                        //Enabling PIN entering screen if PIN is required
                        linearPIN.setVisibility(View.VISIBLE);
                        list_order.setVisibility(View.GONE);
                        img_refresh.setVisibility(View.GONE);
                    }


                }


            } else if (redirectionKey.equalsIgnoreCase("updateOrderPrice")) {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    ArrayOrderListInner.get(selectedPos).setStatus(jobj.optString("Status"));
                    ArrayOrderListInner.get(selectedPos).setButton(jobj.optString("Button"));
                    ArrayOrderListInner.get(selectedPos).setTrip_Cost(Str_TC);
                    ArrayOrderListInner.get(selectedPos).setOther_Cost(Str_OC);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (redirectionKey.equalsIgnoreCase("UpdateQTY")) {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void UpdateStatus(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_JobView=Update&Str_JobStatus=" + Str_JobStatus + "&Str_TripNo=" + TripNumber + "&Str_JobNo=" + jobNumber + "&Str_JobExe=" + Str_JobExe + "&Str_JobFor=" + Str_JobFor;
        URL = URL.replaceAll(" ", "%20");
        APIUtils.sendRequest(mContext, "Update Job Status", URL, "UpdateJobStatus");
    }

    public void DialogueWithList(final ArrayList<ModelAttachment> attachmentList) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle(mContext.getResources().getString(R.string.alert_attachment_title));
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        final ArrayList<ModelAttachment> attachmentListFiltered = new ArrayList<>();
        final ArrayList<ModelAttachment> attachmentListImplemented = new ArrayList<>();
        attachmentListImplemented.addAll(attachmentList);


        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                attachmentListFiltered.clear();
                if (charText.length() == 0) {
                    attachmentListFiltered.addAll(attachmentList);
                } else {
                    for (ModelAttachment wp : attachmentList) {
                        if ((wp.getFileName().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            attachmentListFiltered.add(wp);
                        }

                    }

                }

                attachmentListImplemented.clear();
                attachmentListImplemented.addAll(attachmentListFiltered);

                final String[] values = new String[attachmentListImplemented.size()];
                for (int i = 0; i < attachmentListImplemented.size(); i++) {
                    values[i] = attachmentListImplemented.get(i).getFileName();
                }
                adapterAttachment = new ArrayAdapter<String>(mContext,
                        R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterAttachment);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[attachmentListImplemented.size()];
        for (int i = 0; i < attachmentListImplemented.size(); i++) {
            values[i] = attachmentListImplemented.get(i).getFileName();
        }
        adapterAttachment = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterAttachment);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (attachmentListImplemented.get(position).getType().equalsIgnoreCase("File")) {
                    Intent intent = new Intent(view.getContext(), WebViewActivitiy.class);
                    intent.putExtra("DocumentURL", attachmentListImplemented.get(position).getStr_URL());
                    intent.putExtra("DocumentTitle", attachmentListImplemented.get(position).getFileNameWithoutIndexing());
                    mContext.startActivity(intent);
                    dialog.dismiss();
                } else {
                    Intent intent = new Intent(view.getContext(), ImageViewActivitiy.class);
                    intent.putExtra("DocumentURL", attachmentListImplemented.get(position).getStr_URL());
                    intent.putExtra("DocumentTitle", attachmentListImplemented.get(position).getFileNameWithoutIndexing());
                    mContext.startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public static void DialogueQty(final int Qty, final String jobNo) {
        // custom dialog
        final Dialog dialogbox = new Dialog(mContext);
        dialogbox.setContentView(R.layout.raw_qty);
        dialogbox.setCancelable(false);
        dialogbox.setTitle("ALLOCATED BOX " + Qty);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialogbox.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox.dismiss();
            }
        });

        final EditText edt_pickedup_box = (EditText) dialogbox.findViewById(R.id.edt_pickedup_box);
        final EditText edt_box_detail = (EditText) dialogbox.findViewById(R.id.edt_box_detail);
        Button btn_submit = (Button) dialogbox.findViewById(R.id.btn_submit);
        Button btn_scan = (Button) dialogbox.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ScanActivity.class);
                intent.putExtra("title", "Scan");
                intent.putExtra("isAssignJob", "true");
                intent.putExtra("TripNumber", TripNumber);
                intent.putExtra("JobNo", jobNo);
                activity.startActivity(intent);
                dialogbox.dismiss();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_pickedup_box.getText().toString().trim().length() == 0) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_qty_pickupbox_number), mContext);
                    return;
                }

                if (edt_box_detail.getText().toString().trim().length() == 0) {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_qty_boxdetail), mContext);
                    return;
                }

                int enteredQty = Integer.parseInt(edt_pickedup_box.getText().toString().trim());
                if (enteredQty != Qty) {
                    final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                    builderInner.setMessage("NOT MATCHED WITH ALLOCATED LIMIT, DO YOU WANT TO PROCCED?");
                    builderInner.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogbox.dismiss();
                            Str_PickedUp_Box = "";
                            Str_Box_Detail = "";
                            Str_PickedUp_Box = edt_pickedup_box.getText().toString().trim();
                            Str_Box_Detail = edt_box_detail.getText().toString().trim();

                            double lat = gps.getLatitude();
                            double lng = gps.getLongitude();
                            if (!gps.canGetLocation()) {
                                lat = 0;
                                lng = 0;
                            }

                            APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressQTY");
                        }
                    });

                    builderInner.setNegativeButton(mContext.getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderInner.show();
                } else {
                    dialogbox.dismiss();
                    Str_PickedUp_Box = "";
                    Str_Box_Detail = "";
                    Str_PickedUp_Box = edt_pickedup_box.getText().toString().trim();
                    Str_Box_Detail = edt_box_detail.getText().toString().trim();

                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }

                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressQTY");
                }
            }
        });

        dialogbox.show();
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
                if (edtRemark.getText().toString().trim().length() == 0) {
                    Utils.Alert("ENTER EXCEPTION", mContext);
                    return;
                }
                dialoglist.dismiss();
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }
                Str_JobExe = remark + "~" + edtRemark.getText().toString().trim();
                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressUpdateStatus");
            }
        });

        dialoglist.show();
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
                if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    if (order_type.equalsIgnoreCase("TR")) {
                        builderInner.setMessage("DO YOU WANT TO ADD THIS CONTAINER?");
                    } else {
                        builderInner.setMessage("DO YOU WANT TO ADD THIS VEHICLE?");

                    }
                } else {
                    builderInner.setMessage("DO YOU WANT TO ADD THIS EXCEPTION?");
                }

                builderInner.setTitle("");
                builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialoglist.dismiss();
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();

                        String GPStatus = "ON";
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                            GPStatus = "OFF";
                        }

                        Str_JobExe = exceptionlistImplemented.get(position).getListValueWithoutIndex();
                        if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {

                            String ResType = "VEHICLE";
                            if (order_type.equalsIgnoreCase("TR")) {
                                ResType = "CONTAINER";
                            }

                            getPMTRList("NA", "" + lat, "" + lng, GPStatus, ResType, Str_JobExe, "ASSIGN");
                        } else {
                            APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressUpdateStatus");
                        }


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

    @Override
    protected void onResume() {
        super.onResume();
        //  Toast.makeText(mContext,Utils.ScanResult,Toast.LENGTH_SHORT).show();
    }

    private void getAttachmentList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ProjName = Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext);
        APIUtils.sendRequest(mContext, "Attachment List", "Attc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_FileType=Attachment&Str_TripNo=" + TripNumber + "&Str_JobNo=" + jobNumber + "&Str_ProjName=" + Str_ProjName, "attachmentlist");
    }

    private void UpdaeQTY(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Order_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_TripNo=" + TripNumber + "&Str_JobNo=" + Str_JobNo + "&Str_Qty=" + Str_PickedUp_Box + "&Str_Qty_Details=" + Str_Box_Detail + "&Str_JobFor=" + Str_JobFor;
        APIUtils.sendRequest(mContext, "Update QTY", URL, "UpdateQTY");
    }

    private void getExceptionList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Exception List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lng + "&Str_Long=" + lat + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Exception", "exceptionlist");
    }

    public static void getPMTRList(String address, String lat, String lng, String gpsStatus, String ResType, String ResName, String Str_Event) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "PM/TR List", "Res_Assign.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + Str_Event + "&Str_JobNo=" + Str_JobNo + "&Str_ResType=" + ResType + "&Str_ResName=" + ResName + "&Str_JobFor=BHS", "PMTRList");
    }

    private void UpdateOrderPrice(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Update Order Price", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_JobView=Update&Str_JobStatus=Charges&Str_TripNo=" + TripNumber + "&Str_JobNo=" + Str_JobNo + "&Str_TC=" + Str_TC + "&Str_OC=" + Str_OC, "updateOrderPrice");
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutOrderInner");
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

    public static boolean isAppLocked(){
        boolean isAppLock = false;

        if (Utils.getPref("app_lock", mContext) == null) {
            isAppLock = false;
        }else if (Utils.getPref("app_lock", mContext).equalsIgnoreCase("1")) {
            isAppLock = true;
        }else{
            isAppLock = false;
        }

        return  isAppLock;

    }


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
            return ArrayOrderListInner.size();
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
                v = layoutInflater.inflate(R.layout.raw_orderlist_inner, null);
                h = new ViewHolder();
                h.tv_name = (TextView) v.findViewById(R.id.tv_name);
                h.tv_contact = (TextView) v.findViewById(R.id.tv_contact);
                h.tv_remark = (TextView) v.findViewById(R.id.tv_remark);
                h.tv_qty = (TextView) v.findViewById(R.id.tv_qty);
                h.tv_location = (TextView) v.findViewById(R.id.tv_location);
                h.btn_status = (Button) v.findViewById(R.id.btn_status);
                h.img_cost = (ImageView) v.findViewById(R.id.img_cost);
                h.linear_color = (LinearLayout) v.findViewById(R.id.linear_color);
                h.rel_color = (RelativeLayout) v.findViewById(R.id.rel_color);
                h.tv_date = (TextView) v.findViewById(R.id.tv_date);
                h.tv_time = (TextView) v.findViewById(R.id.tv_time);
                h.rel_datetime = (RelativeLayout) v.findViewById(R.id.rel_datetime);
                h.btn_exception = (Button) v.findViewById(R.id.btn_exception);
                h.img_message = (ImageView) v.findViewById(R.id.img_message);
                h.img_snap = (ImageView) v.findViewById(R.id.img_snap);
                h.img_location = (ImageView) v.findViewById(R.id.img_location);
                h.img_add_qty = (ImageView) v.findViewById(R.id.img_add_qty);
                h.img_more_detail = (ImageView) v.findViewById(R.id.img_more_detail);
                h.img_attachment = (ImageView) v.findViewById(R.id.img_attachment);
                h.layout_pending = (LinearLayout) v.findViewById(R.id.layout_pending);
                h.linear_remark = (LinearLayout) v.findViewById(R.id.linear_remark);
                h.linear_additional = (LinearLayout) v.findViewById(R.id.linear_additional);
                h.tv_additional_info = (TextView) v.findViewById(R.id.tv_additional_info);
                h.layout_location = (LinearLayout) v.findViewById(R.id.layout_location);
                h.tv_status = (TextView) v.findViewById(R.id.tv_status);
                h.tv_erp = (TextView) v.findViewById(R.id.tv_erp);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);
                if (ArrayOrderListInner.get(position).getAttch().equalsIgnoreCase("Y")) {
                    h.img_attachment.setVisibility(View.VISIBLE);
                } else {
                    h.img_attachment.setVisibility(View.GONE);
                }

                h.tv_name.setText(ArrayOrderListInner.get(position).getName());
                h.tv_contact.setText(Html.fromHtml(ArrayOrderListInner.get(position).getvContact_No()));
                h.tv_remark.setText(ArrayOrderListInner.get(position).getvRmks());
                String[] location = ArrayOrderListInner.get(position).getDelivery_Loc().split("To");

                h.tv_location.setText("From: " + location[0] + "\n" + "To:" + location[1]);
                h.btn_status.setText(ArrayOrderListInner.get(position).getButton());


                if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.tv_status.setText("UNASSIGNED");
                } else {
                    h.tv_status.setText(ArrayOrderListInner.get(position).getStatus());
                }

                h.img_more_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(mContext, JobDetailWithPhoto.class);
                        intent.putExtra("title", "Job Detail");
                        intent.putExtra("JobNo", ArrayOrderListInner.get(position).getJobNo());
                        intent.putExtra("Str_JobStatusToCheck", Str_JobStatusToCheck);
                        mContext.startActivity(intent);
                    }
                });
                h.tv_erp.setText(ERP_No);
                h.tv_date.setText(ArrayOrderListInner.get(position).getvDlvry_Dt());
                h.tv_qty.setText(ArrayOrderListInner.get(position).getQty());
                h.tv_additional_info.setText(ArrayOrderListInner.get(position).getvAdd_Info());
                h.tv_time.setText(ArrayOrderListInner.get(position).getvTrip_Tm());
                h.tv_index.setText((position + 1) + ".");
                if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.rel_datetime.setVisibility(View.VISIBLE);
                } else {
                    h.rel_datetime.setVisibility(View.GONE);
                }
                if (ArrayOrderListInner.get(position).getButton().equalsIgnoreCase("NA")) {
                    h.btn_status.setVisibility(View.GONE);
                } else {
                    h.btn_status.setVisibility(View.VISIBLE);
                }


                if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.btn_exception.setText("ASSIGNED PM");
                    h.btn_status.setVisibility(View.VISIBLE);
                    h.btn_status.setText("ASSIGNED TR");
                }


                h.img_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isAppLocked()){
                            return;
                        }

                        if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                            String lat = "0";
                            String lng = "0";
                            String dlat = ArrayOrderListInner.get(position).getdLat();
                            String dlng = ArrayOrderListInner.get(position).getdLon();

                            String plat = ArrayOrderListInner.get(position).getpLat();
                            String plng = ArrayOrderListInner.get(position).getpLon();

                            String vlat = ArrayOrderListInner.get(position).getvLat();
                            String vlng = ArrayOrderListInner.get(position).getvLon();

                            if (gps.canGetLocation()) {
                                lat = "" + gps.getLatitude();
                                lng = "" + gps.getLongitude();
                            }


                            String mapURL = "http://maps.google.com/maps?saddr=" + plat + "," + plng + "&daddr=" + dlat + "," + dlng + "";

                            if (ArrayOrderListInner.get(position).getvGPS().equalsIgnoreCase("Y")) {
                                mapURL = "http://maps.google.com/maps?saddr=" + plat + "," + plng + "&daddr=" + dlat + "," + dlng + "" + "&waypoints=" + vlat + "," + vlng + "&travelmode=driving";
                            }

                            //  mapURL="https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving";

                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(mapURL));

                            mContext.startActivity(intent);
                        }
                    }
                });

                h.img_attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isAppLocked()){
                            return;
                        }
                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                                if (ArrayOrderListInner.get(position).getAttch().equalsIgnoreCase("Y")) {

                                    double lat = gps.getLatitude();
                                    double lng = gps.getLongitude();
                                    if (!gps.canGetLocation()) {
                                        lat = 0;
                                        lng = 0;
                                    }
                                    jobNumber = ArrayOrderListInner.get(position).getJobNo();
                                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddress");
                                }
                            }
                        }
                    }
                });

                h.btn_exception.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if(isAppLocked()){
                            return;
                        }*/

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                String GPSStauts = "ON";
                                order_type = "PM";
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                    GPSStauts = "OFF";
                                }
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                getPMTRList("NA", "" + lat, "" + lng, GPSStauts, "VEHICLE", "NA", "CHECK");
                            } else {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                }
                                jobNumber = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobStatus = "EXCEPTION";
                                Str_JobExe = "";
                                order_type = "EX";
                                selectedPos = position;
                                APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressException");
                            }
                        }
                    }
                });


                final ViewHolder finalH2 = h;
                final ViewHolder finalH4 = h;
                final ViewHolder finalH5 = h;
                final ViewHolder finalH8 = h;
                final ViewHolder finalH9 = h;
                h.btn_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if(isAppLocked()){
                            return;
                        }*/

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                order_type = "TR";
                                String GPSStauts = "ON";
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                    GPSStauts = "OFF";
                                }
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                getPMTRList("NA", "" + lat, "" + lng, GPSStauts, "CONTAINER", "NA", "CHECK");
                            } else {
                                if ((finalH9.btn_status.getText().toString().trim().equalsIgnoreCase("LOADING") || finalH9.btn_status.getText().toString().trim().equalsIgnoreCase("UNLOADING"))) {

                                    if(DashboardActivity.tv_container!=null) {
                                        if (DashboardActivity.tv_container.getText().toString().trim().length() == 0) {
                                            Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_loading), mContext);

                                            return;
                                        }
                                    }
                                }

                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                }
                                jobNumber = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobStatus = ArrayOrderListInner.get(position).getButton();
                                Str_JobExe = "NA";
                                order_type = "NOEX";
                                selectedPos = position;
                                StatusName = finalH9.btn_status.getText().toString().trim();
                                APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressException");

                            }
                        }
                    }
                });

                if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.layout_pending.setVisibility(View.VISIBLE);
                    h.linear_color.setBackgroundResource(R.drawable.bg_orderlist_inner);
                    h.rel_color.setBackgroundResource(0);
                    h.img_cost.setVisibility(View.GONE);
                    h.tv_contact.setVisibility(View.VISIBLE);
                    h.linear_remark.setVisibility(View.VISIBLE);
                    h.tv_status.setVisibility(View.VISIBLE);
                    h.linear_additional.setVisibility(View.VISIBLE);
                    h.tv_additional_info.setVisibility(View.VISIBLE);
                } else {
                    h.layout_pending.setVisibility(View.GONE);
                    h.img_cost.setVisibility(View.VISIBLE);
                    h.tv_contact.setVisibility(View.GONE);
                    h.tv_status.setVisibility(View.GONE);
                    h.linear_color.setBackgroundResource(0);
                    h.rel_color.setBackgroundResource(R.drawable.bg_orderlist);
                    h.linear_remark.setVisibility(View.GONE);
                    h.linear_additional.setVisibility(View.GONE);
                    h.tv_additional_info.setVisibility(View.GONE);
                }
                if (Str_JobStatusToCheck.equalsIgnoreCase("Completed") || Str_JobStatusToCheck.equalsIgnoreCase("Exception")) {
                    h.img_more_detail.setVisibility(View.VISIBLE);
                }


                h.img_more_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isAppLocked()){
                            return;
                        }

                        if (Str_JobStatusToCheck.equalsIgnoreCase("Completed") || Str_JobStatusToCheck.equalsIgnoreCase("Exception")) {
                            Intent intent = new Intent(mContext, JobDetailWithPhoto.class);
                            intent.putExtra("title", "Job Detail");
                            intent.putExtra("Str_JobStatusToCheck", Str_JobStatusToCheck);
                            intent.putExtra("JobNo", ArrayOrderListInner.get(position).getJobNo());
                            mContext.startActivity(intent);
                        }

                    }
                });


                final ViewHolder finalH = h;
                h.tv_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + ArrayOrderListInner.get(position).getvContact_No()));
                        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mContext.startActivity(callIntent);
                    }
                });

                h.img_cost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(v.getContext(), DriverIncentiveActivity.class);
                        intent.putExtra("Str_TripNo", TripNumber);
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        mContext.startActivity(intent);
                    }
                });

                h.img_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(v.getContext(), MessageNotificationTab.class);
                        intent.putExtra("Str_TripNo", TripNumber);
                        intent.putExtra("CurrentTab", 1);
                        intent.putExtra("Stry_Msg_Type", "CS");
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        intent.putExtra("Str_MsgID", "0");
                        intent.putExtra("Str_SentTo", "CS");
                        mContext.startActivity(intent);
                    }
                });

                final ViewHolder finalH1 = h;
                h.img_snap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(mContext, PhotoUploadActivity.class);
                        intent.putExtra("isSign", false);
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        intent.putExtra("Str_Sts", finalH1.btn_status.getText().toString().trim());
                        intent.putExtra("Str_TripNo", TripNumber);
                        intent.putExtra("Str_Event", "Photos");
                        mContext.startActivity(intent);
                    }
                });

                h.img_add_qty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                            Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                            DialogueQty(Integer.parseInt(ArrayOrderListInner.get(position).getQty()), Str_JobNo);
                        }
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_name.setText(ArrayOrderListInner.get(position).getName());
                h.tv_contact.setText(Html.fromHtml(ArrayOrderListInner.get(position).getvContact_No()));
                h.tv_remark.setText(ArrayOrderListInner.get(position).getvRmks());
                h.tv_qty.setText(ArrayOrderListInner.get(position).getQty());
                h.tv_additional_info.setText(ArrayOrderListInner.get(position).getvAdd_Info());
                h.btn_status.setText(ArrayOrderListInner.get(position).getButton());


                h.tv_index.setText((position + 1) + ".");
                if (ArrayOrderListInner.get(position).getButton().equalsIgnoreCase("NA")) {
                    h.btn_status.setVisibility(View.GONE);
                } else {
                    h.btn_status.setVisibility(View.VISIBLE);
                }

                if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.btn_exception.setText("ASSIGNED PM");
                    h.btn_status.setVisibility(View.VISIBLE);
                    h.btn_status.setText("ASSIGNED TR");
                }

                String[] location = ArrayOrderListInner.get(position).getDelivery_Loc().split("To");

                h.tv_location.setText("From: " + location[0] + "\n" + "To:" + location[1]);

                if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.tv_status.setText("UNASSIGNED");
                } else {
                    h.tv_status.setText(ArrayOrderListInner.get(position).getStatus());
                }

                h.tv_erp.setText(ERP_No);
                h.tv_date.setText(ArrayOrderListInner.get(position).getvDlvry_Dt());
                h.tv_time.setText(ArrayOrderListInner.get(position).getvTrip_Tm());
                if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.rel_datetime.setVisibility(View.VISIBLE);
                } else {
                    h.rel_datetime.setVisibility(View.GONE);
                }

                final ViewHolder finalH = h;
                h.tv_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + ArrayOrderListInner.get(position).getvContact_No()));
                        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mContext.startActivity(callIntent);
                    }
                });

                h.img_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                            String lat = "0";
                            String lng = "0";
                            String dlat = ArrayOrderListInner.get(position).getdLat();
                            String dlng = ArrayOrderListInner.get(position).getdLon();

                            String plat = ArrayOrderListInner.get(position).getpLat();
                            String plng = ArrayOrderListInner.get(position).getpLon();

                            String vlat = ArrayOrderListInner.get(position).getvLat();
                            String vlng = ArrayOrderListInner.get(position).getvLon();

                            if (gps.canGetLocation()) {
                                lat = "" + gps.getLatitude();
                                lng = "" + gps.getLongitude();
                            }


                            String mapURL = "http://maps.google.com/maps?saddr=" + plat + "," + plng + "&daddr=" + dlat + "," + dlng + "";

                            if (ArrayOrderListInner.get(position).getvGPS().equalsIgnoreCase("Y")) {
                                mapURL = "http://maps.google.com/maps?saddr=" + plat + "," + plng + "&daddr=" + dlat + "," + dlng + "" + "&waypoints=" + vlat + "," + vlng + "&travelmode=driving";
                            }

                            //  mapURL="https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving";

                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(mapURL));

                            mContext.startActivity(intent);
                        }
                    }
                });

                if (ArrayOrderListInner.get(position).getAttch().equalsIgnoreCase("Y")) {
                    h.img_attachment.setVisibility(View.VISIBLE);
                } else {
                    h.img_attachment.setVisibility(View.GONE);
                }

                h.btn_exception.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if(isAppLocked()){
                            return;
                        }*/

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {

                            if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                order_type = "PM";
                                String GPSStauts = "ON";
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                    GPSStauts = "OFF";
                                }
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                getPMTRList("NA", "" + lat, "" + lng, GPSStauts, "VEHICLE", "NA", "CHECK");
                            } else {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                }
                                Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                                jobNumber = ArrayOrderListInner.get(position).getJobNo();
                                selectedPos = position;
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobStatus = "EXCEPTION";
                                Str_JobExe = "";
                                order_type = "EX";
                                APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressException");
                            }
                        }
                    }
                });

                final ViewHolder finalH3 = h;
                final ViewHolder finalH6 = h;
                final ViewHolder finalH7 = h;
                h.btn_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if(isAppLocked()){
                            return;
                        }*/

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            if (Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                order_type = "TR";
                                String GPSStauts = "ON";
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                    GPSStauts = "OFF";
                                }
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                getPMTRList("NA", "" + lat, "" + lng, GPSStauts, "CONTAINER", "NA", "CHECK");
                            } else {
                                if ((finalH7.btn_status.getText().toString().trim().equalsIgnoreCase("LOADING") || finalH7.btn_status.getText().toString().trim().equalsIgnoreCase("UNLOADING"))) {
                                    if(DashboardActivity.tv_container!=null) {
                                        if (DashboardActivity.tv_container.getText().toString().trim().length() == 0) {
                                            Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_loading), mContext);

                                            return;
                                        }
                                    }
                                }

                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                if (!gps.canGetLocation()) {
                                    lat = 0;
                                    lng = 0;
                                }
                                jobNumber = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                                Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                                Str_JobStatus = ArrayOrderListInner.get(position).getButton();
                                Str_JobExe = "NA";
                                order_type = "NOEX";
                                selectedPos = position;
                                StatusName = finalH6.btn_status.getText().toString().trim();
                                APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressException");

                            }

                        }
                    }
                });

                h.img_cost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(v.getContext(), DriverIncentiveActivity.class);
                        intent.putExtra("Str_TripNo", TripNumber);
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        mContext.startActivity(intent);

                    }
                });

                if (Str_JobStatusToCheck.equalsIgnoreCase("Pending") || Str_JobStatusToCheck.equalsIgnoreCase("Un-Assigned")) {
                    h.layout_pending.setVisibility(View.VISIBLE);
                    h.linear_color.setBackgroundResource(R.drawable.bg_orderlist_inner);
                    h.rel_color.setBackgroundResource(0);
                    h.img_cost.setVisibility(View.GONE);
                    h.tv_contact.setVisibility(View.VISIBLE);
                    h.linear_remark.setVisibility(View.VISIBLE);
                    h.tv_status.setVisibility(View.VISIBLE);
                    h.linear_additional.setVisibility(View.VISIBLE);
                    h.tv_additional_info.setVisibility(View.VISIBLE);
                } else {
                    h.layout_pending.setVisibility(View.GONE);
                    h.img_cost.setVisibility(View.VISIBLE);
                    h.tv_contact.setVisibility(View.GONE);
                    h.tv_status.setVisibility(View.GONE);
                    h.linear_color.setBackgroundResource(0);
                    h.rel_color.setBackgroundResource(R.drawable.bg_orderlist);
                    h.linear_remark.setVisibility(View.GONE);
                    h.linear_additional.setVisibility(View.GONE);
                    h.tv_additional_info.setVisibility(View.GONE);
                }

                if (Str_JobStatusToCheck.equalsIgnoreCase("Completed") || Str_JobStatusToCheck.equalsIgnoreCase("Exception")) {
                    h.img_more_detail.setVisibility(View.VISIBLE);
                }

                h.img_more_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isAppLocked()){
                            return;
                        }

                        if (Str_JobStatusToCheck.equalsIgnoreCase("Completed") || Str_JobStatusToCheck.equalsIgnoreCase("Exception")) {
                            Intent intent = new Intent(mContext, JobDetailWithPhoto.class);
                            intent.putExtra("title", "Job Detail");
                            intent.putExtra("Str_JobStatusToCheck", Str_JobStatusToCheck);
                            intent.putExtra("JobNo", ArrayOrderListInner.get(position).getJobNo());
                            mContext.startActivity(intent);
                        }

                    }
                });

                h.img_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(v.getContext(), MessageNotificationTab.class);
                        intent.putExtra("Str_TripNo", TripNumber);
                        intent.putExtra("CurrentTab", 1);
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        intent.putExtra("Str_MsgID", "0");
                        intent.putExtra("Stry_Msg_Type", "CS");
                        intent.putExtra("Str_SentTo", "CS");
                        mContext.startActivity(intent);
                    }
                });

                final ViewHolder finalH1 = h;
                h.img_snap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        Intent intent = new Intent(mContext, PhotoUploadActivity.class);
                        intent.putExtra("isSign", false);
                        intent.putExtra("Str_JobNo", ArrayOrderListInner.get(position).getJobNo());
                        intent.putExtra("Str_Sts", finalH1.btn_status.getText().toString().trim());
                        intent.putExtra("Str_Event", "Photos");
                        intent.putExtra("Str_TripNo", TripNumber);
                        mContext.startActivity(intent);
                    }
                });

                h.img_add_qty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAppLocked()){
                            return;
                        }

                        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("USER"))) {
                            Str_JobFor = ArrayOrderListInner.get(position).getJobFor();
                            Str_JobNo = ArrayOrderListInner.get(position).getJobNo();
                            DialogueQty(Integer.parseInt(ArrayOrderListInner.get(position).getQty()), Str_JobNo);
                        }
                    }
                });


            }
            return v;
        }

        private boolean setEditTextEnable(EditText edt) {
            if (edt.getText().toString().trim().equalsIgnoreCase("0") || edt.getText().toString().trim().equalsIgnoreCase("0.0")) {

                return true;
            } else {
                return false;
            }
        }


        private class ViewHolder {
            private TextView tv_name;
            private TextView tv_contact;
            private TextView tv_qty;
            private TextView tv_location;
            private TextView tv_remark;
            private TextView tv_erp;
            private TextView tv_status;
            private TextView tv_date;
            private TextView tv_time;
            private RelativeLayout rel_datetime;
            private ImageView img_message;
            private ImageView img_snap;
            private ImageView img_cost;
            private RelativeLayout rel_color;
            private Button btn_status;
            private Button btn_exception;
            private LinearLayout linear_color;
            private ImageView img_location;
            private ImageView img_add_qty;
            private ImageView img_attachment;
            private ImageView img_more_detail;
            private LinearLayout linear_remark;
            private LinearLayout linear_additional;
            private TextView tv_additional_info;
            private LinearLayout layout_pending;
            private LinearLayout layout_location;
            private TextView tv_index;

        }
    }

    //Disabling Back Button
    @Override
    public void onBackPressed() {

    }
}
