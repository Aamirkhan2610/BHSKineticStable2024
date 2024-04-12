package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.ModelBHSNews;
import Model.ReactionModel;
import general.APIUtils;
import general.TrackGPS;
import general.Utils;

/**
 * Created by Aamir on 12/11/2017.
 */

public class BHSNewsActivity extends Activity {
    public static TextView tv_header;
    public static ImageView img_logout;
    public static ImageView img_refresh;
    public static Context mContext;
    public static TrackGPS gps;
    public static Activity activity;
    public static ArrayList<ModelBHSNews> arrayList;
    public static ListView list_notification;
    public static ListElementAdapter adapter;
    public static boolean ScrolledToBottomOnce=false;
    public static String Str_SeqNo="";
    public static DialogPlusBuilder dialogPlusBuilder;
    public static DialogPlus dialogPlus;
    public static ReactionAdapter reactionAdapter;
    public static String like,unlike,smile,star,Love,happy,angry,bored,puzzled,surprised;
    public static ArrayList<ReactionModel> reactionModelArrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashnotification);
        activity = BHSNewsActivity.this;
        mContext = BHSNewsActivity.this;
        gps = new TrackGPS(mContext);
        Init();
    }

    private void Init() {
        like=unlike=smile=star=Love=happy=angry=bored=puzzled=surprised="";
        reactionModelArrayList=new ArrayList<>();
        reactionAdapter=new ReactionAdapter(mContext);
        dialogPlusBuilder=dialogPlus.newDialog(mContext);

        ScrolledToBottomOnce=false;
        Str_SeqNo="";
        arrayList=new ArrayList<>();
        tv_header = (TextView) findViewById(R.id.tv_header);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_logout.setVisibility(View.GONE);
        img_refresh.setImageResource(R.drawable.ic_back);
        Bundle b = getIntent().getExtras();
        tv_header.setText("BHS News");
        list_notification=findViewById(R.id.list_notification);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        getNewsData("",""+gps.getLatitude(),""+gps.getLongitude(),"");
    }


    private void getNewsData(String address, String lat, String lng, String gpsStatus) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "BHS News", "News_Feed_List.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc="+address+"&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_Type=NEWS", "BHSNewsList");

    }

    public static void updateReaction(String address, String lat, String lng, String gpsStatus,String Str_Misc_Status) {
        String IMEINumber = Utils.getPref(mContext.getResources().getString(R.string.pref_IMEINumber), mContext);
        String ClientID = Utils.getPref(mContext.getResources().getString(R.string.pref_Client_ID), mContext);
        String DriverID = Utils.getPref(mContext.getResources().getString(R.string.pref_driverID), mContext);
        APIUtils.sendRequest(mContext, "BHS News Reaction", "Status_Update.jsp?Str_iMeiNo="+IMEINumber+"&Str_Model=Android&Str_ID="+ClientID+"&Str_Lat="+lat+"&Str_Long="+lng+"&Str_Loc=NA&Str_GPS="+gpsStatus+"&Str_DriverID="+DriverID+"&Str_SeqNo="+Str_SeqNo+"&Str_Misc_Type=REACTION&Str_Misc_Status="+Str_Misc_Status+"&Str_JobFor=BHS", "BHSNewsReaction");
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    //ListView Adapter Class
    public static class ReactionAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public ReactionAdapter(Context _context) {
            super();
            this.context = _context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return reactionModelArrayList.size();
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

            //"like":"1","unlike":"1","smile":"1","star":"1","Love":"1","happy":"1","angry":"1","bored":"0","puzzled":"0","surprised":"0

            if (v == null) {
                v = layoutInflater.inflate(R.layout.raw_reaction, null);
                h = new ViewHolder();
                h.img_icon=v.findViewById(R.id.img_icon);
                h.tv_counter=v.findViewById(R.id.tv_counter);

                h.img_icon.setImageResource(reactionModelArrayList.get(position).getReactionIcon());
                h.tv_counter.setText(reactionModelArrayList.get(position).getReactioName()+" ("+reactionModelArrayList.get(position).getReactionCount()+")");

                h.img_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Str_SeqNo=reactionModelArrayList.get(position).getMsgSeqNumber();
                        updateReaction("",""+gps.getLatitude(),""+gps.getLongitude(),"",reactionModelArrayList.get(position).getReactioName());
                    }
                });

                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();

                h.img_icon.setImageResource(reactionModelArrayList.get(position).getReactionIcon());
                h.tv_counter.setText(reactionModelArrayList.get(position).getReactioName()+" ("+reactionModelArrayList.get(position).getReactionCount()+")");
                h.img_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Str_SeqNo=reactionModelArrayList.get(position).getMsgSeqNumber();
                        updateReaction("",""+gps.getLatitude(),""+gps.getLongitude(),"",reactionModelArrayList.get(position).getReactioName());
                    }
                });
            }
            return v;
        }


        private class ViewHolder {
            TextView tv_counter;
            ImageView img_icon;
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
            return arrayList.size();
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
                v = layoutInflater.inflate(R.layout.raw_news_result, null);
                h = new ViewHolder();
                h.layout_delete=v.findViewById(R.id.layout_delete);
                h.tv_scan_result=v.findViewById(R.id.tv_scan_result);
                h.reactLayout=v.findViewById(R.id.reactLayout);


                h.reactLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reactionModelArrayList=new ArrayList<>();
                        like=arrayList.get(position).getLike();
                        unlike=arrayList.get(position).getUnlike();
                        smile=arrayList.get(position).getSmile();
                        star=arrayList.get(position).getStar();
                        Love=arrayList.get(position).getLove();
                        happy=arrayList.get(position).getHappy();
                        angry=arrayList.get(position).getAngry();
                        bored=arrayList.get(position).getBored();
                        puzzled=arrayList.get(position).getPuzzled();
                        surprised=arrayList.get(position).getSurprised();

                        ReactionModel reactionModel;
                        if(!like.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Like");
                            reactionModel.setReactionIcon(R.drawable.ic_like_bhs);
                            reactionModel.setReactionCount(like);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!unlike.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Unlike");
                            reactionModel.setReactionIcon(R.drawable.ic_dislike_bhs);
                            reactionModel.setReactionCount(unlike);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!smile.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Smile");
                            reactionModel.setReactionIcon(R.drawable.ic_smile_bhs);
                            reactionModel.setReactionCount(smile);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!star.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Star");
                            reactionModel.setReactionIcon(R.drawable.ic_star_bhs);
                            reactionModel.setReactionCount(star);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!Love.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Love");
                            reactionModel.setReactionIcon(R.drawable.ic_love_bhs);
                            reactionModel.setReactionCount(Love);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!happy.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Happy");
                            reactionModel.setReactionIcon(R.drawable.ic_happy_bhs);
                            reactionModel.setReactionCount(happy);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!angry.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Angry");
                            reactionModel.setReactionIcon(R.drawable.ic_angry_bhs);
                            reactionModel.setReactionCount(angry);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!bored.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Bored");
                            reactionModel.setReactionIcon(R.drawable.ic_bored_bhs);
                            reactionModel.setReactionCount(bored);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!puzzled.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Puzzled");
                            reactionModel.setReactionIcon(R.drawable.ic_puzzeled_bhs);
                            reactionModel.setReactionCount(puzzled);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!surprised.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Surprised");
                            reactionModel.setReactionIcon(R.drawable.ic_surprise_bhs);
                            reactionModel.setReactionCount(surprised);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }



                        dialogPlusBuilder.setContentWidth(700);
                        dialogPlusBuilder.setContentHolder(new GridHolder(3));
                        dialogPlusBuilder.setAdapter(reactionAdapter);
                        dialogPlus=dialogPlusBuilder.create();
                        dialogPlus.show();

                    }
                });
                h.tv_scan_result.setText(Html.fromHtml(arrayList.get(position).getRemarks())+"\n"+arrayList.get(position).getNft_Dt());
                h.tv_scan_result.setMovementMethod(LinkMovementMethod.getInstance());
                v.setTag(h);
            } else {
                h = (ViewHolder) v.getTag();
                h.tv_scan_result.setText(Html.fromHtml(arrayList.get(position).getRemarks())+"\n"+arrayList.get(position).getNft_Dt());
                h.tv_scan_result.setMovementMethod(LinkMovementMethod.getInstance());

                h.reactLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reactionModelArrayList=new ArrayList<>();
                        like=arrayList.get(position).getLike();
                        unlike=arrayList.get(position).getUnlike();
                        smile=arrayList.get(position).getSmile();
                        star=arrayList.get(position).getStar();
                        Love=arrayList.get(position).getLove();
                        happy=arrayList.get(position).getHappy();
                        angry=arrayList.get(position).getAngry();
                        bored=arrayList.get(position).getBored();
                        puzzled=arrayList.get(position).getPuzzled();
                        surprised=arrayList.get(position).getSurprised();

                        ReactionModel reactionModel;
                        if(!like.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Like");
                            reactionModel.setReactionIcon(R.drawable.ic_like_bhs);
                            reactionModel.setReactionCount(like);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!unlike.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Unlike");
                            reactionModel.setReactionIcon(R.drawable.ic_dislike_bhs);
                            reactionModel.setReactionCount(unlike);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!smile.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Smile");
                            reactionModel.setReactionIcon(R.drawable.ic_smile_bhs);
                            reactionModel.setReactionCount(smile);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!star.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Star");
                            reactionModel.setReactionIcon(R.drawable.ic_star_bhs);
                            reactionModel.setReactionCount(star);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!Love.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Love");
                            reactionModel.setReactionIcon(R.drawable.ic_love_bhs);
                            reactionModel.setReactionCount(Love);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!happy.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Happy");
                            reactionModel.setReactionIcon(R.drawable.ic_happy_bhs);
                            reactionModel.setReactionCount(happy);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!angry.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Angry");
                            reactionModel.setReactionIcon(R.drawable.ic_angry_bhs);
                            reactionModel.setReactionCount(angry);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!bored.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Bored");
                            reactionModel.setReactionIcon(R.drawable.ic_bored_bhs);
                            reactionModel.setReactionCount(bored);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!puzzled.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Puzzled");
                            reactionModel.setReactionIcon(R.drawable.ic_puzzeled_bhs);
                            reactionModel.setReactionCount(puzzled);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }

                        if(!surprised.equalsIgnoreCase("0")){
                            reactionModel=new ReactionModel();
                            reactionModel.setReactioName("Surprised");
                            reactionModel.setReactionIcon(R.drawable.ic_surprise_bhs);
                            reactionModel.setReactionCount(surprised);
                            reactionModel.setMsgSeqNumber(arrayList.get(position).getSeqNo());
                            reactionModelArrayList.add(reactionModel);
                        }



                        dialogPlusBuilder.setContentWidth(700);
                        dialogPlusBuilder.setContentHolder(new GridHolder(3));
                        dialogPlusBuilder.setAdapter(reactionAdapter);
                        dialogPlus=dialogPlusBuilder.create();
                        dialogPlus.show();
                    }
                });
            }
            return v;
        }

        private class ViewHolder {
            LinearLayout layout_delete;
            TextView tv_scan_result;
            ImageView reactLayout;
        }
    }


    public void showResponse(String response, String redirectionKey) {

        if (redirectionKey.equalsIgnoreCase("BHSNewsList")) {
            arrayList.clear();
            try {
                JSONObject jobj = new JSONObject(response);
                JSONArray jsonArray= jobj.optJSONArray("Data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject dataObject=jsonArray.optJSONObject(i);
                    ModelBHSNews modelDashNotification=new ModelBHSNews();
                    modelDashNotification.setRemarks(dataObject.optString("Remarks"));
                    modelDashNotification.setSeqNo(dataObject.optString("SeqNo"));
                    modelDashNotification.setNft_Dt(dataObject.optString("Nft_Dt"));
                    modelDashNotification.setLike(dataObject.optString("like"));
                    modelDashNotification.setUnlike(dataObject.optString("unlike"));
                    modelDashNotification.setSmile(dataObject.optString("smile"));
                    modelDashNotification.setStar(dataObject.optString("star"));
                    modelDashNotification.setLove(dataObject.optString("Love"));
                    modelDashNotification.setHappy(dataObject.optString("happy"));
                    modelDashNotification.setAngry(dataObject.optString("angry"));
                    modelDashNotification.setBored(dataObject.optString("bored"));
                    modelDashNotification.setPuzzled(dataObject.optString("puzzled"));
                    modelDashNotification.setSurprised(dataObject.optString("surprised"));
                    arrayList.add(modelDashNotification);
                }

                if(arrayList.size()>0){
                    adapter = new ListElementAdapter(mContext);
                    list_notification.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else  if (redirectionKey.equalsIgnoreCase("BHSNewsReaction")) {
            try {
                JSONObject jobj = new JSONObject(response);
                if(jobj.optString("recived").equalsIgnoreCase("1")){
                    Toast.makeText(mContext,jobj.optString("Ack_Msg"),Toast.LENGTH_SHORT).show();
                    dialogPlus.dismiss();
                    getNewsData("",""+gps.getLatitude(),""+gps.getLongitude(),"");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

