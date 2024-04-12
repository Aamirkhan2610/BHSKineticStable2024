package Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import Classes.ModelData;
import Classes.ModelData1;
import Classes.ModelList;
import Classes.ModelLogin;
import Classes.RecyclerAdapter;
import Classes.UtilFunctions;
import activities.VtrakLoginActivity;
import bhskinetic.idee.com.bhskinetic_new.DashboardActivity;
import bhskinetic.idee.com.bhskinetic_new.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Classes.UtilFunctions.hideDialog;
import static Classes.UtilFunctions.isNetworkAvailable;
import static Classes.UtilFunctions.retroInterface;
import static activities.HomeActivity.NOTI_FRAG;
import static activities.VtrakLoginActivity.KEY_RESULT_MSG;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TextView.OnEditorActionListener {

    GoogleApiClient mGoogleApiClient;

    ImageView[] ivBottom;
    TextView[] tvBottom;
    RelativeLayout rlHome;
    private EditText etSearch;

    TextView tvNoData;

    String resultValue, resultMessage;
    UtilFunctions utilFunctions;

    RecyclerView recyclerView;

    List<ModelData> listData;
    List<ModelData1> listData1;
    RecyclerAdapter adapterVehicleList;
    SharedPreferences sharedPreferences;
    Handler myHandler;
    Runnable myRunnable;
    int delay = 30000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar1);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        ImageView iv_logout = (ImageView) view.findViewById(R.id.ivLogOut);
        ImageView iv_notifications = (ImageView) view.findViewById(R.id.ivNotifications);
        iv_notifications.setVisibility(View.GONE);
        rlHome = (RelativeLayout) view.findViewById(R.id.rlHome);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etSearch.setSingleLine();
        etSearch.setOnEditorActionListener(this);

        iv_logout.setOnClickListener(onClickToolbar);
        iv_notifications.setOnClickListener(onClickToolbar);

        sharedPreferences = getActivity().getSharedPreferences(VtrakLoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);

        listData = new ArrayList<>();
        listData1 = new ArrayList<>();

        myHandler = new Handler();
        delay = 30000; //milliseconds

        myRunnable = new Runnable() {
            public void run() {
                //do something
                myHandler.postDelayed(this, delay);
                loadAllRecords("ALL");
            }
        };
        myHandler.postDelayed(myRunnable, delay);

        loadAllRecords("ALL");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_homeFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(FragmentRecyclerView.this.getActivity(), DividerItemDecoration.VERTICAL);
                                 dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.drawable_item_decoration, null));
                                 binding.recyclerView.addItemDecoration(dividerItemDecoration);*/

        adapterVehicleList = new RecyclerAdapter(getActivity().getSupportFragmentManager(), getActivity(), listData);
        recyclerView.setAdapter(adapterVehicleList);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if(myHandler!=null){
            myHandler.postDelayed(myRunnable, delay);
        }*/
    }

    private void loadAllRecords(final String list) {
        tvNoData.setVisibility(View.GONE);
        UtilFunctions.showDialog(getActivity());
        String user_id = sharedPreferences.getString(KEY_RESULT_MSG, "n/a");
        Call<ModelList> call1 = retroInterface.getList("SG00iX", "Android", user_id, list);
        listData.clear();
        call1.enqueue(new Callback<ModelList>() {
            @Override
            public void onResponse(Call<ModelList> call, Response<ModelList> response) {


                Log.i("---->>>>", "Home Api : " + response.raw().request().url());

                for (int i = 0; i < response.body().getData().size(); i++) {

                    String asset_id = response.body().getData().get(i).getAssetID();
                    String asset_loc = response.body().getData().get(i).getAssetLoc();
                    String loc_lat = response.body().getData().get(i).getLocLat();
                    String loc_lon = response.body().getData().get(i).getLocLon();
                    String loc_time = response.body().getData().get(i).getLocDatetime();
                    String loc_speed = response.body().getData().get(i).getLocSpeed();
                    String asset_color = response.body().getData().get(i).getAssetColor();
                    String fl = response.body().getData().get(i).getFL();
                    String temperature = response.body().getData().get(i).getTemperature();
                    String load = response.body().getData().get(i).getLoad();

                    String veh_type = response.body().getData().get(i).getVehType();

                    listData.add(new ModelData(asset_id, asset_loc, loc_lat, loc_lon, loc_time, loc_speed, asset_color, fl, temperature, veh_type, load));

                }
                for (int i = 0; i < response.body().getData1().size(); i++) {

                    String vColor = response.body().getData1().get(i).getVColor();
                    String noOfVeh = response.body().getData1().get(i).getNoOfVeh();
                    listData1.add(new ModelData1(vColor, noOfVeh));
                }
                if (listData.isEmpty()) {
                    UtilFunctions.hideDialog();
                    tvNoData.setVisibility(View.VISIBLE);
                }
                adapterVehicleList.notifyDataSetChanged();
                UtilFunctions.hideDialog();
                loadBottomLayout();
            }

            @Override
            public void onFailure(Call<ModelList> call, Throwable t) {
                UtilFunctions.hideDialog();
                if (!isNetworkAvailable(getActivity())) {
                    hideDialog();
                    final Snackbar snackbar = Snackbar.make(rlHome, R.string.no_internet, Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
                    TextView tvSnackBar = (TextView) snackbarView.findViewById(R.id.snackbar_text);
                    tvSnackBar.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    snackbar.setAction(getResources().getString(R.string.try_again), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            loadAllRecords(list);
                        }
                    });
                    snackbar.show();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setMessage(getResources().getString(R.string.no_internet));
//                    builder.setPositiveButton(getResources().getString(R.string.try_again), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                            loadAllRecords(list);
//                        }
//                    });
//                    builder.show();
                } else {
                    Toast.makeText(getActivity(), "Can't Connect.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void loadBottomLayout() {

        ivBottom = new ImageView[]{(ImageView) getActivity().findViewById(R.id.iv_btm1), (ImageView) getActivity().findViewById(R.id.iv_btm2), (ImageView) getActivity().findViewById(R.id.iv_btm3), (ImageView) getActivity().findViewById(R.id.iv_btm4)};
        tvBottom = new TextView[]{(TextView) getActivity().findViewById(R.id.tv_btm1), (TextView) getActivity().findViewById(R.id.tv_btm2), (TextView) getActivity().findViewById(R.id.tv_btm3), (TextView) getActivity().findViewById(R.id.tv_btm4)};
        for (int i = 0; i < ivBottom.length; i++) {

            if (listData1.get(i).getVColor().equalsIgnoreCase("black")) {

                Glide.with(getActivity())
                        .load(R.drawable.car_black)
                        .placeholder(R.drawable.car_placeholder)
                        .into(ivBottom[i]);
            } else if (listData1.get(i).getVColor().equalsIgnoreCase("blue")) {

                Glide.with(getActivity())
                        .load(R.drawable.car_blue)
                        .placeholder(R.drawable.car_placeholder)
                        .into(ivBottom[i]);
            } else if (listData1.get(i).getVColor().equalsIgnoreCase("green")) {

                Glide.with(getActivity())
                        .load(R.drawable.car_green)
                        .placeholder(R.drawable.car_placeholder)
                        .into(ivBottom[i]);
            } else if (listData1.get(i).getVColor().equalsIgnoreCase("red")) {

                Glide.with(getActivity())
                        .load(R.drawable.car_red)
                        .placeholder(R.drawable.car_placeholder)
                        .into(ivBottom[i]);
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.car_placeholder)
                        .into(ivBottom[i]);
            }

            tvBottom[i].setText(listData1.get(i).getNoOfVeh());
        }
    }

    View.OnClickListener onClickToolbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.ivLogOut:
                    logOut();
                    break;

                case R.id.ivNotifications:
                    loadNotiFragment();
                    break;
            }
        }
    };

    private void logOut() {
        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        //builder1.setTitle(R.string.app_name);
        //builder1.setTitle(Html.fromHtml("<font color='#0000FF'>VTrak</font>"));
        builder1.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you really want to Log out?</font>"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        logOutFinal();
                    }
                });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        builder1.setCancelable(true);
        AlertDialog alertDialog = builder1.create();
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(R.string.app_name);
        tvTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.clrBlue));
        tvTitle.setPadding(0, 25, 0, 0);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextSize(20);
        *//*LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTv.setMargins(0, 20, 0, 0);*//*
        //tvTitle.setLayoutParams(lpTv);
        alertDialog.setCustomTitle(tvTitle);
        alertDialog.show();

        alertDialog.getWindow().setLayout(700, 380);
        alertDialog.getWindow().getDecorView().getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.clr_rec_viewHolder), PorterDuff.Mode.DARKEN);
