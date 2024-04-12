package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.litao.android.lib.BaseGalleryActivity;
import com.litao.android.lib.Configuration;
import com.litao.android.lib.entity.PhotoEntry;

import java.util.ArrayList;
import java.util.List;

import general.TrackGPS;
import general.Utils;

public class GalleryActivity extends BaseGalleryActivity {
    List<PhotoEntry> mSelectedPhotos;
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static Activity activity;
    public static int Flag=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        activity=GalleryActivity.this;
        mContext = GalleryActivity.this;
        Init();
        getSupportActionBar().hide();
    }

    private void Init() {
        mSelectedPhotos=new ArrayList<>();
        attachFragment(R.id.gallery_root);
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);

        Bundle b=getIntent().getExtras();
        if(b!=null){
            Flag=b.getInt("Flag");
        }

        img_refresh.setImageResource(R.drawable.ic_back);
        tv_header.setText("Select Image");
        img_logout.setImageResource(R.drawable.ic_arrow_right);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
                Utils.isFromMultipleSelection=true;
                sendPhotos();
            }
        });
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Utils.isFromMultipleSelection=false;

            }
        });
    }

    @Override
    public Configuration getConfiguration() {
        //return your configuration
        Configuration cfg=new Configuration.Builder()
                .hasCamera(false)
                .hasShade(true)
                .hasPreview(true)
                .setSpaceSize(3)
                .setPhotoMaxWidth(120)

                .setCheckBoxColor(0xFF3F51B5)
                .setDialogHeight(Configuration.DIALOG_FULL)
                .setDialogMode(Configuration.DIALOG_GRID)
                .setMaximum(30)
                .setTip(null)
                .setAblumsTitle(null)
                .build();
        return cfg;
    }

    @Override
    public List<PhotoEntry> getSelectPhotos() {
        //return your selected photos
        return mSelectedPhotos;
    }

    @Override
    public void onSelectedCountChanged(int count) {
        //This method will be invoked when the selected photo count is changed.
        //count: selected photos count
    }

    @Override
    public void onAlbumChanged(String name) {
        //This method will be invoked when the albums name is changed.
        //name: current albums name
    }

    @Override
    public void onTakePhoto(PhotoEntry entry) {
        //This method will be invoked when you take the photo.
        //entry: take photo information just now

        Toast.makeText(mContext,entry.getPath(),Toast.LENGTH_SHORT).show();

        if(Flag==0) {
            PhotoUploadActivity.mySelectedPhotos = new ArrayList<>();
            PhotoUploadActivity.mySelectedPhotos.add(entry);
        }else{
            PhotoUploadNew.mySelectedPhotos = new ArrayList<>();
            PhotoUploadNew.mySelectedPhotos.add(entry);
        }
        activity.finish();
    }

    @Override
    public void onChoosePhotos(List<PhotoEntry> entries) {
        //This method will be invoked when you choose photos then call sendPhotos() method.
        //entries: selected photos information
        //Toast.makeText(mContext,""+entries.size(),Toast.LENGTH_SHORT).show();
        if(Flag==0) {
            PhotoUploadActivity.mySelectedPhotos = entries;
        }else{
            PhotoUploadNew.mySelectedPhotos = entries;
        }
        activity.finish();
      //  mContext.startActivity(new Intent(mContext,GalleryActivityUploading.class));

    }

    @Override
    public void onPhotoClick(PhotoEntry entry) {
        //This method will be invoked when you click photo
        //entry： clicked photo information
        mSelectedPhotos.add(entry);
    }

}
