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
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Model.ModelException;
import Model.ModelLeaveType;
import Model.ProductModel;
import general.APIUtils;
import general.PermissionUtil;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 24/07/2019.
 */

public class VehicleUndertakingActivity extends Activity{
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ProductModel productModel;
    public static Button btn_vehicle_number,btn_vehicle_number0;
    public static Button btn_confirm;
    public static Button btn_cancel;
    public static ArrayAdapter<String> adapterleave;
    public static ArrayList<ProductModel> productModelsArrayList;
    public static final int PICK_IMAGE_ID = 234;
    public static String PhotoJSON = "";
    public static ModelException modelException;
    public static ArrayList<ModelException> arrayException;
    public static ArrayAdapter<String> adapterException;
    public static String Str_JobExe = "NA";
    public static String Record_Id="0";
    public static EditText edt_current_odometer,edt_driver_name,edt_datetime;
    public static TextView edt_current_location;
    public static boolean isFromDash=false;
    public static String[] PERMISSIONS_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_PICTURE = 1;
    public static ImageView imgDriver;
    private static String _date = "0";
    private static int _year = 0;
    private static String _month = "0";
    private static String _Hr = "0";
    private static String _mm = "0";
    public static String scanResult="";
    public static String odoValue="";
    public static Bitmap bm1Odo;
    public static String imagenameOdo = "";
    public static ProgressDialog pDialog;
    public static String DriverIDOdo = "";
    public static String ClientIDOdo = "";
    public static String LATITUDEOdo = "";
    public static String LONGITUDEOdo = "";
    public static String GPS_STATUSOdo = "";
    public static String ADDRESSOdo = "";
    public static String REMARKOdo = "";
    public static String IMEINUMBEROdo = "";
    public static ArrayList<ModelLeaveType> arrayLeaveType;
    public static ModelLeaveType modelLeaveType;

