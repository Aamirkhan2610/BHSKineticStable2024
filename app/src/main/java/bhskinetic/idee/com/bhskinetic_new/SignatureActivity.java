package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kyanogen.signatureview.SignatureView;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


import Classes.AppControllerChild;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/20/2017.
 */

public class SignatureActivity extends Activity {
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static TextView tv_header;
    public static Context mContext;
    public static Activity activity;
    public static TrackGPS gps;
    public static boolean isSigned = false;
    public static Button btn_submit, btn_clear;
    public static SignatureView signature_view;
    public static Bitmap combineBitmap;
    public static Bitmap bitmapSignature;
    public static String UploadingImageName = "";
    public static String UploadUrl = "";
    public static String STR_REV = "";
    public static String DriverID = "";
    public static String ClientID = "";
    public static String LATITUDE = "";
    public static String LONGITUDE = "";
    public static String GPS_STATUS = "";
    public static String ADDRESS = "";
    public static String REMARK = "";
    public static String IMEINUMBER = "";
    public static EditText edt_remark;
    public static EditText edt_name;
    public static ProgressDialog pDialog;
    public static String Str_JobNo = "0";
    public static String Str_TripNo = "NA";
    public static String Str_Sts = "NA";
    public static String imagename;
    public static String Cust_Name = "";
    public static Preview preview;
    public static Button buttonClick;
    public static Camera camera;
    private OrientationEventListener orientationListener = null;
    private MySurfaceView framePicture;
    boolean isLayoutPrepared = false;
    public static String PrintText=null;
    File imageFile;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signature);
        activity = SignatureActivity.this;
        mContext = SignatureActivity.this;
        Init();
    }

    private void Init() {
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        tv_header = (TextView) findViewById(R.id.tv_header);
        framePicture = (MySurfaceView) findViewById(R.id.surfaceView1);
        isSigned = false;
        tv_header.setText(mContext.getResources().getString(R.string.str_take_signature));
        gps = new TrackGPS(mContext);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_name = (EditText) findViewById(R.id.edt_name);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        Bundle b = getIntent().getExtras();
        if (b.getString("Cust_Name") != null) {
            Cust_Name = b.getString("Cust_Name");
        } else {
            Cust_Name = "";
        }

        if (b.getString("PrintText") != null) {
            PrintText = b.getString("PrintText");
        }

        edt_name.setText(Cust_Name);

        if (b.getString("edtRemark") != null) {
            edt_remark.setText(b.getString("edtRemark"));
        }

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        signature_view = (SignatureView) findViewById(R.id.signature_view);


        ViewTreeObserver vto = framePicture.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                framePicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                isLayoutPrepared = true;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.getPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),mContext).equalsIgnoreCase("true")) {
                    if (isLayoutPrepared) {
                        bitmapSignature = takeScreenShot();
                        if (!MySurfaceView.isPathDrawn) {
                            Utils.Alert("PLEASE SIGN OVER IMAGE", mContext);
                            return;
                        }
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);

                    } else {
                        Toast.makeText(mContext, "Wait till layout prepared", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (isSigned) {
                        combineBitmap = signature_view.getSignatureBitmap();

                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }

                        UploadData();
                      //  getAddressFromLatLong(mContext, lat, lng, "SigatureAddress");
                    } else {
                        Utils.Alert(mContext.getResources().getString(R.string.alert_take_customer_sign), mContext);
                    }
                }


            }
        });
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.getPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),mContext).equalsIgnoreCase("true")) {
                    Intent intent = new Intent(mContext, SignatureActivity.class);
                    intent.putExtra("isFromClear", true);
                    intent.putExtra("Cust_Name", edt_name.getText().toString().trim());
                    intent.putExtra("edtRemark", edt_remark.getText().toString().trim());
                    bitmapSignature=null;
                    MySurfaceView.isPathDrawn=false;
                    mContext.startActivity(intent);
                    activity.finish();

                }else{
                    isSigned=false;
                    signature_view.clearCanvas();
                }
            }
        });

        signature_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSigned = true;
                return false;
            }
        });

        if (b != null) {

            if (b.getString("Str_TripNo") != null) {
                Str_TripNo = b.getString("Str_TripNo");
            }

            if (b.getString("Str_JobNo") != null) {
                Str_JobNo = b.getString("Str_JobNo");
            }

            if (b.getString("Str_Sts") != null) {
                Str_Sts = b.getString("Str_Sts");
            }

        }

        preview = new Preview(this, (MySurfaceView) findViewById(R.id.surfaceView1));
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
        // setCameraDisplayOrientation(SignatureActivity.this,1,camera);
        //camera.setDisplayOrientation(180);

        if(Utils.getPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),mContext).equalsIgnoreCase("true")) {
            framePicture.setVisibility(View.VISIBLE);
            signature_view.setVisibility(View.GONE);
            findViewById(R.id.tv_sign_label).setVisibility(View.VISIBLE);
        }else{
            framePicture.setVisibility(View.GONE);
            signature_view.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_sign_label).setVisibility(View.INVISIBLE);
        }
    }

    public Bitmap takeScreenShot() {
        try {
            View v1 = framePicture;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            return bitmap;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
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

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(1);
                camera.startPreview();
                preview.setCamera(camera);
                setCameraDisplayOrientation(SignatureActivity.this, 1, camera);
            } catch (RuntimeException ex) {
                Toast.makeText(SignatureActivity.this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }



    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //new SaveImageTask().execute(data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            isSigned = true;

            //  bm1 = signature_view.getSignatureBitmap();
            double lat = gps.getLatitude();
            double lng = gps.getLongitude();
            if (!gps.canGetLocation()) {
                lat = 0;
                lng = 0;
            }
            bitmap=RotateBitmap(bitmap,270);
            combineBitmap = bitmapOverlayToCenter(bitmap, bitmapSignature);
            //getAddressFromLatLong(mContext, lat, lng, "SigatureAddress");

            UploadData();

            Log.d("", "onPictureTaken - jpeg");
        }
    };

    private void UploadData() {
        UploadUrl = APIUtils.BaseUrl + "upload.jsp";
        UploadingImageName = "" + System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
        LATITUDE = ""+gps.getLatitude();
        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
        LONGITUDE = ""+gps.getLongitude();
        STR_REV = SignatureActivity.edt_name.getText().toString().trim();
        REMARK = SignatureActivity.edt_remark.getText().toString().trim();
        IMEINUMBER = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        GPS_STATUS = "ON";
        ADDRESS = "NA";
        new UploadSignature().execute();
    }

    public Bitmap bitmapOverlayToCenter(Bitmap bitmap1, Bitmap overlayBitmap) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = overlayBitmap.getWidth();
        int bitmap2Height = overlayBitmap.getHeight();

        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);

        Bitmap finalBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(finalBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null);
        combineBitmap = finalBitmap;
        return finalBitmap;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d("", "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    public void getAddressFromLatLong(final Context mContext, double LATITUDE, double LONGITUDE, final String redirectKey) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        final String[] fulladdress = {""};
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + LATITUDE + "," + LONGITUDE + "&sensor=true";
        Utils.LogI("API URL", url, mContext);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            response = null;
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("status", "1");
                                HashMap<String, String> hasInner = new HashMap<>();
                                hasInner.put("formatted_address", "NA");
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(new JSONObject(hasInner));
                                response = new JSONObject(hashMap);
                                response.put("results", jsonArray);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (redirectKey.equalsIgnoreCase("SigatureAddress")) {
                                showResponse(response.toString(), redirectKey);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppControllerChild.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutSignature");
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

    public void showResponse(String response, String redirectionKey) {

        if (redirectionKey.equalsIgnoreCase("LogoutSignature")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    SignatureActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("SigatureAddress")) {
            try {

                JSONObject jsonObject = new JSONObject();
                try {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("status", "1");
                    HashMap<String, String> hasInner = new HashMap<>();
                    hasInner.put("formatted_address", "NA");
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(new JSONObject(hasInner));
                    jsonObject = new JSONObject(hashMap);
                    jsonObject.put("results", jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }


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

                UploadUrl = APIUtils.BaseUrl + "upload.jsp";
                UploadingImageName = "" + System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
                LATITUDE = lat;
                imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                LONGITUDE = lng;
                STR_REV = SignatureActivity.edt_name.getText().toString().trim();
                REMARK = SignatureActivity.edt_remark.getText().toString().trim();
                IMEINUMBER = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                GPS_STATUS = GPSStatus;
                ADDRESS = address;
                new UploadSignature().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UploadSignature extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext,R.style.ProgressDialogStyle);
            pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                combineBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();

                Log.i("Params", UploadUrl + "\n" +
                        "Str_iMeiNo" + IMEINUMBER + "\n" +
                        "Str_Model" + "Android" + "\n" +
                        "Str_ID" + ClientID + "\n" +
                        "Str_Lat" + LATITUDE + "\n" +
                        "Str_Long" + LONGITUDE + "\n" +
                        "Str_Loc" + ADDRESS + "\n" +
                        "Str_GPS" + GPS_STATUS + "\n" +
                        "Str_DriverID" + DriverID + "\n" +
                        "Str_JobView" + "Update" + "\n" +
                        "Str_TripNo" + Str_TripNo + "\n" +
                        "Remarks" + REMARK + "\n" +
                        "Str_JobNo" + Str_JobNo + "\n" +
                        "Rev_Name" + STR_REV + "\n" +
                        "Str_Nric" + "NA" + "\n" +
                        "Str_Sts" + Str_Sts + "\n" +
                        "Str_JobExe" + "NA" + "\n" +
                        "Filename" + imagename + "\n" +
                        "fType" + "Sign" + "\n");

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
                reqEntity.addPart("Str_TripNo", new StringBody(Str_TripNo));
                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(Str_JobNo));
                reqEntity.addPart("Rev_Name", new StringBody(STR_REV));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody(Str_Sts));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(imagename));
                reqEntity.addPart("fType", new StringBody("Sign"));
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
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    SignatureActivity.activity.finish();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_signature_uploaded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_signature_error), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
