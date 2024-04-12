/** Copyright (c) 2019 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 *
 *
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
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 *
 */

package bhskinetic.idee.com.bhskinetic_new;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import general.Utils;

public class StartUpActivity extends AppCompatActivity {
    private static final String TAG = "MesiboStartupActivity";
    public final static String INTENTEXIT="exit";
    public final static String SKIPTOUR="skipTour";
    public final static String STARTINBACKGROUND ="startinbackground";
    private boolean mRunInBackground = false;
    private boolean mPermissionAlert = false;
    public static StartUpActivity activity;
    public static void newInstance(Context context, boolean startInBackground) {
        Intent i = new Intent(context, StartUpActivity.class);  //MyActivity can be anything which you want to start on bootup...
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(StartUpActivity.STARTINBACKGROUND, startInBackground);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(StartUpActivity.this,""+getApplicationContext().getPackageName(),Toast.LENGTH_LONG).show();
        activity=this;
        if(getIntent().getBooleanExtra(INTENTEXIT, false)) {
            Log.d(TAG, "onCreate closing");
            finish();
            return;
        }

        mRunInBackground = getIntent().getBooleanExtra(STARTINBACKGROUND, false);

        if(mRunInBackground) {
            Log.e(TAG, "Moving app to background");
            moveTaskToBack(false);
        } else {
            Log.e(TAG, "Not Moving app to background");
        }

        setContentView(R.layout.activity_blank_launcher);

        startNextActivity();
    }

    private void LaunchMesibo() {
     //   UIManager.launchLogin(this, MesiboListeners.getInstance());
    }

    void startNextActivity() {


        if(Utils.getPref(activity.getString(R.string.pref_mesibo_pin), this).equalsIgnoreCase("1")) {
            Utils.setPref(getString(R.string.pref_mesibo_pin), "1", StartUpActivity.this);
        }else{
            Utils.setPref(getString(R.string.pref_mesibo_pin), "0", StartUpActivity.this);
        }
        finish();
    }

}
