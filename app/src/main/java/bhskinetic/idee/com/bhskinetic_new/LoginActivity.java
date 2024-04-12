package bhskinetic.idee.com.bhskinetic_new;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import general.APIUtils;
import general.DatabaseHandler;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/9/2017.
 */
public class LoginActivity extends Activity {
    public static EditText edt_driverId, edt_pword;
    public static Button btn_login, btn_submit;
    public static Context mContext;
    public static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    public static int MY_PERMISSIONS_REQUEST_LOCATION = 999;
    public static TrackGPS gps;
    public static String Lat = "";
    public static String Lng = "";
    public static String IMEINumber = "";
    public static Activity activity;
    PermissionListener permissionlistener;
    private static String _date = "0";
    private static int _year = 0;
    public static String Str_Date = "";
    private static String _month = "0";
    public static LinearLayout mLinearOne;
    public static RelativeLayout relCam;
    public static PreviewWithoutSign preview;
    public static MySurfaceViewWithoutSignature framePicture;
    public static FrameLayout layout;
    public static Camera camera;
    public static Camera.ShutterCallback shutterCallback;
    public static Camera.PictureCallback rawCallback;
    public static Camera.PictureCallback jpegCallback;
    public static ProgressDialog pDialog;
    public static Bitmap driverBitmap;
    public static String UploadUrl = APIUtils.BaseUrl + "Login_t.jsp";
    public static String imagename;
    private boolean isFromLoginButton = false;

