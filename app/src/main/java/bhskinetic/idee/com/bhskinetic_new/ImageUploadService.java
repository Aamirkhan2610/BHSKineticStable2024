package bhskinetic.idee.com.bhskinetic_new;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Model.OfflineImageModel;
import general.APIUtils;
import general.DatabaseHandler;
import general.DatabaseHandlerOfflineURL;
import general.ImagePicker;
import general.Utils;

/**
 * Created by Admin on 1/6/2018.
 */

public class ImageUploadService extends Service {
    int PERIOD = 0;
    public JSONObject userDetail;
    DatabaseHandlerOfflineURL databaseHandler;
    Handler handler;
    Runnable runnable;
    Timer timer;
    OfflineImageModel offlineImageModel;
    Bitmap bm1;
    UploadPhoto uploadPhoto;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // Toast.makeText(ImageUploadService.this,"Started",Toast.LENGTH_SHORT).show();
        Log.i("Service", "Started");
        PERIOD = 5000;  //
        timer = new Timer();
        uploadPhoto=new UploadPhoto();
        databaseHandler = new DatabaseHandlerOfflineURL(ImageUploadService.this);
        final TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                // your code here...
                //Log.i("SERVICE_IMAGE_UPLOAD", "RUNNING");
                Log.i("OFFLINE IMAGE COUNT: ", ""+databaseHandler.getTotalImageCount());
                if (isNetworkAvailable()) {
                    // Log.i("NETWORK_STATUS", "ONLINE");
                    if (databaseHandler.getTotalImageCount() > 0) {
                        UploadImage();
                    }else{
                        stopSelf();
                        timer.cancel();
                    }
                } else {
                    //Log.i("NETWORoK_STATUS", "OFF-LINE");
                }
            }
        };

        timer.schedule(hourlyTask, 0l, PERIOD);
    }

    private void UploadImage() {
        runnable = new Runnable() {
            public void run() {
                offlineImageModel = databaseHandler.getImageRecord();
                Glide.with(ImageUploadService.this)
                        .load(offlineImageModel.getIMG_URI())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                bm1 = resource;
                                if(uploadPhoto.getStatus()!= AsyncTask.Status.RUNNING) {
                                    uploadPhoto=new UploadPhoto();
                                    uploadPhoto.execute();
                                }
                            }
                        });
            }
        };

        handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    class UploadPhoto extends AsyncTask<String, String, String> {
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
                HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload.jsp");
                ByteArrayBody bab = new ByteArrayBody(data, offlineImageModel.getFilename());
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(offlineImageModel.getStr_iMeiNo()));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(offlineImageModel.getStr_ID()));
                reqEntity.addPart("Str_Lat", new StringBody(offlineImageModel.getStr_Lat()));
                reqEntity.addPart("Str_Long", new StringBody(offlineImageModel.getStr_Long()));
                reqEntity.addPart("Str_Loc", new StringBody(offlineImageModel.getStr_Loc()));
                reqEntity.addPart("Str_GPS", new StringBody(offlineImageModel.getStr_GPS()));
                reqEntity.addPart("Str_DriverID", new StringBody(offlineImageModel.getStr_DriverID()));
                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody(offlineImageModel.getStr_TripNo()));
                reqEntity.addPart("Remarks", new StringBody(offlineImageModel.getRemarks()));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(offlineImageModel.getStr_JobNo()));
                reqEntity.addPart("Rev_Name", new StringBody(offlineImageModel.getRev_Name()));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody(offlineImageModel.getStr_Sts()));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(offlineImageModel.getFilename()));
                reqEntity.addPart("fType", new StringBody(offlineImageModel.getfType()));
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(ImageUploadService.this,"PHOTO UPLOADED FROM BACKGROUND PROCESS....", Toast.LENGTH_SHORT).show();
                    databaseHandler.deletePhotoRecord(offlineImageModel.getFilename());
                } else {
                    Toast.makeText(ImageUploadService.this,"ERROR UPLOADING PHOTO......", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("STATUS", "SERVICE_DESTROY");

       // Toast.makeText(ImageUploadService.this,"Destroy",Toast.LENGTH_SHORT).show();
       try {
           if(handler!=null) {
               handler.removeCallbacks(runnable);
           }
           timer.cancel();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
