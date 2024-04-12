/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bhskinetic.idee.com.bhskinetic_new.fcm;


import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import Classes.AppControllerChild;
import bhskinetic.idee.com.bhskinetic_new.AlarmDialogeActivity;
import bhskinetic.idee.com.bhskinetic_new.DashboardActivity;
import bhskinetic.idee.com.bhskinetic_new.LoginActivity;
import bhskinetic.idee.com.bhskinetic_new.MessageSubActivity;
import bhskinetic.idee.com.bhskinetic_new.OrderDetail;
import bhskinetic.idee.com.bhskinetic_new.R;
import general.APIUtils;
import general.DatabaseHandler;
import general.TrackGPS;
import general.Utils;

public class MesiboGcmListenerService extends FirebaseMessagingService {
    private static final String TAG = "FcmListenerService";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    // DatabaseHandler db;
    DatabaseHandler db;
    String Title;
    Random rmd = new Random();
    String SeqNo = "0";
    String dataget = "";
    Context mContext;
    String titleAlarm="";

    String lock_time="";
    String lock_job_id="";
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.i("ONMESSAGE=====>", "remoteMessage");
        mContext = MesiboGcmListenerService.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //{body=893727@Auto~893727~20~geofence msg like lat/long if needed, sound=1, title=PUSH, vibrate=1}

        //https://www.idee.sg/Send_Mobile_Notify.php?id=cVnPvuoIJas:APA91bFK5nanvAg7eIQP3VqHqfNCcap0b_-pzRENlGaA0O5zwz-yRqJ9vuMx_DFKvHkT2xdJoegsRk3A3DBkpGe0JL5b2BK1x8xb5Vyxs-vKCsZjdWtl9j4p38KDn1wJ5veUL1pQUMwF&title=PUSH&body=893727@Auto~893727~20~geofence%20msg%20like%20lat/long%20if%20needed&projectname=BHS

        db = new DatabaseHandler(getApplicationContext());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: 2" + remoteMessage.getData());
        //562891@ALARM@TEST 1234
        try {
            dataget = remoteMessage.getData().get("body").toString().split("@")[1];
            SeqNo = remoteMessage.getData().get("body").toString().split("@")[0];
            if(dataget.equalsIgnoreCase("ALARM")||dataget.equalsIgnoreCase("AUTO")) {
                titleAlarm = remoteMessage.getData().get("body").toString().split("@")[2];
            }else if(dataget.equalsIgnoreCase("APP_LOCK")) {
                lock_job_id = remoteMessage.getData().get("body").toString().split("@")[2];
                lock_time = remoteMessage.getData().get("body").toString().split("@")[3];
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        Title = remoteMessage.getData().get("title").toString();

        Log.i("TITLE>>>>>>>>>>>>>>>", Title);

        if (dataget.equalsIgnoreCase("LOGOUT")) {
            //CALLING API WHILE LOGOUT
            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
            TrackGPS gps = new TrackGPS(mContext);
            sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logout");

        } else if (dataget.equalsIgnoreCase("ALARM")) {
            Intent intent=new Intent(mContext, AlarmDialogeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("title",titleAlarm);
            intent.putExtra("type","ALARM");
            intent.putExtra("Str_SeqNo",SeqNo);
            this.startActivity(intent);

        }else if (dataget.equalsIgnoreCase("AUTO")) {
            Intent intent=new Intent(mContext, AlarmDialogeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("title",titleAlarm);
            intent.putExtra("type","AUTO");
            intent.putExtra("Str_SeqNo",SeqNo);
            this.startActivity(intent);

        }else if (dataget.equalsIgnoreCase("APP_LOCK")) {
            boolean isAppLock = false;

            if (Utils.getPref("app_lock", mContext) == null) {
                isAppLock = false;
            }else if (Utils.getPref("app_lock", mContext).equalsIgnoreCase("1")) {
                isAppLock = true;
            }else{
                isAppLock = false;
            }

            if(isAppLock){
                return;
            }

            Utils.setPref("lock_job_id",lock_job_id,mContext);
            Utils.setPref("lock_time",lock_time,mContext);
            Utils.setPref("app_lock","1",mContext);
            Utils.setPref("lock_timestamp",""+System.currentTimeMillis(),mContext);
            Intent intent=new Intent(mContext, OrderDetail.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);

        } else if (dataget.equalsIgnoreCase("REFRESH")) {
            if (isAppIsInBackground(mContext)) {
                Log.i("APP_STATUS", "BACKGROUND");
            } else {
                Log.i("APP_STATUS", "FOREGROUND");
                Intent i = new Intent("refresh_dashboard");
                sendBroadcast(i);
            }

        } else {
            editor.putString("title", Title);
            editor.commit();
            Intent i = new Intent("refresh_message_push");
            i.putExtra("Type", Title);
            sendBroadcast(i);

          //  if (MessageSubActivity.isNotificationToShow)
                showNotification(MesiboGcmListenerService.this, "New Notification", dataget);
        }

    }



    public void sendRequest(final Context mContext, final String TAG, String query, final String redirectionKey) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";


        String url = APIUtils.BaseUrl + query;

        if (url.contains(" ")) {
            url.replaceAll(" ", "%20");
        }

        Utils.LogI("API URL", url, mContext);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Utils.LogI(TAG, response.toString(), mContext);
                Log.i("SERVER RESPONSE", redirectionKey + "\n" + response.toString());

                if (isAppIsInBackground(mContext)) {
                    Log.i("APP_STATUS:", "BACKGROUND");

                } else {
                    Log.i("APP_STATUS:", "FOREGROUND");
//                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
//                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    mContext.startActivity(logoutIntent);
                 }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(mContext.getClass().getSimpleName() + "\n" + TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppControllerChild.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private boolean isAppIsInBackground(Context context) {
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

    public static void showNotification(Context context, String title, String messageBody) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_notification_icon);

        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                /*.setLargeIcon(largeIcon)*/
                .setSmallIcon(R.drawable.bhs_logo) //needs white icon with transparent BG (For all platforms)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setVibrate(new long[]{1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();

    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
