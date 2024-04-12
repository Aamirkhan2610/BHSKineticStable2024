package Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bhskinetic.idee.com.bhskinetic_new.R;

public class NotifiAdapter extends RecyclerView.Adapter<NotifiAdapter.ViewHolder> {

    Context mContext;
    List<ModelNotifiData> listData;

    public NotifiAdapter(Context mContext, List<ModelNotifiData> listData) {

        this.mContext = mContext;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.row_notifi_rec_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.tvRemarks.setText(listData.get(position).getRemarks());

        if (position == getItemCount() - 1) {
            viewHolder.viewSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemarks;
        View viewSeparator;

        public ViewHolder(View itemView) {
            super(itemView);
            viewSeparator = itemView.findViewById(R.id.viewSeparator);
            tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);

        }
    }
}
