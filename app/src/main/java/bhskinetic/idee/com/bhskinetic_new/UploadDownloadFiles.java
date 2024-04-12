package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelAttachment;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/20/2017.
 */

public class UploadDownloadFiles extends TabActivity {
    public static TextView tv_header;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Context mContext;
    public static Activity activity;
    public static TrackGPS gps;
    public static TabHost tabHost;
    public static ImageView img_newmessage;
    private String jobNumber="NA";
    private String TripNumber="NA";
    public static ModelAttachment modelAttachment;
    public static ArrayList<ModelAttachment> ArrayAttachment;
    public static String Sent_By="";
    public static String Str_Sts="";
    public static String ImageID="";
    public static ImageView img_gallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);

        mContext = UploadDownloadFiles.this;
        gps = new TrackGPS(mContext);
        Sent_By="";
        Str_Sts="";
        ImageID="";
        img_refresh=(ImageView)findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabHost.getCurrentTab()==1){
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }
                    ArrayAttachment = new ArrayList<>();
                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressFiles");
                    tabHost.setCurrentTab(0);

                }else {
                    finish();
                }
            }
        });

        img_logout=(ImageView)findViewById(R.id.img_logout);
        img_gallery=findViewById(R.id.img_gallery);
        img_gallery.setVisibility(View.VISIBLE);

        img_logout.setImageResource(R.drawable.ic_camera);


        img_newmessage = (ImageView) findViewById(R.id.img_newmessage);
        tabHost = getTabHost();
        TabHost.TabSpec notificationspec = tabHost.newTabSpec(mContext.getResources().getString(R.string.title_upload_file));
        notificationspec.setIndicator(mContext.getResources().getString(R.string.title_upload_file), getResources().getDrawable(R.drawable.ic_add));
        Intent notificationIntent = new Intent(this, DownloadFileActivity.class);
        notificationspec.setContent(notificationIntent);
        tabHost.addTab(notificationspec);

        TabHost.TabSpec messagespec = tabHost.newTabSpec(mContext.getResources().getString(R.string.title_download_file));
        messagespec.setIndicator(mContext.getResources().getString(R.string.title_download_file), getResources().getDrawable(R.drawable.ic_add));
        Intent messageIntent = new Intent(this,PhotoUploadNew.class);
        messagespec.setContent(messageIntent);
        tabHost.addTab(messagespec);

        tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(true);

        tv_header = (TextView) findViewById(R.id.tv_header);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            tv_header.setText(b.getString("title"));
        }


        double lat = gps.getLatitude();
        double lng = gps.getLongitude();
        if (!gps.canGetLocation()) {
            lat = 0;
            lng = 0;
        }
        ArrayAttachment = new ArrayList<>();
        APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressFiles");
    }

    public static void CallAPI(){
        double lat = gps.getLatitude();
        double lng = gps.getLongitude();
        if (!gps.canGetLocation()) {
            lat = 0;
            lng = 0;
        }
        ArrayAttachment = new ArrayList<>();
        APIUtils.getAddressFromLatLong(mContext, lat, lng, "OrderInnerAddressFiles");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showResponse(String response, String redirectionKey) {
        try {

            if (redirectionKey.equalsIgnoreCase("OrderInnerAddressFiles")) {
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

            }else if (redirectionKey.equalsIgnoreCase("attachmentlistFiles")) {
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
                            modelAttachment.setSent_By(sublistObj.optString("Sent_By"));
                            modelAttachment.setSent_Date(sublistObj.optString("Sent_Date"));
                            modelAttachment.setImgID(sublistObj.optString("ImgID"));
                            modelAttachment.setStr_Status(sublistObj.optString("Str_Status"));
                            modelAttachment.setType(sublistObj.optString("Type"));
                            ArrayAttachment.add(modelAttachment);
                        }

                        if(DownloadFileActivity.adapter!=null && DownloadFileActivity.list_notification!=null){
                            DownloadFileActivity.list_notification.setAdapter(DownloadFileActivity.adapter);
                        }
                    }

                    Log.i("ATTACHMENT ARRAY SIZE", "==>" + ArrayAttachment.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAttachmentList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ProjName=Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext);
        APIUtils.sendRequest(mContext, "Attachment List", "Attc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_FileType=Download&Str_TripNo=" + TripNumber + "&Str_JobNo=" + jobNumber+"&Str_ProjName="+Str_ProjName, "attachmentlistFiles");
    }
}
