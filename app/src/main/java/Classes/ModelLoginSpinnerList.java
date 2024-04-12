package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelLoginSpinnerList {
    @SerializedName("Data")
    @Expose
    private List<ModelLoginSpinner> data = null;

    public List<ModelLoginSpinner> getData() {
        return data;
    }

    public void setData(List<ModelLoginSpinner> data) {
        this.data = data;
    }
}
