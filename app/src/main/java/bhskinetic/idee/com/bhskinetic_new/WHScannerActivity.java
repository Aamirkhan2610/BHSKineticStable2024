package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import Model.ModelLeaveHistory;
import Model.ModelLeaveType;
import Model.ModelScanStatus;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 04/06/2019.
 */
public class WHScannerActivity extends Activity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static ImageView img_scan;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static Button btn_warehousename,btn_mainpillar,btn_subpillar,btn_submit,btn_scanlist;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ListView lv_request_status;
    public static ModelLeaveType modelLeaveType;
    public static ArrayList<ModelLeaveType> arrayLeaveType;
    public static ArrayList<String> arrayScanResult;
    public static ModelScanStatus modelScanStatus;
    public static ListElementAdapter adapter;
    public static String leaveTypeID = "";
    public static String requestType = "";
    public static String PhotoJSON = "";
    public static String AttachedType = "";
    public static ArrayAdapter<String> adapterleave;
    public static int ReqIndex=0;
    public static String ScanCode="";
    public static boolean isList=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whscanner_activity);
        Init();
    }

    private void Init() {
        ScanCode="";
        isList=false;
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        btn_scanlist= findViewById(R.id.btn_scanlist);
        arrayScanResult = new ArrayList<>();
        mContext = WHScannerActivity.this;
        adapter = new ListElementAdapter(mContext);

        img_scan=findViewById(R.id.img_scan);
        img_refresh.setImageResource(R.drawable.ic_back);
        ReqIndex=0;
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isList=false;
                Intent intent = new Intent(mContext, ScanActivityDefault.class);
                activity.startActivityForResult(intent, 0);
            }
        });

        btn_scanlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isList=true;
                Intent intent = new Intent(mContext, ScanActivityDefault.class);
                activity.startActivityForResult(intent, 0);
            }
        });


        gps = new TrackGPS(mContext);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));

        }

        img_logout.setVisibility(View.GONE);

        activity = WHScannerActivity.this;
        arrayLeaveHistory=new ArrayList<>();

        btn_warehousename = (Button) findViewById(R.id.btn_warehousename);
        btn_mainpillar = (Button) findViewById(R.id.btn_mainpillar);
        btn_subpillar = (Button) findViewById(R.id.btn_subpillar);
        btn_submit= findViewById(R.id.btn_submit);
        btn_scanlist= findViewById(R.id.btn_scanlist);

        lv_request_status = (ListView) findViewById(R.id.lv_request_status);
        lv_request_status.setAdapter(adapter);

        arrayLeaveType = new ArrayList<>();
        leaveTypeID = "";
        AttachedType = "";
        PhotoJSON = "";

        btn_warehousename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReqIndex=0;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"","Warehouse List","NA","Warehouse Scan");
            }
        });

        btn_mainpillar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_warehousename.getText().toString().trim().equalsIgnoreCase("WAREHOUSE NAME")){
                    Utils.Alert("Select Warehouse name",mContext);
                    return;
                }

                ReqIndex=1;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"","Piller Name",btn_warehousename.getText().toString().trim(),"Warehouse Scan");
            }
        });

        btn_subpillar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_mainpillar.getText().toString().trim().equalsIgnoreCase("MAIN PILLER NAME")){
                    Utils.Alert("Select Main Piller Name",mContext);
                    return;
                }

                ReqIndex=2;
                getMaintTypes("",""+gps.getLatitude(),""+gps.getLongitude(),"","Floor Name",btn_mainpillar.getText().toString().trim(),"Warehouse Scan");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_warehousename.getText().toString().trim().equalsIgnoreCase("WAREHOUSE NAME")){
                    Utils.Alert("Select Warehouse name",mContext);
                    return;
                }

                if(btn_mainpillar.getText().toString().trim().equalsIgnoreCase("MAIN PILLER NAME")){
                    Utils.Alert("Select Main Piller Name",mContext);
                    return;
                }

                if(btn_subpillar.getText().toString().trim().equalsIgnoreCase("SUB PILLER NAME")){
                    Utils.Alert("Select Sub Piller Name",mContext);
                    return;
                }

                if(arrayScanResult.size()==0){
                    Utils.Alert("Scan Barecode",mContext);
                    return;
                }

                String ItemBarcode = "";
                for (int i = 0; i < arrayScanResult.size(); i++) {
                    if (ItemBarcode.trim().length() > 0) {
                        ItemBarcode = ItemBarcode + "," + arrayScanResult.get(i);
                    } else {
                        ItemBarcode = arrayScanResult.get(i);
                    }
                }

                SubmitStoreScanner("",""+gps.getLatitude(),""+gps.getLongitude(),"", ItemBarcode);

            }
        });


    }

    private void checkBarecodeValidation(String address, String lat, String lng, String gpsStatus,String barecodeNumber) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Scan Validation", "Chk_Barcode.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_JobNo="+"0"+"&Str_Barcode="+barecodeNumber+"&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_worksite),mContext), "ValidateBarecodeWH");
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
            return arrayScanResult.size();
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
                v = layoutInflater.inflate(R.layout.raw_scan_result, null);
                h = new ViewHolder();
                h.tv_scan_result = (TextView) v.findViewById(R.id.tv_scan_result);
                h.tv_scan_result.setText(arrayScanResult.get(position));
                h.layout_delete = (LinearLayout) v.findViewById(R.id.layout_delete);
                h.img_delete = (ImageView) v.findViewById(R.id.img_delete);
                h.layout_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayScanResult.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_scan_result.setText(arrayScanResult.get(position));
                h.layout_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayScanResult.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_scan_result;
            private ImageView img_delete;
            private LinearLayout layout_delete;
        }

    }

    private static void SubmitStoreScanner(String address, String lat, String lng, String gpsStatus,String ItemBarcode) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Store_Scanner.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_Lat="+lat+"&Str_Long="+lng+"&Str_DriverID="+driverId+"&ReqType=WH%20Scanner&WarehouseName="+btn_warehousename.getText().toString().trim()+"&PillerName="+btn_mainpillar.getText().toString().trim()+"&FloorName="+btn_subpillar.getText().toString().trim()+"&ItemBarcode="+ItemBarcode+"&ProjectType=BHS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "SubmitWHScanner");
    }



    private static void getMaintTypes(String address, String lat, String lng, String gpsStatus, String Str_SEL1, String Str_SEL2,String Str_ListType) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Maint_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_DriverID="+driverId+"&Str_ListType="+Str_ListType+"&Str_SEL1="+Str_SEL1+"&Str_SEL2="+Str_SEL2+"&Str_Lat="+lat+"&Str_Long="+lng;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "WHMaintList");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lv_request_status != null) {
            if(Utils.ScanResult.length()>0) {
                if (!arrayScanResult.contains(Utils.ScanResult)) {
                   /* */

                    checkBarecodeValidation("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF",Utils.ScanResult);

                }
            }
        }
    }

    public void DialogueWithList(final ArrayList<ModelLeaveType> leaveTypes) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);

        if (requestType.equalsIgnoreCase("Maintenances")) {
            dialog.setTitle("SERVICE TYPE");
        } else {
            dialog.setTitle(mContext.getResources().getString(R.string.btn_request_name));
        }

        final ArrayList<ModelLeaveType> leaveTypesFiltered = new ArrayList<>();
        final ArrayList<ModelLeaveType> leaveTypesImplemented = new ArrayList<>();
        leaveTypesImplemented.addAll(leaveTypes);

        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);

        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[leaveTypesImplemented.size()];
        for (int i = 0; i < leaveTypesImplemented.size(); i++) {
            if (requestType.equalsIgnoreCase("Leave")) {
                values[i] = leaveTypesImplemented.get(i).getListValue() + " (" + leaveTypesImplemented.get(i).getL_Bal() + ") ";
            } else {
                values[i] = leaveTypesImplemented.get(i).getListValue();
            }
        }
        adapterleave = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterleave);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                leaveTypesFiltered.clear();
                if (charText.length() == 0) {
                    leaveTypesFiltered.addAll(leaveTypes);
                } else {
                    for (ModelLeaveType wp : leaveTypes) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            leaveTypesFiltered.add(wp);
                        }

                    }

                }

                leaveTypesImplemented.clear();
                leaveTypesImplemented.addAll(leaveTypesFiltered);


                final String[] values = new String[leaveTypesImplemented.size()];
                for (int i = 0; i < leaveTypesImplemented.size(); i++) {

                    if (requestType.equalsIgnoreCase("Leave")) {
                        values[i] = leaveTypesImplemented.get(i).getListValue() + " (" + leaveTypesImplemented.get(i).getL_Bal() + ") ";
                    } else {
                        values[i] = leaveTypesImplemented.get(i).getListValue();
                    }
                }
                adapterleave = new ArrayAdapter<String>(mContext, R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterleave);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialog.dismiss();
                if(ReqIndex==0){
                    btn_warehousename.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }else if(ReqIndex==1){
                    btn_mainpillar.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }else{
                    btn_subpillar.setText(leaveTypesImplemented.get(position).getListValueWithoutIndex());
                }
                leaveTypeID = leaveTypesImplemented.get(position).getListID();
            }
        });
        dialog.show();
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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutLeaverequest");
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
        if (redirectionKey.equalsIgnoreCase("WHMaintList")) {
            try {

                JSONObject jobj = new JSONObject(response);
                arrayLeaveType.clear();

                JSONArray list = jobj.optJSONArray("Data");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject jsonObject = list.optJSONObject(i);
                    modelLeaveType = new ModelLeaveType();
                    modelLeaveType.setListValue((i + 1) + "." + jsonObject.optString("Res_Name"));
                    modelLeaveType.setListValueWithoutIndex(jsonObject.optString("Res_Name"));
                    modelLeaveType.setListID(jsonObject.optString("SeqNo"));
                    modelLeaveType.setReq_Status(jsonObject.optString("Stand_By"));
                    arrayLeaveType.add(modelLeaveType);
                }

                if(arrayLeaveType.size()>0){
                    DialogueWithList(arrayLeaveType);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (redirectionKey.equalsIgnoreCase("SubmitWHScanner")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    Toast.makeText(mContext, jobj.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    arrayScanResult.clear();
                    adapter.notifyDataSetChanged();
                    activity.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (redirectionKey.equalsIgnoreCase("ValidateBarecodeWH")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("MSG").equalsIgnoreCase("Barcode is not Valid")) {
                    Toast.makeText(mContext,jsonObject.optString("MSG"),Toast.LENGTH_SHORT).show();
                }else{
                    if(!isList) {
                        btn_subpillar.setText(Utils.ScanResult);
                    }else{
                        arrayScanResult.add(Utils.ScanResult);
                        adapter.notifyDataSetChanged();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void ConfirmCancelSubmition(final String vseqNumber, final String miscType, final String miscStatus) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        submitCancelRequest("NA",""+gps.getLatitude(),""+gps.getLongitude(),"ON",vseqNumber,miscType,miscStatus);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        builder.setMessage("Are you sure to cancel this reuqest?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void submitCancelRequest(String address, String lat, String lng, String gpsStatus,String seqNumber,String miscType,String miscStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+driverId+"&Str_SeqNo="+seqNumber+"&Str_Misc_Type="+miscType+"&Str_Misc_Status="+miscStatus+"&Str_JobFor=BHS";
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "LeaveCancel");
    }

    private void getLeaveDetail(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + requestType;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "LeaveData");
    }
}
