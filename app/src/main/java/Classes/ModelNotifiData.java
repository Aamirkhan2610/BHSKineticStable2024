package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelNotifiData {

    @SerializedName("Remarks")
    @Expose
    private String remarks;

    public ModelNotifiData(String remarks) {

        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