//        TextView tvTitle = (TextView) alertDialog.findViewById(android.R.id.title);
//        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        LinearLayout llAlert = (LinearLayout) alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).getParent();
        //llAlert.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.clr_bgd_btn));
        llAlert.setGravity(Gravity.CENTER_HORIZONTAL);
        //int widthAlertDialog = llAlert.getWidth();

        Button btnNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnNegative.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.clrGreen));
        btnNegative.setTextColor(Color.WHITE);
        btnNegative.setLayoutParams(layoutParams);
        //btnNegative.getLayoutParams().width = 250;

        Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setBackgroundColor(Color.RED);
        btnPositive.setTextColor(Color.WHITE);
        btnPositive.setLayoutParams(layoutParams);*/
        //btnPositive.getLayoutParams().width = 250;

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        TextView tvExit = (TextView) dialog.findViewById(R.id.tvExit);
        tvExit.setText("Logout");
        Button btnNeg = (Button) dialog.findViewById(R.id.btnNegative);
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnPos = (Button) dialog.findViewById(R.id.btnPositive);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logOutFinal();
            }
        });
        dialog.show();
    }

    private void logOutFinal() {

        UtilFunctions.showDialog(getActivity());
        buildGoogleApi();
        utilFunctions = new UtilFunctions(getActivity(), mGoogleApiClient);

        String user_id = sharedPreferences.getString(VtrakLoginActivity.KEY_USER_ID, null);
        String password = sharedPreferences.getString(VtrakLoginActivity.KEY_PWD, null);

        Call<ModelLogin> call = retroInterface.loginWithCredentials("SG00iX", "Android", user_id, password, "Logout", utilFunctions.latitude, utilFunctions.longitude, "00000", "vTrak_SG");
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(Call<ModelLogin> call, Response<ModelLogin> response) {

                resultValue = response.body().getResultvalue();
                resultMessage = response.body().getResultmsg();

                try {

                    if (resultValue.equalsIgnoreCase("0")) {
                        Log.i("---->>>>", "Logout : " + response.raw().request().url());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(VtrakLoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(KEY_RESULT_MSG);
                        editor.commit();
                        hideDialog();
                        getActivity().finish();

                        Intent i = new Intent(getActivity(), DashboardActivity.class);
// set the new task and clear flags
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        UtilFunctions.showSnackBar(getActivity(), rlHome, response.body().getResultmsg());
                        /*final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(response.body().getResultmsg());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();*/
                    }
                } catch (NullPointerException exception) {
                    Toast.makeText(getActivity(), "Wrong Input!", Toast.LENGTH_SHORT).show();
                    Log.e("-->>", exception.toString());
                }
            }

            @Override
            public void onFailure(Call<ModelLogin> call, Throwable t) {
                UtilFunctions.hideDialog();
                Log.e("-->>", "Failed to log out!");
            }
        });
    }

    private void loadNotiFragment() {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NotificationFragment fragment = new NotificationFragment();
        fragmentTransaction.replace(R.id.frame_layout, fragment, NOTI_FRAG);
        fragmentTransaction.addToBackStack(NOTI_FRAG);
        fragmentTransaction.commit();

        DashboardActivity.sp = getActivity().getSharedPreferences(VtrakLoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = DashboardActivity.sp.edit();
        editor.putString(RecyclerAdapter.KEY_VEH_NUM, "ALL");
        editor.commit();
    }

    private void buildGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        utilFunctions.getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String searchParam = etSearch.getText().toString();
            if (searchParam.isEmpty()) {
                listData.clear();
                loadAllRecords("ALL");
                adapterVehicleList.notifyDataSetChanged();
            } else {
                listData.clear();
                loadAllRecords(searchParam);
                adapterVehicleList.notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }
}
