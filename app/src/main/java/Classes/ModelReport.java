package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelReport {

    @SerializedName("Data")
    @Expose
    private List<ModelReportData> data = null;

    public List<ModelReportData> getData() {
        return data;
    }

    public void setData(List<ModelReportData> data) {
        this.data = data;
    }
}
