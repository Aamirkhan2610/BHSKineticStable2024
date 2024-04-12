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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Model.ModelAttachment;
import Model.ModelException;
import Model.ModelLeaveHistory;
import Model.ModelLeaveType;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 12/11/2017.
 */

public class MaintListSupplier extends Activity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ListView list_maintlist;
    public static ListElementAdapter adapter;
    public static ModelLeaveType modelLeaveType;
    public static ModelLeaveHistory modelleaveHistory;
    public static String requestType = "Maintenances";
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    ArrayAdapter<String> adapterAttachment;
    public static String PhotoJSON = "";
    public static ArrayList<ModelAttachment> ArrayAttachment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maintlist_supplier);
        activity = MaintListSupplier.this;
        mContext = MaintListSupplier.this;
        gps = new TrackGPS(mContext);
        Init();
    }

    private void Init() {
        PhotoJSON = "";
        ArrayAttachment = new ArrayList<>();
        arrayLeaveHistory = new ArrayList<>();
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        list_maintlist = findViewById(R.id.list_maintlist);
        img_refresh.setImageResource(R.drawable.ic_back);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
        }

        img_logout.setVisibility(View.GONE);

        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        getLeaveDetail("", "" + gps.getLatitude(), "" + gps.getLongitude(), "");
    }

    private void getLeaveDetail(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + requestType;
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Data", URL, "MainListSupplier");
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    //Ask User to choose Yes/No
    public static void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutParking");
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
        if (redirectionKey.equalsIgnoreCase("MainListSupplier")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveHistory.clear();
                    JSONArray list2 = jobj.optJSONArray("list2");
                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject jsonObject = list2.optJSONObject(i);
                        modelleaveHistory = new ModelLeaveHistory();
                        modelleaveHistory.setReq_Name(jsonObject.optString("Req_Name"));
                        modelleaveHistory.setReq_Status(jsonObject.optString("Req_Status"));
                        modelleaveHistory.setReq_Display(jsonObject.optString("Req_Display"));
                        modelleaveHistory.setListID(jsonObject.optString("Req_SeqNo"));
                        modelleaveHistory.setComplete_Status(jsonObject.optString("Complete_Status"));
                        modelleaveHistory.setReq_StartDate(jsonObject.optString("Req_StartDate"));
                        modelleaveHistory.setPONo(jsonObject.optString("PONo"));
                        modelleaveHistory.setVehicleNo(jsonObject.optString("VehicleNo"));
                        Log.i("Req_Status============>", jsonObject.optString("Req_Name") + "\n" + jsonObject.optString("Req_Status"));
                        arrayLeaveHistory.add(modelleaveHistory);
                    }


                    if (jobj.has("list1")) {
                        PhotoJSON = jobj.optJSONArray("list1").toString();
                    }


                    adapter = new ListElementAdapter(mContext);
                    list_maintlist.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("maintAttachmentList")) {
            try {
                ArrayAttachment.clear();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray Data = jsonObject.optJSONArray("Data");

                for (int i = 0; i < Data.length(); i++) {
                    JSONObject ojb0 = Data.getJSONObject(i);
                    if (!ojb0.has("sublist")) {
                        Toast.makeText(mContext, "No Attachment Available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                for (int i = 0; i < Data.length(); i++) {
                    JSONObject ojb0 = Data.getJSONObject(i);
                    JSONArray sublist = ojb0.optJSONArray("sublist");
                    ModelAttachment modelAttachment;
                    for (int j = 0; j < sublist.length(); j++) {
                        JSONObject sublistObj = sublist.optJSONObject(j);
                        modelAttachment = new ModelAttachment();
                        modelAttachment.setFileName((j + 1) + "." + sublistObj.optString("FileName"));
                        modelAttachment.setFileNameWithoutIndexing(sublistObj.optString("FileName"));
                        modelAttachment.setStr_URL(sublistObj.optString("Str_URL"));
                        modelAttachment.setType(sublistObj.optString("Type"));
                        ArrayAttachment.add(modelAttachment);
                    }


                    DialogueWithList(ArrayAttachment);
                }
                Log.i("ATTACHMENT ARRAY SIZE", "==>" + ArrayAttachment.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (redirectionKey.equalsIgnoreCase("attachmentlist")) {
            try {
                ArrayAttachment.clear();
                ModelAttachment modelAttachment;
                JSONObject jsonObject = new JSONObject(response);
                JSONArray Data = jsonObject.optJSONArray("Data");
                for (int i = 0; i < Data.length(); i++) {
                    JSONObject ojb0 = Data.getJSONObject(i);
                    JSONArray sublist = ojb0.optJSONArray("sublist");

                    for (int j = 0; j < sublist.length(); j++) {
                        JSONObject sublistObj = sublist.optJSONObject(j);
                        modelAttachment = new ModelAttachment();
                        modelAttachment.setFileName((j + 1) + "." + sublistObj.optString("FileName"));
                        modelAttachment.setFileNameWithoutIndexing(sublistObj.optString("FileName"));
                        modelAttachment.setStr_URL(sublistObj.optString("Str_URL"));
                        modelAttachment.setType(sublistObj.optString("Type"));
                        ArrayAttachment.add(modelAttachment);
                    }
                }

                DialogueWithList(ArrayAttachment);
                Log.i("ATTACHMENT ARRAY SIZE", "==>" + ArrayAttachment.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void DialogueWithListException(final ArrayList<ModelException> exceptionlist, final int flag) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist_addjob);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_select_type));
        final ArrayList<ModelException> exceptionlistFiltered = new ArrayList<>();
        final ArrayList<ModelException> exceptionlistImplemented = new ArrayList<>();
        exceptionlistImplemented.addAll(exceptionlist);
        final ListView lv_resource = (ListView) dialoglist.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialoglist.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });
        RelativeLayout relTop = dialoglist.findViewById(R.id.rel_top);
        relTop.setVisibility(View.INVISIBLE);
        Button btn_submit = dialoglist.findViewById(R.id.btn_submit);
        btn_submit.setVisibility(View.GONE);

        final EditText etSearch = (EditText) dialoglist.findViewById(R.id.edt_search);
        etSearch.setHint("Enter Remark");
        etSearch.setVisibility(View.VISIBLE);


        final String[] values = new String[exceptionlistImplemented.size()];
        for (int i = 0; i < exceptionlistImplemented.size(); i++) {
            values[i] = exceptionlistImplemented.get(i).getListValue();
        }
        ArrayAdapter<String> adapterException = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterException);


        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*if(etSearch.getText().toString().length()==0){
                    Utils.Alert("Please enter Remark",mContext);
                    return;
                }*/
                String REMARK = "";
                dialoglist.dismiss();
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                String STR_REV = exceptionlistImplemented.get(position).getListValue();
                REMARK = etSearch.getText().toString().trim();


            }
        });


        dialoglist.show();
    }

    public void DialogueWithList(final ArrayList<ModelAttachment> attachmentList) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.raw_attachmentlist);
        dialog.setCancelable(false);
        dialog.setTitle(mContext.getResources().getString(R.string.alert_attachment_title));
        final ListView lv_resource = (ListView) dialog.findViewById(R.id.lv_resource);
        final ArrayList<ModelAttachment> attachmentListFiltered = new ArrayList<>();
        final ArrayList<ModelAttachment> attachmentListImplemented = new ArrayList<>();
        attachmentListImplemented.addAll(attachmentList);


        EditText etSearch = (EditText) dialog.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                attachmentListFiltered.clear();
                if (charText.length() == 0) {
                    attachmentListFiltered.addAll(attachmentList);
                } else {
                    for (ModelAttachment wp : attachmentList) {
                        if ((wp.getFileName().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            attachmentListFiltered.add(wp);
                        }

                    }

                }

                attachmentListImplemented.clear();
                attachmentListImplemented.addAll(attachmentListFiltered);

                final String[] values = new String[attachmentListImplemented.size()];
                for (int i = 0; i < attachmentListImplemented.size(); i++) {
                    values[i] = attachmentListImplemented.get(i).getFileName();
                }
                adapterAttachment = new ArrayAdapter<String>(mContext,
                        R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterAttachment);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final String[] values = new String[attachmentListImplemented.size()];
        for (int i = 0; i < attachmentListImplemented.size(); i++) {
            values[i] = attachmentListImplemented.get(i).getFileName();
        }
        adapterAttachment = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterAttachment);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (attachmentListImplemented.get(position).getType().equalsIgnoreCase("File")) {
                    Intent intent = new Intent(view.getContext(), WebViewActivitiy.class);
                    intent.putExtra("DocumentURL", attachmentListImplemented.get(position).getStr_URL());
                    intent.putExtra("DocumentTitle", attachmentListImplemented.get(position).getFileNameWithoutIndexing());
                    mContext.startActivity(intent);
                    dialog.dismiss();
                } else {
                    Intent intent = new Intent(view.getContext(), ImageViewActivitiy.class);
                    intent.putExtra("DocumentURL", attachmentListImplemented.get(position).getStr_URL());
                    intent.putExtra("DocumentTitle", attachmentListImplemented.get(position).getFileNameWithoutIndexing());
                    mContext.startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public static void getAttachmentList(String address, String lat, String lng, String gpsStatus, String tripNumber, String JobNo) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String Str_ProjName = Utils.getPref(mContext.getResources().getString(R.string.pref_worksite), mContext);
        APIUtils.sendRequest(mContext, "Attachment List", "Attc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_FileType=Attachment&Str_TripNo=" + tripNumber + "&Str_JobNo=" + JobNo + "&Str_ProjName=Supplier", "maintAttachmentList");
    }

    private static void getRevList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Rev List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=" + "Photos", "revlistMaintList");
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
            return arrayLeaveHistory.size();
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
                v = layoutInflater.inflate(R.layout.raw_maintlist, null);
                h = new ViewHolder();
                h.tv_leavetitle = (TextView) v.findViewById(R.id.tv_leavetitle);
                h.tv_leavedetail = (TextView) v.findViewById(R.id.tv_leavedetail);
                h.img_status = (ImageView) v.findViewById(R.id.img_status);
                h.img_snap = (ImageView) v.findViewById(R.id.img_snap);
                h.rel_raw = v.findViewById(R.id.rel_raw);
                h.img_message = (ImageView) v.findViewById(R.id.img_message);
                h.img_attachment = (ImageView) v.findViewById(R.id.img_attachment);
                h.tv_leavetitle.setText(" " + (position + 1) + "." + arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());
                boolean isCancel = false;
                //2018-12-06 08:00:00.0
                String appliedDateString = arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if (!appliedDate.before(currentDate)) {
                        isCancel = true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }


                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                }

                h.img_snap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoUploadActivity.class);
                        intent.putExtra("isSign", false);
                        intent.putExtra("Str_JobNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_Sts", arrayLeaveHistory.get(position).getReq_Status());
                        intent.putExtra("isPONO", "1");
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        intent.putExtra("Str_TripNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_Event", "Photos");
                        mContext.startActivity(intent);
                    }
                });

                h.img_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), MessageNotificationTab.class);
                        intent.putExtra("Str_TripNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("CurrentTab", 1);
                        intent.putExtra("Stry_Msg_Type", "CS");
                        intent.putExtra("Str_JobNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_MsgID", "0");
                        intent.putExtra("Str_SentTo", "CS");
                        mContext.startActivity(intent);
                    }
                });

                h.img_attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAttachmentList("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", arrayLeaveHistory.get(position).getListID(), arrayLeaveHistory.get(position).getListID());
                    }
                });

                h.rel_raw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MaintananceReqSupplier.class);
                        String contentValue = "PO NO: " + arrayLeaveHistory.get(position).getPONo() + "\n" +
                                "Requested by: " + Utils.getPref(mContext.getResources().getString(R.string.pref_Client_Name), mContext) + "\n" +
                                "Vehicle No: " + arrayLeaveHistory.get(position).getVehicleNo();
                        intent.putExtra("Req_SeqNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("title", "MAINT REQ UPDATE");
                        intent.putExtra("contentValue", contentValue);
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        mContext.startActivity(intent);
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_leavetitle.setText(" " + (position + 1) + "." + arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());


                boolean isCancel = false;
                //2018-12-06 08:00:00.0
                String appliedDateString = arrayLeaveHistory.get(position).getReq_StartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date appliedDate = sdf.parse(appliedDateString);
                    Date currentDate = Calendar.getInstance().getTime();
                    if (!appliedDate.before(currentDate)) {
                        isCancel = true;
                    }
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Approved")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_approve);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                }

                h.img_snap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // getRevList("",""+gps.getLatitude(),""+gps.getLongitude(),"");
                        Intent intent = new Intent(mContext, PhotoUploadActivity.class);
                        intent.putExtra("isSign", false);
                        intent.putExtra("Str_JobNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_Sts", arrayLeaveHistory.get(position).getReq_Status());
                        intent.putExtra("isPONO", "1");
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        intent.putExtra("Str_TripNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_Event", "Photos");
                        mContext.startActivity(intent);
                    }
                });

                h.img_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), MessageNotificationTab.class);
                        intent.putExtra("Str_TripNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("CurrentTab", 1);
                        intent.putExtra("Stry_Msg_Type", "CS");
                        intent.putExtra("Str_JobNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("Str_MsgID", "0");
                        intent.putExtra("Str_SentTo", "CS");
                        mContext.startActivity(intent);
                    }
                });

                h.img_attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAttachmentList("", "" + gps.getLatitude(), "" + gps.getLongitude(), "", arrayLeaveHistory.get(position).getListID(), arrayLeaveHistory.get(position).getListID());
                    }
                });

                h.rel_raw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MaintananceReqSupplier.class);
                        String contentValue = "PO NO: " + arrayLeaveHistory.get(position).getPONo() + "\n" +
                                "Requested by: " + Utils.getPref(mContext.getResources().getString(R.string.pref_Client_Name), mContext) + "\n" +
                                "Vehicle No: " + arrayLeaveHistory.get(position).getVehicleNo();
                        intent.putExtra("title", "MAINT REQ UPDATE");
                        intent.putExtra("Req_SeqNo", arrayLeaveHistory.get(position).getListID());
                        intent.putExtra("contentValue", contentValue);
                        intent.putExtra("PhotoJSON", PhotoJSON);
                        mContext.startActivity(intent);
                    }
                });

            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_leavetitle;
            private TextView tv_leavedetail;
            private RelativeLayout rel_raw;
            private ImageView img_status, img_snap, img_message, img_attachment;
        }
    }

}