    public static ArrayList<String> ResourceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        mContext = LoginActivity.this;
        activity = LoginActivity.this;
        Init();

    }

    private void Init() {
        mLinearOne = findViewById(R.id.linear_one);
        relCam = findViewById(R.id.rel_cam);
        DatabaseHandler db = new DatabaseHandler(mContext);
        db.Reset();

        edt_driverId = (EditText) findViewById(R.id.edt_driverId);
        edt_pword = (EditText) findViewById(R.id.edt_pword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating driver id
                isFromLoginButton = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPhoneStatePermission();
                } else {
                    normalLogin();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_pword.getText().toString().trim().length() == 0) {
                    Utils.Alert("Enter Password", mContext);
                    return;
                }
                try {
                    camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (isFromLoginButton) {
                    normalLogin();
                } else {
                    initCam();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

      /*  if(Utils.getPref(mContext.getResources().getString(R.string.pref_isUpdateRequired),mContext).equalsIgnoreCase("1")){
            ForceUpdate();
        }*/


        getCurrentDateTime();
        framePicture = (MySurfaceViewWithoutSignature) findViewById(R.id.surfaceView1);
        layout = (FrameLayout) findViewById(R.id.layout);
        preview = new PreviewWithoutSign(mContext, framePicture);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);


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
                imagename = System.currentTimeMillis() + "_" + edt_driverId.getText().toString().trim() + ".jpg";

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

        ResourceList = new ArrayList<>();
        ResourceList.add("Camera Option 0");
        ResourceList.add("Camera Option 1");
        ResourceList.add("Camera Option 2");
        ResourceList.add("Camera Option 3");
        ResourceList.add("Camera Option 4");
        ResourceList.add("Camera Option 5");

        initCam();
        CameraPermission();
    }

    public static void initCam() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(1);
                Thread preview_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startPreview();
                        preview.setCamera(camera);
                        setCameraDisplayOrientation(activity, 1, camera);
                    }
                }, "preview_thread");
                preview_thread.start();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                //  Toast.makeText(mContext, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static class uploadphoto extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext, R.style.ProgressDialogStyle);
            pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                String fcmToken = FirebaseInstanceId.getInstance().getToken();
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
                reqEntity.addPart("Str_iMeiNo", new StringBody(IMEINumber));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody("0"));
                reqEntity.addPart("Str_Lat", new StringBody("" + gps.getLatitude()));
                reqEntity.addPart("Str_Long", new StringBody("" + gps.getLongitude()));
                reqEntity.addPart("Str_Loc", new StringBody("NA"));
                reqEntity.addPart("Str_GPS", new StringBody("OFF"));
                reqEntity.addPart("Str_PWD", new StringBody(edt_pword.getText().toString().trim()));
                reqEntity.addPart("Str_DriverID", new StringBody(edt_driverId.getText().toString().trim()));
                reqEntity.addPart("Str_Way", new StringBody("Login"));
                reqEntity.addPart("Str_PakName", new StringBody("BHS"));
                reqEntity.addPart("Str_Version", new StringBody(BuildConfig.VERSION_NAME));
                reqEntity.addPart("Str_JobDate", new StringBody(Str_Date));
                reqEntity.addPart("Str_gcmid", new StringBody(FirebaseInstanceId.getInstance().getToken()));
                reqEntity.addPart("Filename", new StringBody(imagename));
                reqEntity.addPart("fType", new StringBody("Photo"));
                //"&Str_PakName=BHS" + "&Str_Version=" + BuildConfig.VERSION_NAME + "&Str_JobDate=" + Str_Date
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
            pDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("Status").equalsIgnoreCase("1")) {
                    // Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_uploaded), Toast.LENGTH_SHORT).show();
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driverinfo), jsonObject.toString(), mContext);



                    Utils.setPref(mContext.getResources().getString(R.string.pref_driverID), edt_driverId.getText().toString().trim(), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_IMEINumber), IMEINumber, mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Asst), jsonObject.optString("Asst"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_ceva_url), jsonObject.optString("Ceva_URL"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_ID), jsonObject.optString("Client_ID"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_Name), jsonObject.optString("Client_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driver_photo), jsonObject.optString("Driver_Image"), mContext);

                    Utils.setPref(mContext.getResources().getString(R.string.pref_Container), jsonObject.optString("Container"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Driver_Name), jsonObject.optString("Driver_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jsonObject.optString("Load"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_worksite), jsonObject.optString("Worksite"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_drivertype), jsonObject.optString("Driver_Type"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_checklist_url), jsonObject.optString("Check_List"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_work_status), jsonObject.optString("Work_Status"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo), jsonObject.optString("VehNo"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_isUpdateRequired), "0", mContext);
                    mContext.startActivity(new Intent(mContext, DashboardActivity.class));
                    activity.finish();
                    //Redirect to another activity
                    gps = new TrackGPS(mContext);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


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

    private void getCurrentDateTime() {
        Calendar cc = Calendar.getInstance();
        _year = cc.get(Calendar.YEAR);
        _month = "" + (cc.get(Calendar.MONTH) + 1);
        if (_month.trim().length() == 1) {
            _month = "0" + _month;
        }
        _date = "" + cc.get(Calendar.DAY_OF_MONTH);
        if (_date.trim().length() == 1) {
            _date = "0" + _date;
        }

        Str_Date = _date + "-" + _month + "-" + _year;
        Utils.setPref(mContext.getResources().getString(R.string.pref_datetime), Str_Date, mContext);
    }

    private void ForceUpdate() {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage("");
        builderInner.setTitle("New application version is available to download");
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = "bhskinetic.idee.com.bhskinetic_new"; // getPackageName() from Context or Activity object
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    private void getMapAPIKey() {
        //Get Location and Shift List for a driver
        APIUtils.sendRequest(mContext, "GetMAPKey", "http://203.125.153.221/tt/gmap_api.jsp?&Str_Model=Andorid", "getMapKey");
    }

    public void normalLogin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            IMEINumber = getIMEI();
        }
        gps = new TrackGPS(mContext);
        if (gps.canGetLocation()) {
            Lat = "" + gps.getLongitude();
            Lng = "" + gps.getLatitude();
        } else {
            gps.showSettingsAlert();
            return;
        }

        if (edt_driverId.getText().toString().trim().length() == 0) {
            Utils.Alert(getResources().getString(R.string.alert_enter_driver_id), mContext);
            return;
        }

        double lat = gps.getLatitude();
        double lng = gps.getLongitude();
        if (!gps.canGetLocation()) {
            lat = 0;
            lng = 0;
        }

        if(edt_driverId.getText().toString().trim().equalsIgnoreCase("mobileadmin")){
            edt_driverId.setText("");
            DialogueWithList(ResourceList);
        }else{
            APIUtils.getAddressFromLatLong(mContext, lat, lng, "LoginAddress");
        }

    }

    public void DialogueWithList(ArrayList<String> resourceList) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_resourcelist);
        dialog.setCancelable(false);

        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView img_search = (ImageView) dialog.findViewById(R.id.img_search);
        EditText edt_resourcelist=dialog.findViewById(R.id.edt_resourcelist);

        btn_submit.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);
        img_search.setVisibility(View.GONE);
        edt_resourcelist.setVisibility(View.GONE);

        final ImageView img_barecode = (ImageView) dialog.findViewById(R.id.img_barecode);

        img_barecode.setVisibility(View.GONE);

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

                Utils.setPref(getString(R.string.pref_cam_option),resourceList.get(position),mContext);
                dialog.dismiss();
                Toast.makeText(mContext,resourceList.get(position)+" Selected, Please login and try duty on/off again",Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();

    }

    public boolean checkPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
            return false;
        } else {
            locationRedirect();
            IMEINumber = getIMEI();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 999: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    IMEINumber = getIMEI();
                    locationRedirect();
                } else {
                    locationRedirect();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void locationRedirect() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("YOU CAN NOT USE BHS KINETIC APP, UNLESS YOU ALLOW LOCATION SERVICE")
                    .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } else {
            //Permission already given
            normalLogin();
        }
    }

    public void CameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("YOU CAN NOT USE BHS KINETIC APP, UNLESS YOU ALLOW CAMERA SERVICE")
                    .setPermissions(android.Manifest.permission.CAMERA)
                    .check();
        } else {
            //Permission already given
            //normalLogin();
        }
    }

    public static String getIMEI() throws SecurityException, NullPointerException {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            assert tm != null;
            if (tm.getDeviceId() != null && !tm.getDeviceId().equals("000000000000000")) {
                imei = tm.getDeviceId();
            } else {
                imei = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }

        return imei;
    }


    private void DoLogin(String address, String lat, String lng, String gpsStatus) {
        //Toast.makeText(LoginActivity.this,"IMEI:  "+IMEINumber,Toast.LENGTH_LONG).show();
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        APIUtils.sendRequest(mContext, "User Login", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=0&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_Way=Login&Str_DriverID=" + edt_driverId.getText().toString().trim() + "&Str_gcmid=" + FirebaseInstanceId.getInstance().getToken() + "&Str_PakName=BHS" + "&Str_Version=" + BuildConfig.VERSION_NAME + "&Str_JobDate=" + Str_Date, "login");
    }

    private void CheckCameraOption(String address, String lat, String lng, String gpsStatus) {
        //Toast.makeText(LoginActivity.this,"IMEI:  "+IMEINumber,Toast.LENGTH_LONG).show();
        APIUtils.sendRequest(mContext, "Cam Option", "Entry.jsp?Str_ID=0&Str_Model=Android&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + edt_driverId.getText().toString().trim(), "CheckCam");
    }

    public void showResponse(String response, String redirectionKey) {
        try {  //385
            if (redirectionKey.equalsIgnoreCase("login")) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Status").equalsIgnoreCase("0")) {
                    //Error
                    Utils.Alert(mContext.getResources().getString(R.string.alert_login_failed), mContext);
                } else {
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driverinfo), jsonObject.toString(), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driverID), edt_driverId.getText().toString().trim(), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_IMEINumber), IMEINumber, mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Asst), jsonObject.optString("Asst"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_ceva_url), jsonObject.optString("Ceva_URL"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_ID), jsonObject.optString("Client_ID"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Client_Name), jsonObject.optString("Client_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Container), jsonObject.optString("Container"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Menu_Button_Counter), jsonObject.optString("Menu_Button_Counter"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Driver_Name), jsonObject.optString("Driver_Name"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jsonObject.optString("Load"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_ISOdometer), jsonObject.optString("Odo_Flg"), mContext);

                    if (jsonObject.has("Temp_URL")) {
                        Utils.setPref(mContext.getResources().getString(R.string.pref_Temp_URL), jsonObject.optString("Temp_URL"), mContext);
                    }
                    Utils.setPref(mContext.getResources().getString(R.string.pref_worksite), jsonObject.optString("Worksite"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_drivertype), jsonObject.optString("Driver_Type"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_checklist_url), jsonObject.optString("Check_List"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_driver_photo), jsonObject.optString("Driver_Image"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_pod_otp), jsonObject.optString("Pod_Otp"), mContext);

                    Utils.setPref(mContext.getResources().getString(R.string.pref_work_status), jsonObject.optString("Work_Status"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo), jsonObject.optString("VehNo"), mContext);
                    Utils.setPref(mContext.getResources().getString(R.string.pref_isUpdateRequired), "0", mContext);
                    mContext.startActivity(new Intent(mContext, DashboardActivity.class));
                    activity.finish();
                    //Redirect to another activity
                    gps = new TrackGPS(mContext);

                }
            } else if (redirectionKey.equalsIgnoreCase("getMapKey")) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.i("Google API Key", jsonObject.toString());
                    if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                        //Google Map Key
                        Utils.setPref(mContext.getResources().getString(R.string.map_api_key), jsonObject.optString("Google_APIKey"), mContext);
                        Utils.setPref(mContext.getResources().getString(R.string.Map_Enable), jsonObject.optString("Map_Enable"), mContext);
                    }

                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            } else if (redirectionKey.equalsIgnoreCase("LoginAddress")) {
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

                    CheckCameraOption(address, lat, lng, GPSStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (redirectionKey.equalsIgnoreCase("CheckCam")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String address = "";
                    String lat = "0";
                    String lng = "0";
                    String GPSStatus = "OFF";
                    if (jsonObject.optString("Login_Sts").equalsIgnoreCase("1")) {
                        //Toast.makeText(mContext,"Camera Enabled",Toast.LENGTH_SHORT).show();
                        mLinearOne.setVisibility(View.GONE);
                        relCam.setVisibility(View.VISIBLE);
                    } else {
                        DoLogin(address, lat, lng, GPSStatus);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
