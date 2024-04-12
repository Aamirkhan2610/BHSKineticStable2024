package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelLoginSpinner {
    @SerializedName("Country_Name")
    @Expose
    private String countryName;
    @SerializedName("URL_Name")
    @Expose
    private String uRLName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getURLName() {
        return uRLName;
    }

    public void setURLName(String uRLName) {
        this.uRLName = uRLName;
    }

}
