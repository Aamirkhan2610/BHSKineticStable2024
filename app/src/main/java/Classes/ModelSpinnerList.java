package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelSpinnerList {
    @SerializedName("Data")
    @Expose
    private List<ModelSpinner> data = null;

    public List<ModelSpinner> getData() {
        return data;
    }

    public void setData(List<ModelSpinner> data) {
        this.data = data;
    }
}
