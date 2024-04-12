package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelData1 {

    @SerializedName("vColor")
    @Expose
    private String vColor;
    @SerializedName("No_Of_Veh")
    @Expose
    private String noOfVeh;

    public ModelData1(String vColor, String noOfVeh) {
        this.vColor = vColor;
        this.noOfVeh = noOfVeh;
    }

    public String getVColor() {
        return vColor;
    }

    public void setVColor(String vColor) {
        this.vColor = vColor;
    }

    public String getNoOfVeh() {
        return noOfVeh;
    }

    public void setNoOfVeh(String noOfVeh) {
        this.noOfVeh = noOfVeh;
    }
}
