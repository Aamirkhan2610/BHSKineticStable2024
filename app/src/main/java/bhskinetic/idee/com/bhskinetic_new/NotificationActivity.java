package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import general.Const;
import general.DatabaseHandler;
import general.TrackGPS;
import general.Utils;
import general.offerObject;

/**
 * Created by Aamir on 4/19/2017.
 */

public class NotificationActivity extends Activity {
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    DatabaseHandler db;
    ListView list_notification;
    ArrayList<offerObject> list = new ArrayList<offerObject>();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_notification);
        Init();
    }

    private void Init() {
        mContext = NotificationActivity.this;
        gps = new TrackGPS(mContext);
        activity = NotificationActivity.this;
        db = new DatabaseHandler(NotificationActivity.this);
        list_notification=(ListView)findViewById(R.id.list_notification);
        MessageNotificationTab.img_newmessage.setVisibility(View.VISIBLE);
        new webservice().execute();
    }

    //Ask User to choose Yes/No
    public void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                    DatabaseHandler db = new DatabaseHandler(mContext);
                    db.Reset();
                    new webservice().execute();
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



    public class webservice extends AsyncTask<String, String, String> {

        ProgressDialog dialog = new ProgressDialog(NotificationActivity.this,R.style.ProgressDialogStyle);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (list.size() == 0) {
                //textView.setVisibility(View.VISIBLE);
                list_notification.setAdapter(null);
            } else {
                //pDialog.dismiss();
                //textView.setVisibility(View.GONE);
                ListViewAdapter adapter = new ListViewAdapter(NotificationActivity.this,list);
                if(list.size()>0)
                list_notification.setAdapter(adapter);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject object = new JSONObject();
            list.clear();
            db.getoffer();
            for (int i = 0; i < Const.offerlist.size(); i++) {
                list.add(new offerObject(Const.offerlist.get(i).getOffer_date(), Const.offerlist.get(i).getOffer_detail(), ""));
            }
            return null;
        }
    }
    public class ListViewAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        List<offerObject> appointList = null;
        String displayDate = "";
        public ListViewAdapter(Context context, List<offerObject> appointList) {
            super();
            this.context = context;
            this.appointList = appointList;
            inflater = LayoutInflater.from(NotificationActivity.this);
        }
        public class ViewHolder {
            TextView tv_index;
            TextView tv_title;
        }
        @Override
        public int getViewTypeCount() {
            int a;
            if (getCount() == 0) {
                a = 1;
            } else {
                a = getCount();
            }
            return a;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return appointList.size();
        }

        @Override
        public Object getItem(int position) {
            return appointList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.layout_raw_notification, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_index = (TextView) convertView.findViewById(R.id.tv_index);
                holder.tv_title.setText(appointList.get(position).getOffer_detail()+"\n"+appointList.get(position).getOffer_date());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.tv_title.setText(appointList.get(position).getOffer_detail()+"\n"+appointList.get(position).getOffer_date());
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            return convertView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MessageNotificationTab.tv_header != null) {
            MessageNotificationTab.tv_header.setText(mContext.getResources().getString(R.string.title_notification));
            MessageNotificationTab.img_newmessage.setImageResource(R.drawable.ic_delete_notification);
        }

        MessageNotificationTab.img_newmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list_notification.getCount()>0)
                AlertYesNO("", getResources().getString(R.string.alert_clear_notification), mContext);
            }
        });


    }

    public void showResponse(String response, String redirectionKey) {
        if (redirectionKey.equalsIgnoreCase("LogoutMessage")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    NotificationActivity.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("ScanAddress")) {
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

                // getStatusList(address, lat, lng, GPSStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}