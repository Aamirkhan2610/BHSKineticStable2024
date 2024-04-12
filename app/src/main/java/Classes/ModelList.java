package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mohammed Faizan on 12-11-2017.
 */

public class ModelList {

    @SerializedName("Data")
    @Expose
    private List<ModelData> data = null;
    @SerializedName("Data1")
    @Expose
    private List<ModelData1> data1 = null;

    public List<ModelData1> getData1() {
        return data1;
    }

    public void setData1(List<ModelData1> data1) {
        this.data1 = data1;
    }

    public List<ModelData> getData() {
        return data;
    }

    public void setData(List<ModelData> data) {
        this.data = data;
    }
}
