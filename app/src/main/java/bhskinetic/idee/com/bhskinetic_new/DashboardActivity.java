package bhskinetic.idee.com.bhskinetic_new;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import activities.HomeActivity;
import activities.VtrakLoginActivity;
import general.APIUtils;
import general.PermissionUtil;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/10/2017.
 */
public class DashboardActivity extends Activity {
    public static Context mContext;
    public static GridView grid_dynamicgrid;
    public static GridElementAdapter adapter;
    public static List<String> MenuArray;
    public static List<String> subArrayMain,subArray1,subArray2;
    public static TextView tv_totaljob;
    public static TextView tv_completedjob;
    public static TextView tv_pendingjob;
    public static final int PICK_IMAGE_ID = 234; // the number doesn't matter

    public static ImageView img_logout;
    public static Activity activity;
    public static TextView tv_header;
    public static String dutyStatus = "";
    public static final String SHARED_PREF_NAME = "spLogin";
    public static final String KEY_RESULT_MSG = "resultMsg";
    public static TextView tv_drivername;
    public static TextView tv_assistance;
    public static TextView tv_vehiclenumber;
    public static SharedPreferences sp;
    public static TextView tv_container;
    public static TextView tv_gps;
    public static String strCurrentOdometer = "";
    public static TextView tv_networktype;
    public static TrackGPS gps;
    public static FrameLayout frame_assistance, frame_vehicle, frame_container, frame_driver;
    public static Runnable myRunnable;
    public static Bitmap driverBitmap;
    public static String loadData = "";
    public static int FLAG = 0;
    public static int FLAG_LAGOUT = 0;
    public static int FLAG_ATTACH_ASSISTANT = 1;
    public static int FLAG_DEATTACH_ASSISTANT = 2;
    public static int FLAG_ATTACH_VEHICLE = 3;
    public static int FLAG_DEATTACH_VEHICLE = 4;
    public static int FLAG_ATTACH_CONTAINER = 5;
    public static int FLAG_DEATTACH_CONTAINER = 6;
    public static int FLAG_ATTACH_WORKSITE = 7;
    public static int FLAG_CONTAINER_LOCATION = 9;
    public static EditText edtDriverId;
    public static ImageView imgSearch;
    public static int FLAG_DEATTACH_WORKSITE = 8;
    public static String ADDRESS = "";
    public static ArrayList<String> ResourceList;
    public static String RESOURCE_ATTACH = "attach";
    public static String RESOURCE_DEATTACH = "detach";
    public static String RESOURCE_TYPE_ASSISTANT = "Assistant";
    public static String RESOURCE_TYPE_VEHICLE = "Vehicle";
    public static String RESOURCE_TYPE_CONTAINER = "Container";
    public static String RESOURCE_TYPE_WORKSITE = "Worksite";
    public static String RESOURCE_TYPE_CONTAINER_LOCATION = "TrailerLocation";
    public static String RESOURCE_NAME = "";
    public static EditText edt_resourcelist;
    public static ImageView img_refresh;
    public static RelativeLayout rel_total_job, rel_completed_job, rel_pending_job;
    public static String AttachedWorkSite = "";
    public static String Str_Date = "";
    public static String DriverName = "";
    public static boolean isDriver = true;
    public static RelativeLayout rel_one;
    public static LinearLayout linear_one;
    public static View view_top;
    public static Dialog odoDialogue;
    public static EditText edtCurrentOdometer;
    public static boolean isAttachedodo = false;
    public static Bitmap bm1Odo;
    public static String odoResName = "";
    public static String imagenameOdo = "";
    public static LinearLayout mLinearDriverDetail;
    public static View view_one;
    public static View view_bottom;
    public static PreviewWithoutSign preview;
    public static ImageView img_add;
    public static boolean isFromRefresh = false;
    public static Dialog myDriverDialogue;
    public static FrameLayout layout;
    public static Camera.ShutterCallback shutterCallback;
    public static Camera.PictureCallback rawCallback;
    public static Camera.PictureCallback jpegCallback;
    public static ProgressDialog pDialog;
    public static Camera camera;
    public static String UploadingImageName = "";
    public static String UploadUrl = "";
    public static String STR_REV = "";
    public static String DriverID = "";
    public static String ClientID = "";
    public static String LATITUDE = "";
    public static String LONGITUDE = "";
    public static String GPS_STATUS = "";
    public static String AADDRESS = "";
    public static String REMARK = "";
    public static String IMEINUMBER = "";
    public static String imagename;
    public static String Str_Sts = "NA";
    public static ImageView imgVisitor;
    public static TextView tvDriverName;
    public static TextView tvWorkStatus;
    public static TextView tvDateTime;
    public static Button btnUpdateStatus;
    public static MySurfaceViewWithoutSignature framePicture;
    public static TextView tv_address;
    public static TextView tv_select_job_date;
    private static String _date = "0";
    private static int _year = 0;
    public static String[] PERMISSIONS_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_PICTURE = 1;
    private static String _month = "0";
    public static String OTSeqNumber="",Str_Misc_Type="",OT_Msg="";
    public static ImageView imgNotification;
    public static FrameLayout frame_notification;
    public static List<String> Menu_Button_Counter;
    public static TextView tv_notificationcounter;
    public static String DriverIDOdo = "";
    public static String ClientIDOdo = "";
    public static String LATITUDEOdo = "";
    public static String LONGITUDEOdo = "";
    public static String GPS_STATUSOdo = "";
    public static String ADDRESSOdo = "";
    public static String REMARKOdo = "";
    public static String UploadUrlOdo = APIUtils.BaseUrl + "/Res_Attach_t.jsp";
    public static String IMEINUMBEROdo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard);
        mContext = DashboardActivity.this;
        activity = DashboardActivity.this;
        Init();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        UpdateCounter();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void UpdateCounter() {

        int occupiedCounter = 0;
        int emptyCounter = 0;

        String[] Splitter = null;
        try {
            Splitter = Utils.getPref(mContext.getResources().getString(R.string.pref_Load), mContext).split("\\|");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Splitter.length; i++) {
            if (Splitter[i].equalsIgnoreCase("Y")) {
                emptyCounter++;
            } else if (Splitter[i].equalsIgnoreCase("N")) {
                occupiedCounter++;
            }
        }

        loadData = "(" + occupiedCounter + "/" + Splitter.length + ")";
    }


    private void Init() {
        pDialog = new ProgressDialog(mContext,R.style.ProgressDialogStyle);

        Utils.setPref(mContext.getResources().getString(R.string.pref_isDigitalSignature), "false", mContext);
        Utils.ScanResult = "";
        grid_dynamicgrid = (GridView) findViewById(R.id.grid_dynamicgrid);
        AttachedWorkSite = "";
        dutyStatus = "";
        strCurrentOdometer = "";
        odoResName = "";
        isAttachedodo = false;
        OTSeqNumber="";
        Str_Misc_Type="";
        OT_Msg="";
        MenuArray = new ArrayList<>();
        subArrayMain = new ArrayList<>();
        subArray1 = new ArrayList<>();
        subArray2 = new ArrayList<>();
        String driverInfo = Utils.getPref(mContext.getResources().getString(R.string.pref_driverinfo), mContext);

        tv_notificationcounter=findViewById(R.id.tv_notificationcounter);
       // NOTIFICATION_25
        UpdateNotificationCounter();

        tv_totaljob = (TextView) findViewById(R.id.tv_totaljob);
        tv_completedjob = (TextView) findViewById(R.id.tv_completedjob);
        tv_pendingjob = (TextView) findViewById(R.id.tv_pendingjob);
        tv_header = (TextView) findViewById(R.id.tv_header);
        tv_select_job_date = (TextView) findViewById(R.id.tv_select_job_date);
        tv_select_job_date.setVisibility(View.VISIBLE);
        imgNotification=findViewById(R.id.img_notification);
        frame_notification=findViewById(R.id.frame_notification);


        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        tv_select_job_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitDatePicker();
            }
        });

        frame_notification.setVisibility(View.VISIBLE);
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,DashboardNotificationActivity.class));
            }
        });


        tv_header.setText(mContext.getResources().getString(R.string.text_header_dashboard));
        gps = new TrackGPS(mContext);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        try {
            JSONObject jsonObject = new JSONObject(driverInfo);

            String maindata=jsonObject.optString("Menu_Button");

            if(!jsonObject.optString("Menu_Button_Url").equalsIgnoreCase("NA")) {
                subArrayMain = Arrays.asList(jsonObject.optString("Menu_Button_Url").split("\\s*,\\s*"));
            }
            for(int i=0;i<subArrayMain.toArray().length;i++){
                String main[]=subArrayMain.get(i).split("##");
                    subArray1.add(main[0]);
                    subArray2.add(main[1]);
                maindata=maindata+","+subArrayMain.get(i);
            }

            MenuArray = Arrays.asList(maindata.split("\\s*,\\s*"));


            tv_totaljob.setText(jsonObject.optString("NOJ_Assinged"));
            tv_completedjob.setText(jsonObject.optString("NOJ_Completed"));
            tv_pendingjob.setText(jsonObject.optString("NOJ_Pending"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = FLAG_LAGOUT;
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromRefresh = true;
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + getCurrentDateTime(), "DashboardRefresh");
            }
        });

        grid_dynamicgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrackGPS gps = new TrackGPS(mContext);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                    return;
                }
                HandleGPSNetwork();
                if (MenuArray.get(position).equalsIgnoreCase("EXCEPTION")) {
                    Intent intent = new Intent(view.getContext(), OrderList.class);
                    intent.putExtra("Str_JobStatus", "Exception");
                    intent.putExtra("title", "EXCEPTION");
                    startActivity(intent);
                    activity.finish();
                } else if (MenuArray.get(position).equalsIgnoreCase("ORDER LIST")) {
                    Intent intent = new Intent(view.getContext(), OrderList.class);
                    intent.putExtra("Str_JobStatus", "Pending");
                    intent.putExtra("title", "ORDER LIST");
                    startActivity(intent);
                    activity.finish();
                } else if (MenuArray.get(position).equalsIgnoreCase("TRACK DRIVER")) {
                    Intent intent = new Intent(view.getContext(), OrderList.class);
                    intent.putExtra("Str_JobStatus", "Assigned");
                    intent.putExtra("title", "DRIVER STATUS");
                    startActivity(intent);
                    activity.finish();
                } else if (MenuArray.get(position).equalsIgnoreCase("ASSIGN")) {
                    Intent intent = new Intent(view.getContext(), OrderList.class);
                    intent.putExtra("Str_JobStatus", "Un-Assigned");
                    intent.putExtra("title", "ORDER LIST");
                    startActivity(intent);
                    activity.finish();
                } else if (MenuArray.get(position).equalsIgnoreCase("FACE-ID")) {//
                    boolean isAppInstalled = appInstalledOrNot("com.infotech.mobileattendancenew");
                    if (isAppInstalled) {
                        //This intent will help you to launch if the package is already installed
                        Intent LaunchIntent = mContext.getPackageManager()
                                .getLaunchIntentForPackage("com.infotech.mobileattendancenew");
                        mContext.startActivity(LaunchIntent);

                        Log.i("hello", "Application is already installed.");
                    } else {
                        // Do whatever we want to do if application not installed
                        // For example, Redirect to play store
                        final String appPackageName = "com.infotech.mobileattendancenew"; // getPackageName() from Context or Activity object
                        try {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        Log.i("hello not", "Application is not currently installed.");
                    }
                } else if (MenuArray.get(position).equalsIgnoreCase("LOADED")) {
                    if (tv_container.getText().toString().trim().length() > 0) {
                        Intent intent = new Intent(view.getContext(), MovieSeat.class);
                        intent.putExtra("Str_ResName", tv_container.getText().toString().trim());
                        intent.putExtra("Str_SeqNo","");
                        startActivity(intent);
                    } else {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_loading), mContext);
                        return;
                    }

                } else if (MenuArray.get(position).equalsIgnoreCase("SCAN")) {
                    Intent intent = new Intent(view.getContext(), ScanActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                    finish();
                } else if (MenuArray.get(position).equalsIgnoreCase("MANIFEST")) {
                    Intent intent = new Intent(view.getContext(), ScanActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                    finish();
                }else if (MenuArray.get(position).equalsIgnoreCase("MISC REQ")) {
                    Intent intent = new Intent(view.getContext(), MiscRequestActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("MAP")) {
                    String lat = "" + gps.getLatitude();
                    String lng = "" + gps.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps"));
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("SNAP")) {
                    Intent intent = new Intent(view.getContext(), PhotoUploadActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    intent.putExtra("isSign", false);
                    intent.putExtra("Str_Sts", "Snap");
                    intent.putExtra("Str_Event", "Snap");
                    intent.putExtra("snap", "true");
                    startActivity(intent);
                    finish();
                }  else if (MenuArray.get(position).equalsIgnoreCase("POD")) {
                    if(Utils.getPref(getString(R.string.pref_pod_otp),mContext).equalsIgnoreCase("1")) {
                        getPOD_OTP("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");
                    }else{
                        RedirectToPODList();
                    }
                }else if (MenuArray.get(position).equalsIgnoreCase("LEAVE REQ")) {
                    Intent intent = new Intent(view.getContext(), RequestLeave.class);
                    intent.putExtra("title", MenuArray.get(position));
                    intent.putExtra("requestType", "Leave");
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("OT REQ")) {
                     Intent intent = new Intent(view.getContext(), RequestLeave.class);
                    intent.putExtra("title", MenuArray.get(position));
                    intent.putExtra("requestType", "OT");
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("MAINT REQ")) {
                    if(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("Planner")||Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("Planner Manager")||Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("Supplier")) {
                        Intent intent = new Intent(view.getContext(), MaintananceReqPlanner.class);
                        if(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("Supplier")){
                            intent = new Intent(view.getContext(), MaintListSupplier.class);
                        }
                        intent.putExtra("title", MenuArray.get(position));
                        intent.putExtra("requestType", "Maintenances");
                        startActivity(intent);

                    }else{
                        boolean isAttached = false;
                        if (tv_vehiclenumber.getText().toString().trim().length() > 0 || tv_container.getText().toString().trim().length() > 0) {
                            isAttached = true;
                        }
                        if (isAttached) {
                            Intent intent = new Intent(view.getContext(), RequestLeave.class);
                            intent.putExtra("title", MenuArray.get(position));
                            intent.putExtra("requestType", "Maintenances");
                            intent.putExtra("AttachedVehicle", tv_vehiclenumber.getText().toString().trim());
                            intent.putExtra("AttachedContainer", tv_container.getText().toString().trim());
                            startActivity(intent);
                        } else {
                            Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_maint), mContext);
                            return;
                        }
                    }
                } else if (MenuArray.get(position).equalsIgnoreCase("MESSAGE")) {
                    Intent intent = new Intent(view.getContext(), MessageMainActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);


                }else if (MenuArray.get(position).equalsIgnoreCase("VOICEVIDEO")) {
                    Intent intent = new Intent(view.getContext(), StartUpActivity.class);
                    intent.putExtra("skipTour",true);
                    Utils.setPref(mContext.getString(R.string.pref_mesibo_pin),"1",mContext);
                    startActivity(intent);
                }else if (MenuArray.get(position).equalsIgnoreCase("BHS NEWS")) {
                    Intent intent = new Intent(view.getContext(), BHSNewsActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                }  else if (Utils.getPref(mContext.getResources().getString(R.string.pref_work_status), mContext).contains(MenuArray.get(position))) {
                    // AlertYesNODuty("", "Can I proceed?", mContext);
                    try {
                        ShowDriverDialogue(-1);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return;

                } else if (MenuArray.get(position).equalsIgnoreCase("ADD JOB")) {
                    Intent intent = new Intent(view.getContext(), AssignJobActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    intent.putExtra("isPlanner", "0");
                    startActivity(intent);
                }  else if (MenuArray.get(position).equalsIgnoreCase("WH SCANNER")) {
                    Utils.ScanResult="";
                    Intent intent = new Intent(view.getContext(), WHScannerActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                }else if (MenuArray.get(position).equalsIgnoreCase("BOOKING")) {
                    Intent intent = new Intent(view.getContext(), BookingJobActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    intent.putExtra("isPlanner", "1");
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("CHECKLIST")) {
                    Intent intent = new Intent(view.getContext(), DeviceCheckActivity.class);
                    intent.putExtra("DocumentURL", Utils.getPref(mContext.getResources().getString(R.string.pref_checklist_url), mContext));
                    intent.putExtra("DocumentTitle", "Check List");
                    startActivity(intent);
                } else if (MenuArray.get(position).contains("##")) {


                    Intent intent = new Intent(view.getContext(), ShowWebView.class);
                    intent.putExtra("DocumentURL", MenuArray.get(position).split("##")[1]);
                    intent.putExtra("DocumentTitle", MenuArray.get(position).split("##")[0]);
                    startActivity(intent);
                }
                else if (MenuArray.get(position).equalsIgnoreCase("CEVA")) {
                    Intent intent = new Intent(view.getContext(), ShowWebView.class);
                    intent.putExtra("DocumentURL", Utils.getPref(mContext.getResources().getString(R.string.pref_ceva_url), mContext));
                    intent.putExtra("DocumentTitle", "CEVA");
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("VTRAK")) {
                    if (sp.getString(KEY_RESULT_MSG, null) != null) {
                        Log.e("login val 1", sp.getString(KEY_RESULT_MSG, null));
                        startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                    } else {
                        Intent i = new Intent(DashboardActivity.this, VtrakLoginActivity.class);
                        startActivity(i);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                } else if (MenuArray.get(position).equalsIgnoreCase("DASHBOARD")) {
                    Intent intent = new Intent(view.getContext(), PlannerDashboard.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("FILES")) {
                    Intent intent = new Intent(view.getContext(), UploadDownloadFiles.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("APPROVAL")) {
                    Intent intent = new Intent(view.getContext(), ApprovalActivity.class);
                    intent.putExtra("title", MenuArray.get(position));
                    startActivity(intent);
                } else if (MenuArray.get(position).equalsIgnoreCase("LOGOUT")) {
                    FLAG = FLAG_LAGOUT;
                    AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
                }
            }
        });


        tv_drivername = (TextView) findViewById(R.id.tv_drivername);
        tv_assistance = (TextView) findViewById(R.id.tv_assistance);
        tv_vehiclenumber = (TextView) findViewById(R.id.tv_vehiclenumber);
        tv_container = (TextView) findViewById(R.id.tv_container);
        tv_gps = (TextView) findViewById(R.id.tv_gps);
        tv_networktype = (TextView) findViewById(R.id.tv_networktype);
        tv_networktype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        AttachedWorkSite = Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext);
        DriverName = (Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext));

        if (AttachedWorkSite.contains("NA")) {
            AttachedWorkSite = "";
            tv_drivername.setText(DriverName);
        } else {
            tv_drivername.setText(DriverName + " (" + AttachedWorkSite + " )");
        }


        tv_assistance.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_Asst), mContext));
        tv_vehiclenumber.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_VehNo), mContext));
        tv_container.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_Container), mContext));
        tv_header.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_Client_Name), mContext));

        frame_assistance = (FrameLayout) findViewById(R.id.frame_assistance);
        frame_vehicle = (FrameLayout) findViewById(R.id.frame_vehicle);
        frame_container = (FrameLayout) findViewById(R.id.frame_container);
        frame_driver = (FrameLayout) findViewById(R.id.frame_driver);

        adapter = new GridElementAdapter(mContext);
        grid_dynamicgrid.setAdapter(adapter);

        frame_assistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleGPSNetwork();
                ResourceList.clear();
                String message = getResources().getString(R.string.alert_attach_assistant);
                FLAG = FLAG_ATTACH_ASSISTANT;
                if (tv_assistance.getText().toString().trim().length() > 0) {
                    message = getResources().getString(R.string.alert_deattach_assistant) + " " + tv_assistance.getText().toString().trim() + "?";
                    FLAG = FLAG_DEATTACH_ASSISTANT;
                    AlertYesNO("", message, mContext);
                } else {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }


                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "AttachDeAttachAddress");
                }
            }
        });

        frame_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("Driver"))) {
                    HandleGPSNetwork();
                    ResourceList.clear();
                    String message = getResources().getString(R.string.alert_attach_worksite);
                    FLAG = FLAG_ATTACH_WORKSITE;
                    if (tv_drivername.getText().toString().trim().contains("(")) {
                        message = getResources().getString(R.string.alert_deattach_worksite) + " " + AttachedWorkSite + "?";
                        FLAG = FLAG_DEATTACH_WORKSITE;
                        AlertYesNO("", message, mContext);
                    } else {
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }

                        APIUtils.getAddressFromLatLong(mContext, lat, lng, "AttachDeAttachAddress");
                    }
                }
            }
        });

        frame_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleGPSNetwork();
                ResourceList.clear();
                String message = getResources().getString(R.string.alert_attach_vehicle);
                FLAG = FLAG_ATTACH_VEHICLE;
                if (tv_vehiclenumber.getText().toString().trim().length() > 0) {
                    if (tv_container.getText().toString().trim().length() == 0) {
                        message = getResources().getString(R.string.alert_deattach_vehicle) + " " + tv_vehiclenumber.getText().toString().trim() + "?";
                        FLAG = FLAG_DEATTACH_VEHICLE;
                        AlertYesNO("", message, mContext);
                    } else {
                        Utils.Alert("", "DEATTACH CONTAINER FIRST", mContext);
                    }
                } else {
                    Intent intent = new Intent(mContext, VehicleUndertakingActivity.class);
                    intent.putExtra("title", "VEH-UNDERTAKING");
                    Utils.setPref(mContext.getResources().getString(R.string.pref_odoResName), RESOURCE_TYPE_VEHICLE, mContext);
                    startActivity(intent);
                }
            }
        });

        frame_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleGPSNetwork();
                ResourceList.clear();
                String message = getResources().getString(R.string.alert_attach_container);
                FLAG = FLAG_ATTACH_CONTAINER;
                if (tv_container.getText().toString().trim().length() > 0) {

                    if (tv_vehiclenumber.getText().toString().trim().length() > 0) {
                        message = getResources().getString(R.string.alert_deattach_container) + " " + tv_container.getText().toString().trim() + "?";
                        FLAG = FLAG_DEATTACH_CONTAINER;
                        AlertYesNO("", message, mContext);
                    } else {
                        Utils.Alert("", "ATTACH VEHICLE FIRST", mContext);
                    }


                } else {
                    if (tv_vehiclenumber.getText().toString().trim().length() > 0) {
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }
                        APIUtils.getAddressFromLatLong(mContext, lat, lng, "AttachDeAttachAddress");
                    } else {
                        Utils.Alert("", "ATTACH VEHICLE FIRST", mContext);
                    }
                }


            }
        });

        loadData = "";
        FLAG = FLAG_LAGOUT;
        ADDRESS = "";
        ResourceList = new ArrayList<>();
        RESOURCE_NAME = "";

        rel_completed_job = (RelativeLayout) findViewById(R.id.rel_completed_job);
        rel_pending_job = (RelativeLayout) findViewById(R.id.rel_pending_job);

        rel_completed_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackGPS gps = new TrackGPS(mContext);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                    return;
                }

                if (tv_completedjob.getText().toString().trim().equalsIgnoreCase("0")) {
                    return;
                }

                boolean isAttached = false;
                if (tv_vehiclenumber.getText().toString().trim().length() > 0 && tv_container.getText().toString().trim().length() > 0) {
                    isAttached = true;
                }
                if (isAttached || Utils.getPref(getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("Planner")|| Utils.getPref(getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("CS User")||Utils.getPref(getString(R.string.pref_drivertype),mContext).equalsIgnoreCase("StoreMan")) {
                    HandleGPSNetwork();
                    Intent intent = new Intent(v.getContext(), OrderList.class);
                    intent.putExtra("Str_JobStatus", "Completed");
                    intent.putExtra("title", "COMPLETED ORDER");
                    startActivity(intent);
                    activity.finish();
                } else {
                    Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_vehicle), mContext);
                    return;
                }


            }
        });

        rel_pending_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackGPS gps = new TrackGPS(mContext);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                    return;
                }

                if (tv_pendingjob.getText().toString().trim().equalsIgnoreCase("0")) {
                    return;
                }

                HandleGPSNetwork();
                Intent intent = new Intent(v.getContext(), OrderList.class);
                intent.putExtra("Str_JobStatus", "Pending");
                intent.putExtra("title", "ORDER LIST");
                startActivity(intent);
                activity.finish();
            }
        });

        rel_one = (RelativeLayout) findViewById(R.id.rel_one);
        linear_one = (LinearLayout) findViewById(R.id.linear_one);
        view_top = (View) findViewById(R.id.view_top);
        view_one = (View) findViewById(R.id.view_one);
        img_add = (ImageView) findViewById(R.id.img_add);
        view_bottom = (View) findViewById(R.id.view_bottom);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
        Setheader();
        isFromRefresh = false;
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);

        try {
            APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + getCurrentDateTime(), "DashboardRefresh");
        }catch (Exception e){
            e.printStackTrace();
        }
        getCurrentDateTime();
    }

    private void RedirectToPODList() {
        Intent intent = new Intent(mContext, PhotoUploadActivity.class);
        intent.putExtra("title", "");
        intent.putExtra("isSign", false);
        intent.putExtra("isDirectPOD", "1");
        intent.putExtra("Str_JobNo", "0");
        intent.putExtra("Str_Sts", "Snap");
        intent.putExtra("Str_Event", "Snap");
        intent.putExtra("snap", "true");
        mContext.startActivity(intent);
        finish();
    }

    public static void UpdateNotificationCounter() {
        String buttonCounter=Utils.getPref(mContext.getResources().getString(R.string.pref_Menu_Button_Counter), mContext);
        try {
            Menu_Button_Counter = Arrays.asList(buttonCounter.split("\\s*,\\s*"));
            String[] notifcationCounter = Menu_Button_Counter.get(0).split("_");
            if (!notifcationCounter[1].equalsIgnoreCase("0")) {
                tv_notificationcounter.setVisibility(View.VISIBLE);
                tv_notificationcounter.setText(notifcationCounter[1]);
            } else {
                tv_notificationcounter.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                45);
    }

    public static void ShowDriverDialogue(int i) {
        if(!(Utils.getPref(mContext.getString(R.string.pref_Temp_URL),mContext).equalsIgnoreCase("0"))) {
            if (Utils.getPref(mContext.getString(R.string.pref_tempratureCheck), mContext).equalsIgnoreCase("0")) {
                Intent intent = new Intent(mContext, DeviceCheckActivity.class);
                intent.putExtra("DocumentURL", Utils.getPref(mContext.getString(R.string.pref_Temp_URL),mContext));
                intent.putExtra("DocumentTitle", "BHS Temperature Tracker");
                mContext.startActivity(intent);
                return;
            }
        }

        myDriverDialogue = new Dialog(activity, R.style.WideDialog);
        myDriverDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDriverDialogue.setCancelable(true);
        driverBitmap = null;

        myDriverDialogue.setContentView(R.layout.custom_dialogue_customer_english);

        imgSearch = (ImageView) myDriverDialogue.findViewById(R.id.img_search);
        edtDriverId = (EditText) myDriverDialogue.findViewById(R.id.edt_drvId);

        mLinearDriverDetail = (LinearLayout) myDriverDialogue.findViewById(R.id.linear_driver_detail);
        mLinearDriverDetail.setVisibility(View.GONE);
        imgVisitor = (ImageView) myDriverDialogue.findViewById(R.id.img_visitor);
        tvDriverName = (TextView) myDriverDialogue.findViewById(R.id.tv_driver_name);
        tvWorkStatus = (TextView) myDriverDialogue.findViewById(R.id.tv_work_status);
        tvDateTime = (TextView) myDriverDialogue.findViewById(R.id.tv_date_time);
        btnUpdateStatus = (Button) myDriverDialogue.findViewById(R.id.btn_update_status);
        Button btnClose = (Button) myDriverDialogue.findViewById(R.id.btn_close);

        framePicture = (MySurfaceViewWithoutSignature) myDriverDialogue.findViewById(R.id.surfaceView1);
        layout = (FrameLayout) myDriverDialogue.findViewById(R.id.layout);

        preview = new PreviewWithoutSign(myDriverDialogue.getContext(), framePicture);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) myDriverDialogue.findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    isFromRefresh = true;
                    String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                    String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                    APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Date=" + Str_Date + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + getCurrentDateTime(), "DashboardRefresh");
                    myDriverDialogue.dismiss();
                    camera.stopPreview();
                    camera.release();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    myDriverDialogue.dismiss();
                }
            }
        });

        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressDriverTimeUpdate");
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtDriverId.getText().toString().trim().length() > 0) {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }
                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "FindDriverDialogue");
                } else {
                    Utils.Alert("Enter Driver ID", mContext);
                }
            }
        });


        shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
                //			 Log.d(TAG, "onShutter'd");
            }
        };

        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        };

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, final Camera camera) {
                //new SaveImageTask().execute(data);
                driverBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                driverBitmap = RotateBitmap(driverBitmap, 270);
                UploadUrl = APIUtils.BaseUrl + "upload.jsp";
                UploadingImageName = "" + System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
                LATITUDE = "0";
                imagename = System.currentTimeMillis() + "_" + edtDriverId.getText().toString().trim() + ".jpg";
                LONGITUDE = "0";
                STR_REV = "";
                REMARK = "";
                IMEINUMBER = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                GPS_STATUS = "OFF";
                Str_Sts = btnUpdateStatus.getText().toString().trim();
                ADDRESS = "";
                Thread preview_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startPreview();
                    }
                }, "preview_thread");
                preview_thread.start();

                new uploadphoto().execute();
                //  bm1 = signature_view.getSignatureBitmap();
            }
        };


        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(1);
                Thread preview_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            camera.startPreview();
                            preview.setCamera(camera);
                            setCameraDisplayOrientation(activity, 1, camera);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, "preview_thread");
                preview_thread.start();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                Toast.makeText(mContext, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }


        if (i == -1) {
            //mLinearDriverDetail.setVisibility(View.VISIBLE);
            edtDriverId.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext));
            double lat = gps.getLatitude();
            double lng = gps.getLongitude();
            if (!gps.canGetLocation()) {
                lat = 0;
                lng = 0;
            }
            APIUtils.getAddressFromLatLong(mContext, lat, lng, "FindDriverDialogue");
        }
        Utils.setPref(mContext.getString(R.string.pref_tempratureCheck),"0",mContext);
        try {
            myDriverDialogue.show();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public static boolean appInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static class uploadphoto extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            btnUpdateStatus.setEnabled(false);
            pDialog.show();
            Log.e("PARAM====>", "==========" + IMEINUMBER + "\n" + ClientID + "\n" + LATITUDE + "\n" + LONGITUDE + "\n" + ADDRESS + "\n" + GPS_STATUS + "\n" + DriverID + "\n"
                    + REMARK + "\n" + STR_REV + "\n" + Str_Sts + "\n" + imagename + "\n");

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                driverBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
                Log.i("Photo Upload URL", UploadUrl);
                HttpPost postRequest = new HttpPost(UploadUrl);
                ByteArrayBody bab = new ByteArrayBody(data, imagename);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(IMEINUMBER));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(ClientID));
                reqEntity.addPart("Str_Lat", new StringBody(LATITUDE));
                reqEntity.addPart("Str_Long", new StringBody(LONGITUDE));
                reqEntity.addPart("Str_Loc", new StringBody(ADDRESS));
                reqEntity.addPart("Str_GPS", new StringBody(GPS_STATUS));
                reqEntity.addPart("Str_DriverID", new StringBody(DriverID));
                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody("NA"));
                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody("0"));
                reqEntity.addPart("Rev_Name", new StringBody(STR_REV));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody(Str_Sts));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(imagename));
                reqEntity.addPart("fType", new StringBody("Photo"));
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                uploadresponse = "" + s;
            } catch (Exception e) {
                // handle exception here
                Log.e(e.getClass().getName(), e.getMessage());
            }

            return uploadresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("UPLOAD RESPONSE.....", result);
            btnUpdateStatus.setEnabled(true);

            pDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_uploaded), Toast.LENGTH_SHORT).show();
                  /* myDriverDialogue.dismiss();
                     ShowDriverDialogue(1);*/
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }

                    /*if (camera != null) {
                        camera.release();
                        camera = null;
                    }*/

                    Date date = new Date();
                    String strDateFormat = "dd-MMMM-yyyy HH:mm";
                    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                    String formattedDate = dateFormat.format(date);

                    /*String messageBody = "Your Status change to " + btnUpdateStatus.getText().toString().trim() + " at " + formattedDate + ". Pls confirm?";

                    if (btnUpdateStatus.getText().toString().trim().equalsIgnoreCase("OFF-DUTY")) {
                        //messageBody="Your Status change to "+btnUpdateStatus.getText().toString().trim()+" Your Work Duration From "+tvDateTime.getText().toString().trim()+" To " +formattedDate+". Pls confirm?";
                        messageBody = "You are in " + btnUpdateStatus.getText().toString().trim() + " Now. DUTY Started At " + tvDateTime.getText().toString().trim() + ", End At " + formattedDate + ". Please confirm?";
                    }*/

                    String messageBody=OT_Msg;

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    dialog.dismiss();
                                    OTUpdate("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON",OTSeqNumber,Str_Misc_Type,"YES");
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    OTUpdate("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON",OTSeqNumber,Str_Misc_Type,"NO");

                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(messageBody).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "FindDriverDialogue");

                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void Setheader() {
        isDriver = true;
        if (!(Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext).equalsIgnoreCase("Driver"))) {
            isDriver = false;
        }
        view_top.setVisibility(View.GONE);
        if (!isDriver) {
            rel_one.setVisibility(View.GONE);
            linear_one.setVisibility(View.GONE);

            view_one.setVisibility(View.GONE);
            img_add.setImageResource(R.drawable.ic_driver);
            view_bottom.setVisibility(View.GONE);
            tv_gps.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (edt_resourcelist != null) {
            edt_resourcelist.setText(Utils.ScanResult);
            Log.i("APP_STATUS", "ON RESUME");
            HandleGPSNetwork();
        }
        if (mContext != null) {
            mContext.registerReceiver(mMessageReceiver, new IntentFilter("refresh_dashboard"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mMessageReceiver);
        if (APIUtils.pDialog != null) {
            APIUtils.pDialog.dismiss();
        }
        Log.i("APP_STATUS", "ON PAUSE");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Refereshing Activity on receiving push notification
            Log.i("APP_STATUS", "ON RECEIVE");
            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + getCurrentDateTime(), "DashboardRefresh");
        }
    };

    private void HandleGPSNetwork() {
        TrackGPS gpsSync = new TrackGPS(mContext);
        Log.i("GPS Network", "-->" + gpsSync.canGetLocation());
        //Updating GPS Status each second
        if (gpsSync.canGetLocation()) {
            tv_gps.setText(getResources().getString(R.string.str_network_on));
        } else {
            tv_gps.setText(getResources().getString(R.string.str_no_network));
        }

       /* if (APIUtils.isNetworkConnected(mContext)) {
            tv_networktype.setText(getResources().getString(R.string.str_network_on));
        } else {
            tv_networktype.setText(getResources().getString(R.string.str_no_network));
        }*/


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
                if (FLAG == FLAG_LAGOUT) {
                    APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logout");
                } else {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }

                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "AttachDeAttachAddress");
                }
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

    public static void CallAttachDeAttach(String address, String resourceName) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String Str_GPS = "ON";
        String lat = "" + gps.getLatitude();
        String lng = "" + gps.getLongitude();
        String addressToSent = address;
        String AttachType = "";
        String ResType = "";
        String ResName = resourceName;
        if (!gps.canGetLocation()) {
            Str_GPS = "OFF";
            addressToSent = "";
            lat = "0";
            lng = "0";
        }

        if (FLAG == FLAG_ATTACH_ASSISTANT || FLAG == FLAG_ATTACH_VEHICLE || FLAG == FLAG_ATTACH_CONTAINER || FLAG == FLAG_ATTACH_WORKSITE) {
            AttachType = RESOURCE_ATTACH;
        } else if (FLAG == FLAG_DEATTACH_ASSISTANT || FLAG == FLAG_DEATTACH_VEHICLE || FLAG == FLAG_DEATTACH_CONTAINER || FLAG == FLAG_DEATTACH_WORKSITE) {
            AttachType = RESOURCE_DEATTACH;
        }

        if (FLAG == FLAG_ATTACH_ASSISTANT || FLAG == FLAG_DEATTACH_ASSISTANT) {
            ResType = RESOURCE_TYPE_ASSISTANT;
        }

        if (FLAG == FLAG_ATTACH_VEHICLE || FLAG == FLAG_DEATTACH_VEHICLE) {
            ResType = RESOURCE_TYPE_VEHICLE;
        }

        if (FLAG == FLAG_ATTACH_CONTAINER || FLAG == FLAG_DEATTACH_CONTAINER) {
            ResType = RESOURCE_TYPE_CONTAINER;
        }

        if (FLAG == FLAG_ATTACH_WORKSITE || FLAG == FLAG_DEATTACH_WORKSITE) {
            ResType = RESOURCE_TYPE_WORKSITE;
        }


        if (ResName.contains(" ")) {
            ResName = ResName.replaceAll(" ", "%20");
        }

        APIUtils.sendRequest(mContext, "Attach De Attach", "Res_Attach.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + Str_GPS + "&Str_DriverID=" + DriverID + "&Str_AttchType=" + AttachType + "&Str_ResType=" + ResType + "&Str_ResName=" + ResName, "AttachDeAttach");
    }

    public static void getResourceList(String address) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String Str_GPS = "ON";
        String lat = "" + gps.getLatitude();
        String lng = "" + gps.getLongitude();
        String addressToSent = address;
        String ResType = "";

        if (FLAG == FLAG_ATTACH_ASSISTANT || FLAG == FLAG_DEATTACH_ASSISTANT) {
            ResType = RESOURCE_TYPE_ASSISTANT;
        }

        if (FLAG == FLAG_ATTACH_VEHICLE || FLAG == FLAG_DEATTACH_VEHICLE) {
            ResType = RESOURCE_TYPE_VEHICLE;
        }

        if (FLAG == FLAG_ATTACH_CONTAINER || FLAG == FLAG_DEATTACH_CONTAINER) {
            ResType = RESOURCE_TYPE_CONTAINER;
        }

        if (FLAG == FLAG_ATTACH_WORKSITE || FLAG == FLAG_DEATTACH_WORKSITE) {
            ResType = RESOURCE_TYPE_WORKSITE;
        }

        if (FLAG == FLAG_CONTAINER_LOCATION) {
            ResType = RESOURCE_TYPE_CONTAINER_LOCATION;
            DriverID = address;
            addressToSent = "NA";
        }

        if (!gps.canGetLocation()) {
            Str_GPS = "OFF";
            addressToSent = "";
            lat = "0";
            lng = "0";
        }


        APIUtils.sendRequest(mContext, "Resource List", "Res_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + addressToSent + "&Str_GPS=" + Str_GPS + "&Str_DriverID=" + DriverID + "&Str_ResType=" + ResType, "ResourceList");
    }

    public void showResponse(String response, String redirectionKey) {

        if (redirectionKey.equalsIgnoreCase("logout")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    DashboardActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("podOTP")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {

                    JSONArray list = exceptionlist.optJSONArray("list");
                    JSONObject value = list.optJSONObject(0);

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    dialog.dismiss();
                                    RedirectToPODList();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    dialog.dismiss();
                                    showPODCancelDialogue();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage(value.getString("OTPMessage")).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                    }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else  if (redirectionKey.equalsIgnoreCase("OTUpdate")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (redirectionKey.equalsIgnoreCase("GetDriverDetailDialgue")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    JSONArray list = jsonObject.optJSONArray("list");
                    if (list.length() > 0) {
                        JSONObject jobj = list.optJSONObject(0);
                        mLinearDriverDetail.setVisibility(View.VISIBLE);
                        Glide.with(activity)
                                .load(jobj.optString("Drv_Image")) // image url
                                .error(R.drawable.ic_driver)  // any image in case of error
                                .override(200, 200)
                                .into(imgVisitor); // resizing
                        tvDriverName.setText(jobj.optString("Drv_Name"));
                        Utils.setPref(activity.getResources().getString(R.string.pref_is_timephoto), jobj.optString("Camera_Flg"), mContext);
                        // Utils.setPref(mContext.getResources().getString(R.string.pref_is_timephoto),"0", mContext);
                        tvDateTime.setText(jobj.optString("vDateTime"));

                        if (jobj.optString("Work_Status").equalsIgnoreCase("OFF-DUTY")) {
                            btnUpdateStatus.setText("ON-DUTY");
                            tvWorkStatus.setText("OFF-DUTY");
                            dutyStatus = "ON-DUTY";
                        } else {
                            dutyStatus = "OFF-DUTY";
                            btnUpdateStatus.setText("OFF-DUTY");
                            tvWorkStatus.setText("ON-DUTY");
                        }


                        if (Utils.getPref(mContext.getResources().getString(R.string.pref_is_timephoto), mContext).equalsIgnoreCase("1")) {
                            layout.setVisibility(View.VISIBLE);
                            imgVisitor.setVisibility(View.GONE);
                            imgSearch.setVisibility(View.GONE);
                        } else {
                            layout.setVisibility(View.GONE);
                            imgVisitor.setVisibility(View.VISIBLE);
                            imgSearch.setVisibility(View.VISIBLE);
                        }

                    } else {
                        mLinearDriverDetail.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Driver Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("UpdateDriverTimeStatus")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();

                    OTSeqNumber=jobj.optString("SeqNo");
                    Str_Misc_Type=jobj.optString("OT_Type");
                    OT_Msg=jobj.optString("OT_Msg");

                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }


                    /*String messageBody="Your Status change to DUTY OFF at 28-Mar-2019 13:50. Pls confirm? YES   NO";
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    dialog.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(messageBody).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();*/

                    if (Utils.getPref(mContext.getResources().getString(R.string.pref_is_timephoto), mContext).equalsIgnoreCase("1")) {
                        //Capture Camera Image
                        try {
                            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        APIUtils.getAddressFromLatLong(mContext, lat, lng, "FindDriverDialogue");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("DashboardRefresh")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Status").equalsIgnoreCase("0")) {
                    //Error
                    Utils.Alert(mContext.getResources().getString(R.string.alert_login_failed), mContext);
                } else {
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driverinfo), jsonObject.toString(), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Asst), jsonObject.optString("Asst"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_ID), jsonObject.optString("Client_ID"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_Name), jsonObject.optString("Client_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Container), jsonObject.optString("Container"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Driver_Name), jsonObject.optString("Driver_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jsonObject.optString("Load"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_worksite), jsonObject.optString("Worksite"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Menu_Button_Counter), jsonObject.optString("Menu_Button_Counter"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_work_status), jsonObject.optString("Work_Status"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo), jsonObject.optString("VehNo"), mContext);
                    if (isFromRefresh) {
                        mContext.startActivity(new Intent(mContext, DashboardActivity.class));
                        activity.finish();
                    } else {
                        Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo), jsonObject.optString("VehNo"), mContext);
                        Utils.setPref(mContext.getResources().getString(R.string.pref_driverinfo), jsonObject.toString(), mContext);
                        UpdateJobCount();
                    }
                    //Redirect to another activity
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("AddressDriverTimeUpdate")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }
                }

                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
                String lat = "" + gps.getLatitude();
                String lng = "" + gps.getLongitude();
                String Str_GPS = "";
                if (!gps.canGetLocation()) {
                    Str_GPS = "OFF";
                }
                APIUtils.sendRequest(mContext, "Update Driver Time Status", "Time_Attendance.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + Str_GPS + "&Str_DriverID=" + edtDriverId.getText().toString().trim() + "&Str_Way=" + dutyStatus + "&Str_VehNo=NA", "UpdateDriverTimeStatus");
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("AttachDeAttach")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    if (FLAG == FLAG_DEATTACH_ASSISTANT) {
                        tv_assistance.setText("");
                    } else if (FLAG == FLAG_ATTACH_ASSISTANT) {
                        tv_assistance.setText(RESOURCE_NAME);
                    } else if (FLAG == FLAG_DEATTACH_VEHICLE) {
                        tv_vehiclenumber.setText("");
                    } else if (FLAG == FLAG_ATTACH_VEHICLE) {
                        tv_vehiclenumber.setText(RESOURCE_NAME);
                    } else if (FLAG == FLAG_DEATTACH_CONTAINER) {
                        tv_container.setText("");
                    } else if (FLAG == FLAG_ATTACH_CONTAINER) {
                        tv_container.setText(RESOURCE_NAME);
                    } else if (FLAG == FLAG_DEATTACH_WORKSITE) {
                        tv_drivername.setText(DriverName);
                        AttachedWorkSite = "";
                    } else if (FLAG == FLAG_ATTACH_WORKSITE) {
                        if (AttachedWorkSite.contains("NA")) {
                            AttachedWorkSite = "";
                            tv_drivername.setText(DriverName);
                        } else {
                            tv_drivername.setText(DriverName + " (" + AttachedWorkSite + " )");
                        }


                        Utils.setPref(mContext.getResources().getString(R.string.pref_worksite), AttachedWorkSite, mContext);


                    }

                    if (FLAG == FLAG_DEATTACH_CONTAINER || FLAG == FLAG_ATTACH_CONTAINER) {
                        UpdateJobCountRefresh();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ResourceList")) {

            try {
                ResourceList.clear();
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    JSONArray arrayResource = jobj.getJSONArray("list");
                    for (int i = 0; i < arrayResource.length(); i++) {
                        JSONObject jobjInner = arrayResource.getJSONObject(i);

                        ResourceList.add(jobjInner.optString("ResName"));
                    }
                    Log.i("Resource List Size", "====>" + ResourceList.size());

                    if (FLAG == FLAG_CONTAINER_LOCATION) {
                        tv_address.setText(ResourceList.get(0));
                    } else {
                        DialogueWithList(ResourceList);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("FindDriverDialogue")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }
                }

                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
                String lat = "" + gps.getLatitude();
                String lng = "" + gps.getLongitude();
                String Str_GPS = "";
                if (!gps.canGetLocation()) {
                    Str_GPS = "OFF";

                }

                APIUtils.sendRequest(mContext, "Get Driver Detail Dialogue", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + Str_GPS + "&Str_DriverID=" + edtDriverId.getText().toString().trim() + "&Str_Event=DRIVER", "GetDriverDetailDialgue");
                //APIUtils.sendRequest(mContext, "Duty ON/OFF", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Date=" +Str_Date+ "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken(), "duty_on_off");
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("AttachDeAttachAddress")) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }
                }

                ADDRESS = address;

                if (FLAG == FLAG_ATTACH_ASSISTANT || FLAG == FLAG_ATTACH_VEHICLE || FLAG == FLAG_ATTACH_CONTAINER || FLAG == FLAG_ATTACH_WORKSITE) {
                    getResourceList(address);
                } else {

                    String resourceName = "";

                    if (FLAG == FLAG_DEATTACH_ASSISTANT) {
                        resourceName = tv_assistance.getText().toString().trim();
                    } else if (FLAG == FLAG_DEATTACH_VEHICLE) {
                        resourceName = tv_vehiclenumber.getText().toString().trim();
                    } else if (FLAG == FLAG_DEATTACH_CONTAINER) {
                        resourceName = tv_container.getText().toString().trim();
                    } else if (FLAG == FLAG_DEATTACH_WORKSITE) {
                        resourceName = AttachedWorkSite;

                    }

                    if (FLAG == FLAG_DEATTACH_VEHICLE && Utils.getPref(mContext.getResources().getString(R.string.pref_ISOdometer), mContext).equalsIgnoreCase("Y")) {
                        DialogueOdometer(false);
                    } else {
                        CallAttachDeAttach(address, resourceName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void showPODCancelDialogue() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        dialog.dismiss();
                        getPOD_OTP("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setMessage("Do You want to continue with another OTP?").setPositiveButton("Another OTP", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private void GrantPicturePermission() {
        ActivityCompat.requestPermissions(DashboardActivity.activity, PERMISSIONS_PICTURE, REQUEST_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PermissionUtil.verifyPermissions(grantResults)) {

            //      selectImage();
        }
    }

    public void DialogueOdometer(final boolean isAttach) {
        // custom dialog
        odoDialogue = new Dialog(activity, R.style.WideDialog);
        odoDialogue.setContentView(R.layout.custom_dialogue_odometer);
        final EditText edtLastOdometer, edtDistanceTravelled;
        edtLastOdometer = (EditText) odoDialogue.findViewById(R.id.edt_last_odometer);
        edtCurrentOdometer = (EditText) odoDialogue.findViewById(R.id.edt_current_odometer);
        edtDistanceTravelled = (EditText) odoDialogue.findViewById(R.id.edt_distance_travelled);
        TextView tv_notice = (TextView) odoDialogue.findViewById(R.id.tv_notice);
        Button btnUpdateStatus = (Button) odoDialogue.findViewById(R.id.btn_update_status);
        ImageView btnClose = (ImageView) odoDialogue.findViewById(R.id.img_close);
        isAttachedodo = false;
        if (isAttach) {
            tv_notice.setText("If vehicle " + odoResName + "'s odometer values differ from below text box, Please click camera icon to take picture and submit for enable the text box to enter current odometer value. Thank you!");
        }
        ImageView img_edit_current_meter = (ImageView) odoDialogue.findViewById(R.id.img_edit_current_meter);
        img_edit_current_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Enable for Photo Upload Function
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        GrantPicturePermission();

                    } else {
                        selectImage();
                    }

                } else {
                    selectImage();
                }

            }
        });
        String LastOdometervalue = "";
        String VehicleName = "";

        if (isAttach) {
            VehicleName = edt_resourcelist.getText().toString();
        } else {
            VehicleName = tv_vehiclenumber.getText().toString();
        }

        LastOdometervalue = Utils.getPref(mContext.getResources().getString(R.string.pref_odolast), mContext);

        if (LastOdometervalue.trim().length() == 0) {
            LastOdometervalue = "0";
        }
        edtLastOdometer.setText(LastOdometervalue);


        if (isAttach) {
            edtLastOdometer.setVisibility(View.GONE);
            edtDistanceTravelled.setVisibility(View.GONE);
            edtCurrentOdometer.setEnabled(false);
            img_edit_current_meter.setVisibility(View.VISIBLE);
            tv_notice.setVisibility(View.VISIBLE);
        } else {
            edtLastOdometer.setVisibility(View.VISIBLE);
            edtDistanceTravelled.setVisibility(View.VISIBLE);
            edtLastOdometer.setEnabled(false);
            edtDistanceTravelled.setEnabled(false);
            edtCurrentOdometer.setEnabled(true);
            img_edit_current_meter.setVisibility(View.GONE);
            tv_notice.setVisibility(View.GONE);
        }

        final String finalVehicleName = VehicleName;
        final String finalLastOdometervalue = LastOdometervalue;
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strCurrentOdometer = edtCurrentOdometer.getText().toString().trim();
                if (edtCurrentOdometer.getText().toString().trim().length() == 0) {
                    Utils.Alert("ENTER CURRENT ODOMETER VALUE", mContext);
                    return;
                }

                if (!isAttach) {
                    double COV = Double.parseDouble(edtCurrentOdometer.getText().toString().trim());
                    double POV = Double.parseDouble(edtLastOdometer.getText().toString().trim());
                    odoResName = tv_vehiclenumber.getText().toString().trim();
                    if (COV < POV) {
                        Utils.Alert("CURRENT ODOMETER VALUE CAN NOT BE LESS THAN LAST ODOMETER VALUE", mContext);
                        return;
                    }
                }


                if (isAttach) {
                    Utils.setPref(mContext.getResources().getString(R.string.pref_CurrentOdometer), strCurrentOdometer, mContext);
                }

                isAttachedodo = isAttach;

                ClientIDOdo = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
                LATITUDEOdo = "" + gps.getLongitude();
                LONGITUDEOdo = "" + gps.getLongitude();
                IMEINUMBEROdo = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                GPS_STATUS = "ON";

                new uploadphotoOdo().execute();
                //  CallAttachDeAttach(ADDRESS, finalVehicleName);
            }
        });

        edtCurrentOdometer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String lastOdometer = edtLastOdometer.getText().toString().trim();
                String currentOdometer = edtCurrentOdometer.getText().toString().trim();

                if (lastOdometer.trim().length() == 0) {
                    lastOdometer = "0";
                }

                if (currentOdometer.trim().length() == 0) {
                    currentOdometer = "0";
                }
                if (!isAttach) {
                    Double LOV = Double.parseDouble(lastOdometer);
                    Double COV = Double.parseDouble(currentOdometer);
                    edtDistanceTravelled.setText("" + (COV - LOV));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odoDialogue.dismiss();
            }
        });
        odoDialogue.setCancelable(false);
        odoDialogue.show();
    }

    class uploadphotoOdo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub


            Log.i("POST DATA", edtCurrentOdometer.getText().toString().trim() + "\n" + UploadUrlOdo + "\n" + IMEINUMBEROdo + "\n" + ClientIDOdo + "\n" + LATITUDEOdo + "\n" + LONGITUDEOdo + "\n" + ADDRESSOdo + "\n" + GPS_STATUS + "\n" + imagenameOdo);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                HttpPost postRequest = new HttpPost(UploadUrlOdo);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                HttpClient httpClient = new DefaultHttpClient();
                if (bm1Odo != null) {
                    bm1Odo.compress(Bitmap.CompressFormat.JPEG, 75, bos);

                    byte[] data = bos.toByteArray();

                    Log.i("Photo Upload URL", UploadUrlOdo);

                    ByteArrayBody bab = new ByteArrayBody(data, imagenameOdo);
                    reqEntity.addPart("uploaded", bab);
                }


                reqEntity.addPart("Str_iMeiNo", new StringBody(IMEINUMBEROdo));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(ClientIDOdo));
                reqEntity.addPart("Str_Lat", new StringBody(LATITUDEOdo));
                reqEntity.addPart("Str_Long", new StringBody(LONGITUDEOdo));
                reqEntity.addPart("Str_Loc", new StringBody(ADDRESSOdo));
                reqEntity.addPart("Str_GPS", new StringBody(GPS_STATUS));
                reqEntity.addPart("Str_DriverID", new StringBody(Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext)));

                if (isAttachedodo) {
                    reqEntity.addPart("Str_AttchType", new StringBody(RESOURCE_ATTACH));
                } else {
                    reqEntity.addPart("Str_AttchType", new StringBody(RESOURCE_DEATTACH));
                }

                reqEntity.addPart("Str_ResType", new StringBody("Vehicle"));
                reqEntity.addPart("Str_ResName", new StringBody(odoResName));
                reqEntity.addPart("Str_Odo", new StringBody(edtCurrentOdometer.getText().toString().trim()));
                if (bm1Odo != null) {
                    reqEntity.addPart("Filename", new StringBody(imagenameOdo));
                } else {
                    reqEntity.addPart("Filename", new StringBody("NA"));
                }
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                uploadresponse = "" + s;
            } catch (Exception e) {
                // handle exception here
                Log.e(e.getClass().getName(), e.getMessage());
            }

            return uploadresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
           /* if (isAttachedodo) {
                tv_vehiclenumber.setText(odoResName);
            } else {
                if(FLAG==FLAG_DEATTACH_CONTAINER) {
                    tv_container.setText("");
                }else{
                    tv_vehiclenumber.setText("");
                }
            }*/

            bm1Odo = null;
            odoResName = "";
            imagenameOdo = "";
            edtCurrentOdometer.setEnabled(false);
            isAttachedodo = false;

            if (pDialog.isShowing()) {
                pDialog.dismiss();
                if (odoDialogue != null) {
                    odoDialogue.dismiss();
                }
            }
            Log.i("UPLOAD RESPONSE.....", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    //activity.finish();


                    if (FLAG == FLAG_DEATTACH_VEHICLE) {
                        Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                        tv_vehiclenumber.setText("");
                        isFromRefresh = false;
                        //Change 04-02-2017 (Refreshing Page in onCreate)
                        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                        Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo), "", mContext);
                        APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Date=" + Str_Date + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken(), "DashboardRefresh");
                    } else if (FLAG == FLAG_DEATTACH_CONTAINER) {
                        Toast.makeText(mContext, "De Attached Container: " + tv_container.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                        tv_container.setText("");
                        isFromRefresh = false;
                        //Change 04-02-2017 (Refreshing Page in onCreate)
                        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                        Utils.setPref(mContext.getResources().getString(R.string.pref_Container), "", mContext);

                        APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_Date=" + Str_Date + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken(), "DashboardRefresh");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                // activity.finish();
            }
        }
    }


    private void selectImage() {
        Intent chooseImageIntent = ImagePickerClass.getPickImageIntent(DashboardActivity.activity);
        DashboardActivity.activity.startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }


    public static void UpdateJobCount() {
        try {
            String driverInfo = Utils.getPref(mContext.getResources().getString(R.string.pref_driverinfo), mContext);
            JSONObject jsonObject = new JSONObject(driverInfo);


            String maindata=jsonObject.optString("Menu_Button");

            if(!jsonObject.optString("Menu_Button_Url").equalsIgnoreCase("NA")) {
                subArrayMain = Arrays.asList(jsonObject.optString("Menu_Button_Url").split("\\s*,\\s*"));
            }
            for(int i=0;i<subArrayMain.toArray().length;i++){
                String main[]=subArrayMain.get(i).split("##");
                subArray1.add(main[0]);
                subArray2.add(main[1]);
                maindata=maindata+","+subArrayMain.get(i);
            }

            MenuArray = Arrays.asList(maindata.split("\\s*,\\s*"));


            tv_totaljob.setText(jsonObject.optString("NOJ_Assinged"));
            tv_completedjob.setText(jsonObject.optString("NOJ_Completed"));
            tv_pendingjob.setText(jsonObject.optString("NOJ_Pending"));
            adapter.notifyDataSetChanged();
            UpdateNotificationCounter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateJobCountRefresh() {
        try {
            String driverInfo = Utils.getPref(mContext.getResources().getString(R.string.pref_driverinfo), mContext);
            JSONObject jsonObject = new JSONObject(driverInfo);
            MenuArray = Arrays.asList(jsonObject.optString("Menu_Button").split("\\s*,\\s*"));
            tv_totaljob.setText(jsonObject.optString("NOJ_Assinged"));
            tv_completedjob.setText(jsonObject.optString("NOJ_Completed"));
            tv_pendingjob.setText(jsonObject.optString("NOJ_Pending"));
            adapter.notifyDataSetChanged();
            isFromRefresh = true;
            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + getCurrentDateTime(), "DashboardRefresh");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DialogueWithList(ArrayList<String> resourceList) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_resourcelist);
        dialog.setCancelable(false);

        if (FLAG == FLAG_ATTACH_ASSISTANT) {
            dialog.setTitle(mContext.getResources().getString(R.string.alert_select_resources_assistant));
        } else if (FLAG == FLAG_ATTACH_VEHICLE) {
            dialog.setTitle(mContext.getResources().getString(R.string.alert_select_resources_vehicle));
        } else if (FLAG == FLAG_ATTACH_CONTAINER) {
            dialog.setTitle(mContext.getResources().getString(R.string.alert_select_resources_container));
        } else if (FLAG == FLAG_ATTACH_WORKSITE) {
            dialog.setTitle(mContext.getResources().getString(R.string.alert_select_worksite));
        }

        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView img_search = (ImageView) dialog.findViewById(R.id.img_search);
        tv_address = (TextView) dialog.findViewById(R.id.tv_address);
        final ImageView img_barecode = (ImageView) dialog.findViewById(R.id.img_barecode);


        if (FLAG == FLAG_ATTACH_CONTAINER) {
            img_search.setVisibility(View.VISIBLE);
            tv_address.setVisibility(View.VISIBLE);
            img_barecode.setVisibility(View.VISIBLE);
        }


        edt_resourcelist = (EditText) dialog.findViewById(R.id.edt_resourcelist);
        // set the custom dialog components - text, image and button
        final String[] values = new String[resourceList.size()];
        for (int i = 0; i < resourceList.size(); i++) {
            values[i] = resourceList.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapter);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                edt_resourcelist.setText("" + values[position]);
            }
        });

        if (FLAG == FLAG_ATTACH_ASSISTANT) {
            img_barecode.setVisibility(View.GONE);
        } else if (FLAG == FLAG_ATTACH_VEHICLE) {
            img_barecode.setVisibility(View.GONE);
        } else if (FLAG == FLAG_ATTACH_CONTAINER) {
            img_barecode.setVisibility(View.VISIBLE);
        } else if (FLAG == FLAG_ATTACH_WORKSITE) {
            img_barecode.setVisibility(View.GONE);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_resourcelist.getText().toString().trim().length() > 0) {
                    if (FLAG == FLAG_ATTACH_WORKSITE) {
                        AttachedWorkSite = edt_resourcelist.getText().toString().trim();
                    }
                    CallAttachDeAttach(ADDRESS, edt_resourcelist.getText().toString());
                    RESOURCE_NAME = "" + edt_resourcelist.getText().toString();
                    dialog.dismiss();
                } else {
                    String alertString = "";
                    if (FLAG == FLAG_ATTACH_ASSISTANT) {
                        alertString = mContext.getResources().getString(R.string.alert_select_resources_assistant);

                    } else if (FLAG == FLAG_ATTACH_VEHICLE) {
                        alertString = mContext.getResources().getString(R.string.alert_select_resources_vehicle);

                    } else if (FLAG == FLAG_ATTACH_CONTAINER) {
                        alertString = mContext.getResources().getString(R.string.alert_select_resources_container);
                    } else if (FLAG == FLAG_ATTACH_WORKSITE) {
                        alertString = mContext.getResources().getString(R.string.alert_select_worksite);
                    }
                    Utils.Alert(alertString, mContext);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        img_barecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScanActivityDefault.class);
                activity.startActivityForResult(intent, 0);

            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search Container Location
                if (edt_resourcelist.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, "Enter Keyword", Toast.LENGTH_SHORT).show();
                    return;
                }
                FLAG = FLAG_CONTAINER_LOCATION;
                getResourceList(edt_resourcelist.getText().toString().trim());

            }
        });


        dialog.show();

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
                Utils.setPref(mContext.getResources().getString(R.string.pref_datetime), Str_Date, mContext);
                isFromRefresh = false;
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + Str_Date, "DashboardRefresh");
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
                        Utils.setPref(mContext.getResources().getString(R.string.pref_datetime), Str_Date, mContext);
                        isFromRefresh = false;
                        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                        APIUtils.sendRequest(mContext, "DashBoard Refresh", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Refresh&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_JobDate=" + Str_Date, "DashboardRefresh");

                    }
                }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public static String getCurrentDateTime() {
        Str_Date = Utils.getPref(mContext.getResources().getString(R.string.pref_datetime), mContext);
        tv_select_job_date.setText(Str_Date);
        return Str_Date;
    }


    //GridView adapter calss
    public class GridElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public GridElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return MenuArray.size();
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

                v = layoutInflater.inflate(R.layout.raw_dynamicgrid, null);
                h = new ViewHolder();
                h.tv_title = (TextView) v.findViewById(R.id.tv_title);
                h.tv_notificationcounter=(TextView)v.findViewById(R.id.tv_notificationcounter);
                h.img_icon = (ImageView) v.findViewById(R.id.img_icon);
                h.img_icon_ceva = (ImageView) v.findViewById(R.id.img_icon_ceva);
                if (MenuArray.get(position).contains("LOADED")) {
                    h.tv_title.setText(MenuArray.get(position) + "" + loadData);
                } else if (MenuArray.get(position).contains("DUTY")) {
                    h.tv_title.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_work_status), mContext));
                } else if (MenuArray.get(position).contains("##")) {
                    h.tv_title.setText(MenuArray.get(position).split("##")[0]);
                }

                else {
                    h.tv_title.setText(MenuArray.get(position));
                }


                if (MenuArray.get(position).contains("ORDER LIST")) {
                    h.img_icon.setImageResource(R.drawable.ic_completed_job);
                } else if (MenuArray.get(position).contains("LOADED")) {
                    h.img_icon.setImageResource(R.drawable.loaded);
                } else if (MenuArray.get(position).contains("MESSAGE")) {
                    h.img_icon.setImageResource(R.drawable.message);
                }else if (MenuArray.get(position).contains("VOICEVIDEO")) {
                    h.img_icon.setImageResource(R.drawable.ic_voicevideo);
                } else if (MenuArray.get(position).contains("MAP")) {
                    h.img_icon.setImageResource(R.drawable.g_map);
                } else if (MenuArray.get(position).contains("ADD JOB")) {
                    h.img_icon.setImageResource(R.drawable.ic_add_job);
                } else if (MenuArray.get(position).contains("REQUEST")) {
                    h.img_icon.setImageResource(R.drawable.leave_request);
                } else if (MenuArray.get(position).contains("LEAVE REQ")) {
                    h.img_icon.setImageResource(R.drawable.leave_request);
                }else if (MenuArray.get(position).contains("BHS NEWS")) {
                    h.img_icon.setImageResource(R.drawable.ic_dashboard_bhsnews);
                }  else if (MenuArray.get(position).contains("MAINT REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_maint_req);
                } else if (MenuArray.get(position).contains("SCAN")) {
                    h.img_icon.setImageResource(R.drawable.scan);
                } else if (MenuArray.get(position).contains("MANIFEST")) {
                    h.img_icon.setImageResource(R.drawable.scan);
                } else if (MenuArray.get(position).contains("SNAP")) {
                    h.img_icon.setImageResource(R.drawable.snap);
                } else if (MenuArray.get(position).contains("FILES")) {
                    h.img_icon.setImageResource(R.drawable.ic_files);
                } else if (MenuArray.get(position).contains("BOOKING")) {
                    h.img_icon.setImageResource(R.drawable.ic_booking);
                } else if (MenuArray.get(position).contains("TRACK DRIVER")) {
                    h.img_icon.setImageResource(R.drawable.ic_assign_job);
                } else if (MenuArray.get(position).contains("LOGOUT")) {
                    h.img_icon.setImageResource(R.drawable.ic_logout);
                } else if (Utils.getPref(mContext.getResources().getString(R.string.pref_work_status), mContext).contains(MenuArray.get(position))) {
                    h.img_icon.setImageResource(R.drawable.ic_on_duty);
                } else if (MenuArray.get(position).contains("CHECKLIST")) {
                    h.img_icon.setImageResource(R.drawable.ic_checklist);
                } else if (MenuArray.get(position).contains("EXCEPTION")) {
                    h.img_icon.setImageResource(R.drawable.ic_dashboard_exception);
                } else if (MenuArray.get(position).contains("DASHBOARD")) {
                    h.img_icon.setImageResource(R.drawable.ic_planner_dashboard);
                } else if (MenuArray.get(position).contains("APPROVAL")) {
                    h.img_icon.setImageResource(R.drawable.ic_leave_approval);
                }else if (MenuArray.get(position).contains("FACE-ID")) {
                    h.img_icon.setImageResource(R.drawable.ic_faceid);
                }  else if (MenuArray.get(position).contains("OT REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_ot_req);
                } else if (MenuArray.get(position).contains("VTRAK")) {
                    h.img_icon.setImageResource(R.drawable.ic_vtrak);
                }else if (MenuArray.get(position).contains("POD")) {
                    h.img_icon.setImageResource(R.drawable.ic_pod);
                }  else if (MenuArray.get(position).contains("MISC REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_misclist_dash);
                }else if (MenuArray.get(position).contains("##")) {
                    h.img_icon.setImageResource(R.drawable.ic_browser);
                }


                if (MenuArray.get(position).contains("CEVA") || MenuArray.get(position).equalsIgnoreCase("vTRAK")) {
                    h.img_icon.setVisibility(View.GONE);
                    h.tv_title.setVisibility(View.GONE);
                    h.img_icon_ceva.setVisibility(View.VISIBLE);

                    if (MenuArray.get(position).contains("CEVA")) {
                        h.img_icon_ceva.setImageResource(R.drawable.ic_ceva);
                    } else {
                        h.img_icon_ceva.setImageResource(R.drawable.ic_vtrak);
                    }
                } else {
                    h.img_icon.setVisibility(View.VISIBLE);
                    h.tv_title.setVisibility(View.VISIBLE);
                    h.img_icon_ceva.setVisibility(View.GONE);
                }

                //NOTIFICATION_25,MESSAGE_29,LEAVE REQ_1,OT REQ_0,MAINT REQ_7,APPROVAL_41,EXCEPTION_0,TRACK DRIVER_0,BHS NEWS_0
                boolean isContain=false;
                for(int i=0;i<Menu_Button_Counter.size();i++){
                    if(Menu_Button_Counter.get(i).contains(MenuArray.get(position))){
                        isContain=true;
                        String[] counter=Menu_Button_Counter.get(i).split("_");
                        if(!counter[1].equalsIgnoreCase("0")){
                            h.tv_notificationcounter.setText(counter[1]);
                        }else{
                            isContain=false;
                        }

                        break;
                    }
                }

                if(isContain){
                    h.tv_notificationcounter.setVisibility(View.VISIBLE);
                }else{
                    h.tv_notificationcounter.setVisibility(View.GONE);
                }

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_title.setText(MenuArray.get(position));
                if (MenuArray.get(position).contains("LOADED")) {
                    h.tv_title.setText(MenuArray.get(position) + "" + loadData);
                } else if (MenuArray.get(position).contains("DUTY")) {
                    h.tv_title.setText(Utils.getPref(mContext.getResources().getString(R.string.pref_work_status), mContext));
                }
                else if (MenuArray.get(position).contains("##")) {
                    h.tv_title.setText(MenuArray.get(position).split("##")[0]);
                }
                else {
                    h.tv_title.setText(MenuArray.get(position));
                }


                if (MenuArray.get(position).contains("ORDER LIST")) {
                    h.img_icon.setImageResource(R.drawable.ic_completed_job);
                } else if (MenuArray.get(position).contains("LOADED")) {
                    h.img_icon.setImageResource(R.drawable.loaded);
                } else if (MenuArray.get(position).contains("MESSAGE")) {
                    h.img_icon.setImageResource(R.drawable.message);
                } else if (MenuArray.get(position).contains("VOICEVIDEO")) {
                    h.img_icon.setImageResource(R.drawable.ic_voicevideo);
                }else if (MenuArray.get(position).contains("MAP")) {
                    h.img_icon.setImageResource(R.drawable.g_map);
                } else if (MenuArray.get(position).contains("REQUEST")) {
                    h.img_icon.setImageResource(R.drawable.leave_request);
                } else if (MenuArray.get(position).contains("ADD JOB")) {
                    h.img_icon.setImageResource(R.drawable.ic_add_job);
                } else if (MenuArray.get(position).contains("LEAVE REQ")) {
                    h.img_icon.setImageResource(R.drawable.leave_request);
                } else if (MenuArray.get(position).contains("BHS NEWS")) {
                    h.img_icon.setImageResource(R.drawable.ic_dashboard_bhsnews);
                }else if (MenuArray.get(position).contains("ASSIGN")) {
                    h.img_icon.setImageResource(R.drawable.ic_job_assigned);
                } else if (MenuArray.get(position).contains("MAINT REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_maint_req);
                } else if (MenuArray.get(position).contains("SCAN")) {
                    h.img_icon.setImageResource(R.drawable.scan);
                } else if (MenuArray.get(position).contains("MANIFEST")) {
                    h.img_icon.setImageResource(R.drawable.scan);
                } else if (MenuArray.get(position).contains("SNAP")) {
                    h.img_icon.setImageResource(R.drawable.snap);
                } else if (MenuArray.get(position).contains("LOGOUT")) {
                    h.img_icon.setImageResource(R.drawable.ic_logout);
                } else if (MenuArray.get(position).contains("FILES")) {
                    h.img_icon.setImageResource(R.drawable.ic_files);
                } else if (MenuArray.get(position).contains("BOOKING")) {
                    h.img_icon.setImageResource(R.drawable.ic_booking);
                } else if (MenuArray.get(position).contains("TRACK DRIVER")) {
                    h.img_icon.setImageResource(R.drawable.ic_assign_job);
                } else if (Utils.getPref(mContext.getResources().getString(R.string.pref_work_status), mContext).contains(MenuArray.get(position))) {
                    h.img_icon.setImageResource(R.drawable.ic_on_duty);
                } else if (MenuArray.get(position).contains("CHECKLIST")) {
                    h.img_icon.setImageResource(R.drawable.ic_checklist);
                } else if (MenuArray.get(position).contains("FACE-ID")) {
                    h.img_icon.setImageResource(R.drawable.ic_faceid);
                } else if (MenuArray.get(position).contains("EXCEPTION")) {
                    h.img_icon.setImageResource(R.drawable.ic_dashboard_exception);
                } else if (MenuArray.get(position).contains("APPROVAL")) {
                    h.img_icon.setImageResource(R.drawable.ic_leave_approval);
                } else if (MenuArray.get(position).contains("DASHBOARD")) {
                    h.img_icon.setImageResource(R.drawable.ic_planner_dashboard);
                } else if (MenuArray.get(position).contains("OT REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_ot_req);
                } else if (MenuArray.get(position).contains("VTRAK")) {
                    h.img_icon.setImageResource(R.drawable.ic_vtrak);
                }else if (MenuArray.get(position).contains("POD")) {
                    h.img_icon.setImageResource(R.drawable.ic_pod);
                } else if (MenuArray.get(position).contains("MISC REQ")) {
                    h.img_icon.setImageResource(R.drawable.ic_misclist_dash);
                }else if (MenuArray.get(position).contains("##")) {
                    h.img_icon.setImageResource(R.drawable.ic_browser);
                }

                if (MenuArray.get(position).contains("CEVA") || MenuArray.get(position).equalsIgnoreCase("vTRAK")) {
                    h.img_icon.setVisibility(View.GONE);
                    h.tv_title.setVisibility(View.GONE);
                    h.img_icon_ceva.setVisibility(View.VISIBLE);

                    if (MenuArray.get(position).contains("CEVA")) {
                        h.img_icon_ceva.setImageResource(R.drawable.ic_ceva);
                    } else {
                        h.img_icon_ceva.setImageResource(R.drawable.ic_vtrak);
                    }
                } else {
                    h.img_icon.setVisibility(View.VISIBLE);
                    h.tv_title.setVisibility(View.VISIBLE);
                    h.img_icon_ceva.setVisibility(View.GONE);
                }

                //NOTIFICATION_25,MESSAGE_29,LEAVE REQ_1,OT REQ_0,MAINT REQ_7,APPROVAL_41,EXCEPTION_0,TRACK DRIVER_0,BHS NEWS_0
                boolean isContain=false;
                for(int i=0;i<Menu_Button_Counter.size();i++){
                    if(Menu_Button_Counter.get(i).contains(MenuArray.get(position))){
                        isContain=true;
                        String[] counter=Menu_Button_Counter.get(i).split("_");
                        if(!counter[1].equalsIgnoreCase("0")){
                            h.tv_notificationcounter.setText(counter[1]);
                        }else{
                            isContain=false;
                        }
                        break;
                    }
                }

                if(isContain){
                    h.tv_notificationcounter.setVisibility(View.VISIBLE);
                }else{
                    h.tv_notificationcounter.setVisibility(View.GONE);
                }


            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_title,tv_notificationcounter;
            private ImageView img_icon;
            private ImageView img_icon_ceva;
        }
    }


    private void getPOD_OTP(String address, String lat, String lng, String gpsStatus) {
        String _driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "POD OTP", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + _driverId + "&Str_Event=" + "OTP"+ "&Str_JobNo=0", "podOTP");
    }


    public static void OTUpdate(String address, String lat, String lng, String gpsStatus,String Str_SeqNo,String Str_OT_Type,String Str_Misc_Status) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String URL = "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_SeqNo="+Str_SeqNo+"&Str_Misc_Type="+Str_OT_Type+"&Str_Misc_Status="+Str_Misc_Status+"&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_worksite),mContext);

        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "OT Update", URL, "OTUpdate");
    }
}
