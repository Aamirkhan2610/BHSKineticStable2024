package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.litao.android.lib.entity.PhotoEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.ModelLeaveHistory;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/18/2017.
 */
public class JobDetailWithPhoto extends FragmentActivity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ListView lv_request_status;
    public static ModelLeaveHistory modelleaveHistory;
    public static ArrayList<ModelLeaveHistory> arrayLeaveHistory;
    public static ListElementAdapter adapter;
    public static String requestType = "";
    public static String JobNo = "";
    public static List<PhotoEntry> mySelectedPhotos;
    public static Gallery selectedImageGallery;
    GalleryImageAdapter galleryImageAdapter;
    public static String Str_JobStatusToCheck="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_jobdetail_with_photo);
        activity = JobDetailWithPhoto.this;
        Init();
    }

    private void Init() {
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        selectedImageGallery = (Gallery) findViewById(R.id.selected_image_gallery);
        selectedImageGallery.setSpacing(50);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        mContext = JobDetailWithPhoto.this;
        gps = new TrackGPS(mContext);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            tv_header.setText(b.getString("title"));
            JobNo = b.getString("JobNo");
            Str_JobStatusToCheck=b.getString("Str_JobStatusToCheck");
        }


        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });


        lv_request_status = (ListView) findViewById(R.id.lv_request_status);
        arrayLeaveHistory = new ArrayList<>();
        getLeaveDetail("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "OFF");


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
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutDetailPhoto");
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

        if (redirectionKey.equalsIgnoreCase("logoutDetailPhoto")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    JobDetailWithPhoto.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("JobDetailWithPhotoDetail")) {
            try {

                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("recived").equalsIgnoreCase("1")) {
                    arrayLeaveHistory.clear();
                    mySelectedPhotos = new ArrayList<>();
                    mySelectedPhotos.clear();
                    ModelLeaveHistory modelLeaveType;
                    JSONArray list = jobj.optJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.optJSONObject(i);
                        modelLeaveType = new ModelLeaveHistory();
                        modelLeaveType.setReq_Name(jsonObject.optString("ActionName"));
                        modelLeaveType.setReq_Status(jsonObject.optString("ActionType"));
                        if (jsonObject.optString("ActionType").equalsIgnoreCase("Photo")) {
                            mySelectedPhotos.add(new PhotoEntry(1, 1, System.currentTimeMillis(), jsonObject.optString("ActionName")));
                        } else {
                            arrayLeaveHistory.add(modelLeaveType);
                        }

                    }
                    adapter = new ListElementAdapter(mContext);
                    lv_request_status.setAdapter(adapter);

                    galleryImageAdapter = new GalleryImageAdapter(mContext);
                    if (mySelectedPhotos.size() > 0) {
                        selectedImageGallery.setAdapter(galleryImageAdapter);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
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
                v = layoutInflater.inflate(R.layout.raw_leave_log, null);
                h = new ViewHolder();
                h.tv_leavetitle = (TextView) v.findViewById(R.id.tv_leavetitle);
                h.layout_right = (LinearLayout) v.findViewById(R.id.layout_right);
                h.tv_leavedetail = (TextView) v.findViewById(R.id.tv_leavedetail);
                h.img_status = (ImageView) v.findViewById(R.id.img_status);
                h.tv_index = (TextView) v.findViewById(R.id.tv_index);
                h.tv_cancel = (TextView) v.findViewById(R.id.tv_cancel);
                h.tv_index.setText((position + 1) + ".");
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());
                h.layout_right.setVisibility(View.GONE);
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
                    if (isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    } else {
                        h.tv_cancel.setVisibility(View.GONE);
                    }

                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                    h.tv_cancel.setVisibility(View.GONE);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                    h.tv_cancel.setVisibility(View.GONE);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                    h.tv_cancel.setVisibility(View.VISIBLE);
                }


                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Maintenances", "Cancelled");
                        } else {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Leave", "Cancelled");

                        }
                    }
                });

                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }

                h.tv_cancel.setVisibility(View.GONE);

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_leavetitle.setText(arrayLeaveHistory.get(position).getReq_Name());
                h.tv_index.setText((position + 1) + ".");
                h.tv_leavedetail.setText(arrayLeaveHistory.get(position).getReq_Display());
                h.layout_right.setVisibility(View.GONE);

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
                    if (isCancel) {
                        h.tv_cancel.setVisibility(View.VISIBLE);
                    } else {
                        h.tv_cancel.setVisibility(View.GONE);
                    }
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Rejected")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_reject);
                    h.tv_cancel.setVisibility(View.GONE);
                } else if (arrayLeaveHistory.get(position).getReq_Status().equalsIgnoreCase("Cancelled")) {
                    h.img_status.setImageResource(R.drawable.ic_leave_cancel);
                    h.tv_cancel.setVisibility(View.GONE);
                } else {
                    h.img_status.setImageResource(R.drawable.ic_leave_pending);
                    h.tv_cancel.setVisibility(View.VISIBLE);
                }

                h.tv_cancel.setVisibility(View.GONE);

                h.tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestType.equalsIgnoreCase("Maintenances")) {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Maintenances", "Cancelled");
                        } else {
                            ConfirmCancelSubmition(arrayLeaveHistory.get(position).getListID(), "Leave", "Cancelled");
                        }
                    }
                });

                if (arrayLeaveHistory.get(position).getVisibleDetailItem() == position) {
                    h.tv_leavedetail.setVisibility(View.VISIBLE);
                } else {
                    h.tv_leavedetail.setVisibility(View.GONE);
                }
            }
            return v;
        }

        private class ViewHolder {
            private TextView tv_leavetitle;
            private TextView tv_index;
            private TextView tv_leavedetail;
            private TextView tv_cancel;
            private ImageView img_status;
            private LinearLayout layout_right;
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
                        submitCancelRequest("NA", "" + gps.getLatitude(), "" + gps.getLongitude(), "ON", vseqNumber, miscType, miscStatus);
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

    public static void submitCancelRequest(String address, String lat, String lng, String gpsStatus, String seqNumber, String miscType, String miscStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String URL = "Status_Update.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_SeqNo=" + seqNumber + "&Str_Misc_Type=" + miscType + "&Str_Misc_Status=" + miscStatus + "&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_worksite),mContext);
        if (URL.contains(" ")) {
            URL = URL.replaceAll(" ", "%20");
        }
        APIUtils.sendRequest(mContext, "Leave Cancel", URL, "LeaveCancel");
    }

    private void getLeaveDetail(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "Order List", "Order_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + DriverID + "&Str_JobView=" + "Gallery" + "&Str_JobStatus=" + Str_JobStatusToCheck + "&Str_TripNo=NA&Str_JobNo=" + JobNo+"&Str_JobFor="+Utils.getPref(mContext.getResources().getString(R.string.pref_worksite),mContext), "JobDetailWithPhotoDetail");
    }

    public static class GalleryImageAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;

        public GalleryImageAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mySelectedPhotos.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // Override this method according to your need
        public View getView(final int index, View view, ViewGroup viewGroup) {
            // TODO Auto-generated method stub


            View v = view;
            GalleryImageAdapter.ViewHolder h = null;

            if (v == null) {
                v = layoutInflater.inflate(R.layout.layout_gallery_image, null);
                h = new GalleryImageAdapter.ViewHolder();
                h.imgSelectedImage = (ImageView) v.findViewById(R.id.img_job_photo);
                h.imgDelImage = (ImageView) v.findViewById(R.id.img_delete_image);

                Glide.with(mContext)
                        .load(mySelectedPhotos.get(index).getPath())
                        .error(R.drawable.ic_driver)
                        .override(250, 250)
                        .into(h.imgSelectedImage);


                h.imgDelImage.setImageResource(R.drawable.ic_expand);
                h.imgSelectedImage.setScaleType(ImageView.ScaleType.FIT_XY);
                h.imgDelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(mySelectedPhotos.get(index).getPath());
                    }
                });
                v.setTag(h);
            } else {
                h = (GalleryImageAdapter.ViewHolder) v.getTag();
                Glide.with(mContext)
                        .load(mySelectedPhotos.get(index).getPath()) // image url
                        .error(R.drawable.ic_driver)  // any image in case of error
                        .override(250, 250)
                        .into(h.imgSelectedImage);

                h.imgDelImage.setImageResource(R.drawable.ic_expand);
                h.imgSelectedImage.setScaleType(ImageView.ScaleType.FIT_XY);

                h.imgDelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(mySelectedPhotos.get(index).getPath());
                    }
                });
            }

            return v;
        }

        private class ViewHolder {
            private ImageView imgSelectedImage, imgDelImage;
        }
    }

    public static void showImage(final String imagePath) {
        Dialog imageDialogue = new Dialog(activity, R.style.WideDialog);
        imageDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialogue.setCancelable(true);

        imageDialogue.setContentView(R.layout.custom_dialogue_jobdetail_photo);
        ImageView imgPhoto=(ImageView)imageDialogue.findViewById(R.id.img_job_photo);
        Button btn_download=(Button) imageDialogue.findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(imagePath);
            }
        });
        Glide.with(mContext)
                .load(imagePath) // image url
                .error(R.drawable.ic_access_denied)  // any image in case of error
                //.override(200, 200)
                .into(imgPhoto);

        imageDialogue.show();
    }
    public static void downloadFile(String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/BHS_KINETIC");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("BHS Kinetic")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/BHS_KINETIC", "BHS"+System.currentTimeMillis()+".jpg");


        mgr.enqueue(request);

           Toast.makeText(mContext,"PHOTO DOWNLOADED ON GALLERY",Toast.LENGTH_SHORT).show();

    }


}