    public static String UploadUrlOdo = APIUtils.BaseUrl + "Res_Attach_t.jsp";
    public static boolean isDropDown=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_veh_undertaking);
        activity=VehicleUndertakingActivity.this;
        mContext = VehicleUndertakingActivity.this;
        gps = new TrackGPS(mContext);
        Init();
    }

    private void Init() {
        arrayLeaveType = new ArrayList<>();
        isDropDown=false;
        isFromDash=false;
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);
        imgDriver = (ImageView) findViewById(R.id.img_driver);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        btn_vehicle_number =(Button)findViewById(R.id.btn_vehicle_number);
        btn_vehicle_number0 =findViewById(R.id.btn_vehicle_number0);
        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        Glide.with(this).load(Utils.getPref(mContext.getResources().getString(R.string.pref_driver_photo),mContext)).into(imgDriver);
        scanResult="";
        odoValue="";

        pDialog = new ProgressDialog(mContext,R.style.ProgressDialogStyle);
        pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        edt_driver_name =(EditText) findViewById(R.id.edt_driver_name);
        edt_current_odometer =(EditText) findViewById(R.id.edt_current_odometer);
        edt_current_location=(TextView) findViewById(R.id.edt_current_location);
        edt_datetime=(EditText) findViewById(R.id.edt_datetime);

        edt_driver_name.setText("You Are: "+Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name),mContext));

        productModelsArrayList=new ArrayList<>();
        arrayException = new ArrayList<>();
        Record_Id="0";
        img_refresh.setImageResource(R.drawable.ic_back);
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
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });


        if(!Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle")){
            edt_current_odometer.setVisibility(View.GONE);
        }


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_vehicle_number.getText().toString().trim().length()==0){
                    Utils.Alert("SCAN VEHICLE NUMBER",mContext);
                    return;
                }

                if(Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle") && edt_current_odometer.getText().toString().trim().length()==0){
                    Utils.Alert("ENTER ODOMETER VALUE",mContext);
                    return;
                }

                if(!odoValue.equalsIgnoreCase(edt_current_odometer.getText().toString().trim())&& Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle")){
                   /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                            GrantPicturePermission();

                        } else {
                            selectImage();
                        }

                    } else {
                        selectImage();
                    }*/
                    selectImage();
                }else{
                    bm1Odo=null;
                    new uploadphotoOdo().execute();
                }
               // AttachVehicle(edt_current_location.getText().toString().trim(),""+gps.getLatitude(),""+gps.getLongitude(),"OFF");
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VehicleUndertakingActivity.activity.finish();
            }
        });
        btn_vehicle_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScanActivityDefault.class);
                activity.startActivityForResult(intent, 0);
                isDropDown=false;
            }
        });

        btn_vehicle_number0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDropDown=true;
                CheckVehicleValidation(edt_current_location.getText().toString().trim(), "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");
            }
        });


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                GrantPicturePermission();
            }
        }


        if (!Utils.isGPSON(mContext)) {
            Toast.makeText(mContext,"Please turn on GPS",Toast.LENGTH_SHORT).show();
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(gpsOptionsIntent);
            return;
        }



       // getCurrentAddress();
        getCurrentDateTime();
    }

    private void selectImage() {
        Intent chooseImageIntent = ImagePickerClass.getPickImageIntent(VehicleUndertakingActivity.activity);
        VehicleUndertakingActivity.activity.startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void getCurrentDateTime() {
        Calendar cc = Calendar.getInstance();
        _year=cc.get(Calendar.YEAR);
        _month=""+(cc.get(Calendar.MONTH)+1);
        _Hr=""+(cc.get(Calendar.HOUR_OF_DAY));
        _mm=""+(cc.get(Calendar.MINUTE));
        if(_month.trim().length()==1){
            _month="0"+_month;
        }
        _date = ""+cc.get(Calendar.DAY_OF_MONTH);
        if(_date.trim().length()==1){
            _date="0"+_date;
        }

        String Str_Date=_date+"-"+_month+"-"+_year+" "+_Hr+":"+_mm;
        edt_datetime.setText(Str_Date);
    }

    private void getCurrentAddress() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            edt_current_location.setText(address);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void GrantPicturePermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_PICTURE, REQUEST_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PermissionUtil.verifyPermissions(grantResults)) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        /*Gallery code end*/
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ID) {
            if (resultCode != 0) {
                bm1Odo = ImagePickerClass.getImageFromResult(mContext, resultCode, data);
                imagenameOdo = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
                DriverIDOdo=driverId;
                ClientIDOdo=ClientID;
                LATITUDEOdo=""+gps.getLatitude();
                LONGITUDEOdo=""+gps.getLongitude();
                ADDRESSOdo=""+edt_current_location.getText().toString().trim();

                IMEINUMBEROdo=IMEINumber;
                new uploadphotoOdo().execute();
            }
        }
        // TODO use bitmap
    }

    class uploadphotoOdo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
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
                reqEntity.addPart("Str_GPS", new StringBody("OFF"));
                reqEntity.addPart("Str_DriverID", new StringBody(Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext)));
                reqEntity.addPart("Str_AttchType", new StringBody("attach"));
                reqEntity.addPart("Str_ResType", new StringBody(Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext)));
                reqEntity.addPart("Str_ResName", new StringBody(btn_vehicle_number.getText().toString().trim()));
                if(Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle")) {
                    reqEntity.addPart("Str_Odo", new StringBody(edt_current_odometer.getText().toString().trim()));
                }
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
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            Log.i("UPLOAD RESPONSE.....", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {


                    if(edt_current_odometer.getText().toString().trim().length()>0) {
                        Utils.setPref(mContext.getResources().getString(R.string.pref_odolast), edt_current_odometer.getText().toString().trim(), mContext);
                    }

                    if(Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle")) {
                        if (DashboardActivity.tv_vehiclenumber != null) {
                            DashboardActivity.tv_vehiclenumber.setText(btn_vehicle_number.getText().toString().trim());
                        }

                        Utils.setPref(mContext.getResources().getString(R.string.pref_VehNo),btn_vehicle_number.getText().toString().trim(),mContext);
                    }else{
                        if (DashboardActivity.tv_container != null) {
                            DashboardActivity.tv_container.setText(btn_vehicle_number.getText().toString().trim());
                        }

                        Utils.setPref(mContext.getResources().getString(R.string.pref_Container),btn_vehicle_number.getText().toString().trim(),mContext);

                    }
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // activity.finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(edt_current_location!=null) {
            getCurrentAddress();
        }
        if (btn_vehicle_number != null) {
            isDropDown=false;
            scanResult=Utils.ScanResult;
            if(scanResult.trim().length()>0 && Utils.getPref(mContext.getResources().getString(R.string.pref_odoResName),mContext).equalsIgnoreCase("Vehicle")) {
                btn_vehicle_number.setText("");
                CheckVehicleValidation(edt_current_location.getText().toString().trim(), "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");
            }else{
                if(Utils.ScanResult.trim().length()>0) {
                    btn_vehicle_number.setText(Utils.ScanResult);
                }
            }
            Utils.ScanResult="";
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutvehUndertaking");
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


    private void AttachVehicle(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Res_Attach.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_AttchType=" + "attach" + "&Str_ResType=" + "Vehicle" + "&Str_ResName=" + "NA" + "&Str_VehNo=" + btn_vehicle_number.getText().toString().trim() + "&Str_Odo=" + edt_current_odometer.getText().toString().trim();
        URL = URL.replaceAll(" ", "%20");
        APIUtils.sendRequest(mContext, "Attach De Attach", URL, "AttachVehicleNew");
    }

    private void CheckVehicleValidation(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);

        String Str_ResType="Vehicle List";

        if(isDropDown){
            Str_ResType="Vehicle";
        }

        String URL = "Res_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_ResType="+Str_ResType;
        URL = URL.replaceAll(" ", "%20");
        APIUtils.sendRequest(mContext, "Attach De Attach", URL, "CheckVehicleValidation");
    }


    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("logoutvehUndertaking")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    VehicleUndertakingActivity.activity.finish();
                    Utils.clearPref(mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("AttachVehicleNew")) {
            try {

                //{"recived":1,"Ack_Msg":"attach Vehicle : NA"}
                JSONObject jobj = new JSONObject(response);
                if(jobj.optString("recived").equalsIgnoreCase("1")){
                    Toast.makeText(mContext, ""+jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    VehicleUndertakingActivity.activity.finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("CheckVehicleValidation")) {
            try {
                arrayLeaveType.clear();
                boolean isValid=false;
                //{"recived":1,"Ack_Msg":"attach Vehicle : NA"}
                JSONObject jobj = new JSONObject(response);
                if(jobj.optString("recived").equalsIgnoreCase("1")){
                    JSONArray jsonArray=jobj.optJSONArray("list");
                    if(!isDropDown) {
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject resObject=jsonArray.optJSONObject(i);
                            if(resObject.optString("ResName").equalsIgnoreCase(scanResult)){
                                isValid=true;
                                odoValue=resObject.optString("Odometer");
                                break;
                            }
                        }
                        if (!isValid) {
                            Toast.makeText(mContext, "Invalid Vehicle", Toast.LENGTH_SHORT).show();
                        } else {
                            btn_vehicle_number.setText(scanResult);
                        }
                    }else{
                        //Open Vehicle Number Dropdown
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject resObject=jsonArray.optJSONObject(i);
                            modelLeaveType = new ModelLeaveType();
                            modelLeaveType.setListValue((i + 1) + "." + resObject.optString("ResName"));
                            modelLeaveType.setListValueWithoutIndex(resObject.optString("ResName"));
                            modelLeaveType.setListID(resObject.optString("Odometer"));
                            arrayLeaveType.add(modelLeaveType);
                        }
                        DialogueWithList(arrayLeaveType);
                    }
                }


            }catch (Exception  e){
                e.printStackTrace();
            }
        }
    }

    public void DialogueWithList(final ArrayList<ModelLeaveType> leaveTypes) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist_addjob);
        dialog.setCancelable(false);



        final ArrayList<ModelLeaveType> leaveTypesFiltered = new ArrayList<>();
        final ArrayList<ModelLeaveType> leaveTypesImplemented = new ArrayList<>();
        leaveTypesImplemented.addAll(leaveTypes);

        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        Button btn_submit=dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odoValue="0";
                btn_vehicle_number.setText(etSearch.getText().toString().trim());
                dialog.dismiss();
            }
        });


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
            values[i] = leaveTypesImplemented.get(i).getListValue();
        }
        adapterleave = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterleave);



        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();

                    odoValue=leaveTypesImplemented.get(position).getListID();
                    btn_vehicle_number.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());

            }
        });
        dialog.show();
    }
}

