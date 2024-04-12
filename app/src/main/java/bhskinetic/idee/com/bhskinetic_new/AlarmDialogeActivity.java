package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 12/11/2017.
 */

public class AlarmDialogeActivity extends Activity {
    private Button btn_close,btn_yes,btn_no;
    private LinearLayout linearAuto;
    private TextView textView;
    Ringtone ringtoneSound;
    public static Context mContext;
    public static TrackGPS gps;
    public static Bundle bundle;
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmdialog_dialog);
        mContext=this;
        activity=this;
        gps = new TrackGPS(mContext);
        btn_close=findViewById(R.id.btn_close);
        textView=findViewById(R.id.tvExit);

        linearAuto=findViewById(R.id.linear_auto);
        btn_yes =findViewById(R.id.btn_yes);
        btn_no=findViewById(R.id.btn_no);

        bundle =getIntent().getExtras();
        textView.setText(bundle.getString("title"));
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendACK("",""+gps.getLatitude(),""+gps.getLongitude(),"","Read","ALARM");
                //finish();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendACK("",""+gps.getLatitude(),""+gps.getLongitude(),"","YES","AUTO");
                //finish();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendACK("",""+gps.getLatitude(),""+gps.getLongitude(),"","NO","AUTO");
                //finish();
            }
        });

        if(bundle.getString("type").equalsIgnoreCase("AUTO")){
            linearAuto.setVisibility(View.VISIBLE);
            btn_close.setVisibility(View.GONE);

        }

        StartAudio();
        Vibrate();
    }

    private void Vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(3000);
        }


    }

    public static void sendACK(String address, String lat, String lng, String gpsStatus,String Str_Misc_Status,String Str_Misc_Type) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Send ACK", "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc=NA&Str_GPS=ON&Str_DriverID="+driverId+"&Str_SeqNo="+bundle.getString("Str_SeqNo")+"&Str_Misc_Type="+Str_Misc_Type+"&Str_Misc_Status="+Str_Misc_Status+"&Str_JobFor=BHS", "SendACKMsg");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ringtoneSound.isPlaying()) {
            ringtoneSound.stop();
        }
    }

    private void StartAudio() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
         ringtoneSound = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

        if (ringtoneSound != null) {
            ringtoneSound.play();

        }
    }

    public void showResponse(String toString, String redirectionKey) {

        try{

            JSONObject jsonObject=new JSONObject(toString);
            if(jsonObject.optString("recived").equalsIgnoreCase("1")){
                Toast.makeText(mContext,jsonObject.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                activity.finish();
            }

        }catch (Exception e){

        }
    }
}
