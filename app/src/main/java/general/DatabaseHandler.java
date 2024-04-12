package general;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "android_api";
    private static String url_all_survey = "http://mysurveyapp.cloudapp.net/mobileapi/samplesurvey";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_CHCKD_DNNO = "checkeddnno";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CLIENTID = "client_id";
    private static final String KEY_CLIENTUSERNAME = "clientusername";
    private static final String KEY_CLIENTPASSWORD = "clientpassword";
    private static final String KEY_CLIENTBATLIMIT = "clientbatlimit";
    private static final String KEY_CLIENTBATAMTLIMIT = "clientbatamtlimit";
    private static final String KEY_BARCODE = "barcodelist";
    private static final String KEY_DNNO = "dnnolist";

    private static final String KEY_DNNOCHKD = "checked_dnno";
    private static final String KEY_DNNOCHKDTYPE = "checked_dnnotype";
    private static final String TABLE_BARCODE = "barcode";
    private static final String TABLE_DNNO = "dnno";
    //private static final String TABLE_SURVEY_QUE = "survey_sections_que";
    private static final String TABLE_OFFERS = "offers_table";
    private static final String TABLE_Message = "message_table";


    private static final String KEY_OFFERID = "offerid";
    private static final String KEY_OFFERDATE = "offerdate";
    private static final String KEY_OFFERDETAIL = "offerdetail";


    private static final String KEY_MESSAGEID = "messageid";
    private static final String KEY_MESSAGEDATE = "messagedate";
    private static final String KEY_MESSAGEDETAIL = "messagedetail";
    private static final String KEY_MESSAGESENDER = "messagesender";


    private static final String TABLE_GLOBALSETTINGS = "globalsettings";
    private static final String KEY_REMARKS = "Remarks";
    private static final String KEY_MODULE = "Module";
    private static final String KEY_STRURL = "Str_URL";
    private static final String KEY_CLIENTIDglobal = "Client_ID";
    // Contacts Table Columns names
    JSONArray data = null;
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CLIENTID + " TEXT,"
                + KEY_CLIENTUSERNAME + " TEXT,"
                + KEY_CLIENTPASSWORD + " TEXT,"
                + KEY_CLIENTBATLIMIT + " TEXT,"
                + KEY_CLIENTBATAMTLIMIT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);


        String CREATE_BARCODE_TABLE = "CREATE TABLE " + TABLE_BARCODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BARCODE + " TEXT" + ")";
        db.execSQL(CREATE_BARCODE_TABLE);

        String CREATE_CHCKEDDNNO_TABLE = "CREATE TABLE " + TABLE_CHCKD_DNNO + "("
                //+ KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DNNOCHKD + " TEXT,"
                //	+ KEY_CLIENTUSERNAME + " TEXT,"
                //	+ KEY_CLIENTPASSWORD + " TEXT,"
                //	+ KEY_CLIENTBATLIMIT + " TEXT,"
                + KEY_DNNOCHKDTYPE + " TEXT" + ")";
        db.execSQL(CREATE_CHCKEDDNNO_TABLE);


        String CREATE_OFFER_TABLE = "CREATE TABLE " + TABLE_OFFERS
                + "("
                + KEY_OFFERID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + KEY_OFFERDATE + " TEXT,"
                + KEY_OFFERDETAIL + " TEXT "
                + ")";
        db.execSQL(CREATE_OFFER_TABLE);

        Log.i("LoginRegicreated", "Database Created");


        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_Message
                + "("
                + KEY_MESSAGEID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + KEY_MESSAGEDATE + " TEXT,"
                + KEY_MESSAGEDETAIL + " TEXT,"
                + KEY_MESSAGESENDER + " TEXT "
                + ")";
        db.execSQL(CREATE_MESSAGE_TABLE);

        Log.i("messagetable", "Table Created");


        String CREATE_DNNO_TABLE = "CREATE TABLE " + TABLE_DNNO + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                //+ KEY_CLIENTID + " TEXT,"
                //	+ KEY_CLIENTUSERNAME + " TEXT,"
                //	+ KEY_CLIENTPASSWORD + " TEXT,"
                //	+ KEY_CLIENTBATLIMIT + " TEXT,"
                + KEY_DNNO + " TEXT" + ")";
        db.execSQL(CREATE_DNNO_TABLE);

        String CREATE_GLOBATSETTINGS_TABLE = "CREATE TABLE " + TABLE_GLOBALSETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_MODULE + " TEXT,"
                + KEY_REMARKS + " TEXT,"
                + KEY_CLIENTIDglobal + " TEXT,"
                + KEY_STRURL + " TEXT" + ")";
        db.execSQL(CREATE_GLOBATSETTINGS_TABLE);


        Log.i("database createddddd", "database created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARCODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHCKD_DNNO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GLOBALSETTINGS);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String client_id, String pass, String batlimit, String batamtlimit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLIENTID, client_id);
        values.put(KEY_CLIENTUSERNAME, name); // Name
        values.put(KEY_CLIENTPASSWORD, pass); // Pass
        values.put(KEY_CLIENTBATLIMIT, batlimit);
        values.put(KEY_CLIENTBATAMTLIMIT, batamtlimit);
        // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public void Reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_OFFERS);
        db.close();
    }

    public void addglobalsettings(String module, String Clientid, String strurl, String remarks) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MODULE, module);
        values.put(KEY_CLIENTIDglobal, Clientid);
        values.put(KEY_STRURL, strurl);
        values.put(KEY_REMARKS, remarks);
        db.insert(TABLE_GLOBALSETTINGS, null, values);
        Log.i("database", "successfull");
        db.close(); // Closing database connection
    }

    public void addBarcode(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, barcode);

        // Created At
        // Inserting Row
        db.insert(TABLE_BARCODE, null, values);

        Log.i("database", "successfull");
        db.close(); // Closing database connection
    }


    public boolean addoffer(String offerdate, String offerdetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_OFFERDATE, offerdate);
        values.put(KEY_OFFERDETAIL, offerdetail);

        // Created At
        // Inserting Row
        db.insert(TABLE_OFFERS, null, values);

        Log.i("data added", "data added");
        db.close(); // Closing database connection

        return true;
    }


    public boolean addmessage(String messagedate, String messagedetail, String messagesender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGEDATE, messagedate);
        values.put(KEY_MESSAGEDETAIL, messagedetail);
        values.put(KEY_MESSAGESENDER, messagesender);

        // Created At
        // Inserting Row
        db.insert(TABLE_Message, null, values);

        Log.i("data added", "data added");
        db.close(); // Closing database connection

        return true;
    }


    public String display() {
        SQLiteDatabase db = this.getWritableDatabase();
        //use cursor to keep all data
        //cursor can keep data of any data type
        Cursor c = db.rawQuery("select * from login", null);
        // tv.setText("");
        //move cursor to first position
        c.moveToFirst();

        String name;
        //fetch all data one by one
        do {
            //we can use c.getString(0) here
            //or we can get data using column index
            name = c.getString(c.getColumnIndex("client_id"));
            String surname = c.getString(1);
            //display on text view
            // tv.append("Name:"+name+" and SurName:"+surname+"\n");
            //move next position until end of the data
        } while (c.moveToNext());

        Log.i("ashjdgaystduyasd", name);
        return name;
    }


    public void addadmin(String name, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLIENTUSERNAME, name); // Name
        values.put(KEY_CLIENTPASSWORD, pass); // Pass

        // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }


    public String[] getAppCategorydetail() {

        String Table_Name = TABLE_LOGIN;

        String selectQuery = "SELECT  * FROM " + Table_Name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {

                data[0] = cursor.getString(cursor.getColumnIndex("client_id"));
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }


    public boolean deleteitemfrombarcodelist(String dnno) {

        SQLiteDatabase db = this.getWritableDatabase();
        //return db.delete(TABLE_CART, KEY_CART_ITEM_ID + "=" + rowId +" AND "+ KEY_CART_OPTION_SELECTED + "=" + option, null) > 0;

        db.delete(TABLE_BARCODE, "barcodelist = ?",
                new String[]{dnno});


        return true;
    }


    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("clientusername", cursor.getString(1));
            user.put("clientpassword", cursor.getString(2));
            user.put("clientbatlimit", cursor.getString(3));
            user.put("clientbatamtlimit", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }


    public String getremarks() {

        String id = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery("select Remarks from globalsettings", null);

            if (c.getCount() > 0) {
                c.moveToNext();
                id = c.getString(0);

            }
        } catch (Exception e) {

        }

        return id;

    }


    public ArrayList<offerObject> getoffer() {
        String email = null;
        String detail = null;

        // ArrayList<offerObject> offerlistnew = null;

        Const.offerlist.clear();
        offerObject ob;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery("select * from offers_table order by offerid DESC", null);
            //  if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                while (c.isAfterLast() == false) {
                    //    String name = c.getString(c
                    //           .getColumnIndex(countyname));

                    //  list.add(name);

                    email = c.getString(1);
                    detail = c.getString(2);
                    Log.i("offerdate", email);
                    Log.i("offerdetail", detail);
                    Const.offerlist.add(new offerObject(email, detail, ""));


                    c.moveToNext();
                }
            }
            //  c.moveToNext();
            //    id = "";


            // }
        } catch (Exception e) {
        }
        return Const.offerlist;

    }


    public ArrayList<offerObject> getmessage() {
        String email = null;

        String detail = null;

        String sender = null;
        // ArrayList<offerObject> offerlistnew = null;

        Const.offerlist.clear();
        offerObject ob;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery("select * from message_table order by messageid ASC", null);
            //  if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                while (c.isAfterLast() == false) {
                    //    String name = c.getString(c
                    //           .getColumnIndex(countyname));

                    //  list.add(name);

                    email = c.getString(1);
                    detail = c.getString(2);
                    sender = c.getString(3);

                    Log.i("offerdate", email);
                    Log.i("offerdetail", detail);
                    Const.offerlist.add(new offerObject(email, detail, sender));


                    c.moveToNext();
                }
            }
            //  c.moveToNext();
            //    id = "";


            // }
        } catch (Exception e) {
        }
        return Const.offerlist;

    }


    /**
     * Getting user login status
     * return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database
     * Delete all tables and create them again
     */
    public void resetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

    public void resetTablesDNNO() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_DNNO, null, null);
        db.close();
    }

    public void resetTablesBarcode() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_BARCODE, null, null);
        db.close();
    }

    public void resetTableschkdnno() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CHCKD_DNNO, null, null);
        db.close();
    }


}
