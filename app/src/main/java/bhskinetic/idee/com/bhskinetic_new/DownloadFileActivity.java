package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import general.TrackGPS;

/**
 * Created by Aamir on 4/19/2017.
 */

public class DownloadFileActivity extends Activity {
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ListView list_notification;
    public static ListViewAdapter adapter;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_notification);
        Init();
    }

    private void Init() {
        mContext = DownloadFileActivity.this;
        gps = new TrackGPS(mContext);
        activity = DownloadFileActivity.this;
        list_notification=(ListView)findViewById(R.id.list_notification);
        adapter=new ListViewAdapter(mContext);


        if(UploadDownloadFiles.img_gallery!=null){
            UploadDownloadFiles.img_gallery.setVisibility(View.GONE);
        }

        if(UploadDownloadFiles.img_logout!=null){
            UploadDownloadFiles.img_logout.setVisibility(View.GONE);
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;

        String displayDate = "";
        public ListViewAdapter(Context context) {
            super();
            this.context = context;

            inflater = LayoutInflater.from(DownloadFileActivity.this);
        }
        public class ViewHolder {
            TextView tv_title;
            TextView tv_sent_by;
            TextView tv_date;
            TextView tv_status;
            ImageView img_download;
            ImageView img_upload;
            ImageView img_view_image;
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
            return UploadDownloadFiles.ArrayAttachment.size();
        }

        @Override
        public Object getItem(int position) {
            return UploadDownloadFiles.ArrayAttachment.get(position);
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
                convertView = inflater.inflate(R.layout.layout_raw_download_list, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_sent_by = (TextView) convertView.findViewById(R.id.tv_sent_by);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.img_download=(ImageView)convertView.findViewById(R.id.img_download);
                holder.img_upload=(ImageView)convertView.findViewById(R.id.img_upload);
                holder.img_view_image=(ImageView)convertView.findViewById(R.id.img_view_image);

                holder.img_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertYesNO("", getResources().getString(R.string.alert_save_image),v.getContext(),UploadDownloadFiles.ArrayAttachment.get(position).getStr_URL());
                    }
                });

                holder.img_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadDownloadFiles.Sent_By=UploadDownloadFiles.ArrayAttachment.get(position).getSent_By();
                        UploadDownloadFiles.Str_Sts=UploadDownloadFiles.ArrayAttachment.get(position).getStr_Status();
                        UploadDownloadFiles.ImageID=UploadDownloadFiles.ArrayAttachment.get(position).getImgID();
                        UploadDownloadFiles.tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(true);
                        UploadDownloadFiles.tabHost.setCurrentTab(1);
                    }
                });

                holder.img_view_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DownloadFileActivity.this, ImageViewActivitiy.class);
                        intent.putExtra("DocumentURL", UploadDownloadFiles.ArrayAttachment.get(position).getStr_URL());
                        intent.putExtra("DocumentTitle",UploadDownloadFiles.ArrayAttachment.get(position).getFileNameWithoutIndexing());
                        intent.putExtra("IsFromDownload",true);

                        startActivity(intent);
                    }
                });

                holder.tv_title.setText(UploadDownloadFiles.ArrayAttachment.get(position).getFileName());
                holder.tv_sent_by.setText(UploadDownloadFiles.ArrayAttachment.get(position).getSent_By());
                holder.tv_date.setText(UploadDownloadFiles.ArrayAttachment.get(position).getSent_Date());
                holder.tv_status.setText(UploadDownloadFiles.ArrayAttachment.get(position).getStr_Status());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.tv_title.setText(UploadDownloadFiles.ArrayAttachment.get(position).getFileName());
                holder.tv_sent_by.setText(UploadDownloadFiles.ArrayAttachment.get(position).getSent_By());
                holder.tv_date.setText(UploadDownloadFiles.ArrayAttachment.get(position).getSent_Date());
                holder.tv_status.setText(UploadDownloadFiles.ArrayAttachment.get(position).getStr_Status());

                holder.img_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertYesNO("", getResources().getString(R.string.alert_save_image),v.getContext(),UploadDownloadFiles.ArrayAttachment.get(position).getStr_URL());
                    }
                });

                holder.img_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadDownloadFiles.Sent_By=UploadDownloadFiles.ArrayAttachment.get(position).getSent_By();
                        UploadDownloadFiles.Str_Sts=UploadDownloadFiles.ArrayAttachment.get(position).getStr_Status();
                        UploadDownloadFiles.tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(true);
                        UploadDownloadFiles.ImageID=UploadDownloadFiles.ArrayAttachment.get(position).getImgID();
                        UploadDownloadFiles.tabHost.setCurrentTab(1);
                    }
                });

                holder.img_view_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DownloadFileActivity.this, ImageViewActivitiy.class);
                        intent.putExtra("DocumentURL", UploadDownloadFiles.ArrayAttachment.get(position).getStr_URL());
                        intent.putExtra("DocumentTitle",UploadDownloadFiles.ArrayAttachment.get(position).getFileNameWithoutIndexing());
                        intent.putExtra("IsFromDownload",true);

                        startActivity(intent);
                    }
                });

            }

            return convertView;
        }

        public void AlertYesNO(String alertTitle, String alertMessage, final Context mContext, final String DocumentURL) {
            final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
            builderInner.setMessage(alertMessage);
            builderInner.setTitle(alertTitle);
            builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadFile(DocumentURL);

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

        public void downloadFile(String uRl) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/BHS_KINETIC");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/BHS_KINETIC", "BHS"+System.currentTimeMillis()+".jpg");

            mgr.enqueue(request);

            Toast.makeText(mContext,"PHOTO DOWNLOADED ON GALLERY",Toast.LENGTH_SHORT).show();
        }

    }



}