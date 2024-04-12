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
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.litao.android.lib.entity.PhotoEntry;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import Model.ModelException;
import Model.PhotoEntryUploading;
import general.APIUtils;
import general.DatabaseHandlerOfflineURL;
import general.ImagePicker;
import general.PermissionUtil;
import general.TrackGPS;
import general.Utils;

public class PhotoUploadNew extends Activity {
    //Activity Object
    public static Activity activity;
    public static Context mContext;
    public static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    public static TextView tv_header;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static TrackGPS gps;
    public static Button btn_select_photo;
    public static Button btn_upload_photo;
    GalleryImageAdapter galleryImageAdapter;
    public static Button btn_select;
    public static String imagename;
    public static CharSequence[] options = new CharSequence[3];
    private static final int GALLERY = 6;
    private static final int TAKEPHOTO = 5;
    public static List<PhotoEntry> mySelectedPhotos;
    public static Gallery selectedImageGallery;
    public static ImageView img_photo;
    public static Bitmap bm1;
    public static String UploadingImageName = "";
    public static boolean isDefault = true;
    public static String UploadUrl = "";
    public static ProgressDialog pDialog;
    public static String DriverID = "";
    public static String ClientID = "";
    public static String LATITUDE = "";
    public static String LONGITUDE = "";
    public static String GPS_STATUS = "";
    public static String ADDRESS = "";
    public static String REMARK = "";
    public static String IMEINUMBER = "";
    public static EditText edt_remark;
    public static ModelException modelException;
    public static ArrayList<ModelException> arrayException;
    public static String STR_REV = "";
    public static String Str_JobNo = "0";
    public static String Str_TripNo = "NA";
    public static Button btn_no_photo;
    public static boolean isSign = false;
    public static String Str_Sts = "";
    public static String Str_Event = "";
    public static String snap = "";
    public static String[] PERMISSIONS_PICTURE = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_PICTURE = 1;
    public static String PhotoJSON = "";
    ArrayAdapter<String> adapterException;
    Uri fileUri;
    File camFile;
    public static TextView tv_upload_counter;
    public static int totalPhoto=0;
    public DatabaseHandlerOfflineURL databaseHandlerOfflineURL;
    public static int PHOTO_TYPE = 0;
    public static ArrayList<PhotoEntryUploading> mySelectedPhotosGlobal;
    public static GridElementAdapter adapter;
    private GridView gridDynamic;
    private int uploadCounter=0;
    public static int myflag=0;
    public static LinearLayout linear_center;
    public static ImageView ic_gallery_big,ic_cam_big;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photoupload_new);
        mContext = PhotoUploadNew.this;
        activity = PhotoUploadNew.this;
        databaseHandlerOfflineURL=new DatabaseHandlerOfflineURL(mContext);
        Init();
    }

    private void Init() {
        pDialog = new ProgressDialog(mContext);
        linear_center = findViewById(R.id.linear_center);
        ic_gallery_big = findViewById(R.id.ic_gallery_big);
        ic_cam_big = findViewById(R.id.ic_cam_big);
        pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        tv_upload_counter = (TextView) findViewById(R.id.tv_upload_counter);
        PHOTO_TYPE = 0;
        snap = "";
        tv_header = (TextView) findViewById(R.id.tv_header);
        Str_JobNo = "";
        Str_TripNo = "";
        selectedImageGallery = (Gallery) findViewById(R.id.selected_image_gallery);
        selectedImageGallery.setSpacing(50);
        mySelectedPhotosGlobal=new ArrayList<>();

        if(mySelectedPhotosGlobal!=null){
            mySelectedPhotosGlobal.clear();
        }

        ic_cam_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag=1;
                selectImage(1);
            }
        });

        ic_gallery_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag=0;
                selectImage(0);
            }
        });


        gps = new TrackGPS(mContext);
        PhotoJSON = "";
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("Str_JobNo") != null) {
                Str_JobNo = b.getString("Str_JobNo");
            }

            if (b.get("Str_TripNo") != null) {
                Str_TripNo = b.getString("Str_TripNo");

            }
            isSign = b.getBoolean("isSign");

            if (b.getString("snap") != null) {
                snap = b.getString("snap");
            }

            if (b.getString("PhotoJSON") != null) {
                PhotoJSON = b.getString("PhotoJSON");
            } else {
                PhotoJSON = "";
            }
            Str_Sts = "";
            Str_Event = b.getString("Str_Event");
        }

        tv_header.setText("UPLOAD PHOTO");

        if(UploadDownloadFiles.img_gallery!=null){
            UploadDownloadFiles.img_gallery.setVisibility(View.GONE);
        }

        if(UploadDownloadFiles.img_logout!=null){
            UploadDownloadFiles.img_logout.setVisibility(View.GONE);
        }


        UploadDownloadFiles.img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag=1;
                selectImage(1);
            }
        });

        UploadDownloadFiles.img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag=0;
                selectImage(0);
            }
        });



        btn_select_photo = (Button) findViewById(R.id.btn_select_photo);
        btn_upload_photo = (Button) findViewById(R.id.btn_upload_photo);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_select.setVisibility(View.GONE);
        btn_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        GrantPicturePermission();

                    } else {
                        selectImage(myflag);
                    }

                } else {
                    selectImage(myflag);
                }

            }
        });
        btn_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDefault) {
                    //if (!btn_select.getText().toString().equalsIgnoreCase("SELECT")) {
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }
                    getAddressFromLatLong(mContext, lat, lng, "PhotoAddressNew");
                    /*} else {
                        Utils.Alert(getResources().getString(R.string.alert_select_type), mContext);
                    }*/
                } else {
                    Utils.Alert(getResources().getString(R.string.alert_select_photo), mContext);
                }
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhotoJSON.trim().length() > 0) {
                    try {
                        arrayException.clear();
                        JSONArray list = new JSONArray(PhotoJSON);
                        for (int i = 0; i < list.length(); i++) {

                            JSONObject value = list.optJSONObject(i);
                            modelException = new ModelException();
                            modelException.setListID(value.optString("ListID"));
                            modelException.setListValue(value.optString("ListValue"));
                            arrayException.add(modelException);
                        }

                        DialogueWithListException(arrayException);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "AddressPhotoNew");
                }

            }
        });


        options[0] = mContext.getResources().getString(R.string.str_select_camera);
        options[1] = mContext.getResources().getString(R.string.str_select_gallery);
        options[2] = mContext.getResources().getString(R.string.str_cancel);
        isDefault = true;
        img_photo = (ImageView) findViewById(R.id.img_photo);
        edt_remark = (EditText) findViewById(R.id.edt_remark);

        arrayException = new ArrayList<>();
        btn_no_photo = (Button) findViewById(R.id.btn_no_photo);

        btn_no_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelPicMorePhotos();
            }
        });

        UploadDownloadFiles.tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);

        gridDynamic = findViewById(R.id.grid_dynamicgrid);
        adapter=new GridElementAdapter(mContext);
        gridDynamic.setAdapter(adapter);


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/BHSTemp");
        if(myDir.isDirectory()) {
            deleteDirectory(myDir);
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    public static class GridElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public GridElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return mySelectedPhotosGlobal.size();
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
            GridElementAdapter.ViewHolder h = null;
            if (v == null) {
                v = layoutInflater.inflate(R.layout.raw_uploadimage, null);
                h = new GridElementAdapter.ViewHolder();
                h.img=v.findViewById(R.id.img);
                h.uploadPorgress=v.findViewById(R.id.upload_progress);
                h.imgTick=v.findViewById(R.id.img_tick);
                h.tvDocType=v.findViewById(R.id.tv_doc_type);
                if(mySelectedPhotosGlobal.get(position).isChecked()){
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);
                }else{
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }
                h.tvDocType.setVisibility(View.GONE);
                final GridElementAdapter.ViewHolder finalH = h;
                Glide.with(mContext).load(mySelectedPhotosGlobal.get(position).getPath()).into(finalH.img);
                v.setTag(h);
            } else {
                h = (GridElementAdapter.ViewHolder) v.getTag();
                File imgFile = new File(mySelectedPhotosGlobal.get(position).getPath());
                final GridElementAdapter.ViewHolder finalH = h;
                h.tvDocType.setVisibility(View.GONE);
                Glide.with(mContext).load(mySelectedPhotosGlobal.get(position).getPath()).into(finalH.img);
                if(mySelectedPhotosGlobal.get(position).isChecked()){
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);
                }else{
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }
            }
            return v;
        }

        private class ViewHolder {
            private ImageView img,imgTick;
            private ProgressBar uploadPorgress;
            private TextView tvDocType;

        }
    }

    private void GrantPicturePermission() {

        ActivityCompat.requestPermissions(this, PERMISSIONS_PICTURE, REQUEST_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PermissionUtil.verifyPermissions(grantResults)) {
            selectImage(myflag);
        }
    }

    private void CancelPicMorePhotos() {
        isDefault = true;
        img_photo.setImageResource(R.drawable.ic_photo);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                mContext);
        // Setting Dialog Title
        alertDialog.setTitle(null);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        // Setting Icon to Dialog
        alertDialog.setMessage(mContext.getResources().getString(R.string.alert_error_want_more_photo));
        //  alertDialog.setIcon(R.drawable.dialog_icon);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(mContext.getResources().getString(R.string.alert_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // finish();
                        dialog.cancel();
                        if (isSign) {
                            Intent intent = new Intent(mContext, SignatureActivity.class);
                            intent.putExtra("Str_JobNo", Str_JobNo);
                            intent.putExtra("Str_Sts", Str_Sts);
                            intent.putExtra("Str_TripNo", Str_TripNo);
                            startActivity(intent);
                            PhotoUploadNew.activity.finish();
                        } else {
                            PhotoUploadNew.activity.finish();
                        }

                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(mContext.getResources().getString(R.string.alert_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();

                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }

    public void getAddressFromLatLong(final Context mContext, double LATITUDE, double LONGITUDE, final String redirectKey) {
        // Tag used to cancel the request
        /*String tag_json_obj = "json_obj_req";
        final String[] fulladdress = {""};
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + LATITUDE + "," + LONGITUDE + "&sensor=true";
        Utils.LogI("API URL", url, mContext);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (redirectKey.equalsIgnoreCase("PhotoAddressNew")) {
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
*/


        JSONObject response = new JSONObject();
        try {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("status", "1");
            HashMap<String, String> hasInner = new HashMap<>();
            hasInner.put("formatted_address", "NA");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(new JSONObject(hasInner));
            response = new JSONObject(hashMap);
            response.put("results", jsonArray);
            showResponse(response.toString(), redirectKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class uploadphoto extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


            Log.e("PARAM====>", "==========" + IMEINUMBER + "\n" + ClientID + "\n" + LATITUDE + "\n" + LONGITUDE + "\n" + ADDRESS + "\n" + GPS_STATUS + "\n" + DriverID + "\n"
                    + REMARK + "\n" + STR_REV + "\n" + Str_Sts + "\n" + imagename + "\n");

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm1.compress(Bitmap.CompressFormat.JPEG, 75, bos);
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
                reqEntity.addPart("Str_TripNo", new StringBody(Str_TripNo));
                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(UploadDownloadFiles.ImageID));
                reqEntity.addPart("Rev_Name", new StringBody(UploadDownloadFiles.Sent_By));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody("Submitted"));
                Str_Sts = UploadDownloadFiles.Str_Sts;
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
            boolean isSucess = false;
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    startSound();
                    if (mySelectedPhotos.size() > 0) {
                        mySelectedPhotos.remove(0);
                    }

                    tv_upload_counter.setVisibility(View.VISIBLE);
                    tv_upload_counter.setText("Uploaded ("+(totalPhoto-mySelectedPhotos.size())+"/"+totalPhoto+") Photos...");
                    if (mySelectedPhotos.size() > 0) {
                        Glide.with(mContext)
                                .load(mySelectedPhotos.get(uploadCounter).getPath())
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                        isDefault = false;
                                        bm1 = resource;
                                        new uploadphoto().execute();
                                    }
                                });

                    } else {
                        isSucess = true;
                        img_photo.setVisibility(View.VISIBLE);
                        isDefault = true;
                        btn_select_photo.setVisibility(View.VISIBLE);
                        selectedImageGallery.setVisibility(View.GONE);
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_uploaded), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isSucess) {
                UploadDownloadFiles.tabHost.setCurrentTab(0);
                UploadDownloadFiles.CallAPI();
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    isDefault = false;
                    img_photo.setVisibility(View.VISIBLE);
                    img_photo.setImageResource(R.drawable.ic_photo);
                    selectedImageGallery.setVisibility(View.GONE);
                    btn_select_photo.setVisibility(View.VISIBLE);

                }
            }


            //PicMorePhotos();

            if (galleryImageAdapter != null) {
                galleryImageAdapter.notifyDataSetChanged();
            }
        }

        public  void startSound() {
            AssetFileDescriptor afd = null;
            try {
                afd = mContext.getResources().getAssets().openFd("audio.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaPlayer player = new MediaPlayer();
            try {
                assert afd != null;
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.start();
        }

        private void PicMorePhotos() {
            isDefault = true;
            img_photo.setImageResource(R.drawable.ic_photo);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    mContext);
            // Setting Dialog Title
            alertDialog.setTitle(null);
            alertDialog.setCancelable(false);
            // Setting Dialog Message
            // Setting Icon to Dialog
            alertDialog.setMessage(mContext.getResources().getString(R.string.alert_upload_more_photo));
            //  alertDialog.setIcon(R.drawable.dialog_icon);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(mContext.getResources().getString(R.string.alert_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // finish();
                            dialog.cancel();
                            selectImage(myflag);
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(mContext.getResources().getString(R.string.alert_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                            if (isSign) {
                                Intent intent = new Intent(mContext, SignatureActivity.class);
                                intent.putExtra("Str_JobNo", Str_JobNo);
                                intent.putExtra("Str_Sts", Str_Sts);
                                intent.putExtra("Str_TripNo", Str_TripNo);
                                startActivity(intent);
                                PhotoUploadNew.activity.finish();
                            } else {
                                UploadDownloadFiles.tabHost.setCurrentTab(0);
                            }
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

    }

    public static class GalleryImageAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater layoutInflater;

        public GalleryImageAdapter(Context context) {
            mContext = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mySelectedPhotos.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // Override this method according to your need
        public View getView(final int index, View view, ViewGroup viewGroup) {
            // TODO Auto-generated method stub
            View v = view;
            ViewHolder h = null;

            if (v == null) {
                v = layoutInflater.inflate(R.layout.layout_gallery_image, null);
                h = new ViewHolder();
                h.imgSelectedImage = (ImageView) v.findViewById(R.id.img_job_photo);
                h.imgDelImage = (ImageView) v.findViewById(R.id.img_delete_image);

                Log.i("Preview Image Path", mySelectedPhotos.get(index).getPath());
                File imgFile = new File(mySelectedPhotos.get(index).getPath());
                if (imgFile.exists()) {
                    final ViewHolder finalH = h;
                    /*Glide.with(mContext)
                            .load(mySelectedPhotos.get(index).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    finalH.imgSelectedImage.setImageBitmap(resource);
                                }
                            });*/
                    Picasso.with(mContext).load(imgFile).fit().centerCrop().into(finalH.imgSelectedImage);

                }

                h.imgDelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mySelectedPhotos.remove(index);
                        if (mySelectedPhotos.size() == 0) {
                            btn_select_photo.setVisibility(View.VISIBLE);
                            img_photo.setVisibility(View.VISIBLE);
                            selectedImageGallery.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });


                h.imgSelectedImage.setScaleType(ImageView.ScaleType.FIT_XY);
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                File imgFile = new File(mySelectedPhotos.get(index).getPath());
                Log.i("Preview Image Path", mySelectedPhotos.get(index).getPath());
                if (imgFile.exists()) {
                    final ViewHolder finalH = h;
                    /*Glide.with(mContext)
                            .load(mySelectedPhotos.get(index).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    finalH.imgSelectedImage.setImageBitmap(resource);
                                }
                            });*/

                    Picasso.with(mContext).load(imgFile).fit().centerCrop().into(finalH.imgSelectedImage);

                }

                h.imgDelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mySelectedPhotos.remove(index);
                        if (mySelectedPhotos.size() == 0) {
                            btn_select_photo.setVisibility(View.VISIBLE);
                            selectedImageGallery.setVisibility(View.GONE);
                            img_photo.setVisibility(View.VISIBLE);
                        }
                        notifyDataSetChanged();
                    }
                });

                h.imgSelectedImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            return v;
        }

        private class ViewHolder {
            private ImageView imgSelectedImage, imgDelImage;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        /*Gallery code end*/
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ID) {
            if (resultCode != 0) {
                bm1 = ImagePicker.getImageFromResult(mContext, resultCode, data);
                imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                isDefault = false;

                PhotoEntryUploading photoEntryUploading=new PhotoEntryUploading();
                photoEntryUploading.setPath(SaveImage(bm1));
                photoEntryUploading.setChecked(false);
                mySelectedPhotosGlobal.add(photoEntryUploading);
                adapter.notifyDataSetChanged();

                new uploadPhotoNew().execute();
                gridDynamic.getChildAt(gridDynamic.getChildCount());

                if(mySelectedPhotosGlobal.size()>0){
                    linear_center.setVisibility(View.GONE);
                    UploadDownloadFiles.img_logout.setVisibility(View.VISIBLE);
                    UploadDownloadFiles.img_gallery.setVisibility(View.VISIBLE);
                }


            }
        }
        // TODO use bitmap
    }

    public class uploadPhotoNew extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm1.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
               // HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload.jsp");
                HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload_V1.jsp");
                ByteArrayBody bab = new ByteArrayBody(data, imagename);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(Utils.getPref(getString(R.string.pref_IMEINumber),mContext)));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(Utils.getPref(getString(R.string.pref_Client_ID),mContext)));
                reqEntity.addPart("Str_Lat", new StringBody(""+gps.getLatitude()));
                reqEntity.addPart("Str_Long", new StringBody(""+gps.getLongitude()));
                reqEntity.addPart("Str_Loc", new StringBody("NA"));
                reqEntity.addPart("Str_GPS", new StringBody("NA"));
                reqEntity.addPart("Str_DriverID", new StringBody(Utils.getPref(getString(R.string.pref_driverID),mContext)));
                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody(Str_TripNo));
                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(STR_REV));
                reqEntity.addPart("Rev_Name", new StringBody(Str_JobNo));
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
            //  pDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    //startSound();
                    mySelectedPhotosGlobal.get(uploadCounter).setChecked(true);
                    adapter.notifyDataSetChanged();
                    uploadCounter++;
                    if (mySelectedPhotosGlobal.size() > 0) {
                        Glide.with(mContext)
                                .load(mySelectedPhotosGlobal.get(0).getPath())
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                        isDefault = false;
                                        bm1= resource;
                                        new uploadPhotoNew().execute();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }
    }


    private String SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/BHSTemp");
        if(!myDir.isDirectory()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isFromMultipleSelection) {
            Utils.isFromMultipleSelection = false;
            if (mySelectedPhotos != null) {
                if (mySelectedPhotos.size() > 0) {
                    for(int i=0;i<mySelectedPhotos.size();i++){
                        PhotoEntryUploading photoEntryUploading=new PhotoEntryUploading();
                        photoEntryUploading.setPath(mySelectedPhotos.get(i).getPath());
                        photoEntryUploading.setChecked(false);
                        mySelectedPhotosGlobal.add(photoEntryUploading);
                    }
                    adapter.notifyDataSetChanged();

                    Glide.with(mContext)
                            .load(mySelectedPhotosGlobal.get(uploadCounter).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                    isDefault = false;
                                    bm1= resource;
                                    new uploadPhotoNew().execute();
                                }
                            });
                }

                if(mySelectedPhotosGlobal.size()>0){
                    linear_center.setVisibility(View.GONE);
                    UploadDownloadFiles.img_logout.setVisibility(View.VISIBLE);
                    UploadDownloadFiles.img_gallery.setVisibility(View.VISIBLE);
                }

            }
        } else {
            if (mySelectedPhotos != null) {
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        imagename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        bm1 = BitmapFactory.decodeFile(selectedImagePath, o);

        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    RotateBitmap(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    RotateBitmap(bm, 180);
                    break;
                // etc.
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isDefault = false;
        bm1 = BitmapFactory.decodeFile(selectedImagePath);
        img_photo.setImageBitmap(bm);
    }

    private void onSelectFromCameraResult(Intent data) {
        /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        img_photo.setImageBitmap(thumbnail);*/
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri capturedURI = fileUri;

        String selectedImagePath = camFile.toString();

        Log.i("CAPTURED IMAGE PATH", selectedImagePath);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        imagename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        bm1 = BitmapFactory.decodeFile(selectedImagePath, o);

        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    RotateBitmap(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    RotateBitmap(bm, 180);
                    break;
                // etc.
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isDefault = false;
        bm1 = BitmapFactory.decodeFile(selectedImagePath);
        img_photo.setImageBitmap(bm1);
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        imagename = System.currentTimeMillis() + ".jpg";
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isDefault = false;
        img_photo.setImageBitmap(thumbnail);
        bm1 = BitmapFactory.decodeFile(destination.getPath().toString());

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void selectImage(int flag) {
        imagename = "";
        StrictMode.VmPolicy.Builder builderpolicy = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderpolicy.build());
        imagename = "";
        if(flag==0){
            Intent intent = new Intent(mContext, GalleryActivity.class);
            intent.putExtra("Flag", 1);
            mContext.startActivity(intent);
        }else{
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            PHOTO_TYPE = 0;
        }
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutPhotoNew");
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

    private void getRevList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Rev List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + DriverID + "&Str_Event=" + Str_Event, "revlist");
    }

    /*public void DialogueWithListException(final ArrayList<ModelException> exceptionlist) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle(mContext.getResources().getString(R.string.alert_select_type));
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[exceptionlist.size()];
        for (int i = 0; i < exceptionlist.size(); i++) {
            values[i] = (i + 1) + "." + exceptionlist.get(i).getListValue();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapter);
        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                STR_REV = exceptionlist.get(position).getListValue();
                btn_select.setText(STR_REV);

            }
        });
        dialog.show();
    }*/

    public void DialogueWithListException(final ArrayList<ModelException> exceptionlist) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_select_type));
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
                                    int position, long id) {
                dialoglist.dismiss();
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                STR_REV = exceptionlistImplemented.get(position).getListValue();
                btn_select.setText(STR_REV);

            }
        });


        dialoglist.show();
    }


    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutPhotoNew")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    PhotoUploadNew.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("revlist")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {

                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListID(value.optString("ListID"));
                        modelException.setListValue(value.optString("ListValue"));
                        arrayException.add(modelException);
                    }

                    DialogueWithListException(arrayException);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("PhotoAddressNew")) {
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

                UploadUrl = APIUtils.BaseUrl + "upload.jsp";
                UploadingImageName = "" + System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);

                ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
                LATITUDE = lat;
                LONGITUDE = lng;
                REMARK = PhotoUploadNew.edt_remark.getText().toString().trim();
                IMEINUMBER = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                GPS_STATUS = GPSStatus;
                ADDRESS = address;

                if (mySelectedPhotos.size() > 0) {
                    Glide.with(mContext)
                            .load(mySelectedPhotos.get(0).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                    isDefault = false;
                                    bm1 = resource;

                                    if(PHOTO_TYPE!=1) {
                                        pDialog.show();
                                        new uploadphoto().execute();
                                    }else {
                                        //START SERVICE
                                        if (pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }
                                        databaseHandlerOfflineURL.enterPhotoDetail(mySelectedPhotos, IMEINUMBER, "Android", ClientID, LATITUDE, LONGITUDE, ADDRESS, GPS_STATUS,
                                                DriverID, "Update", Str_TripNo, REMARK, Str_JobNo, STR_REV, "NA", Str_Sts, "NA", "Photo");
                                        if (!Utils.isMyServiceRunning(ImageUploadService.class, mContext)) {
                                            mContext.startService(new Intent(mContext, ImageUploadService.class));
                                        }

                                        PhotoUploadNew.activity.finish();
                                    }}
                            });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("AddressPhotoNew")) {
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

                getRevList(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}