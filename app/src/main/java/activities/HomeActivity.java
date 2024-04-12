package activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.GoogleApiClient;

import Classes.UtilFunctions;
import Fragments.HomeFragment;
import bhskinetic.idee.com.bhskinetic_new.R;

public class HomeActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;
    FragmentTransaction fragmentTransaction;
    public static final String LOGIN_FRAG = "login_frag";
    public static final String NOTI_FRAG = "noti_frag";
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frame_layout, homeFragment, LOGIN_FRAG);
        fragmentTransaction.addToBackStack(LOGIN_FRAG);
        fragmentTransaction.commit();
        HomeActivity.activity= HomeActivity.this;
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilFunctions.checkPlayServices(HomeActivity.this);
    }
}