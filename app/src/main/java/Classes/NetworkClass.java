package Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkClass implements Runnable {
    private volatile boolean isNetwrokAvailable;
    NetworkInfo activeNetworkInfo;
    Context mContext;

    public NetworkClass(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void run() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public boolean getNetworkStat() {
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
