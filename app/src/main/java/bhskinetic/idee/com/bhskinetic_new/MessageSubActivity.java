package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelMessage;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/19/2017.
 */

public class MessageSubActivity extends Activity {
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ListView list_message;
    public static ListElementAdapter adapter;
    public static ModelMessage modelMessage;
    public static ArrayList<ModelMessage> modelMessageArrayList;
    public static ImageView img_send;
    public static EditText edt_name;
    public static boolean isNotificationToShow = true;
    public static boolean ScrolledToBottomOnce=false;
    public static String Str_SeqNo="";
    public static ViewTreeObserver observer;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_message);
        Init();
    }

    private void Init() {

        isNotificationToShow = false;
        mContext = MessageSubActivity.this;
        activity = MessageSubActivity.this;
        gps = new TrackGPS(mContext);
        edt_name = (EditText) findViewById(R.id.edt_name);
        adapter = new ListElementAdapter(mContext);
        modelMessageArrayList = new ArrayList<>();
        list_message = (ListView) findViewById(R.id.list_message);
        list_message.setAdapter(adapter);
        img_send = (ImageView) findViewById(R.id.img_send);
        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_name.getText().toString().trim().length()>0) {
                    if (gps != null) {
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        if (!gps.canGetLocation()) {
                            lat = 0;
                            lng = 0;
                        }
                        APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressSendMessage");
                    }
                }else{
                    Toast.makeText(mContext,"ENTER MESSAGE",Toast.LENGTH_SHORT).show();
                }
            }
        });

        MessageNotificationTab.img_newmessage.setVisibility(View.VISIBLE);

        list_message.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = list_message.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        //  Toast.makeText(mContext,"TOP",Toast.LENGTH_SHORT).show();
                        // reached the top:
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  list_message.getChildAt(totalItemCount-1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {

                        UpdateReadStatus();

                        return;
                    }
                }
            }
        });

    }

    private void UpdateReadStatus() {
     //   if(!ScrolledToBottomOnce) {
            // reached the bottom:
            ScrolledToBottomOnce=true;
            Str_SeqNo="";
            for(int i=0;i<modelMessageArrayList.size();i++){
                if(Str_SeqNo.trim().length()==0){
                    Str_SeqNo=modelMessageArrayList.get(i).getStr_ID();
                }else{
                    Str_SeqNo=Str_SeqNo+","+modelMessageArrayList.get(i).getStr_ID();
                }
            }
            // Toast.makeText(mContext, "BOTTOM", Toast.LENGTH_SHORT).show();
            notificationReadStatus("",""+gps.getLatitude(),""+gps.getLatitude(),"");
      //  }
    }

    boolean willMyListScroll() {

        try {
            int pos = list_message.getLastVisiblePosition();
            if (list_message.getChildAt(pos).getBottom() > list_message.getHeight()) {
                return true;
            } else {
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }

    private void notificationReadStatus(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Notification Read Status", "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_SeqNo="+Str_SeqNo+"&Str_Misc_Type=MESSAGE&Str_Misc_Status=Read&Str_JobFor=BHS", "MSGReadUpdate");

    }

    @Override
    protected void onPause() {
        super.onPause();
        isNotificationToShow = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MessageNotificationTab.tv_header != null) {
            MessageNotificationTab.tv_header.setText(mContext.getResources().getString(R.string.title_message));
        }
        MessageNotificationTab.img_newmessage.setImageResource(R.drawable.ic_compose);
        isNotificationToShow = false;
        getLatestMessage();

        MessageNotificationTab.img_newmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserListActivity.class);
                intent.putExtra("title","New Message");
                startActivity(intent);
            }
        });
    }

    public static void getLatestMessage() {
        if (gps != null) {
            double lat = gps.getLatitude();
            double lng = gps.getLongitude();
            if (!gps.canGetLocation()) {
                lat = 0;
                lng = 0;
            }
            APIUtils.getAddressFromLatLong(mContext, lat, lng, "AddressMessage");
        }
    }

    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("AddressSendMessage")) {
            try {
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


                sendMessage(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("AddressMessage")) {
            try {
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


                getMessageList(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("messageList")) {

            try {
                modelMessageArrayList.clear();
                ArrayList<ModelMessage> arrayTemp = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    edt_name.setText("");
                    JSONArray Data = jsonObject.optJSONArray("list");
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject jsonObj = Data.getJSONObject(i);
                        modelMessage = new ModelMessage();
                        modelMessage.setDateTime(jsonObj.optString("Str_Msd_Dt"));
                        modelMessage.setMessage(jsonObj.optString("Str_Message"));
                        modelMessage.setStr_Type(jsonObj.optString("Str_MsgType"));
                        modelMessage.setStr_ID(jsonObj.optString("Str_MsgID"));
//                    modelMessage.setStr_Type(jsonObj.optString("Str_Type"));
                        arrayTemp.add(modelMessage);
                    }

                    for (int i = arrayTemp.size() - 1; i >= 0; i--) {
                        modelMessageArrayList.add(arrayTemp.get(i));
                    }

                    adapter.notifyDataSetChanged();
                    list_message.setSelection(MessageSubActivity.adapter.getCount() - 1);

                    UpdateReadStatus();

                    /*if(modelMessageArrayList.size()>0){
                        observer = list_message.getViewTreeObserver();
                        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (!willMyListScroll()) {
                                    // Do something
                                    UpdateReadStatus();
                                }
                            }
                        });
                    }*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("sendMessage")) {
            try {
                modelMessageArrayList.clear();
                ArrayList<ModelMessage> arrayTemp = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    edt_name.setText("");

                    JSONArray Data = jsonObject.optJSONArray("list");
                    JSONObject jsonObj = Data.getJSONObject(0);
                    MessageNotificationTab.Str_MsgID=jsonObj.optString("Str_MsgID");
                    getLatestMessage();


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (redirectionKey.equalsIgnoreCase("MSGReadUpdate")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if(jobj.optString("recived").equalsIgnoreCase("1")){
                     //Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        //IMEINumber = "11";
        String message = edt_name.getText().toString().trim();
        if (message.contains(" ")) {
            message = message.replaceAll(" ", "%20");
        }

        String  messageType="SUB";

        APIUtils.sendRequest(mContext, "Send Message","Message_Log.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_Msg_Type="+messageType+"&Str_DriverID="+driverId+"&Str_SentTo="+MessageNotificationTab.Str_SentTo+"&Str_MsgID="+MessageNotificationTab.Str_MsgID+"&Str_TripNo="+MessageNotificationTab.Str_TripNo+"&Str_JobNo="+MessageNotificationTab.Str_JobNo+"&Str_Message="+message, "sendMessage");
    }

    private void getMessageList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String messageType="MAIN";

        if(MessageNotificationTab.NewMessageSub.equalsIgnoreCase("1")){
            messageType="SUB";
        }
        //IMEINumber = "11";
        APIUtils.sendRequest(mContext, "Message List","Message_Log.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_Msg_Type="+messageType+"&Str_DriverID="+driverId+"&Str_SentTo="+MessageNotificationTab.Str_SentTo+"&Str_MsgID="+MessageNotificationTab.Str_MsgID+"&Str_TripNo="+MessageNotificationTab.Str_TripNo+"&Str_JobNo="+MessageNotificationTab.Str_JobNo+"&Str_Message=NA", "messageList");
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
            return modelMessageArrayList.size();
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
                h = new ViewHolder();

                v = layoutInflater.inflate(R.layout.item_chat_right, null);

                h.rel_right = (RelativeLayout) v.findViewById(R.id.rel_right);
                h.rel_left = (RelativeLayout) v.findViewById(R.id.rel_left);

                if (modelMessageArrayList.get(position).getStr_Type().equalsIgnoreCase("SENT")) {

                    h.rel_right.setVisibility(View.VISIBLE);
                    h.rel_left.setVisibility(View.GONE);
                } else {
                    h.rel_left.setVisibility(View.VISIBLE);
                    h.rel_right.setVisibility(View.GONE);
                }

                h.txt_msg = (TextView) v.findViewById(R.id.txt_msg);
                h.txt_datetime = (TextView) v.findViewById(R.id.txt_datetime);

                h.txt_msg1 = (TextView) v.findViewById(R.id.txt_msg1);
                h.txt_datetime1 = (TextView) v.findViewById(R.id.txt_datetime1);

                h.txt_msg.setText(modelMessageArrayList.get(position).getMessage());
                h.txt_datetime.setText(modelMessageArrayList.get(position).getDateTime());

                h.txt_msg1.setText(modelMessageArrayList.get(position).getMessage());
                h.txt_datetime1.setText(modelMessageArrayList.get(position).getDateTime());
                v.setTag(h);
            } else

            {
                h = (ViewHolder) v.getTag();
                if (modelMessageArrayList.get(position).getStr_Type().equalsIgnoreCase("SENT")) {

                    h.rel_right.setVisibility(View.VISIBLE);
                    h.rel_left.setVisibility(View.GONE);
                } else {
                    h.rel_left.setVisibility(View.VISIBLE);
                    h.rel_right.setVisibility(View.GONE);
                }
                h.txt_msg.setText(modelMessageArrayList.get(position).getMessage());
                h.txt_datetime.setText(modelMessageArrayList.get(position).getDateTime());

                h.txt_msg1.setText(modelMessageArrayList.get(position).getMessage());
                h.txt_datetime1.setText(modelMessageArrayList.get(position).getDateTime());
            }


            return v;

        }

        private class ViewHolder {
            private TextView txt_msg;
            private TextView txt_datetime;
            private TextView txt_msg1;
            private TextView txt_datetime1;
            private RelativeLayout rel_right;
            private RelativeLayout rel_left;
        }
    }

}