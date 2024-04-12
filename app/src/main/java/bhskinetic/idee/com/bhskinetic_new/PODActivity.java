package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import com.joanzapata.pdfview.PDFView;
import com.kyanogen.signatureview.SignatureView;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import general.APIUtils;
import general.TrackGPS;
import general.Utils;


/**
 * Created by Aamir on 4/18/2017.
 */

public class PODActivity extends Activity implements View.OnTouchListener{
    private TextView tv_header;
    private ImageView img_logout;
    private ImageView img_refresh;
    private ImageView img_gallery;
    private ImageView img_signatureapproved;
    private ImageView img_document;
    private ImageView img_signature;
    private ImageView img_newmessage;
    private TrackGPS gps;
    private FrameLayout layout_frame;
    private String Sign_Status="0";
    private String Stamp_Status="0";
    private String SignPad_Status="0";
    private String Job_SeqNo="0";
    private String Sign_URL="NA";
    private ImageView img_stamp;
    private  ProgressDialog pDialog;
    private File pdfFile;
    private ImageView img_web_sign;
    private  String imagename;
    private RelativeLayout header;
    private String isGoogleDrive="0";
    private SignatureView signatureView;
    private FrameLayout frame_signature;
    private Bitmap bm1;
    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
// we can be in one of these 3 states

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

// remember some things for zooming

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private String DriverID = "";
    private String ClientID = "";
    private String LATITUDE = "";
    private String LONGITUDE = "";
    private String GPS_STATUS = "";
    private String ADDRESS = "";
    private String REMARK = "";
    private String IMEINUMBER = "";

    private String DocumentURL = "";
    private String Pod_SeqNo = "";
    private String Pod_SeqNoGlobal = "";
    private String DocumentTitle = "";
    private String title="";
    private String Stamp="";
    private String File_Type="";
    private String Pod_Upload_Status="";
    private PDFView pdfView;

