package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelHisData {

    @SerializedName("Loc_lat")
    @Expose
    private String locLat;
    @SerializedName("Loc_lon")
    @Expose
    private String locLon;
    @SerializedName("Remarks")
    @Expose
    private String remarks;
    @SerializedName("Loc_Speed")
    @Expose
    private String locSpeed;
    @SerializedName("Asset_Color")
    @Expose
    private String assetColor;

    public String getLocLat() {
        return locLat;
    }

    public void setLocLat(String locLat) {
        this.locLat = locLat;
    }

    public String getLocLon() {
        return locLon;
    }

    public void setLocLon(String locLon) {
        this.locLon = locLon;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLocSpeed() {
        return locSpeed;
    }

    public void setLocSpeed(String locSpeed) {
        this.locSpeed = locSpeed;
    }

    public String getAssetColor() {
        return assetColor;
    }

    public void setAssetColor(String assetColor) {
        this.assetColor = assetColor;
    }

}
