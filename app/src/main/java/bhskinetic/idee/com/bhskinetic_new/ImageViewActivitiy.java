package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import bhskinetic.idee.com.bhskinetic_new.Utils.TouchImageView;


/**
 * Created by Aamir on 4/18/2017.
 */

public class ImageViewActivitiy extends Activity {
    private TextView tv_header;
    private ImageView img_logout;
    private ImageView img_refresh;
    public static ProgressDialog pDialog;
    String DocumentURL = "";
    String title="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_imageview);
        Init();
    }

    private void Init() {
        final TouchImageView imageView =findViewById(R.id.img_document);
        Bundle b=getIntent().getExtras();

        if(b!=null){
            DocumentURL=b.getString("DocumentURL");
            title=b.getString("DocumentTitle");
        }

        tv_header=(TextView)findViewById(R.id.tv_header);
        img_logout=(ImageView)findViewById(R.id.img_logout);
        img_refresh=(ImageView)findViewById(R.id.img_refresh);
        img_refresh.setVisibility(View.GONE);
        img_logout.setImageResource(R.drawable.ic_download);
        pDialog = new ProgressDialog(ImageViewActivitiy.this,R.style.ProgressDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.str_progress_loading));
        tv_header.setText(title);

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_save_image), ImageViewActivitiy.this);
            }
        });

        final String finalDocumentURL = DocumentURL;
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    //Your code goes here
//                    try {
//                        URL url = new URL(finalDocumentURL);
//                        Bitmap bmp = null;
//                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                        imageView.setImageBitmap(bmp);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();

        new AsyncTask<Void,Bitmap,Bitmap>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.show();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                // perform your network operation
                // get json or xml string from server
                //store in a local variable (say response) and return

                try {
                    URL url = new URL(finalDocumentURL);
                    Bitmap bmp = null;
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    return bmp;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Bitmap bmp){
                // response returned by doInBackGround() will be received
                // by onPostExecute(String results)
                // Now manipulate your jason/xml String(results)
                imageView.setImageBitmap(bmp);
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }

        }.execute();

        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setVisibility(View.VISIBLE);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
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

        Toast.makeText(this,"PHOTO DOWNLOADED\nOpen File Manager->BHS_KINETIC",Toast.LENGTH_SHORT).show();

    }


}
