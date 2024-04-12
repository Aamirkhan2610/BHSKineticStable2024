package activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Classes.ModelHistory;
import Classes.RecyclerAdapter;
import Classes.UtilFunctions;
import bhskinetic.idee.com.bhskinetic_new.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDate, tvStartTime, tvEndTime;
    ImageView ivGo, ivBack;
    Calendar calendar;

    Double lat, lng;
    int mYear, mMonth, mDay, mHour, mMinute;

    ArrayList<Double> listLat;
    ArrayList<Double> listLong;
    ArrayList<String> listColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        ivGo = (ImageView) findViewById(R.id.ivGo);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        tvDate.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        ivGo.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        tvDate.setText(sdf.format(date));

        showInMap(1);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tvDate) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                    calendar.setTimeInMillis(0);
                    calendar.set(year, monthOfYear, dayOfMonth);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                    tvDate.setText(sdf.format(date));
                }
            }, mYear, mMonth, mDay);

            datePickerDialog.show();

        }

        if (view.getId() == R.id.tvStartTime) {

            final Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(HistoryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                    tvStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
            }, mHour, mMinute, false);

            timePickerDialog.show();
        }

        if (view.getId() == R.id.tvEndTime) {

            Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(HistoryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                    tvEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
            }, mHour, mMinute, false);


            timePickerDialog.show();
        }

        if (view.getId() == R.id.ivBack) {

            super.onBackPressed();
        }

        if (view.getId() == R.id.ivGo) {

            UtilFunctions.showDialog(HistoryActivity.this);
            getHistory();
        }
    }

    private void getHistory() {
        String assetID = null;
        String vehicleType = null;
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            assetID = bun.getString(RecyclerAdapter.KEY_VEH_NUM, null);
            vehicleType= bun.getString(RecyclerAdapter.KEY_VEHICLE_TYPE, null);
        }
        String startDate = tvDate.getText() + " " + tvStartTime.getText();
        String endDate = tvDate.getText() + " " + tvEndTime.getText();

        Call<ModelHistory> call = UtilFunctions.retroInterface.getHistory(vehicleType, "Android", Integer.parseInt(VtrakLoginActivity.sharedPreferences.getString(VtrakLoginActivity.KEY_RESULT_MSG, null)), assetID, startDate, endDate);
        call.enqueue(new Callback<ModelHistory>() {
            @Override
            public void onResponse(Call<ModelHistory> call, Response<ModelHistory> response) {

                listLat = new ArrayList<Double>();
                listLong = new ArrayList<Double>();
                listColor = new ArrayList<>();
                for (int i = 0; i < response.body().getData().size(); i++) {


                    Log.i("lat long : ", response.body().getData().get(i).getLocLat() + "   " + response.body().getData().get(i).getLocLon() + "   " + response.body().getData().get(i).getAssetColor());

                    listLat.add(Double.valueOf(response.body().getData().get(i).getLocLat()));
                    listLong.add(Double.valueOf(response.body().getData().get(i).getLocLon()));
                    listColor.add(response.body().getData().get(i).getAssetColor());
                    UtilFunctions.hideDialog();

                }

                showInMap(2);

            }

            @Override
            public void onFailure(Call<ModelHistory> call, Throwable t) {
                UtilFunctions.hideDialog();
                UtilFunctions.showAlert(HistoryActivity.this, "Can't load data, please try again later");
                Log.i("no, ", "its failure" + t.toString());
            }
        });
    }

    private void showInMap(int choice) {

        if (choice == 1) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    Bundle b = getIntent().getExtras();

                    if (b != null) {

                        lat = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LAT));
                        lng = Double.parseDouble(b.getString(RecyclerAdapter.KEY_LONG));
                        String color = b.getString(RecyclerAdapter.KEY_ASSET_COLOR);

                        LatLng latLng = new LatLng(lat, lng);


                        if (color.trim().equalsIgnoreCase("black")) {

                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_black)));

                        } else if (color.trim().equalsIgnoreCase("blue")) {


                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_blue)));

                        } else if (color.trim().equalsIgnoreCase("green")) {

                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_green)));

                        } else if (color.trim().equalsIgnoreCase("red")) {

                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_red)));
                        }

                        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(16).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            });
        }

        if (choice == 2) {
            final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    PolylineOptions po = new PolylineOptions();
                    LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();

                    for (int i = 0; i < listLat.size(); i++) {

                        LatLng latLng = new LatLng(listLat.get(i), listLong.get(i));
                        po.add(latLng);

                        if (listColor.get(i).equalsIgnoreCase("black")) {
                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_black)));
                        } else if (listColor.get(i).equalsIgnoreCase("blue")) {
                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_blue)));
                        } else if (listColor.get(i).equalsIgnoreCase("green")) {
                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_green)));
                        } else if (listColor.get(i).toString().equalsIgnoreCase("red")) {
                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_red)));
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_grey)));
                        }

                        mBuilder.include(latLng);

                    }

                    po.width(5).color(Color.GREEN).geodesic(true);
                    googleMap.addPolyline(po);

                    LatLngBounds bounds = mBuilder.build();

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                }
            });
        }


    }
}
