package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelNotifi {

    @SerializedName("Data")
    @Expose
    private List<ModelNotifiData> data = null;

    public List<ModelNotifiData> getData() {
        return data;
    }

    public void setData(List<ModelNotifiData> data) {
        this.data = data;
    }
}
