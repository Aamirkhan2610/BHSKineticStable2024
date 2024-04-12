/**
 * Copyright (c) 2019 Mesibo
 * https://mesibo.com
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 * <p>
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * <p>
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 * <p>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * Documentation
 * https://mesibo.com/documentation/
 * <p>
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 */

package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import general.APIUtils;
import general.Utils;


/**
 * Created by Mesibo on 29/09/17.
 */

public class AppController extends Application implements  Application.ActivityLifecycleCallbacks {
    public static final String TAG = "MesiboSampleApplication";
    private static Context mContext = null;
  //  private static MesiboCall mCall = null;
    private static AppConfig mConfig = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //Toast.makeText(mContext,this.getPackageName(),Toast.LENGTH_LONG).show();
        //Mesibo.setRestartListener(this);
        mConfig = new AppConfig(this);
        //SampleAPI.init(getApplicationContext());

   //     mCall = MesiboCall.getInstance();
   //     mCall.init(this);

     /*   MesiboUI.Config opt = MesiboUI.getConfig();
        opt.mToolbarColor = 0xff00868b;
        opt.emptyUserListMessage = "Ask your family and friends to download so that you can try out Mesibo functionalities";
        MediaPicker.setToolbarColor(opt.mToolbarColor);
*/

        registerActivityLifecycleCallbacks(this);

        mInstance = this;


    }


    public static String getRestartIntent() {
        return "com.mesibo.sampleapp.restart";
    }

    public static Context getAppContext() {
        return mContext;
    }

/*
    @Override
    public void Mesibo_onRestart() {
        Log.d(TAG, "OnRestart");
        StartUpActivity.newInstance(this, true);
    }
*/


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        //Toast.makeText(activity,activity.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        if (activity.getClass().getSimpleName().equalsIgnoreCase("MesiboActivity")) {
            String mesiboPIN = Utils.getPref(activity.getString(R.string.pref_mesibo_pin), mContext);
            if(mesiboPIN.equalsIgnoreCase("0")){
                startActivity(new Intent(activity,DashboardActivity.class));
                activity.finish();
            }
            Utils.setPref(getString(R.string.pref_mesibo_pin), "0", activity);
        }else if (!activity.getLocalClassName().equalsIgnoreCase("MessagingActivity")) {
            Utils.setPref(getString(R.string.pref_mesibo_pin), "1", activity);
        }
        else if (!activity.getLocalClassName().equalsIgnoreCase("SplashScreen")) {
            CheckApplicationUpdates(activity);
        }
    }
        @Override
        public void onActivityPaused (Activity activity){

        }

        @Override
        public void onActivityStopped (Activity activity){

        }

        @Override
        public void onActivitySaveInstanceState (Activity activity, Bundle bundle){

        }

        @Override
        public void onActivityDestroyed (Activity activity){

        }

        public void CheckApplicationUpdates ( final Context mContext){
            String tag_json_obj = "json_obj_req";
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, APIUtils.BaseUrl + "index.jsp?Str_AppsName=A_BHS&Str_Model=Android", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Utils.LogI(TAG, response.toString(), mContext);
                    JSONObject jobj = response;
                /*if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    //String currentVersion="1.1";
                    String currentVersion=BuildConfig.VERSION_NAME;
                    String liveVersion=jobj.optString("Build_Version");


                    if(!currentVersion.equalsIgnoreCase(liveVersion)) {
                        if(Utils.getPref(mContext.getResources().getString(R.string.pref_isUpdateRequired),mContext).equalsIgnoreCase("0")) {
                            String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                            String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                            sendLogoutRequest(mContext, "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + "0" + "&Str_Long=" + "0" + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "");
                        }
                    }else{
                        Utils.setPref(mContext.getResources().getString(R.string.pref_isUpdateRequired),"0",mContext);
                    }
                }*/

                    if (jobj.optString("recived").equalsIgnoreCase("1")) {
                        //String currentVersion="1.20";
                        String currentVersion = BuildConfig.VERSION_NAME;
                        String liveVersion = jobj.optString("Build_Version");

                        if (!currentVersion.equalsIgnoreCase(liveVersion)) {
                            if (DashboardActivity.tv_networktype != null) {
                                DashboardActivity.tv_networktype.setEnabled(true);
                                DashboardActivity.tv_networktype.setText("VER: " + currentVersion);
                                DashboardActivity.tv_networktype.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_download, 0, 0, 0);
                            }
                        } else {
                            if (DashboardActivity.tv_networktype != null) {
                                DashboardActivity.tv_networktype.setEnabled(false);
                                DashboardActivity.tv_networktype.setText("VER: " + currentVersion);
                                DashboardActivity.tv_networktype.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            }
                        }
                        //  Toast.makeText(mContext,currentVersion+"\n"+liveVersion,Toast.LENGTH_SHORT).show();


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
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

        public void sendLogoutRequest ( final Context mContext, String param){
            String tag_json_obj = "json_obj_req";
            Log.i("LOGOUT REQUEST", APIUtils.BaseUrl + param);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, APIUtils.BaseUrl + param, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Utils.LogI(TAG, response.toString(), mContext);
                    JSONObject jobj = response;
                    if (jobj.optString("Status").equalsIgnoreCase("1")) {
                        Utils.setPref(mContext.getResources().getString(R.string.pref_isUpdateRequired), "1", mContext);
                        Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(logoutIntent);
                        Utils.clearPref(mContext);
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

