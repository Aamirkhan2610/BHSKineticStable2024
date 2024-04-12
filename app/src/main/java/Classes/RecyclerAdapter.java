package Classes;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import activities.HomeActivity;
import activities.LiveTrackActivity;
import bhskinetic.idee.com.bhskinetic_new.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context mContext;

    List<ModelData> listData;
    FragmentManager fragmentManager;
    String[] arrayDialog;
    public static final String KEY_LAT = "keyLat";
    public static final String KEY_LONG = "keyLong";
    public static final String KEY_VEHICLE_TYPE = "keyVehicleType";
    public static final String KEY_VEH_NUM = "vehNum";
    public static final String KEY_VEH_LOAD = "vehLoad";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_LOC = "loc";
    public static final String KEY_DATE_TIME = "dateTime";
    public static final String KEY_ASSET_COLOR = "keyColor";

    public RecyclerAdapter(FragmentManager fragmentManager, Context mContext, List<ModelData> listData) {

        this.fragmentManager = fragmentManager;
        this.mContext = mContext;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.row_recycler_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tv_assetId.setText(listData.get(position).getAssetID());
        viewHolder.tv_dateTime.setText(listData.get(position).getLocDatetime());
        viewHolder.tv_speed.setText(listData.get(position).getLocSpeed());
        viewHolder.tv_address.setText(listData.get(position).getAssetLoc());

        /*if (position == getItemCount() - 1) {

            viewHolder.view_separator.setVisibility(View.INVISIBLE);
        }*/

        if (listData.get(position).getAssetColor().equalsIgnoreCase("black")) {

            Glide.with(mContext)
                    .load(R.drawable.car_black)
                    .placeholder(R.drawable.car_placeholder)
                    .into(viewHolder.iv_car);
        } else if (listData.get(position).getAssetColor().equalsIgnoreCase("blue")) {

            Glide.with(mContext)
                    .load(R.drawable.car_blue)
                    .placeholder(R.drawable.car_placeholder)
                    .into(viewHolder.iv_car);
        } else if (listData.get(position).getAssetColor().equalsIgnoreCase("green")) {

            Glide.with(mContext)
                    .load(R.drawable.car_green)
                    .placeholder(R.drawable.car_placeholder)
                    .into(viewHolder.iv_car);
        } else if (listData.get(position).getAssetColor().equalsIgnoreCase("red")) {

            Glide.with(mContext)
                    .load(R.drawable.car_red)
                    .placeholder(R.drawable.car_placeholder)
                    .into(viewHolder.iv_car);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intentLiveTrack = new Intent(mContext, LiveTrackActivity.class);
                intentLiveTrack.putExtra(KEY_LAT, listData.get(position).getLocLat());
                intentLiveTrack.putExtra(KEY_LONG, listData.get(position).getLocLon());
                intentLiveTrack.putExtra(KEY_VEH_NUM, listData.get(position).getAssetID());
                intentLiveTrack.putExtra(KEY_SPEED, listData.get(position).getLocSpeed());
                intentLiveTrack.putExtra(KEY_VEH_LOAD, listData.get(position).getLoad());
                intentLiveTrack.putExtra(KEY_LOC, listData.get(position).getAssetLoc());
                intentLiveTrack.putExtra(KEY_DATE_TIME, listData.get(position).getLocDatetime());
                intentLiveTrack.putExtra(KEY_ASSET_COLOR, listData.get(position).getAssetColor());
                HomeActivity.activity.finish();
                mContext.startActivity(intentLiveTrack);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_assetId, tv_dateTime, tv_speed, tv_address;
        ImageView iv_car;
        View view_separator;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_assetId = (TextView) itemView.findViewById(R.id.tv_AssetId);
            tv_dateTime = (TextView) itemView.findViewById(R.id.tv_DateTime);
            tv_speed = (TextView) itemView.findViewById(R.id.tv_speed);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            iv_car = (ImageView) itemView.findViewById(R.id.iv_car);
            view_separator = itemView.findViewById(R.id.view_separator);
        }
    }
}