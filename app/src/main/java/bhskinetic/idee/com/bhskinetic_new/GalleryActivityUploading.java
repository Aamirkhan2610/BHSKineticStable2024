package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.litao.android.lib.entity.PhotoEntry;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import Model.PhotoEntryUploading;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;
import okhttp3.internal.Util;

public class GalleryActivityUploading extends Activity {
    private TextView tv_header;
    private ImageView img_logout;
    private ImageView img_refresh;
    private Context mContext;
    private Activity activity;
    private int Flag = 0;
    private GridView gridDynamic;
    private GridElementAdapter adapter;
    private ArrayList<PhotoEntryUploading> mySelectedPhotos;
    private String imagename;
    private Bitmap photoBitmap;
    private String UploadingImageName = "";
    private boolean isDefault = true;
    private int uploadCounter=0;
    private TrackGPS gps;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout_uploading);
        activity = GalleryActivityUploading.this;
        mContext = GalleryActivityUploading.this;
        Init();
    }

    private void Init() {
        gps = new TrackGPS(mContext);
        tv_header = findViewById(R.id.tv_header);
        img_logout = findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);
        img_refresh = findViewById(R.id.img_refresh);
        gridDynamic = findViewById(R.id.grid_dynamicgrid);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Flag = b.getInt("Flag");
        }

        img_refresh.setImageResource(R.drawable.ic_back);

        img_refresh.setVisibility(View.GONE);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
               // Utils.isFromMultipleSelection = false;
            }
        });

        mySelectedPhotos=new ArrayList<>();
        for(int i=0;i<PhotoUploadActivity.mySelectedPhotos.size();i++){
            PhotoEntryUploading photoEntryUploading=new PhotoEntryUploading();
            photoEntryUploading.setPath(PhotoUploadActivity.mySelectedPhotos.get(i).getPath());
            mySelectedPhotos.add(photoEntryUploading);
        }
        adapter=new GridElementAdapter(this);
        gridDynamic.setAdapter(adapter);

        UpdateHeader();

            if (mySelectedPhotos.size() > 0) {
            Glide.with(mContext)
                    .load(mySelectedPhotos.get(0).getPath())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                            isDefault = false;
                            photoBitmap= resource;
                            new uploadphoto().execute();
                        }
                    });
        }

    }

    private void UpdateHeader() {
        tv_header.setText("Photo Upload["+mySelectedPhotos.size()+"]");
    }

    //GridView adapter calss
    public class GridElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public GridElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(GalleryActivityUploading.this);
        }


        @Override
        public int getCount() {
            return mySelectedPhotos.size();
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
                v = layoutInflater.inflate(R.layout.raw_uploadimage, null);
                h = new ViewHolder();
                h.img=v.findViewById(R.id.img);
                h.uploadPorgress=v.findViewById(R.id.upload_progress);
                h.imgTick=v.findViewById(R.id.img_tick);

                if(mySelectedPhotos.get(position).isChecked()){
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);
                }else{
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }

                final ViewHolder finalH = h;
                Glide.with(mContext).load(mySelectedPhotos.get(position).getPath()).into(finalH.img);
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                File imgFile = new File(mySelectedPhotos.get(position).getPath());
                final ViewHolder finalH = h;
                Glide.with(mContext).load(mySelectedPhotos.get(position).getPath()).into(finalH.img);
                if(mySelectedPhotos.get(position).isChecked()){
                    h.imgTick.setVisibility(View.VISIBLE);
                    h.uploadPorgress.setVisibility(View.GONE);
                }else{
                    h.uploadPorgress.setVisibility(View.VISIBLE);
                    h.imgTick.setVisibility(View.GONE);
                }
            }
            return v;
        }

        private class ViewHolder {
            private ImageView img,imgTick;
            private ProgressBar uploadPorgress;
        }
    }


    class uploadphoto extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uploadresponse = "";
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload.jsp");
                ByteArrayBody bab = new ByteArrayBody(data, imagename);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(Utils.getPref(getString(R.string.pref_IMEINumber),GalleryActivityUploading.this)));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(Utils.getPref(getString(R.string.pref_Client_ID),GalleryActivityUploading.this)));
                reqEntity.addPart("Str_Lat", new StringBody(""+gps.getLatitude()));
                reqEntity.addPart("Str_Long", new StringBody(""+gps.getLongitude()));
                reqEntity.addPart("Str_Loc", new StringBody("NA"));
                reqEntity.addPart("Str_GPS", new StringBody("NA"));
                reqEntity.addPart("Str_DriverID", new StringBody(Utils.getPref(getString(R.string.pref_driverID),GalleryActivityUploading.this)));
                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody(PhotoUploadActivity.Str_TripNo));
                reqEntity.addPart("Remarks", new StringBody(PhotoUploadActivity.REMARK));   //Edit text value
                reqEntity.addPart("Str_JobNo", new StringBody(PhotoUploadActivity.STR_REV));
                reqEntity.addPart("Rev_Name", new StringBody(PhotoUploadActivity.Str_JobNo));
                reqEntity.addPart("Str_Nric", new StringBody("NA"));  //From Dropdownlist
                reqEntity.addPart("Str_Sts", new StringBody(PhotoUploadActivity.Str_Sts));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(imagename));
                reqEntity.addPart("fType", new StringBody("Photo"));
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                uploadresponse = "" + s;
            } catch (Exception e) {
                // handle exception here
                Log.e(e.getClass().getName(), e.getMessage());
            }

            return uploadresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("UPLOAD RESPONSE.....", result);
            //  pDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {

                    mySelectedPhotos.get(uploadCounter).setChecked(true);
                    adapter.notifyDataSetChanged();
                    uploadCounter++;
                    if (mySelectedPhotos.size() > 0) {
                        Glide.with(mContext)
                                .load(mySelectedPhotos.get(0).getPath())
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
                                        isDefault = false;
                                        photoBitmap= resource;
                                        new uploadphoto().execute();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.str_photo_error), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to intrupt upload operation and exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
