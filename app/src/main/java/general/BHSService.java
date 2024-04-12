package general;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bhskinetic.idee.com.bhskinetic_new.AppController;

/**
 * Created by Admin on 1/6/2018.
 */

public class BHSService extends Service {
    int PERIOD = 0;
    public JSONObject userDetail;
    public int VolleyRetryTime = 300000;
    public RequestQueue requestQueue;
    ArrayList<String> arrayOfflineURL;
    DatabaseHandlerOfflineURL databaseHandler;
    Handler handler;
    Runnable runnable;
    Timer timer; @Nullable
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

        arrayOfflineURL = new ArrayList<>();
        PERIOD = 5000;  //5 second
        timer = new Timer();
        databaseHandler = new DatabaseHandlerOfflineURL(BHSService.this);
        final TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                // your code here...
                if (isNetworkAvailable()) {
                    Log.i("STATUS", "APP_ONLINE");
                    arrayOfflineURL = databaseHandler.getAllLocalURL();
                    CallURL();
                    stopSelf();
                    timer.cancel();
                } else {
                    Log.i("STATUS", "APP_OFFLINE");
                }
            }
        };

        timer.schedule(hourlyTask, 0l, PERIOD);
        try {
            requestQueue = Volley.newRequestQueue(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallURL() {
        runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < arrayOfflineURL.size(); i++) {
                    SubmitURL(arrayOfflineURL.get(i),i);
                }
            }
        };
        handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void SubmitURL(final String URL, final int record) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = URL;
        if (url.contains(" ")) {
            url.replaceAll(" ", "%20");
        }
        Log.i("Offline URL submit",url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("offline URL Submit res",  response.toString());
               // Toast.makeText(BHSService.this, "Uploading Offline Record......", Toast.LENGTH_SHORT).show();
                databaseHandler.deleteURL(URL);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hide the progress dialog
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("STATUS", "SERVICE_DESTROY");
        handler.removeCallbacks(runnable);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
