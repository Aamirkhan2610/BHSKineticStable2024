package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelHistory {

    @SerializedName("Data")
    @Expose
    private List<ModelHisData> data = null;

    public List<ModelHisData> getData() {
        return data;
    }

    public void setData(List<ModelHisData> data) {
        this.data = data;
    }
}
