package Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Classes.ModelNotifi;
import Classes.ModelNotifiData;
import Classes.NotifiAdapter;
import Classes.RecyclerAdapter;
import Classes.UtilFunctions;
import activities.VtrakLoginActivity;
import bhskinetic.idee.com.bhskinetic_new.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    List<ModelNotifiData> listData;
    TextView tvNoData;
    ImageView ivBack;

    String param = "ALL";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);


        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        param = VtrakLoginActivity.sharedPreferences.getString(RecyclerAdapter.KEY_VEH_NUM, null);

        getNotifications(param);

        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });
        return view;
    }

    private void getNotifications(String param) {
        UtilFunctions.showDialog(getActivity());
        Call<ModelNotifi> call = UtilFunctions.retroInterface.getNotifi("SG00iX", "iPhone", Integer.parseInt(VtrakLoginActivity.sharedPreferences.getString(VtrakLoginActivity.KEY_RESULT_MSG, null)), param);
        call.enqueue(new Callback<ModelNotifi>() {
            @Override
            public void onResponse(Call<ModelNotifi> call, Response<ModelNotifi> response) {

                listData = new ArrayList<ModelNotifiData>();
                for (int i = 0; i < response.body().getData().size(); i++) {

                    Log.e("onresp noti", response.body().getData().get(i).getRemarks());
                    listData.add(new ModelNotifiData(response.body().getData().get(i).getRemarks()));

                }

                if (!listData.isEmpty()) {
                    NotifiAdapter adapterNoti = new NotifiAdapter(getActivity(), listData);
                    recyclerView.setAdapter(adapterNoti);
                    UtilFunctions.hideDialog();
                } else {
                    UtilFunctions.hideDialog();
                    tvNoData.setVisibility(View.VISIBLE);
                    /*UtilFunctions.showAlert(getActivity(), "Can't load data, please try again later");
                    getActivity().getSupportFragmentManager().popBackStackImmediate();*/
                }
            }

            @Override
            public void onFailure(Call<ModelNotifi> call, Throwable t) {
                UtilFunctions.hideDialog();
                tvNoData.setVisibility(View.VISIBLE);
                /*UtilFunctions.showAlert(getActivity(), "Can't load data, please try again later");
                getActivity().getSupportFragmentManager().popBackStackImmediate();*/
                Log.e("On Failed", ":(");
            }
        });
    }
}
