package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import Model.ModelException;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 4/15/2017.
 */

public class MovieSeat extends Activity {
    public static TextView tv_header;
    public static TextView tv_occupied;
    public static TextView tv_empty;
    public static TextView tv_selected;
    public static Context mContext;
    public static GridView grid_moviesheet;
    public static GridElementAdapter adapter;
    public static ArrayList<String> arraySeat;
    public static ArrayList<String> arrayDisplay;
    public static int occupiedCounter = 0;
    public static int emptyCounter = 0;
    public static String Str_SeqNo = "";
    public static int selectedCounter = 0;
    public static Button btn_submit;
    public static String SelectedLoad = "";
    public static TrackGPS gps;
    public static Activity activity;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static String Str_ResName = "";
    public static String Str_Exe = "NA";
    public static String StatusName="";
    public static String Str_JobNo="";
    public static RecyclerView recycleMovie;
    public static boolean isFromOrderDetail = false;
    public static ArrayList<Integer> posArray;
    private MyRecyclerViewAdapter resadapter;
    public static ArrayList<ModelException> arrayException;
    public static ModelException modelException;
    public static ArrayAdapter<String> adapterException;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_moviesheet);
        Init();
    }

    private void Init() {
        mContext = MovieSeat.this;
        tv_header = (TextView) findViewById(R.id.tv_header);
        grid_moviesheet = (GridView) findViewById(R.id.grid_moviesheet);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        posArray=new ArrayList<>();
        arraySeat = new ArrayList<>();
        arrayDisplay = new ArrayList<>();
        arrayException = new ArrayList<>();

        recycleMovie = findViewById(R.id.rvDashboard);
        recycleMovie.setLayoutManager(new GridLayoutManager(mContext, 2));


        isFromOrderDetail = false;
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setVisibility(View.GONE);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        Str_Exe="";
        String[] Splitter = null;
        try {
           //String values="U|U|C|C|Y|Y|Y|Y|Y|Y";
            String values=Utils.getPref(mContext.getResources().getString(R.string.pref_Load),mContext);
            Splitter = values.split("\\|");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SelectedLoad = "";


        int counter = 1;
        for (int i = 0; i < Splitter.length; i++) {
            arraySeat.add(Splitter[i]);
            if(Splitter[i].equalsIgnoreCase("C")){
                posArray.add(i);
            }
            arrayDisplay.add("" + (100 / Splitter.length) * counter);
            counter++;
        }

     /*   arraySheets.add("Y");
        arraySheets.add("Y");
        arraySheets.add("Y");
        arraySheets.add("Y");*/

        adapter = new GridElementAdapter(mContext);
        grid_moviesheet.setAdapter(adapter);


        resadapter = new MyRecyclerViewAdapter(mContext);
        recycleMovie.setAdapter(resadapter);

        Log.i("LOAD DATA", "===>" + arraySeat.toString());

        //tv_header.setText(mContext.getResources().getString(R.string.str_movie_sheet) + "(" + arraySheets.size() + ")");
        tv_header.setText("UTILIZATION");

        tv_occupied = (TextView) findViewById(R.id.tv_occupied);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        tv_selected = (TextView) findViewById(R.id.tv_selected);
        UpdateCounter();

        grid_moviesheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*for (int i = 0; i < arraySeat.size(); i++) {
                    arraySeat.set(i, "Y");
                    adapter.notifyDataSetChanged();
                }

                if (arraySeat.get(position).equalsIgnoreCase("Y")) {
                    //If Empty
                    for (int i = 0; i <= position; i++) {
                        arraySeat.set(i, "S");
                    }
                } else if (arraySeat.get(position).equalsIgnoreCase("S")) {
                    //If Selected
                    arraySeat.set(position, "Y");
                } else {
                    //If Occupied
                    arraySeat.set(position, "Y");
                }*/


                if(!arraySeat.get(position).equalsIgnoreCase("U")){
                    if (arraySeat.get(position).equalsIgnoreCase("S")) {
                        arraySeat.set(position, "Y");
                    }else {
                        //If Empty
                        for (int i = 0; i <= position; i++) {
                            if(!arraySeat.get(position).equalsIgnoreCase("U")) {
                                arraySeat.set(i, "S");
                            }
                        }
                    }
                    UpdateCounter();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isError=false;
                if(StatusName.equalsIgnoreCase("UNLOADING")){
                        for(int i=0;i<posArray.size();i++){
                            if(!arraySeat.get(posArray.get(i)).equalsIgnoreCase("S")){
                                isError=true;
                                break;
                            }
                        }

                    for(int j=0;j<arraySeat.size();j++){
                        if(!posArray.contains(j)){
                            if(arraySeat.get(j).equalsIgnoreCase("S")){
                                isError=true;
                                break;
                            }
                        }
                    }

                    if(isError){
                        Alert(mContext.getResources().getString(R.string.error_unloading),mContext);
                        return;
                    }

                    SelectedLoad = "";

                    for (int i = 0; i < arraySeat.size(); i++) {

                        String load = "";

                        if (arraySeat.get(i).equalsIgnoreCase("S")) {
                            load = ""+(i+1);
                            if (SelectedLoad.trim().length() > 0) {
                                SelectedLoad = SelectedLoad + "," + load;
                            } else {
                                SelectedLoad = load;
                            }
                        }

                    }

                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();
                    if (!gps.canGetLocation()) {
                        lat = 0;
                        lng = 0;
                    }

                    String SELECTION=SelectedLoad;

                    APIUtils.getAddressFromLatLong(mContext, lat, lng, "MovieSeat");

                }else{
                    int QTY=0;
                    for(int j=0;j<arraySeat.size();j++){
                        if(arraySeat.get(j).equalsIgnoreCase("S")){
                            QTY=QTY+1;
                        }
                    }
                    AlertLoading("Please confirm the quantity is "+QTY,mContext);
                }

            }
        });

        gps = new TrackGPS(mContext);
        activity = MovieSeat.this;
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNO("", getResources().getString(R.string.alert_logout_user), mContext);
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            Str_ResName = b.getString("Str_ResName");
            Str_SeqNo = b.getString("Str_SeqNo");
            Str_JobNo=b.getString("Str_JobNo");
            StatusName=b.getString("StatusName");
            if (b.getString("isFromOrderDetail") != null) {
                isFromOrderDetail = true;
            }
        }

    }

    public static void AlertLoading(String alertMessage, Context mContext){
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle("");
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Select Exception
                SelectedLoad = "";

                for (int i = 0; i < arraySeat.size(); i++) {

                    String load = "";

                    if (arraySeat.get(i).equalsIgnoreCase("S")) {
                        load = ""+(i+1);
                        if (SelectedLoad.trim().length() > 0) {
                            SelectedLoad = SelectedLoad + "," + load;
                        } else {
                            SelectedLoad = load;
                        }
                    }

                }

                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                String SELECTION=SelectedLoad;
                APIUtils.getAddressFromLatLong(mContext, lat, lng, "MovieSeat");
                dialog.dismiss();
            }
        });


        builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when No button clicked
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public static void Alert(String alertMessage, Context mContext){
        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
        builderInner.setCancelable(false);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle("");
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Select Exception
                getExceptionList("NA",""+gps.getLatitude(),""+gps.getLongitude(),"OFF");
                dialog.dismiss();
            }
        });


        builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when No button clicked
               dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public static void getExceptionList(String address, String lat, String lng, String gpsStatus) {
        String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        APIUtils.sendRequest(mContext, "Exception List", "Misc_List.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientID + "&Str_Lat=" + lng + "&Str_Long=" + lat + "&Str_Loc=" + address + "&Str_GPS=" + gpsStatus + "&Str_DriverID=" + driverId + "&Str_Event=Exception", "PMTRListMovieSeat");
    }


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
        private LayoutInflater mInflater;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the cell layout from xml when needed
        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.raw_gridmoviesheet, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        // binds the data to the TextView in each cell
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.tv_title.setText(arrayDisplay.get(position) + "%");
            if (arraySeat.get(position).equalsIgnoreCase("Y")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_green));
                }

            } else if (arraySeat.get(position).equalsIgnoreCase("U")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_grey));
                }

            } else if (arraySeat.get(position).equalsIgnoreCase("S")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //  h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_blue));
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_yellow));
                }
            }

            if (arraySeat.get(position).equalsIgnoreCase("S")) {
                holder.checkBox.setImageResource(R.drawable.chb_selected);
            }else{
                holder.checkBox.setImageResource(R.drawable.chb_normal);
            }


            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!arraySeat.get(position).equalsIgnoreCase("U")){

                        if(StatusName.equalsIgnoreCase("UNLOADING") && arraySeat.get(position).equalsIgnoreCase("Y")){
                            return;
                        }
                        if (arraySeat.get(position).equalsIgnoreCase("S")) {
                            boolean isC=false;
                            for(int i=0;i<posArray.size();i++){
                                if(posArray.get(i)==position){
                                    isC=true;
                                    break;
                                }
                            }

                            if(isC){
                                arraySeat.set(position, "C");
                            }else{
                                arraySeat.set(position, "Y");
                            }

                        }else {
                            //If Empty

                            for (int i = 0; i <= position; i++) {
                                if(!arraySeat.get(i).equalsIgnoreCase("U")) {
                                    if(StatusName.equalsIgnoreCase("UNLOADING") && arraySeat.get(i).equalsIgnoreCase("Y")){

                                    }else{
                                        arraySeat.set(i, "S");
                                    }

                                }
                            }
                        }
                        UpdateCounter();
                        notifyDataSetChanged();
                    }
                }
            });
        }

        // total number of cells
        @Override
        public int getItemCount() {
            return arraySeat.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            FrameLayout layout_moviesheet_cell;
            ImageView checkBox;
            TextView tv_title;
            ViewHolder(View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                checkBox=itemView.findViewById(R.id.chb_movie_seat);
                layout_moviesheet_cell=itemView.findViewById(R.id.layout_moviesheet_cell);
            }
        }
    }


    //Ask User to choose Yes/No
    public static void AlertYesNO(String alertTitle, String alertMessage, final Context mContext) {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        builderInner.setMessage(alertMessage);
        builderInner.setTitle(alertTitle);
        builderInner.setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String driverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
                String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
                APIUtils.sendRequest(mContext, "User Logout", "Login.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_Way=Logout&Str_Lat=" + gps.getLatitude() + "&Str_Long=" + gps.getLongitude() + "&Str_DriverID=" + driverId + "&Str_gcmid=" + "", "logoutMovieSheet");
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

    @Override
    public void onBackPressed() {
  //       super.onBackPressed();
    }

    private void SentSheetDetails(String address) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientId = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String lat = "" + gps.getLatitude();
        String lng = "" + gps.getLongitude();
        String DriverId = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        String Str_SpaceSeq = SelectedLoad;
        String Str_GPS = "ON";
        String addressToSent = address;
        if (!gps.canGetLocation()) {
            Str_GPS = "OFF";
            addressToSent = "";
            lat = "0";
            lng = "0";
        }
        APIUtils.sendRequest(mContext, "Movie Sheet", "Movie_Seat_V1.jsp?Str_iMeiNo=" + IMEINumber + "&Str_Model=Android&Str_ID=" + ClientId + "&Str_Lat=" + lat + "&Str_Long=" + lng + "&Str_Loc=" + addressToSent + "&Str_GPS=" + Str_GPS + "&Str_DriverID=" + DriverId + "&Str_SpaceSeq=" + Str_SpaceSeq + "&Str_ResName=" + Str_ResName + "&Str_SeqNo=" + Str_SeqNo+ "&Str_Exe=" + Str_Exe, "sendLoad");
    }

    private void UpdateCounter() {

        occupiedCounter = 0;
        emptyCounter = 0;
        selectedCounter = 0;

        for (int i = 0; i < arraySeat.size(); i++) {
            if (arraySeat.get(i).equalsIgnoreCase("Y")) {

                emptyCounter++;

            } else if (arraySeat.get(i).equalsIgnoreCase("N")) {
                occupiedCounter++;
            } else if (arraySeat.get(i).equalsIgnoreCase("S")) {
                selectedCounter++;
            }
        }
        tv_occupied.setText(mContext.getResources().getString(R.string.str_movie_sheet_occupied) + "(" + occupiedCounter + ")");
        tv_empty.setText(mContext.getResources().getString(R.string.str_movie_sheet_empty) + "(" + emptyCounter + ")");
        tv_selected.setText(mContext.getResources().getString(R.string.str_movie_sheet_selected) + "(" + selectedCounter + ")");
    }

    public void showResponse(String response, String redirectionKey) {

        if (redirectionKey.equalsIgnoreCase("sendLoad")) {

            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("recived").equalsIgnoreCase("1")) {
                    Utils.setPref(mContext.getResources().getString(R.string.pref_Load), jsonObject.optString("Load"), mContext);
                    if (!isFromOrderDetail) {
                        Intent intent = new Intent(mContext, MovieSeat.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    }
                    activity.finish();
                } else {

                    Utils.Alert(mContext.getResources().getString(R.string.alert_unabletoupdate_sheet), mContext);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (redirectionKey.equalsIgnoreCase("logoutMovieSheet")) {

            try {
                JSONObject jobj = new JSONObject(response);
                if (jobj.optString("Status").equalsIgnoreCase("1")) {
                    Intent logoutIntent = new Intent(mContext, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(logoutIntent);
                    MovieSeat.activity.finish();
                    Utils.clearPref(mContext);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (redirectionKey.equalsIgnoreCase("MovieSeat")) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String address = "";
                if (!jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jobaddress = results.getJSONObject(0);
                    address = jobaddress.optString("formatted_address");
                    if (address.contains(" ")) {
                        address = address.replaceAll(" ", "%20");
                    }
                }

                SentSheetDetails(address);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (redirectionKey.equalsIgnoreCase("PMTRListMovieSeat")) {
            try {
                JSONObject exceptionlist = new JSONObject(response);
                if (exceptionlist.optString("recived").equalsIgnoreCase("1")) {
                    arrayException.clear();
                    if (exceptionlist.has("Ack_Msg")) {
                        Toast.makeText(mContext, exceptionlist.optString("Ack_Msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray list = exceptionlist.optJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject value = list.optJSONObject(i);
                            modelException = new ModelException();
                            modelException.setListValue(((i + 1) + ".") + value.optString("ListValue"));
                            modelException.setListValueWithoutIndex(value.optString("ListValue"));
                            arrayException.add(modelException);
                        }
                        DialogueWithListException(arrayException);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void DialogueWithListException(final ArrayList<ModelException> exceptionlist) {
        // custom dialog
        final Dialog dialoglist = new Dialog(mContext);
        dialoglist.setContentView(R.layout.raw_attachmentlist);
        dialoglist.setCancelable(false);
        dialoglist.setTitle(mContext.getResources().getString(R.string.alert_exception_title));

        final ArrayList<ModelException> exceptionlistFiltered = new ArrayList<>();
        final ArrayList<ModelException> exceptionlistImplemented = new ArrayList<>();
        exceptionlistImplemented.addAll(exceptionlist);
        final ListView lv_resource = (ListView) dialoglist.findViewById(R.id.lv_resource);
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialoglist.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglist.dismiss();
            }
        });

        EditText etSearch = (EditText) dialoglist.findViewById(R.id.edt_search);
        etSearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charText, int start, int before, int count) {
                exceptionlistFiltered.clear();
                if (charText.length() == 0) {
                    exceptionlistFiltered.addAll(exceptionlist);
                } else {
                    for (ModelException wp : exceptionlist) {
                        if ((wp.getListValue().toLowerCase(Locale.getDefault()).contains(charText.toString().toLowerCase(Locale.getDefault())))) {
                            exceptionlistFiltered.add(wp);
                        }

                    }

                }

                exceptionlistImplemented.clear();
                exceptionlistImplemented.addAll(exceptionlistFiltered);


                final String[] values = new String[exceptionlistImplemented.size()];
                for (int i = 0; i < exceptionlistImplemented.size(); i++) {
                    values[i] = exceptionlistImplemented.get(i).getListValue();
                }
                adapterException = new ArrayAdapter<String>(mContext,
                        R.layout.listtext, R.id.tv_title, values);
                lv_resource.setAdapter(adapterException);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final String[] values = new String[exceptionlistImplemented.size()];
        for (int i = 0; i < exceptionlistImplemented.size(); i++) {
            values[i] = exceptionlistImplemented.get(i).getListValue();
        }
        adapterException = new ArrayAdapter<String>(mContext,
                R.layout.listtext, R.id.tv_title, values);
        lv_resource.setAdapter(adapterException);

        lv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Str_Exe = exceptionlistImplemented.get(position).getListValueWithoutIndex();

                SelectedLoad = "";

                for (int i = 0; i < arraySeat.size(); i++) {

                    String load = "";

                    if (arraySeat.get(i).equalsIgnoreCase("S")) {
                        load = ""+(i+1);
                        if (SelectedLoad.trim().length() > 0) {
                            SelectedLoad = SelectedLoad + "," + load;
                        } else {
                            SelectedLoad = load;
                        }
                    }

                }

                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                if (!gps.canGetLocation()) {
                    lat = 0;
                    lng = 0;
                }

                String SELECTION=SelectedLoad;

                APIUtils.getAddressFromLatLong(mContext, lat, lng, "MovieSeat");
            }
        });


        dialoglist.show();
    }

    //GridView adapter calss
    public class GridElementAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        public GridElementAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arraySeat.size();
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
                v = layoutInflater.inflate(R.layout.raw_gridmoviesheet, null);
                h = new ViewHolder();
                h.tv_title = (TextView) v.findViewById(R.id.tv_title);
                h.checkBox=(ImageView) v.findViewById(R.id.chb_movie_seat);
                h.tv_title.setText(arrayDisplay.get(position) + "%");

                h.layout_moviesheet_cell = v.findViewById(R.id.layout_moviesheet_cell);

                if (arraySeat.get(position).equalsIgnoreCase("Y")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_green));
                    }

                } else if (arraySeat.get(position).equalsIgnoreCase("U")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_grey));
                    }

                } else if (arraySeat.get(position).equalsIgnoreCase("S")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                      //  h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_blue));
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_yellow));
                    }
                }

                if (arraySeat.get(position).equalsIgnoreCase("S")) {
                    h.checkBox.setImageResource(R.drawable.chb_selected);
                }else{
                    h.checkBox.setImageResource(R.drawable.chb_normal);
                }


                h.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!arraySeat.get(position).equalsIgnoreCase("U")){
                            if (arraySeat.get(position).equalsIgnoreCase("S")) {
                                arraySeat.set(position, "Y");
                            }else {
                                //If Empty
                                for (int i = 0; i <= position; i++) {
                                    if(!arraySeat.get(i).equalsIgnoreCase("U")) {
                                        arraySeat.set(i, "S");
                                    }
                                }
                            }
                            UpdateCounter();
                            notifyDataSetChanged();
                        }
                    }
                });


                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_title.setText(arrayDisplay.get(position) + "%");

                if (arraySeat.get(position).equalsIgnoreCase("Y")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_green));
                    }

                } else if (arraySeat.get(position).equalsIgnoreCase("U")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_grey));
                    }

                } else if (arraySeat.get(position).equalsIgnoreCase("S")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                      //  h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_blue));
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        h.layout_moviesheet_cell.setBackground(getResources().getDrawable(R.drawable.moviesheet_yellow));
                    }
                }

                if (arraySeat.get(position).equalsIgnoreCase("S")) {
                    h.checkBox.setImageResource(R.drawable.chb_selected);
                }else{
                    h.checkBox.setImageResource(R.drawable.chb_normal);
                }
                h.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!arraySeat.get(position).equalsIgnoreCase("U")){
                            if (arraySeat.get(position).equalsIgnoreCase("S")) {
                                arraySeat.set(position, "Y");
                            }else {
                                //If Empty
                                for (int i = 0; i <= position; i++) {
                                    if(!arraySeat.get(i).equalsIgnoreCase("U")) {
                                        arraySeat.set(i, "S");
                                    }
                                }
                            }
                            UpdateCounter();
                            notifyDataSetChanged();
                        }
                    }
                });



            }
            return v;
        }

        private class ViewHolder {
            ImageView checkBox;
            TextView tv_title;
            FrameLayout layout_moviesheet_cell;
        }
    }
}
