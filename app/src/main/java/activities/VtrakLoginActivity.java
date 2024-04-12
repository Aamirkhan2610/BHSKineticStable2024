package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import Classes.ModelLogin;
import Classes.ModelLoginSpinnerList;
import Classes.RetrofitClient;
import Classes.UtilFunctions;
import Interface.RetroInterface;
import bhskinetic.idee.com.bhskinetic_new.DashboardActivity;
import bhskinetic.idee.com.bhskinetic_new.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Classes.UtilFunctions.BASE_URL;
import static Classes.UtilFunctions.isNetworkAvailable;
import static Classes.UtilFunctions.retroInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class VtrakLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText etUserName, etPassword;
    Button btnLogin;
    String country;
    private GoogleApiClient mGoogleApiClient;
    public static SharedPreferences sharedPreferences;
    AutoCompleteTextView acCountry;
    View llParent;
    public static CharSequence[] options = new CharSequence[4];
    String resultValue, resultMessage, breakService, userName, password;
    String selectedCountry = "";
    public static final String SHARED_PREF_NAME = "spLogin";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_PWD = "password";
    public static final String KEY_RESULT_MSG = "resultMsg";
    public static final String KEY_BREAK_SERVICE = "keyBreakService";
    public static final String KEY_TOKEN = "keyToken";
    public static final String KEY_URL_NAME = "keyUrlName";
    public static final String KEY_URL = "keyUrl";
    public static final String KEY_COUNTRY_NAME = "keyCountryName";
    //public static final String KEY_IS_LOGGED_IN = "keyIsLoggedIn";
    List<String> listCountry;
    List<String> listUrl;
    public UtilFunctions utilFunctions;
    SharedPreferences.Editor editor;
    ImageView ivFlag;
    TextView tvSelectedCountry;
    RelativeLayout rvSelectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vtrak_login);

        UtilFunctions.showDialog(VtrakLoginActivity.this);
        init();

        if (isNetworkAvailable(VtrakLoginActivity.this)) {
            buildGoogleApi();
            utilFunctions = new UtilFunctions(VtrakLoginActivity.this, mGoogleApiClient);
        }
        setSpinnerData();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                // country = acCountry.getText().toString();
                country = selectedCountry;
                if (!isNetworkAvailable(VtrakLoginActivity.this)) {
                    //showErrorDialog(LogInActivity.this, getResources().getString(R.string.no_internet), getResources().getString(R.string.try_again));
                    UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, getResources().getString(R.string.no_internet));
                    return;
                }

                if (userName.trim().length() == 0) {

                    //showErrorDialog(LogInActivity.this, getResources().getString(R.string.enter_username), getResources().getString(R.string.ok));
                    UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, getResources().getString(R.string.enter_username));
                    return;
                }

                if (password.trim().length() == 0) {

                    //showErrorDialog(LogInActivity.this, getResources().getString(R.string.enter_password), getResources().getString(R.string.ok));
                    UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, getResources().getString(R.string.enter_password));
                    return;
                }

                if (country.trim().length() == 0) {

                    //showErrorDialog(LogInActivity.this, getResources().getString(R.string.enter_password), getResources().getString(R.string.ok));
                    UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, getResources().getString(R.string.enter_country_name));
                    return;
                }

                if (!listUrl.isEmpty()) {
                    for (int i = 0; i < listCountry.size(); i++) {
                        if (listCountry.get(i).equalsIgnoreCase(selectedCountry.trim())) {
                            retroInterface = RetrofitClient.getRetrofitClient(BASE_URL).create(RetroInterface.class);
                            editor = DashboardActivity.sp.edit();
                            editor.putString(KEY_URL, BASE_URL);
                            editor.putString(KEY_URL_NAME, listUrl.get(i));
                            editor.putString(KEY_COUNTRY_NAME, listCountry.get(i));
                            editor.apply();
                        }
                    }
                }
                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                DashboardActivity.sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                UtilFunctions.showDialog(VtrakLoginActivity.this);
                String token="";
                Call<ModelLogin> call = retroInterface.loginWithCredentials("SG00iX", "Android", userName, password, "Login", UtilFunctions.latitude, UtilFunctions.longitude, token, "vTrak_SG");
                call.enqueue(new Callback<ModelLogin>() {
                    @Override
                    public void onResponse(Call<ModelLogin> call, Response<ModelLogin> response) {

                        try {
                            resultValue = response.body().getResultvalue();
                            resultMessage = response.body().getResultmsg();
                            breakService = response.body().getBrkService();
                            if (resultValue.equalsIgnoreCase("0")) {
                                Log.i("---->>>>", "Login Api : " + response.raw().request().url());
                                UtilFunctions.hideDialog();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USER_ID, userName);
                                editor.putString(KEY_PWD, password);
                                editor.putString(KEY_RESULT_MSG, resultMessage);
                                editor.putString(KEY_BREAK_SERVICE, breakService);
                                editor.commit();
                                startActivity(new Intent(VtrakLoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                UtilFunctions.hideDialog();
                                UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, resultMessage);
                            }
                        } catch (NullPointerException exception) {
                            UtilFunctions.hideDialog();
                            UtilFunctions.showSnackBar(VtrakLoginActivity.this, llParent, "Wrong input");
                            Log.e("-->>", exception.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<ModelLogin> call, Throwable t) {
                        UtilFunctions.hideDialog();
                        UtilFunctions.showAlert(VtrakLoginActivity.this, "Can't login, please try again later");
                        Log.e("---->>>>", "On Failure : " + t.toString());
                    }
                });

            }
        });
    }

    private void setSpinnerData() {
        RetroInterface retroInterface = RetrofitClient.getRetrofitClient("http://203.125.153.221/tt_tsg/").create(RetroInterface.class);
        Call<ModelLoginSpinnerList> call = retroInterface.getLoginSpinnerData(10, "Android", "Country");
        call.enqueue(new Callback<ModelLoginSpinnerList>() {
            @Override
            public void onResponse(Call<ModelLoginSpinnerList> call, Response<ModelLoginSpinnerList> response) {
                listCountry = new ArrayList<>();
                listUrl = new ArrayList<>();
                for (int i = 0; i < response.body().getData().size(); i++) {
                    UtilFunctions.hideDialog();
                    listCountry.add(response.body().getData().get(i).getCountryName());
                    listUrl.add(response.body().getData().get(i).getURLName());
                }
                if (!listCountry.isEmpty()) {
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VtrakLoginActivity.this, android.R.layout.simple_dropdown_item_1line, listCountry);
                    acCountry.setThreshold(1);
                    acCountry.setAdapter(arrayAdapter);
                    acCountry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View arg0) {
                            // acCountry.showDropDown();
                        }
                    });
                    acCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                acCountry.showDropDown();
                            }
                        }
                    });
                    acCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        }
                    });
                }

                //Changes by Aamir 26/04/2018 [Hiding Spinner and adding country selection on flag tap]
                for (int i = 0; i < listCountry.size(); i++) {
                    options[i] = listCountry.get(i);
                }

            }

            @Override
            public void onFailure(Call<ModelLoginSpinnerList> call, Throwable t) {
                UtilFunctions.hideDialog();
                //UtilFunctions.showAlert(LogInActivity.this, "Can't load data, please try again later");
            }
        });
    }

    public static void showErrorDialog(Context mContext, String message, String posButton) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton(posButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void buildGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(VtrakLoginActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void init() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        llParent = findViewById(R.id.llParent);
        acCountry = (AutoCompleteTextView) findViewById(R.id.actv);
        ivFlag = (ImageView) findViewById(R.id.ivFlag);
        tvSelectedCountry = (TextView) findViewById(R.id.tvSelectedCountry);
        rvSelectedCountry = (RelativeLayout) findViewById(R.id.rvSelectedCountry);
        rvSelectedCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        acCountry.setText(DashboardActivity.sp.getString(KEY_COUNTRY_NAME, "Singapore"));
        acCountry.setVisibility(View.GONE);

        tvSelectedCountry.setText("Country : " + DashboardActivity.sp.getString(KEY_COUNTRY_NAME, "Singapore"));
        selectedCountry = DashboardActivity.sp.getString(KEY_COUNTRY_NAME, "Singapore");
        /*acCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() != 0) {
                    if (charSequence.equals("Singapore"))
                        ivFlag.setImageResource(R.mipmap.flag_singapore);

                    if (charSequence.equals("Malaysia"))
                        ivFlag.setImageResource(R.mipmap.flag_malaysia);

                    if (charSequence.equals("Indonesia"))
                        ivFlag.setImageResource(R.mipmap.flag_indonesia);

                    if (charSequence.equals("India"))
                        ivFlag.setImageResource(R.mipmap.flag_india);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
    }

    private void selectImage() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Select Country");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                tvSelectedCountry.setText("Country : " + listCountry.get(item));
                selectedCountry = listCountry.get(item);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        UtilFunctions.getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("------->>>>>>", "on connection failed.....");
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
        UtilFunctions.checkPlayServices(VtrakLoginActivity.this);
    }
}