    private String[] _DocumentURL,_title,_Stamp,_Job_SeqNo,_Sign_Status,_Sign_URL,_Stamp_Status,_SignPad_Status,_File_Type,_Pod_Upload_Status,_DocumentTitle,_Pod_SeqNo;
    private int counter=0,totallength=0;
    private Button btn_next;
    private int pod_direct=-1;
    private int pod_direct_selection=-1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pod);
        Init();
    }

    private void Init() {
        //PDFViewPager pdfViewPager=findViewById(R.id.pdfViewPager);
         pdfView=findViewById(R.id.ViewPdf);
        pdfView.enableSwipe(false);
        signatureView=findViewById(R.id.signature_view);
        frame_signature=findViewById(R.id.frame_signature);
        img_signatureapproved =findViewById(R.id.img_sign_done);
        img_document=findViewById(R.id.img_document);
        btn_next=findViewById(R.id.btn_next);

        img_signature=findViewById(R.id.img_signature);
        img_stamp=findViewById(R.id.img_stamp);
        bm1=null;
        gps=new TrackGPS(this);
        img_web_sign=findViewById(R.id.img_web_sign);
        Bundle b=getIntent().getExtras();
        counter=0;
        if(b!=null){
            _DocumentURL =b.getString("DocumentURL").split("##");
            _Pod_SeqNo=b.getString("Pod_SeqNo").split("_");
            Pod_SeqNoGlobal=b.getString("Pod_SeqNo");
            _DocumentTitle =b.getString("DocumentTitle").split("##");
            _title=b.getString("DocumentTitle").split("##");
            _Stamp=b.getString("Stamp").split("##");
            _Job_SeqNo=b.getString("Job_SeqNo").split("##");
            _Sign_Status=b.getString("Sign_Status").split("##");
            _Sign_URL=b.getString("Sign_URL").split("##");
            _Stamp_Status=b.getString("Stamp_Status").split("##");
            _SignPad_Status=b.getString("SignPad_Status").split("##");
            _File_Type=b.getString("File_Type").split("##");
            _Pod_Upload_Status=b.getString("Pod_Upload_Status").split("##");
            pod_direct=b.getInt("POD_DIRECT");
            totallength=_DocumentURL.length;
        }

        tv_header=(TextView)findViewById(R.id.tv_header);
        img_gallery=findViewById(R.id.img_gallery);
        layout_frame=findViewById(R.id.layout_frame);
        img_newmessage=findViewById(R.id.img_newmessage);
        header=findViewById(R.id.header);
        img_refresh=(ImageView)findViewById(R.id.img_refresh);
        img_logout=(ImageView)findViewById(R.id.img_logout);
        img_logout.setImageResource(R.drawable.ic_signature_web);
        img_newmessage.setImageResource(R.drawable.ic_pod);
        img_gallery.setImageResource(R.drawable.ic_signature);
        img_gallery.setVisibility(View.VISIBLE);
        img_newmessage.setVisibility(View.VISIBLE);

        tv_header.setText("");

        InitUI();
    }

    private void InitUI() {
        img_signature.setVisibility(View.GONE);
        img_stamp.setVisibility(View.GONE);
        img_web_sign.setVisibility(View.GONE);

        bm1=null;
        DocumentURL=_DocumentURL[counter];
        //DocumentURL="http://119.81.238.250:86/DocumentImages/5013308687.jpg";
        title=_title[counter];
        Stamp=_Stamp[counter];
        DocumentTitle=_DocumentTitle[counter];
        Job_SeqNo =_Job_SeqNo[counter];
        Pod_SeqNo=_Pod_SeqNo[counter];
        Sign_Status=_Sign_Status[counter];
        Sign_URL=_Sign_URL[counter];
        Stamp_Status=_Stamp_Status[counter];
        SignPad_Status =_SignPad_Status[counter];
        File_Type=_File_Type[counter];
        Pod_Upload_Status=_Pod_Upload_Status[counter];

        if(Pod_Upload_Status.equalsIgnoreCase("1")){
            Sign_Status="0";
            Stamp_Status="0";
            SignPad_Status="0";
            img_refresh.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }else{
            img_refresh.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                if(counter==totallength){
                    Toast.makeText(PODActivity.this, "All Documents Uploaded...",Toast.LENGTH_SHORT).show();
                  //  finish();
                    Intent i = new Intent(PODActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else{
                    InitUI();
                }
            }
        });


        if(Stamp_Status.equalsIgnoreCase("1")){
            Glide.with(this).load(Stamp).into(img_stamp);
            img_newmessage.setVisibility(View.VISIBLE);
        }else{
            img_newmessage.setVisibility(View.INVISIBLE);
        }

        if(SignPad_Status.equalsIgnoreCase("1")){
            img_gallery.setVisibility(View.VISIBLE);
        }else{
            img_gallery.setVisibility(View.INVISIBLE);
        }


        if(Sign_Status.equalsIgnoreCase("1")){
            Glide.with(this).load(Sign_URL).into(img_web_sign);
            img_logout.setVisibility(View.VISIBLE);
        }else{
            img_logout.setVisibility(View.INVISIBLE);
        }


        if(File_Type.equalsIgnoreCase("File")){
            isGoogleDrive="1";
            img_document.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
        }else{
            img_document.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);
            Glide.with(this).load(DocumentURL).into(img_document);
            Toast.makeText(PODActivity.this," ( "+(counter+1)+"/"+totallength+" ) " +"Document Loaded...",Toast.LENGTH_SHORT).show();

        }

        img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
                frame_signature.setVisibility(View.VISIBLE);
                img_signature.setVisibility(View.GONE);
                img_stamp.setOnTouchListener(null);
                img_web_sign.setOnTouchListener(null);

            }
        });

        img_newmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isPadSign=false,isWebSign=false;
                if (img_signature.getVisibility() == View.VISIBLE) {
                    isPadSign=true;
                }

                if (img_web_sign.getVisibility() == View.VISIBLE) {
                    isWebSign=true;
                }

                if(!(isPadSign||isWebSign)){
                    if(SignPad_Status.equalsIgnoreCase("1")||Sign_Status.equalsIgnoreCase("1")) {
                        Toast.makeText(PODActivity.this, "Please add siganture", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                img_web_sign.setOnTouchListener(null);
                img_stamp.setOnTouchListener(PODActivity.this);
                img_stamp.setVisibility(View.VISIBLE);
            }
        });

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Sign_Status.equalsIgnoreCase("1")) {
                   /* if (img_stamp.getVisibility() == View.GONE) {
                        if(Stamp_Status.equalsIgnoreCase("1")) {
                            Toast.makeText(PODActivity.this, "Please add stamp first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }*/

                    img_web_sign.setOnTouchListener(PODActivity.this);
                    img_web_sign.setVisibility(View.VISIBLE);
                }
            }
        });

        img_signatureapproved.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                frame_signature.setVisibility(View.GONE);
                Bitmap bitmap=signatureView.getSignatureBitmap();



                img_signature.setImageBitmap(bitmap);
                img_signature.setVisibility(View.VISIBLE);
                Toast.makeText(PODActivity.this,"Siganture Captured",Toast.LENGTH_SHORT).show();
            }
        });


        img_refresh.setImageResource(R.drawable.ic_upload);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isPadSign=false,isWebSign=false;
                if (img_signature.getVisibility() == View.VISIBLE) {
                    isPadSign=true;
                }

                if (img_web_sign.getVisibility() == View.VISIBLE) {
                    isWebSign=true;
                }

                if(!(isPadSign||isWebSign)){
                    if(SignPad_Status.equalsIgnoreCase("1")||Sign_Status.equalsIgnoreCase("1")) {
                        Toast.makeText(PODActivity.this, "Please add siganture", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(Stamp_Status.equalsIgnoreCase("1")) {
                    if (img_stamp.getVisibility() == View.GONE) {
                        Toast.makeText(PODActivity.this, "Please add stamp", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //header.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        pDialog = new ProgressDialog(PODActivity.this);
                        pDialog.setMessage(getResources().getString(R.string.str_progress_loading));
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();

                        saveFileToSDCard(layout_frame);


                    }
                }, 1000);
            }
        });


        if(File_Type.equalsIgnoreCase("File")) {
            pDialog = new ProgressDialog(PODActivity.this);
            pDialog.setMessage(getResources().getString(R.string.str_progress_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            new DownloadFile().execute(DocumentURL, "BHS_TEMP.pdf");
        }

        img_signature.setOnTouchListener(this);
        img_stamp.setOnTouchListener(this);
        img_web_sign.setOnTouchListener(this);
    }

    // Convert transparentColor to be transparent in a Bitmap.



    private void saveFileToSDCard(View layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache(true);
        final Bitmap bmp = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);
        Canvas c = new Canvas(bmp);


        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        try {

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            c.drawBitmap(bmp, 0, 0, null);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Bitmap mybitmap= BitmapFactory.decodeFile(f.getPath());;
            out.flush();
            out.close();


            bm1=mybitmap;
            f.delete();
            imagename = System.currentTimeMillis() + "_" + Job_SeqNo + ".png";


            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            pod_direct_selection=1;
                            new uploadDocument().execute();
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            pod_direct_selection=0;
                            new uploadDocument().execute();
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(PODActivity.this);

            if(pod_direct==1) {
                builder.setMessage("Confirm to Apply to All?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }else{
                pod_direct_selection=-1;
                new uploadDocument().execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class DownloadFile extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "BHS Kinetic");
            folder.mkdir();
            pdfFile= new File(folder, fileName);

            if(pdfFile.isDirectory()){
                pdfFile.delete();
            }

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            pdfView.fromFile(pdfFile).load();
            Toast.makeText(PODActivity.this," ( "+(counter+1)+"/"+totallength+" ) " +"Document Loaded...",Toast.LENGTH_SHORT).show();

            Log.d("Download complete", "----------");
        }
    }
    public class uploadDocument extends AsyncTask<String, String, String> {
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
                byte[] data=null;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm1.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    data = bos.toByteArray();


                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost postRequest = new HttpPost("http://119.81.238.250:20/api/bhskapi/UploadFiles");
                HttpPost postRequest = new HttpPost(APIUtils.BaseUrl + "upload_Pod.jsp");
                ByteArrayBody bab = new ByteArrayBody(data, imagename);
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                DriverID = Utils.getPref(getResources().getString(R.string.pref_driverID), PODActivity.this);

                ClientID = Utils.getPref(getResources().getString(R.string.pref_Driver_Name), PODActivity.this);
                LATITUDE = "" + gps.getLatitude();
                LONGITUDE = "" + gps.getLongitude();
                IMEINUMBER = Utils.getPref(getResources().getString(R.string.pref_IMEINumber), PODActivity.this);
                GPS_STATUS = "NA";
                ADDRESS = "NA";


                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("Str_iMeiNo", new StringBody(IMEINUMBER));
                reqEntity.addPart("Str_Model", new StringBody("Android"));
                reqEntity.addPart("Str_ID", new StringBody(ClientID));
                reqEntity.addPart("Str_Lat", new StringBody(LATITUDE));
                reqEntity.addPart("Str_Long", new StringBody(LONGITUDE));
                reqEntity.addPart("Str_Loc", new StringBody(ADDRESS));
                reqEntity.addPart("Str_GPS", new StringBody(GPS_STATUS));
                reqEntity.addPart("Str_DriverID", new StringBody(DriverID));
                if (bm1.getWidth() > bm1.getHeight()) {
                    //meaning the image is landscape view
                    Log.i("Str_PhotoSize", "L");
                    reqEntity.addPart("Str_PhotoSize", new StringBody("L"));
                    //  Toast.makeText(mContext,"LANDSCAPE",Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Str_PhotoSize", "P");
                    reqEntity.addPart("Str_PhotoSize", new StringBody("P"));
                    // Toast.makeText(mContext,"PORTRAIT",Toast.LENGTH_SHORT).show();
                }


                reqEntity.addPart("Str_JobView", new StringBody("Update"));
                reqEntity.addPart("Str_TripNo", new StringBody(Job_SeqNo));
                reqEntity.addPart("Str_JobNo", new StringBody(Pod_SeqNo));

                if(pod_direct==1){
                    reqEntity.addPart("Str_Pod_SeqNo", new StringBody(Pod_SeqNoGlobal));
                }else{
                    reqEntity.addPart("Str_Pod_SeqNo", new StringBody(""+Pod_SeqNo));
                }


                reqEntity.addPart("Remarks", new StringBody(REMARK));   //Edit text value
                reqEntity.addPart("Str_Sts", new StringBody("POD"));
                reqEntity.addPart("Str_JobExe", new StringBody("NA"));
                reqEntity.addPart("Filename", new StringBody(imagename));
                if(Pod_Upload_Status.equalsIgnoreCase("1")){
                    reqEntity.addPart("Str_DisplayName", new StringBody(DocumentTitle));
                }else{
                    reqEntity.addPart("Str_DisplayName", new StringBody(DocumentTitle));
                }

                reqEntity.addPart("Str_ButtonStatus", new StringBody(""+ pod_direct_selection));


                reqEntity.addPart("fType", new StringBody("Photo"));
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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


            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(result);

                if(pod_direct!=1){
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    counter++;
                    if(counter==totallength){
                        Toast.makeText(PODActivity.this, "All Documents Uploaded...",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(PODActivity.this," ( "+counter+"/"+totallength+" ) " +"Document Uploaded...",Toast.LENGTH_SHORT).show();
                        InitUI();
                    }
                    //
                } else {
                    Toast.makeText(PODActivity.this,"Error Uploading Document...",Toast.LENGTH_SHORT).show();

                }
                }else{

                    if(pod_direct_selection==1){
                        Intent i = new Intent(PODActivity.this, DashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                     }else{
                        RedirectToPODList();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }

    private void RedirectToPODList() {
        Intent intent = new Intent(PODActivity.this, PhotoUploadActivity.class);
        intent.putExtra("title", "");
        intent.putExtra("isSign", false);
        intent.putExtra("isDirectPOD", "1");
        intent.putExtra("Str_Sts", "Snap");
        intent.putExtra("Str_JobNo",Job_SeqNo);
        intent.putExtra("Str_Event", "Snap");
        intent.putExtra("snap", "true");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

// handle touch events here
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                if (oldDist > 50f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }

                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {

                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);

                } else if (mode == ZOOM) {
                    float newDist = spacing(event);

                    if (newDist > 50f) {

                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);

                    }

                    if (lastEvent != null && event.getPointerCount() == 3) {

                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);

                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }
    /**

     * Determine the space between the first two fingers

     */

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (x * x + y * y);

    }

    /**

     * Calculate the mid point of the first two fingers

     */

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    /**

     * Calculate the degree to be rotated by.

     * @param event

     * @return Degrees

     */

    private float rotation(MotionEvent event) {

        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);

    }


}
