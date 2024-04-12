package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import Model.ModelOrderList;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/13/2017.
 */

public class OrderList extends Activity {
    public static TextView tv_header;
    public static Context mContext;
    public static ListView list_order;
    public static ListElementAdapter adapter;
    public static ModelOrderList modelOrderList;
    public static ArrayList<ModelOrderList> ArrayOrderList;
    public static ArrayList<ModelOrderList> ArrayOrderListFiltered;
    public static ArrayList<ModelOrderList> ArrayOrderListImplemented;
    public static TrackGPS gps;
    public static ImageView img_refresh;
    public static ImageView img_logout;
    public static Activity activity;
    public static String Str_JobView = "View";
    public static String Str_JobStatus = "";
    public static EditText edt_search_order;
    public static int arraySize = 0;
    public static LinearLayout linearPIN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orderlist);
        mContext = OrderList.this;
        Init();
    }

    private void Init() {
        linearPIN = findViewById(R.id.linear_pin);
        linearPIN.setVisibility(View.GONE);
        tv_header = (TextView) findViewById(R.id.tv_header);
        list_order = (ListView) findViewById(R.id.list_order);
        list_order.setVisibility(View.VISIBLE);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DashboardActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        ArrayOrderList = new ArrayList<>();
        ArrayOrderListFiltered = new ArrayList<>();
        ArrayOrderListImplemented = new ArrayList<>();

        gps = new TrackGPS(mContext);

        list_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Str_JobStatus.equalsIgnoreCase("Assigned")) {

                    if(ArrayOrderList.get(position).getAppLock().equalsIgnoreCase("1")){
                        Utils.Alert("This job is locked",mContext);
                        return;
                    }
                    Intent intent = new Intent(mContext, OrderDetail.class);
                    intent.putExtra("OrderInner", "" + ArrayOrderList.get(position).getSublist());
                    intent.putExtra("Trip_No", "" + ArrayOrderList.get(position).getTrip_No());
                    intent.putExtra("ERP_No", "" + ArrayOrderList.get(position).getERP_No());
                    intent.putExtra("Viewed", "" + ArrayOrderList.get(position).getViewed());
                    intent.putExtra("Str_JobStatus", Str_JobStatus);
                    startActivity(intent);
                }
            }
        });

        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        activity = OrderList.this;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            Str_JobStatus = b.getString("Str_JobStatus");
            tv_header.setText(b.getString("title"));
        }

        edt_search_order = (EditText) findViewById(R.id.edt_search_order);
        edt_search_order.setVisibility(View.VISIBLE);
        edt_search_order.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter("" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(mContext, DashboardActivity.class);
        activity.startActivity(intent);
        activity.finish();
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, DashboardActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ArrayOrderListFiltered.clear();
        if (charText.length() == 0) {
            ArrayOrderListFiltered.addAll(ArrayOrderList);
        } else {
            for (ModelOrderList wp : ArrayOrderList) {
                if ((wp.getDisplay().toLowerCase(Locale.getDefault()).contains(charText)) || (wp.getTrip_No().toLowerCase(Locale.getDefault()).contains(charText))) {
                    ArrayOrderListFiltered.add(wp);
                }

            }

        }

        ArrayOrderListImplemented.clear();
        ArrayOrderListImplemented.addAll(ArrayOrderListFiltered);

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (edt_search_order != null) {
            edt_search_order.setText("");
        }
        APIUtils.getAddressFromLatLong(mContext, gps.getLatitude(), gps.getLongitude(), "OrderListAddress");
    }

    //Ask User to choose Yes/No
    public static void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext,R.style.AlertDialogStyle);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutOrderList");
            }
        });

        builderInner.setNegativeButton(mContext.getResources().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public void showResponse(String response, String redirectionKey) {
        try {
            if (redirectionKey.equalsIgnoreCase("OrderListAddress")) {
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                String lat = "0";
                String lng = "0";
                String GPSStatus = "OFF";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }

                    lat = "" + gps.getLatitude();
                    lng = "" + gps.getLongitude();
                    GPSStatus = "ON";
                }

                getOrderList(address, lat, lng, GPSStatus);
            } else if (redirectionKey.equalsIgnoreCase("logoutOrderList")) {

                try {
                    JSONObject jobj = new JSONObject(response);
                    if (jobj.optString("Status").equalsIgnoreCase("1")) {
                        Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(logoutIntent);
                        OrderList.activity.finish();
                        Utils.clearPref(mContext);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (redirectionKey.equalsIgnoreCase("OrderList")) {
                try {
                    ArrayOrderList.clear();
                    JSONObject jobj = new JSONObject(response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject innerObj = jsonArray.getJSONObject(i);
                        modelOrderList = new ModelOrderList();
                        modelOrderList.setTrip_No(innerObj.optString("Trip_No"));
                        modelOrderList.setDisplay(innerObj.optString("Display"));
                        modelOrderList.setvTrip_Dt(innerObj.optString("vTrip_Dt"));
                        if (innerObj.optString("vTrip_Tm").contains("_")) {
                            modelOrderList.setvTrip_Tm(innerObj.optString("vTrip_Tm").split("_")[0]);
                        } else {
                            modelOrderList.setvTrip_Tm(innerObj.optString("vTrip_Tm"));
                        }
                        modelOrderList.setERP_No(innerObj.optString("ERP_No"));
                        modelOrderList.setZONE(innerObj.optString("ZONE"));
                        modelOrderList.setViewed(innerObj.optString("Viewed"));

                        JSONArray sublist=innerObj.optJSONArray("sublist");
                        JSONObject sublistobj=sublist.optJSONObject(0);
                        modelOrderList.setMovie_Seats(sublistobj.optString("Movie_Seats"));
                        modelOrderList.setJobNo(sublistobj.optString("JobNo"));
                        modelOrderList.setAppLock(sublistobj.optString("AppLock"));
                        modelOrderList.setMovie_Milestone(sublistobj.optString("Movie_Milestone"));
                        modelOrderList.setApps_Status(innerObj.optString("Apps_Status"));
                        modelOrderList.setVeh_Status(innerObj.optString("Veh_Status"));
                        modelOrderList.setSublist(innerObj.optJSONArray("sublist"));
                        ArrayOrderList.add(modelOrderList);
                    }


                    arraySize = ArrayOrderList.size();
                    ArrayOrderListImplemented.clear();
                    ArrayOrderListImplemented.addAll(ArrayOrderList);
                    Log.i("ORDER ARRAY", "==============" + ArrayOrderList.size());
                    adapter = new ListElementAdapter(mContext);
                    list_order.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getOrderList(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Order List", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + DriverID + "&Str_JobView=" + Str_JobView + "&Str_JobStatus=" + Str_JobStatus + "&Str_TripNo=NA&Str_JobNo=0" + "&Str_JobDate=" + Utils.getPref(mContext.getResources().getString(R.string.pref_datetime), mContext), "OrderList");
    }

    //ListView Adapter Class
    public static class ListElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public ListElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ArrayOrderListImplemented.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder h = null;

            if (v == null) {
                v = layoutInflater.inflate(R.layout.raw_orderlist, null);
                h = new ViewHolder();
                h.tv_tripno = (TextView) v.findViewById(R.id.tv_tripno);
                h.tv_display = (TextView) v.findViewById(R.id.tv_display);
                h.tv_date = (TextView) v.findViewById(R.id.tv_date);
                h.tv_time = (TextView) v.findViewById(R.id.tv_time);
                h.tv_zone = (TextView) v.findViewById(R.id.tv_zone);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);
                h.mLinearAppVehicleStatus = (LinearLayout) v.findViewById(R.id.linear_app_Vehicle_status);
                h.imgAppStatus = (ImageView) v.findViewById(R.id.img_app_status);
                h.imgVehicleStatus = (ImageView) v.findViewById(R.id.img_vehicle_status);

                h.tv_tripno.setText(ArrayOrderListImplemented.get(position).getTrip_No());
                h.tv_date.setText(ArrayOrderListImplemented.get(position).getvTrip_Dt());
                h.tv_time.setText(ArrayOrderListImplemented.get(position).getvTrip_Tm());

                String displayValue = ArrayOrderListImplemented.get(position).getDisplay();


                try {
                    if (Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext).equalsIgnoreCase("AMAT") || Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext).equalsIgnoreCase("BHS")) {
                        //17 CHANGI SOUTH STREET 2 (17 CHANGI SOUTH STREET 2 To SLC 3)
                        String[] array1 = displayValue.split("\\[");
                        String subString1 = array1[1].substring(0, array1[1].length() - 1);
                        String[] array2 = subString1.split("To");
                        h.tv_display.setText(Html.fromHtml("<font color='#000000'>" + array1[0] + "[</font>" + "<font color='#85C1E9'>" + array2[0] + "</font>" + "<font color='#000000'> To </font>" + "" + array2[1] + "" + "<font color='#000000'>]</font>"));

                    } else {
                        h.tv_display.setText(displayValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    h.tv_display.setText(displayValue);

                }

                if (Str_JobStatus.equalsIgnoreCase("Assigned")) {
                    h.mLinearAppVehicleStatus.setVisibility(View.VISIBLE);
                    h.tv_zone.setVisibility(View.GONE);

                    if (ArrayOrderListImplemented.get(position).getApps_Status().equalsIgnoreCase("ON")) {
                        h.imgAppStatus.setImageResource(R.drawable.ic_app_unlock);
                    } else {
                        h.imgAppStatus.setImageResource(R.drawable.ic_app_lock);
                    }


                    if (ArrayOrderListImplemented.get(position).getVeh_Status().equalsIgnoreCase("ON")) {
                        h.imgVehicleStatus.setImageResource(R.drawable.ic_vehicle_on);
                    } else {
                        h.imgVehicleStatus.setImageResource(R.drawable.ic_vehicle_off);
                    }

                } else {
                    h.mLinearAppVehicleStatus.setVisibility(View.GONE);
                    h.tv_zone.setVisibility(View.VISIBLE);

                    if(ArrayOrderListImplemented.get(position).getMovie_Seats().equalsIgnoreCase("1")){
                        h.tv_zone.setVisibility(View.VISIBLE);
                        h.tv_zone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loaded, 0, 0, 0);
                        h.tv_zone.setText("");
                    }
                }



                h.tv_zone.setText(ArrayOrderListImplemented.get(position).getZONE());
                h.tv_index.setText((position + 1) + ".");



                h.tv_zone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ArrayOrderListImplemented.get(position).getMovie_Seats().equalsIgnoreCase("1")) {
                            if (DashboardActivity.tv_container.getText().toString().trim().length() > 0) {
                                Intent intent = new Intent(view.getContext(), MovieSeat.class);
                                intent.putExtra("Str_ResName", DashboardActivity.tv_container.getText().toString().trim());
                                intent.putExtra("Str_SeqNo",ArrayOrderListImplemented.get(position).getJobNo());
                                activity.startActivity(intent);
                            } else {
                                Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_loading), mContext);
                                return;
                            }
                        }
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_tripno.setText(ArrayOrderListImplemented.get(position).getTrip_No());
                String displayValue = ArrayOrderListImplemented.get(position).getDisplay();

                try {
                    if (Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext).equalsIgnoreCase("AMAT") || Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext).equalsIgnoreCase("BHS")) {
                        //17 CHANGI SOUTH STREET 2 (17 CHANGI SOUTH STREET 2 To SLC 3)
                        String[] array1 = displayValue.split("\\[");
                        String subString1 = array1[1].substring(0, array1[1].length() - 1);
                        String[] array2 = subString1.split("To");
                        h.tv_display.setText(Html.fromHtml("<font color='#000000'>" + array1[0] + "[</font>" + "<font color='#85C1E9'>" + array2[0] + "</font>" + "<font color='#000000'> To </font>" + "" + array2[1] + "" + "<font color='#000000'>]</font>"));

                    } else {
                        h.tv_display.setText(displayValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    h.tv_display.setText(displayValue);

                }


                h.tv_date.setText(ArrayOrderListImplemented.get(position).getvTrip_Dt());
                h.tv_time.setText(ArrayOrderListImplemented.get(position).getvTrip_Tm());
                h.tv_zone.setText(ArrayOrderListImplemented.get(position).getZONE());
                h.tv_index.setText((position + 1) + ".");


                if (Str_JobStatus.equalsIgnoreCase("Assigned")) {
                    h.mLinearAppVehicleStatus.setVisibility(View.VISIBLE);
                    h.tv_zone.setVisibility(View.GONE);

                    if (ArrayOrderListImplemented.get(position).getApps_Status().equalsIgnoreCase("ON")) {
                        h.imgAppStatus.setImageResource(R.drawable.ic_app_unlock);
                    } else {
                        h.imgAppStatus.setImageResource(R.drawable.ic_app_lock);
                    }


                    if (ArrayOrderListImplemented.get(position).getVeh_Status().equalsIgnoreCase("ON")) {
                        h.imgVehicleStatus.setImageResource(R.drawable.ic_vehicle_on);
                    } else {
                        h.imgVehicleStatus.setImageResource(R.drawable.ic_vehicle_off);
                    }

                } else {
                    h.mLinearAppVehicleStatus.setVisibility(View.GONE);
                    h.tv_zone.setVisibility(View.VISIBLE);
                    if(ArrayOrderListImplemented.get(position).getMovie_Seats().equalsIgnoreCase("1")){
                        h.tv_zone.setVisibility(View.VISIBLE);
                        h.tv_zone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loaded, 0, 0, 0);
                        h.tv_zone.setText("");
                    }
                }



                h.tv_zone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ArrayOrderListImplemented.get(position).getMovie_Seats().equalsIgnoreCase("1")) {
                            if (DashboardActivity.tv_container.getText().toString().trim().length() > 0) {
                                Intent intent = new Intent(view.getContext(), MovieSeat.class);
                                intent.putExtra("Str_ResName", DashboardActivity.tv_container.getText().toString().trim());
                                intent.putExtra("Str_SeqNo",ArrayOrderListImplemented.get(position).getJobNo());
                                activity.startActivity(intent);
                            } else {
                                Utils.Alert(mContext.getResources().getString(R.string.alert_attach_container_while_loading), mContext);
                                return;
                            }
                        }
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_tripno;
            private TextView tv_display;
            private TextView tv_date;
            private TextView tv_time;
            private TextView tv_zone;
            private TextView tv_index;
            private LinearLayout mLinearAppVehicleStatus;
            private ImageView imgAppStatus, imgVehicleStatus;
        }
    }
}
