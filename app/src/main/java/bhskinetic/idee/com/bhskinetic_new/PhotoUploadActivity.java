package bhskinetic.idee.com.bhskinetic_new;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
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

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.litao.android.lib.entity.PhotoEntry;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class PhotoUploadActivity extends Activity {
    //Activity Object
    public static Activity activity;
    public static Context mContext;
    public static TextView tv_header;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static ImageView img_gallery;
    public static ImageView img_newmessage;
    public static TrackGPS gps;
    public static Button btn_select_photo;
    public static Button btn_upload_photo;
    public static Button btn_select;
    public static String imagename;
    public static CharSequence[] options = new CharSequence[3];
    private static final int GALLERY = 6;
    private static final int TAKEPHOTO = 5;
    public static ImageView img_photo;
    public static ImageView img_notification;
    public static TextView tv_notificationcounter;
    public static FrameLayout frame_notification;
    public static Bitmap bm1;
    public static boolean isPONO = false;
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
    public static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    public static List<PhotoEntry> mySelectedPhotos;
    public static Gallery selectedImageGallery;
    Uri fileUri;
    File camFile;
    public static boolean isAssignJob = false;

    private String profilepath = "";
    public static ArrayList<Bitmap> bitmapModelArrayList;
    GalleryImageAdapter galleryImageAdapter;
    public static int PHOTO_TYPE = 0;
    public static TextView tv_upload_counter;
    public static int totalPhoto = 0;
    public static int myflag = 0;
    public DatabaseHandlerOfflineURL databaseHandlerOfflineURL;
    public static ArrayList<PhotoEntryUploading> mySelectedPhotosGlobal;
    public static GridElementAdapter adapter;
    public static GridView gridDynamic;
    public static int uploadCounter = 0;
    public static LinearLayout linear_center;
    public static ImageView ic_gallery_big, ic_cam_big;
    public static Button btn_phototype;
    public static Button btn_hawb_member;
    public static String workgroup = "";
    public static boolean isDirectPOD = false;
    public static String isViaAssign = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photoupload);
        mContext = PhotoUploadActivity.this;
        databaseHandlerOfflineURL = new DatabaseHandlerOfflineURL(mContext);
        activity = PhotoUploadActivity.this;
        Init();
    }

    private void getImageHistory(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Image History", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + DriverID + "&Str_JobView=Gallery&Str_JobStatus=Image-History&Str_TripNo=NA&Str_JobNo=" + Str_JobNo + "&Str_JobExe=NA&Str_JobFor=" + Utils.getPref(mContext.getString(R.string.pref_Client_Name), mContext), "ImageHistory");
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isFromMultipleSelection) {
            Utils.isFromMultipleSelection = false;

            //ApplyImages();

        } else {
            if (mySelectedPhotos != null) {
            }
        }
    }

    private void ApplyImages() {
        if (mySelectedPhotos != null) {
            if (mySelectedPhotos.size() > 0) {
                for (int i = 0; i < mySelectedPhotos.size(); i++) {
                    PhotoEntryUploading photoEntryUploading = new PhotoEntryUploading();
                    photoEntryUploading.setPath(mySelectedPhotos.get(i).getPath());
                    photoEntryUploading.setImageID(mySelectedPhotos.get(i).getImageId());
                    photoEntryUploading.setDocType(STR_REV);
                    photoEntryUploading.setChecked(false);
                    mySelectedPhotosGlobal.add(photoEntryUploading);
                }
                adapter.notifyDataSetChanged();


                if (mySelectedPhotosGlobal.get(uploadCounter).getImageID() == 0) {
                    Glide.with(mContext)
                            .load(mySelectedPhotosGlobal.get(uploadCounter).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                    isDefault = false;
                                    bm1 = resource;
                                    new uploadPhotoNew().execute();
                                }
                            });
                } else {
                    imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + "." + mySelectedPhotosGlobal.get(uploadCounter).getPath().split("_")[1];
                    isDefault = false;
                    new uploadPhotoNew().execute();
                }

                if (mySelectedPhotosGlobal.size() > 0) {
                    linear_center.setVisibility(View.GONE);
                    img_logout.setVisibility(View.VISIBLE);
                    img_gallery.setVisibility(View.VISIBLE);
                } else {
                    img_logout.setVisibility(View.VISIBLE);
                    img_gallery.setVisibility(View.GONE);
                }
            }
        }
    }

    private static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
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
                h.img = v.findViewById(R.id.img);
                h.uploadPorgress = v.findViewById(R.id.upload_progress);
                h.imgTick = v.findViewById(R.id.img_tick);
                h.tvDocType = v.findViewById(R.id.tv_doc_type);

                if (mySelectedPhotosGlobal.get(position).isChecked()) {
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);
                } else {
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }

                h.tvDocType.setText(mySelectedPhotosGlobal.get(position).getDocType());
                final GridElementAdapter.ViewHolder finalH = h;
                if (mySelectedPhotosGlobal.get(position).getImageID() == 0) {
                    Glide.with(mContext).load(mySelectedPhotosGlobal.get(position).getPath()).into(finalH.img);
                } else {
                    finalH.img.setImageResource(R.drawable.ic_file);
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mySelectedPhotosGlobal.get(position).getPath().contains("http")) {
                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
                            String mimeType = myMime.getMimeTypeFromExtension(fileExt(mySelectedPhotosGlobal.get(position).getPath()).substring(1));
                            newIntent.setDataAndType(Uri.parse(mySelectedPhotosGlobal.get(position).getPath()), mimeType);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                v.setTag(h);
            } else {
                h = (GridElementAdapter.ViewHolder) v.getTag();
                final GridElementAdapter.ViewHolder finalH = h;
                if (mySelectedPhotosGlobal.get(position).getImageID() == 0) {
                    Glide.with(mContext).load(mySelectedPhotosGlobal.get(position).getPath()).into(finalH.img);
                } else {
                    finalH.img.setImageResource(R.drawable.ic_file);
                }
                h.tvDocType.setText(mySelectedPhotosGlobal.get(position).getDocType());
                if (mySelectedPhotosGlobal.get(position).isChecked()) {
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);

                } else {
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mySelectedPhotosGlobal.get(position).getPath().contains("http")) {
                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
                            String mimeType = myMime.getMimeTypeFromExtension(fileExt(mySelectedPhotosGlobal.get(position).getPath()).substring(1));
                            newIntent.setDataAndType(Uri.parse(mySelectedPhotosGlobal.get(position).getPath()), mimeType);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private ImageView img, imgTick;
            private TextView tvDocType;
            private ProgressBar uploadPorgress;
        }
    }

    public static void startSound() {
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
                byte[] data = null;

                if (mySelectedPhotosGlobal.get(uploadCounter).getImageID() == 0) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm1.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    data = bos.toByteArray();
                } else {
                    InputStream iStream = getContentResolver().openInputStream(Uri.parse(mySelectedPhotosGlobal.get(uploadCounter).getPath().split("_")[0]));
                    data = getBytes(iStream);
                }

                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload.jsp");
                HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload_V1.jsp");
                ByteArrayBody bab = new ByteArrayBody(data, imagename);
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                UploadUrl = APIUtils.BaseUrl + "upload_V1.jsp";
                UploadingImageName = "" + System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);

                ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Driver_Name), mContext);
                LATITUDE = "" + gps.getLatitude();
                LONGITUDE = "" + gps.getLongitude();
                IMEINUMBER = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                GPS_STATUS = "NA";
                ADDRESS = "NA";
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

                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(IMEINUMBER));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(ClientID));
                reqEntity.addPart("Str_Lat", new StringBody(LATITUDE));
                reqEntity.addPart("Str_Long", new StringBody(LONGITUDE));
                reqEntity.addPart("Str_Loc", new StringBody(ADDRESS));
                reqEntity.addPart("Str_GPS", new StringBody(GPS_STATUS));
                reqEntity.addPart("Str_DriverID", new StringBody(DriverID));
                if (mySelectedPhotosGlobal.get(uploadCounter).getImageID() == 0) {

                    if (bm1.getWidth() > bm1.getHeight()) {
                        //meaning the image is landscape view
                        Log.i("Str_PhotoSize", "L");
                        reqEntity.addPart("Str_PhotoSize", new StringBody("L"));
                        //  Toast.makeText(mContext,"LANDSCAPE",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Str_PhotoSize", "P");
                        reqEntity.addPart("Str_PhotoSize", new StringBody("P"));
                        // Toast.makeText(mContext,"PORTRAIT",Toast.LENGTH_SHORT).show();
                    }
                }
                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody(btn_hawb_member.getText().toString().trim()));
                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(Str_JobNo));
                reqEntity.addPart("Rev_Name", new StringBody(STR_REV));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody(Str_Sts));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(imagename));
                reqEntity.addPart("fType", new StringBody("Photo"));
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
                    mySelectedPhotosGlobal.get(uploadCounter).setChecked(true);
                    adapter.notifyDataSetChanged();
                    uploadCounter++;

                    if (uploadCounter == mySelectedPhotosGlobal.size()) {
                        //  Toast.makeText(mContext,"last",Toast.LENGTH_SHORT).show();
                        if (isAssignJob) {
                            createJob(ADDRESS, LATITUDE, LONGITUDE, GPS_STATUS);
                        }
                    } else {


                        if (mySelectedPhotosGlobal.size() > 0) {
                            if (mySelectedPhotosGlobal.get(uploadCounter).getImageID() == 0) {
                                Glide.with(mContext)
                                        .load(mySelectedPhotosGlobal.get(uploadCounter).getPath())
                                        .asBitmap()
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                                isDefault = false;
                                                bm1 = resource;
                                                new uploadPhotoNew().execute();
                                            }
                                        });
                            } else {
                                imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + "." + mySelectedPhotosGlobal.get(uploadCounter).getPath().split("_")[1];
                                isDefault = false;
                                new uploadPhotoNew().execute();
                            }
                        }
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

    private byte[] getBytes(InputStream iStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while (true) {
            try {
                if (!((len = iStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void Init() {
        isAssignJob = false;
        snap = "";
        tv_header = (TextView) findViewById(R.id.tv_header);
        Str_JobNo = "";
        Str_TripNo = "";
        uploadCounter = 0;
        isPONO = false;
        isDirectPOD = false;
        selectedImageGallery = (Gallery) findViewById(R.id.selected_image_gallery);
        selectedImageGallery.setSpacing(50);
        bitmapModelArrayList = new ArrayList<Bitmap>();
        bitmapModelArrayList.clear();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        myflag = 0;
        PHOTO_TYPE = 0;
        mySelectedPhotos = new ArrayList<>();
        mySelectedPhotosGlobal = new ArrayList<>();

        if (mySelectedPhotosGlobal != null) {
            mySelectedPhotosGlobal.clear();
        }

        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DashboardActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_gallery = (ImageView) findViewById(R.id.img_gallery);
        img_newmessage = (ImageView) findViewById(R.id.img_newmessage);
        img_gallery.setVisibility(View.VISIBLE);
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
            if (b.get("isPONO") != null) {
                isPONO = true;

            }

            if (b.getString("isViaAssign") != null) {
                isViaAssign = b.getString("isViaAssign");
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

            if (b.getString("workGroup") != null) {
                workgroup = b.getString("workGroup");
            }

            if (b.getString("isAssignJob") != null) {
                isAssignJob = Boolean.parseBoolean(b.getString("isAssignJob"));
            }

            Str_Sts = b.getString("Str_Sts");
            Str_Event = b.getString("Str_Event");
        }
        tv_header.setText(Str_JobNo);

        if (isAssignJob) {
            tv_header.setText("Add Job");
        }


        linear_center = findViewById(R.id.linear_center);
        ic_gallery_big = findViewById(R.id.ic_gallery_big);
        ic_cam_big = findViewById(R.id.ic_cam_big);
        img_notification = findViewById(R.id.img_notification);
        tv_notificationcounter = findViewById(R.id.tv_notificationcounter);
        frame_notification = findViewById(R.id.frame_notification);


        if (isSign) {
            img_notification.setVisibility(View.VISIBLE);
            frame_notification.setVisibility(View.VISIBLE);
            tv_notificationcounter.setVisibility(View.GONE);
            img_notification.setImageResource(R.drawable.ic_signature);
            img_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SignatureActivity.class);
                    intent.putExtra("Str_JobNo", Str_JobNo);
                    intent.putExtra("Str_Sts", Str_Sts);
                    intent.putExtra("Str_TripNo", Str_TripNo);
                    startActivity(intent);
                    PhotoUploadActivity.activity.finish();
                }
            });
        }

        img_gallery.setImageResource(R.drawable.ic_camera);
        img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag = 1;
                selectImage(1);
            }
        });

        img_newmessage.setImageResource(R.drawable.ic_file);
        img_newmessage.setVisibility(View.VISIBLE);
        img_newmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag = 0;
                selectImage(0);

            }
        });

        img_logout.setImageResource(R.drawable.ic_pod);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPODData("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "");
            }
        });

        if (b.getString("isDirectPOD") != null) {
            isDirectPOD = true;
            getPODData("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "");
        }


        ic_cam_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag = 1;
                selectImage(1);
            }
        });

        ic_gallery_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myflag = 0;
                selectImage(0);
            }
        });


        btn_select_photo = (Button) findViewById(R.id.btn_select_photo);
        btn_upload_photo = (Button) findViewById(R.id.btn_upload_photo);


        btn_select = (Button) findViewById(R.id.btn_select);
        tv_upload_counter = (TextView) findViewById(R.id.tv_upload_counter);
        btn_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        GrantPicturePermission();

                    } else {
                        selectImage(0);
                    }

                } else {
                    selectImage(0);
                }

            }
        });
        btn_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackGPS gps = new TrackGPS(mContext);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                    return;
                }

                if (!isDefault) {
                    if (!btn_select.getText().toString().equalsIgnoreCase("SELECT")) {
                        if (isAssignJob) {
                            if (edt_remark.getText().toString().trim().length() == 0) {
                                Utils.Alert("ENTER REMARK", mContext);
                                return;
                            }
                        }

                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }
                        getAddressFromLatLong(mContext, lat, lng, "PhotoAddress");

                        pDialog.show();

                    } else {
                        Utils.Alert(getResources().getString(R.string.alert_select_type), mContext);
                    }
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

                        DialogueWithListException(arrayException, -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "AddressPhoto");
                }

            }
        });


        options[0] = mContext.getResources().getString(R.string.str_select_camera);
        options[1] = mContext.getResources().getString(R.string.str_select_gallery);
        options[2] = mContext.getResources().getString(R.string.str_cancel);
        isDefault = true;
        img_photo = (ImageView) findViewById(R.id.img_photo);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_remark = (EditText) findViewById(R.id.edt_remark);

        String driverType = Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext);
        if (driverType.equalsIgnoreCase("Planner")) {
            if (BookingJobActivity.btn_work_group != null) {
                edt_remark.setText(BookingJobActivity.btn_work_group.getText().toString().trim());
            }
        }

        arrayException = new ArrayList<>();
        btn_no_photo = (Button) findViewById(R.id.btn_no_photo);

        btn_no_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelPicMorePhotos();
            }
        });

        if (isAssignJob) {
            btn_no_photo.setVisibility(View.GONE);
            btn_upload_photo.setText("SUBMIT");
        }

        gridDynamic = findViewById(R.id.grid_dynamicgrid);
        adapter = new GridElementAdapter(mContext);
        gridDynamic.setAdapter(adapter);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/BHSTemp");
        if (myDir.isDirectory()) {
            deleteDirectory(myDir);
        }


        img_gallery.setVisibility(View.GONE);


        if (!isAssignJob) {
            getImageHistory("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON");
        }


    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
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
                            PhotoUploadActivity.activity.finish();
                        } else {
                            PhotoUploadActivity.activity.finish();
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


    private void selectImage(int flag) {
        StrictMode.VmPolicy.Builder builderpolicy = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderpolicy.build());
        imagename = "";
        DialoguePhoto();
    }


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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "LogoutPhoto");
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
                ///storage/emulated/0/DCIM/Camera/IMG_20191008_142113.jpg
                ///storage/emulated/0/Download/images (8).jpeg
                Log.e("Preview Image Path", mySelectedPhotos.get(index).getPath());
                File imgFile = new File(mySelectedPhotos.get(index).getPath());
                if (imgFile.exists()) {
                    final ViewHolder finalH = h;
                  /*  Glide.with(mContext)
                            .load(mySelectedPhotos.get(index).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    finalH.imgSelectedImage.setImageBitmap(resource);
                                }
                            });*/

                    Glide.with(mContext).load(mySelectedPhotos.get(index).getPath()).into(finalH.imgSelectedImage);

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
                Log.e("Preview Image Path", mySelectedPhotos.get(index).getPath());
                if (imgFile.exists()) {
                    final ViewHolder finalH = h;
                   /* Glide.with(mContext)
                            .load(mySelectedPhotos.get(index).getPath())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    finalH.imgSelectedImage.setImageBitmap(resource);
                                }
                            });*/
                    Glide.with(mContext).load(mySelectedPhotos.get(index).getPath()).into(finalH.imgSelectedImage);

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
            boolean isSucess = false;

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {

                    if (mySelectedPhotos.size() > 0) {
                        mySelectedPhotos.remove(0);
                    }

                    tv_upload_counter.setVisibility(View.GONE);
                    tv_upload_counter.setText("Uploaded (" + (totalPhoto - mySelectedPhotos.size()) + "/" + totalPhoto + ") Photos...");

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
                                        new uploadphoto().execute();
                                    }
                                });


                    } else {
                        isSucess = true;
                        img_photo.setVisibility(View.VISIBLE);
                        isDefault = true;
                        btn_select.setVisibility(View.VISIBLE);
                        img_photo.setImageResource(R.drawable.ic_photo);
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_uploaded), Toast.LENGTH_SHORT).show();
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        if (isAssignJob) {
                            createJob(ADDRESS, LATITUDE, LONGITUDE, GPS_STATUS);
                        } else {
                            if (PHOTO_TYPE == 1) {
                                if (isSign) {
                                    Intent intent = new Intent(mContext, SignatureActivity.class);
                                    intent.putExtra("Str_JobNo", Str_JobNo);
                                    intent.putExtra("Str_Sts", Str_Sts);
                                    intent.putExtra("Str_TripNo", Str_TripNo);
                                    startActivity(intent);
                                    PhotoUploadActivity.activity.finish();
                                } else {
                                    PhotoUploadActivity.activity.finish();
                                }
                            }
                        }


                    }


                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (galleryImageAdapter != null) {
                galleryImageAdapter.notifyDataSetChanged();
            }

            if (isSucess && PHOTO_TYPE == 0) {
                if (!isAssignJob) {

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    PicMorePhotos();

                }
            }


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
                            com.github.dhaval2404.imagepicker.ImagePicker.with(PhotoUploadActivity.this)
                                    .compress(1024)
                                    .maxResultSize(1080, 1080)
                                    .cameraOnly()
                                    .start(PICK_IMAGE_ID);


                            //     Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
                            //   startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                          /*  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, PICK_IMAGE_ID);
                            } else {
                                Toast.makeText(mContext, "No camera app found", Toast.LENGTH_SHORT).show();
                            }*/

                            PHOTO_TYPE = 0;
                            //selectImage();

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
                                PhotoUploadActivity.activity.finish();
                            } else {
                                PhotoUploadActivity.activity.finish();
                            }
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

    }


    private void createJobViaAssign(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SNAP&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_BarCode=" + REMARK + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
        String tripNumber = REMARK;

        // String URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SNAP&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + _startDateStr + "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat + " " + timeslot2 + ":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim() + "&Str_WorkGroup=" + workgroup;

        if (URL.contains(" ")) {
            URL = URL.replace(" ", "%20");
        }

        APIUtils.sendRequest(mContext, "Create Job", URL, "createJobPhoto");
    }


    private void createJob(String address, String lat, String lng, String gpsStatus) {

        if (isViaAssign.equalsIgnoreCase("1")) {
            createJobViaAssign("", "" + lat, "" + lng, "");
        } else {
            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
            // String URL = "Order_Create.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + AssignJobActivity.namefromLocation + "&Str_PickUP_Dt=" + AssignJobActivity.btn_startdate.getText().toString().trim() + "&Str_Delivery_Loc=" + AssignJobActivity.nametoLocation + "&Str_Delivery_Dt=" + AssignJobActivity.btn_enddate.getText().toString().trim() + "&Str_OrderNo=" + "NA" + "&Str_NoofTrip=" + AssignJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + AssignJobActivity.nameVehicle;
            String tripNumber = REMARK;
            String driverType = Utils.getPref(mContext.getResources().getString(R.string.pref_drivertype), mContext);
            String endDate = BookingJobActivity.btn_enddate.getText().toString().trim();
            String _startDateStr = "";
            String timeslot1 = BookingJobActivity.timeSlotSelection.split("-")[0];
            String timeslot2 = BookingJobActivity.timeSlotSelection.split("-")[1];
            if (driverType.equalsIgnoreCase("Planner") || driverType.equalsIgnoreCase("CS User")) {
                String endDateStr = BookingJobActivity.btn_duration.getText().toString().trim();
                if (endDateStr.contains("Min")) {
                    endDateStr = endDateStr.replaceAll("Min", "").trim();
                }
                long TimeStampStart;


                _startDateStr = BookingJobActivity.selectedDat + " " + timeslot1 + ":00";
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                try {
                    Date startDate = format.parse(_startDateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(cal.MINUTE, Integer.parseInt(endDateStr));
                    endDate = format.format(cal.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            String URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SNAP&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + _startDateStr + "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat + " " + timeslot2 + ":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim() + "&Str_WorkGroup=" + workgroup;
            ;

            if (driverType.equalsIgnoreCase("Planner")) {
                URL = "Adhoc_Job_Create.jsp?Str_CreateMode=SNAP&Str_BarCode=" + tripNumber + "&Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_PickUP_Loc=" + BookingJobActivity.namefromLocation + "&Str_PickUP_Dt=" + _startDateStr + "&Str_Delivery_Loc=" + BookingJobActivity.nametoLocation + "&Str_Delivery_Dt=" + BookingJobActivity.selectedDat + " " + timeslot2 + ":00" + "&Str_NoofTrip=" + BookingJobActivity.edt_numberof_trip.getText().toString().trim() + "&Str_VehicleNo=" + BookingJobActivity.btn_container.getText().toString().trim() + "&Str_Customer=" + BookingJobActivity.btn_customer_list.getText().toString().trim() + "&Str_WorkGroup=" + workgroup;
            }

            if (URL.contains(" ")) {
                URL = URL.replace(" ", "%20");
            }
            APIUtils.sendRequest(mContext, "Create Job Photo", URL, "createJobPhoto");
        }
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        Log.d("", options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
    }

    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d("", "resizer: new bitmap width = " + bm.getWidth());
            i++;
        } while (bm.getWidth() < 400 && i < sampleSizes.length);

        return bm;
    }


    public static Uri getImageUriFromBitmap(Bitmap bitmap) {
        // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String saveImageToStorage(Bitmap bitmap) {
        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = new File(storageDir, imagename);
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return imageFile.getAbsolutePath() ;
        }catch (Exception e){
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        /*Gallery code end*/
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ID && resultCode == Activity.RESULT_OK) {
            try {
                bm1 = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

               // bm1 = (Bitmap) data.getExtras().get("data");
                profilepath = saveImageToStorage(bm1).toString();
                Log.e("currentPhotoPath", "=$currentPhotoPath");

                //    bm1 = null;
                //  bm1 = ImagePicker.getImageFromResult(mContext, resultCode, data);
                //    bm1 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());

                isDefault = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bm1 == null) {
                Toast.makeText(mContext, "NULL", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "!NULL", Toast.LENGTH_SHORT).show();
            }

            /*    if (requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK) {
                    Bitmap compressedBitmap = null;
                    try {
                       // compreonssedBitmap = compressBitmap((Bitmap) data.getExtras().get("data"), 70); // Adjust compression quality as needed
                    compressedBitmap = getImageResized(mContext,getImageUriFromBitmap((Bitmap) data.getExtras().get("data")));

                    } catch (Exception e) {
                        Log.e("CAM", "Error compressing bitmap: " + e.getMessage());
                    }
                    bm1 = null;
                    if (compressedBitmap != null) {
                        // Use the compressed bitmap here
                        //Toast.makeText(this, "Image captured and compressed successfully", Toast.LENGTH_SHORT).show();
                        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";

                        bm1 = compressedBitmap;
                    } else {
                        Toast.makeText(this, "Error compressing image", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
                }*/

            gridDynamic.getChildAt(gridDynamic.getChildCount());

            PhotoEntryUploading photoEntryUploading = new PhotoEntryUploading();
            photoEntryUploading.setPath(SaveImage(bm1));
            photoEntryUploading.setChecked(false);
            photoEntryUploading.setDocType(STR_REV);
            mySelectedPhotosGlobal.add(photoEntryUploading);
            adapter.notifyDataSetChanged();

            if (mySelectedPhotosGlobal.size() > 0) {
                linear_center.setVisibility(View.GONE);
                img_logout.setVisibility(View.VISIBLE);
                img_gallery.setVisibility(View.VISIBLE);
            }

            new uploadPhotoNew().execute();

        } else if (requestCode == 786) {
            mySelectedPhotos.clear();
            if (data.getClipData() != null) {
                try {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        Log.e("IMAGE_URI", imageUri.toString());
                        PhotoEntry photoEntry = new PhotoEntry();


                        if (isImage(getMimeType(imageUri))) {
                            photoEntry.setImageId(0);
                            photoEntry.setPath(imageUri.toString());
                        } else {
                            photoEntry.setImageId(1);
                            photoEntry.setPath(imageUri.toString() + "_" + getMimeType(imageUri));
                        }
                        mySelectedPhotos.add(photoEntry);

                        //do what do you want to do
                    }
                    ApplyImages();
                } catch (Exception e) {
                    Toast.makeText(mContext, "This file is not accessible", Toast.LENGTH_SHORT).show();
                }
            } else if (data.getData() != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    PhotoEntry photoEntry = new PhotoEntry();
                    photoEntry.setPath(selectedImageUri.toString());
                    if (isImage(getMimeType(selectedImageUri))) {
                        photoEntry.setImageId(0);
                        photoEntry.setPath(selectedImageUri.toString());
                    } else {
                        photoEntry.setImageId(1);
                        photoEntry.setPath(selectedImageUri.toString() + "_" + getMimeType(selectedImageUri));
                    }
                    mySelectedPhotos.add(photoEntry);
                    ApplyImages();
                } catch (Exception e) {
                    Toast.makeText(mContext, "This file is not accessible", Toast.LENGTH_SHORT).show();
                }
                //do what do you want to do
            }
        }
        // TODO use bitmap
    }

    public static Bitmap compressBitmap(Bitmap bitmap, int quality) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] byteArray = stream.toByteArray();
        stream.close();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    private boolean isImage(String mimeType) {

        if (mimeType.equalsIgnoreCase("jpg") || mimeType.equalsIgnoreCase("png") || mimeType.equalsIgnoreCase("gif")) {
            return true;
        } else {
            return false;
        }


    }

    private String getMimeType(Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        return type;
    }
    /*public static String SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/BHSTemp");
        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                Log.e("SaveImage", "Failed to create directory: " + myDir.getAbsolutePath());
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fname = "Image-" + timeStamp + ".jpg";
        File file = new File(myDir, fname);

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public static String SaveImage(Bitmap finalBitmap) {
        // Get the directory for the app's private pictures directory.
        File directory = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory == null) {
            // Directory not found, handle the error accordingly
            return null;
        }

        // Create a directory named "BHSTemp" within the app's private pictures directory
        File myDir = new File(directory, "BHSTemp");
        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                // Directory creation failed, handle the error accordingly
                return null;
            }
        }

        // Create a unique filename for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fname = "Image-" + timeStamp + ".jpg";
        File file = new File(myDir, fname);

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            // File saving failed, handle the error accordingly
            return null;
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutPhoto")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    PhotoUploadActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("createJobPhoto")) {
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

                    activity.finish();
                    //Reseting all fields
                } else {
                    Toast.makeText(mContext, jsonObject.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("ImageHistory")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                mySelectedPhotosGlobal = new ArrayList<>();
                mySelectedPhotosGlobal.clear();
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject value = list.optJSONObject(i);
                        PhotoEntryUploading photoEntryUploading = new PhotoEntryUploading();
                        photoEntryUploading.setChecked(true);
                        photoEntryUploading.setDocType(value.optString("ActionType"));
                        photoEntryUploading.setPath(value.optString("ActionName"));

                        if (value.optString("ActionName").toString().contains(".jpg") || value.optString("ActionName").toString().contains(".jpeg") ||
                                value.optString("ActionName").toString().contains(".JPG") || value.optString("ActionName").toString().contains(".JPEG") ||
                                value.optString("ActionName").toString().contains(".png") || value.optString("ActionName").toString().contains(".PNG") ||
                                value.optString("ActionName").toString().contains(".image") || value.optString("ActionName").toString().contains(".IMAGE") ||

                                value.optString("ActionName").toString().contains(".gif") || value.optString("ActionName").toString().contains(".GIF")) {

                            photoEntryUploading.setImageID(0);

                        } else {
                            photoEntryUploading.setImageID(1);
                        }


                        mySelectedPhotosGlobal.add(photoEntryUploading);
                    }
                    if (list.length() > 0) {
                        uploadCounter = list.length();
                        adapter.notifyDataSetChanged();
                        linear_center.setVisibility(View.GONE);
                        img_logout.setVisibility(View.VISIBLE);
                        img_gallery.setVisibility(View.VISIBLE);
                    } else {
                        linear_center.setVisibility(View.VISIBLE);
                        img_logout.setVisibility(View.VISIBLE);
                        img_gallery.setVisibility(View.INVISIBLE);
                    }

                }
            } catch (Exception e) {
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

                    DialogueWithListException(arrayException, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("podList")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");

                    // list=new JSONArray();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListValueWithoutIndex(value.optString("POD"));
                        modelException.setListValue(value.optString("Job_SeqNo") + "-" + value.optString("Display_Name"));
                        modelException.setTypeOfException(value.optString("Stamp"));
                     /*  if(i==2){
                           modelException.setListID(value.optString("Job_SeqNo") + "@" +
                                   value.optString("Sign_Status") + "@" +
                                   value.optString("Sign_URL") + "@" +
                                   value.optString("Stamp_Status") + "@" +
                                   value.optString("SignPad_Status") + "@" +
                                   "1"+ "@" +
                                   value.optString("Display_Name"));
                        }else {*/
                        modelException.setListID(value.optString("Job_SeqNo") + "@" +
                                value.optString("Sign_Status") + "@" +
                                value.optString("Sign_URL") + "@" +
                                value.optString("Stamp_Status") + "@" +
                                value.optString("SignPad_Status") + "@" +
                                value.optString("Pod_Upload_Status") + "@" +
                                value.optString("Display_Name") + "@" +
                                value.optString("Pod_SeqNo"));
                        //}
                        modelException.setpCode(value.optString("File_Type"));
                        arrayException.add(modelException);
                    }

                    if (list.length() > 0) {
                        DialogueWithListException(arrayException, -2);
                    } else {
                        Toast.makeText(mContext, "POD Value Not available", Toast.LENGTH_SHORT).show();
                        if (isDirectPOD) {
                            activity.finish();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("HAWBList")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    JSONArray list = exceptionlist.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {

                        JSONObject value = list.optJSONObject(i);
                        modelException = new ModelException();
                        modelException.setListID(value.optString("ActionType"));
                        modelException.setListValue(value.optString("ActionName"));
                        arrayException.add(modelException);
                    }

                    DialogueWithListException(arrayException, 1);

                }
            } catch (Exception e) {
                Toast.makeText(mContext, "4", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("PhotoAddress")) {
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
                REMARK = PhotoUploadActivity.edt_remark.getText().toString().trim();
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
                                    if (PHOTO_TYPE != 1) {
                                        new uploadphoto().execute();
                                    } else {
                                        //START SERVICE
                                        if (pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }
                                        if (isAssignJob) {
                                            createJob(ADDRESS, LATITUDE, LONGITUDE, GPS_STATUS);
                                        } else {
                                            if (PHOTO_TYPE == 1) {
                                                if (isSign) {
                                                    Intent intent = new Intent(mContext, SignatureActivity.class);
                                                    intent.putExtra("Str_JobNo", Str_JobNo);
                                                    intent.putExtra("Str_Sts", Str_Sts);
                                                    intent.putExtra("Str_TripNo", Str_TripNo);
                                                    startActivity(intent);
                                                    PhotoUploadActivity.activity.finish();
                                                } else {
                                                    PhotoUploadActivity.activity.finish();
                                                }
                                            }
                                        }

                                        databaseHandlerOfflineURL.enterPhotoDetail(mySelectedPhotos, IMEINUMBER, "Android", ClientID, LATITUDE, LONGITUDE, ADDRESS, GPS_STATUS,
                                                DriverID, "Update", Str_TripNo, REMARK, Str_JobNo, STR_REV, "NA", Str_Sts, "NA", "Photo");

                                        if (!Utils.isMyServiceRunning(ImageUploadService.class, mContext)) {
                                            mContext.startService(new Intent(mContext, ImageUploadService.class));
                                        }

                                    }

                                }
                            });


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("AddressPhoto")) {
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

    public void DialoguePhoto() {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist_phototype);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_select_type));

        // set the custom dialog components - text, image and button
        ImageView img_close = dialoglist.findViewById(R.id.img_close);
        final EditText edt_search = dialoglist.findViewById(R.id.edt_search);
        Button btn_submit = dialoglist.findViewById(R.id.btn_submit);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });

        btn_phototype = dialoglist.findViewById(R.id.btn_phototype);
        btn_hawb_member = dialoglist.findViewById(R.id.btn_hawb_member);

        if (isPONO) {
            btn_hawb_member.setText("PO NO");
        }


        btn_phototype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "AddressPhoto");

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

                        DialogueWithListException(arrayException, -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "AddressPhoto");
                }
            }
        });

        btn_hawb_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHAWBList("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAssignJob && edt_search.getText().toString().trim().length() == 0) {
                    Utils.Alert("Enter Remark", mContext);
                    return;
                }

                if (btn_phototype.getText().toString().trim().equalsIgnoreCase("PHOTO TYPE")) {
                    Utils.Alert("Select Photo Type", mContext);
                    return;
                }

                if (btn_hawb_member.getText().toString().trim().equalsIgnoreCase("HAWB Number")) {
                    Utils.Alert("Select HAWB Number", mContext);
                    return;
                }

                if (isPONO) {
                    if (btn_hawb_member.getText().toString().trim().equalsIgnoreCase("PO NO")) {
                        Utils.Alert("Select PO NO", mContext);
                        return;
                    }
                }

                STR_REV = btn_phototype.getText().toString().trim();
                REMARK = edt_search.getText().toString().trim();
                btn_select.setText(STR_REV);

                if (myflag == 0) {


                    pickMultipleImages();

                } else {

                    com.github.dhaval2404.imagepicker.ImagePicker.with(PhotoUploadActivity.this)
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .cameraOnly()
                            .start(PICK_IMAGE_ID);


/*
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
                    activity.startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
*/
                    PHOTO_TYPE = 0;

                   /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, PICK_IMAGE_ID);
                    } else {
                        Toast.makeText(mContext, "No camera app found", Toast.LENGTH_SHORT).show();
                    }*/
                }

                dialoglist.dismiss();

            }
        });

        final EditText etSearch = dialoglist.findViewById(R.id.edt_search);
        etSearch.setHint("Enter Remark");
        etSearch.setVisibility(View.VISIBLE);

        dialoglist.show();
    }

    private void pickMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), 786);

        //  Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //   chooseFile.setType("*/*");
        //  chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        //  startActivityForResult(chooseFile, 101);

    }


    public void DialogueWithListException(final ArrayList<ModelException> exceptionlist, final int flag) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist_addjob);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_select_type));
        final ArrayList<ModelException> exceptionlistFiltered = new ArrayList<>();
        final ArrayList<ModelException> exceptionlistImplemented = new ArrayList<>();
        exceptionlistImplemented.addAll(exceptionlist);
        final ListView lv_resource = (ListView) dialoglist.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialoglist.findViewById(R.id.img_close);
        ListCheckAdapter listCheckAdapter = null;

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });
        RelativeLayout relTop = dialoglist.findViewById(R.id.rel_top);
        RelativeLayout rel_selectall = dialoglist.findViewById(R.id.rel_selectall);
        relTop.setVisibility(View.INVISIBLE);
        Button btn_submit = dialoglist.findViewById(R.id.btn_submit);

        if (flag != -2) {
            btn_submit.setVisibility(View.GONE);
        } else {
            relTop.setVisibility(View.GONE);
            rel_selectall.setVisibility(View.VISIBLE);
        }


        final EditText etSearch = (EditText) dialoglist.findViewById(R.id.edt_search);
        etSearch.setHint("Enter Remark");
        etSearch.setVisibility(View.VISIBLE);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionCounter = 0;
                for (int i = 0; i < exceptionlist.size(); i++) {
                    if (exceptionlist.get(i).isSelected()) {

                        selectionCounter++;
                    }
                }

                if (selectionCounter > 1) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    dialog.dismiss();
                                    dialoglist.dismiss();
                                    RedirectToPOD(exceptionlist, 1);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    dialog.dismiss();
                                    dialoglist.dismiss();
                                    RedirectToPOD(exceptionlist, 0);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage("Do You want to apply All company chop & stamp to All DN?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                } else {
                    if (selectionCounter == 1) {
                        RedirectToPOD(exceptionlist, 0);
                        dialoglist.dismiss();
                    } else {
                        Utils.Alert("Select Document", mContext);
                    }
                }

            }
        });


        final String[] values = new String[exceptionlistImplemented.size()];
        for (int i = 0; i < exceptionlistImplemented.size(); i++) {
            values[i] = exceptionlistImplemented.get(i).getListValue();
        }

        if (flag != -2) {
            adapterException = new ArrayAdapter<String>(mContext,
                    R.layout.listtext, R.id.tv_title, values);
            lv_resource.setAdapter(adapterException);
        } else {
            listCheckAdapter = new ListCheckAdapter(mContext, exceptionlist);
            lv_resource.setAdapter(listCheckAdapter);
        }


        ListCheckAdapter finalListCheckAdapter = listCheckAdapter;
        rel_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = false;

                if (exceptionlist.get(0).isSelected()) {
                    isSelected = true;
                }

                for (int i = 0; i < exceptionlist.size(); i++) {
                    if (isSelected) {
                        exceptionlist.get(i).setSelected(false);
                    } else {
                        exceptionlist.get(i).setSelected(true);
                    }
                }

                finalListCheckAdapter.notifyDataSetChanged();
            }
        });

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                REMARK = "";
                dialoglist.dismiss();
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                if (flag == -1) {
                    STR_REV = exceptionlistImplemented.get(position).getListValue();
                    REMARK = etSearch.getText().toString().trim();
                    btn_phototype.setText(STR_REV);
                } else if (flag == -2) {


                } else {
                    btn_hawb_member.setText(exceptionlistImplemented.get(position).getListValue());
                }

            }
        });


        dialoglist.show();
    }

    private void RedirectToPOD(ArrayList<ModelException> exceptionlist, int pod_direct) {
        String DocumentTitle = "";
        String File_Type = "";
        String Stamp = "";
        String DocumentURL = "";
        String Job_SeqNo = "";
        String Sign_Status = "";
        String Sign_URL = "";
        String Stamp_Status = "";
        String SignPad_Status = "";
        String Pod_Upload_Status = "";
        String Pod_SeqNo = "";

        for (int i = 0; i < exceptionlist.size(); i++) {
            if (exceptionlist.get(i).isSelected()) {


                if (DocumentURL.length() == 0) {
                    DocumentURL = exceptionlist.get(i).getListValueWithoutIndex();
                } else {
                    DocumentURL = DocumentURL + "##" + exceptionlist.get(i).getListValueWithoutIndex();
                }

                if (File_Type.length() == 0) {
                    File_Type = exceptionlist.get(i).getpCode();
                } else {
                    File_Type = File_Type + "##" + exceptionlist.get(i).getpCode();
                }

                if (Stamp.length() == 0) {
                    Stamp = exceptionlist.get(i).getTypeOfException();
                } else {
                    Stamp = Stamp + "##" + exceptionlist.get(i).getTypeOfException();
                }

                String[] array = exceptionlist.get(i).getListID().split("@");


                if (Job_SeqNo.length() == 0) {
                    Job_SeqNo = array[0];
                } else {
                    Job_SeqNo = Job_SeqNo + "##" + array[0];
                }

                if (Sign_Status.length() == 0) {
                    Sign_Status = array[1];
                } else {
                    Sign_Status = Sign_Status + "##" + array[1];
                }

                if (Sign_URL.length() == 0) {
                    Sign_URL = array[2];
                } else {
                    Sign_URL = Sign_URL + "##" + array[2];
                }


                if (Stamp_Status.length() == 0) {
                    Stamp_Status = array[3];
                } else {
                    Stamp_Status = Stamp_Status + "##" + array[3];
                }


                if (SignPad_Status.length() == 0) {
                    SignPad_Status = array[4];
                } else {
                    SignPad_Status = SignPad_Status + "##" + array[4];
                }

                if (Pod_Upload_Status.length() == 0) {
                    Pod_Upload_Status = array[5];
                } else {
                    Pod_Upload_Status = Pod_Upload_Status + "##" + array[5];
                }

                if (DocumentTitle.length() == 0) {
                    DocumentTitle = array[6];
                } else {
                    DocumentTitle = DocumentTitle + "##" + array[6];
                }

                if (Pod_SeqNo.length() == 0) {
                    Pod_SeqNo = array[7];
                } else {
                    Pod_SeqNo = Pod_SeqNo + "_" + array[7];
                }


            }
        }

        Intent intent = new Intent(mContext, PODActivity.class);

        intent.putExtra("DocumentTitle", DocumentTitle);
        intent.putExtra("DocumentURL", DocumentURL);
        intent.putExtra("Stamp", Stamp);
        intent.putExtra("Job_SeqNo", Job_SeqNo);
        intent.putExtra("Sign_Status", Sign_Status);
        intent.putExtra("Stamp_Status", Stamp_Status);
        intent.putExtra("Pod_Upload_Status", Pod_Upload_Status);
        intent.putExtra("SignPad_Status", SignPad_Status);
        intent.putExtra("Pod_SeqNo", Pod_SeqNo);
        intent.putExtra("Sign_URL", Sign_URL);
        intent.putExtra("POD_DIRECT", pod_direct);
        intent.putExtra("File_Type", File_Type);
        activity.startActivity(intent);
        activity.finish();

    }


    public static class ListCheckAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ModelException> exceptionlist;

        public ListCheckAdapter(Context _context, ArrayList<ModelException> _exceptionlist) {
            super();
            this.context = _context;
            this.exceptionlist = _exceptionlist;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return exceptionlist.size();
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
                v = layoutInflater.inflate(R.layout.listtext_checkbox, null);
                h = new ViewHolder();
                h.tv_title = (TextView) v.findViewById(R.id.tv_title);
                h.rel_main = (RelativeLayout) v.findViewById(R.id.rel_main);


                h.img_select_worksite = (ImageView) v.findViewById(R.id.img_select_worksite);
                h.tv_title.setText(exceptionlist.get(position).getListValue());
                if (exceptionlist.get(position).isSelected()) {
                    h.img_select_worksite.setVisibility(View.VISIBLE);
                } else {
                    h.img_select_worksite.setVisibility(View.GONE);
                }
                String[] array = exceptionlist.get(position).getListID().split("@");
                if (array[5].equalsIgnoreCase("1")) {
                    h.rel_main.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_700));
                } else {
                    h.rel_main.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                }


                h.rel_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (exceptionlist.get(position).isSelected()) {
                            exceptionlist.get(position).setSelected(false);
                        } else {
                            exceptionlist.get(position).setSelected(true);
                        }

                        notifyDataSetChanged();
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_title.setText(exceptionlist.get(position).getListValue());
                if (exceptionlist.get(position).isSelected()) {
                    h.img_select_worksite.setVisibility(View.VISIBLE);
                } else {
                    h.img_select_worksite.setVisibility(View.GONE);
                }

                String[] array = exceptionlist.get(position).getListID().split("@");
                if (array[5].equalsIgnoreCase("1")) {
                    h.rel_main.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_700));
                } else {
                    h.rel_main.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                }

                h.rel_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (exceptionlist.get(position).isSelected()) {
                            exceptionlist.get(position).setSelected(false);
                        } else {
                            exceptionlist.get(position).setSelected(true);
                        }

                        notifyDataSetChanged();
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_title;
            private RelativeLayout rel_main;
            private ImageView img_select_worksite;

        }
    }


    private void getRevList(String address, String lat, String lng, String gpsStatus) {
        String _driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Rev List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + _driverId + "&Str_Event=" + Str_Event, "revlist");
    }

    private void getPODData(String address, String lat, String lng, String gpsStatus) {
        String _driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "POD Data", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + _driverId + "&Str_Event=" + "PODLIST" + "&Str_JobNo=" + Str_JobNo, "podList");
    }

    private void getHAWBList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        // Toast.makeText(mContext,"2",Toast.LENGTH_SHORT).show();
        APIUtils.sendRequest(mContext, "HAWB List", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_JobStatus=" + STR_REV + "&Str_DriverID=" + driverId + "&Str_JobView=Gallery&Str_JobStatus=HAWB&Str_TripNo=NA&Str_JobNo=" + Str_JobNo + "&Str_JobFor=" + Utils.getPref(mContext.getString(R.string.pref_Client_Name), mContext), "HAWBList");
    }
}
