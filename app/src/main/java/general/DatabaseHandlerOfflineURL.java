package general;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.litao.android.lib.entity.PhotoEntry;

import java.util.ArrayList;
import java.util.List;

import Model.OfflineImageModel;
import bhskinetic.idee.com.bhskinetic_new.R;

/**
 * Created by Aamir on 28/5/2018.
 */

public class DatabaseHandlerOfflineURL extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BHSDb";
    private static final String TBL_URL = "TblURL";
    private static final String TBL_Image = "TblImg";
    private static final String URL = "webUrl";
    private static final String IMG_URI = "img_uri";
    private static final String Str_iMeiNo = "Str_iMeiNo";
    private static final String Str_Model = "Str_Model";
    private static final String Str_ID = "Str_ID";
    private static final String Str_Lat = "Str_Lat";
    private static final String Str_Long = "Str_Long";
    private static final String Str_Loc = "Str_Loc";
    private static final String Str_GPS = "Str_GPS";
    private static final String Str_DriverID = "Str_DriverID";
    private static final String Str_JobView = "Str_JobView";
    private static final String Str_TripNo = "Str_TripNo";
    private static final String Remarks = "Remarks";
    private static final String Str_JobNo = "Str_JobNo";
    private static final String Rev_Name = "Rev_Name";
    private static final String Str_Nric = "Str_Nric";
    private static final String Str_Sts = "Str_Sts";
    private static final String Str_JobExe = "Str_JobExe";
    private static final String Filename = "Filename";
    private static final String fType = "fType";
    private Context mContext;

    public DatabaseHandlerOfflineURL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TBL_URL + "("
                + URL + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TBL_Image + "("
                + IMG_URI + " TEXT,"
                + Str_iMeiNo + " TEXT,"
                + Str_Model + " TEXT,"
                + Str_ID + " TEXT,"
                + Str_Lat + " TEXT,"
                + Str_Long + " TEXT,"
                + Str_Loc + " TEXT,"
                + Str_GPS + " TEXT,"
                + Str_DriverID + " TEXT,"
                + Str_JobView + " TEXT,"
                + Str_TripNo + " TEXT,"
                + Remarks + " TEXT,"
                + Str_JobNo + " TEXT,"
                + Rev_Name + " TEXT,"
                + Str_Nric + " TEXT,"
                + Str_Sts + " TEXT,"
                + Str_JobExe + " TEXT,"
                + Filename + " TEXT,"
                + fType + " TEXT" + ")";
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_URL);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_Image);
        onCreate(db);
    }

    public void updateURL(String storedURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(URL, storedURL);
        db.insert(TBL_URL, null, values);
        db.close();
    }
    public boolean checkURL(String storedURL) {
        String selectQuery = "SELECT  * FROM " + TBL_URL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String record = cursor.getString(cursor.getColumnIndex(URL));
                if (record.contains(storedURL)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public boolean checkURI(String storedURL) {
        String selectQuery = "SELECT  * FROM " + TBL_Image;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String record = cursor.getString(cursor.getColumnIndex(IMG_URI));
                if (record.contains(storedURL)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public ArrayList<String> getAllLocalURL() {
        String selectQuery = "SELECT  * FROM " + TBL_URL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> inWardSaveModelArrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String InwardNo = cursor.getString(cursor.getColumnIndex(URL));
                inWardSaveModelArrayList.add(InwardNo);
            } while (cursor.moveToNext());
        }
        return inWardSaveModelArrayList;
    }

    public int getTotalCount() {
        String selectQuery = "SELECT  * FROM " + TBL_URL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int counter = 0;
        ArrayList<String> inWardSaveModelArrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                counter++;
            } while (cursor.moveToNext());
        }
        return counter;
    }

    public int getTotalImageCount() {
        try {
            String selectQuery = "SELECT  * FROM " + TBL_Image;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            int counter = 0;
            ArrayList<String> inWardSaveModelArrayList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    counter++;
                } while (cursor.moveToNext());
            }
            return counter;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void DeleteAllRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TBL_URL);
    }

    @SuppressLint("LongLogTag")
    public void deleteURL(String storedURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TBL_URL, URL + " = ?",
                new String[]{storedURL});
        db.close();
        if (!checkURL(storedURL)) {
            Log.i("DELETED OFFLINE RECORD FOR INWARD NO:", storedURL);
        }
    }

    public void deletePhotoRecord(String filename) {

        Log.i("DELETING IMAGE WITH ID:",filename);

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TBL_Image, Filename + " = ?",
                new String[]{filename});
        db.close();
    }

    public void enterPhotoDetail(List<PhotoEntry> mySelectedPhotos, String _Str_iMeiNo, String _Str_Model, String _Str_ID,
                                 String _Str_Lat, String _Str_Long, String _Str_Loc, String _Str_GPS, String _Str_DriverID,
                                 String _Str_JobView, String _Str_TripNo, String _Remarks, String _Str_JobNo, String _Rev_Name,
                                 String _Str_Nric, String _Str_Sts, String _Str_JobExe, String _fType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < mySelectedPhotos.size(); i++) {
            values.put(IMG_URI, mySelectedPhotos.get(i).getPath());
            values.put(Str_iMeiNo, _Str_iMeiNo);
            values.put(Str_Model, _Str_Model);
            values.put(Str_ID, _Str_ID);
            values.put(Str_Lat, _Str_Lat);
            values.put(Str_Long, _Str_Long);
            values.put(Str_Loc, _Str_Loc);
            values.put(Str_GPS, _Str_GPS);
            values.put(Str_DriverID, _Str_DriverID);
            values.put(Str_JobView, _Str_JobView);
            values.put(Str_TripNo, _Str_TripNo);
            values.put(Remarks, _Remarks);
            values.put(Str_JobNo, _Str_JobNo);
            values.put(Rev_Name, _Rev_Name);
            values.put(Str_Nric, _Str_Nric);
            values.put(Str_Sts, _Str_Sts);
            values.put(Str_JobExe, _Str_JobExe);
            String imagename = System.currentTimeMillis() + "_" + Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext) + ".jpg";
            values.put(Filename, imagename);
            values.put(fType, _fType);
            if (!checkURI(mySelectedPhotos.get(i).getPath())) {
                db.insert(TBL_Image, null, values);
            }
        }
        db.close();
    }

    public OfflineImageModel getImageRecord() {
        OfflineImageModel offlineImageModel = new OfflineImageModel();
        String selectQuery = "SELECT  * FROM " + TBL_Image;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String _IMG_URI = cursor.getString(cursor.getColumnIndex(IMG_URI));
            String _Str_iMeiNo = cursor.getString(cursor.getColumnIndex(Str_iMeiNo));
            String _Str_Model = cursor.getString(cursor.getColumnIndex(Str_Model));
            String _Str_ID = cursor.getString(cursor.getColumnIndex(Str_ID));
            String _Str_Lat = cursor.getString(cursor.getColumnIndex(Str_Lat));
            String _Str_Long = cursor.getString(cursor.getColumnIndex(Str_Long));
            String _Str_Loc = cursor.getString(cursor.getColumnIndex(Str_Loc));
            String _Str_GPS = cursor.getString(cursor.getColumnIndex(Str_GPS));
            String _Str_DriverID = cursor.getString(cursor.getColumnIndex(Str_DriverID));
            String _Str_JobView = cursor.getString(cursor.getColumnIndex(Str_JobView));
            String _Str_TripNo = cursor.getString(cursor.getColumnIndex(Str_TripNo));
            String _Remarks = cursor.getString(cursor.getColumnIndex(Remarks));
            String _Str_JobNo = cursor.getString(cursor.getColumnIndex(Str_JobNo));
            String _Rev_Name = cursor.getString(cursor.getColumnIndex(Rev_Name));
            String _Str_Nric = cursor.getString(cursor.getColumnIndex(Str_Nric));
            String _Str_Sts = cursor.getString(cursor.getColumnIndex(Str_Sts));
            String _Str_JobExe = cursor.getString(cursor.getColumnIndex(Str_JobExe));
            String _Filename = cursor.getString(cursor.getColumnIndex(Filename));
            String _fType = cursor.getString(cursor.getColumnIndex(fType));
            offlineImageModel.setIMG_URI(_IMG_URI);
            offlineImageModel.setStr_iMeiNo(_Str_iMeiNo);
            offlineImageModel.setStr_Model(_Str_Model);
            offlineImageModel.setStr_ID(_Str_ID);
            offlineImageModel.setStr_Lat(_Str_Lat);
            offlineImageModel.setStr_Long(_Str_Long);
            offlineImageModel.setStr_Loc(_Str_Loc);
            offlineImageModel.setStr_GPS(_Str_GPS);
            offlineImageModel.setStr_DriverID(_Str_DriverID);
            offlineImageModel.setStr_JobView(_Str_JobView);
            offlineImageModel.setStr_TripNo(_Str_TripNo);
            offlineImageModel.setRemarks(_Remarks);
            offlineImageModel.setStr_JobNo(_Str_JobNo);
            offlineImageModel.setRev_Name(_Rev_Name);
            offlineImageModel.setStr_Nric(_Str_Nric);
            offlineImageModel.setStr_Sts(_Str_Sts);
            offlineImageModel.setStr_JobExe(_Str_JobExe);
            offlineImageModel.setFilename(_Filename);
            offlineImageModel.setfType(_fType);
        }
        return offlineImageModel;
    }

}
