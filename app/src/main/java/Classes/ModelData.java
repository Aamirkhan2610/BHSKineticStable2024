package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelData {

    @SerializedName("AssetID")
    @Expose
    private String assetID;
    @SerializedName("AssetLoc")
    @Expose
    private String assetLoc;
    @SerializedName("Loc_lat")
    @Expose
    private String locLat;
    @SerializedName("Loc_lon")
    @Expose
    private String locLon;
    @SerializedName("Loc_Datetime")
    @Expose
    private String locDatetime;
    @SerializedName("Loc_Speed")
    @Expose
    private String locSpeed;
    @SerializedName("Asset_Color")
    @Expose
    private String assetColor;
    @SerializedName("FL")
    @Expose
    private String fL;
    @SerializedName("temperature")
    @Expose
    private String temperature;
    @SerializedName("Veh_type")
    @Expose
    private String vehType;

    public String getLoad() {
        return Load;
    }

    public void setLoad(String load) {
        Load = load;
    }

    @SerializedName("Load")
    @Expose
    private String Load;

    public ModelData(String asset_id, String asset_loc, String loc_lat, String loc_lon, String loc_time, String loc_speed, String asset_color, String fl, String temperature, String veh_type, String load) {

        assetID = asset_id;
        assetLoc = asset_loc;
        locLat = loc_lat;
        locLon = loc_lon;
        locDatetime = loc_time;
        locSpeed = loc_speed;
        assetColor = asset_color;
        fL = fl;
        this.temperature = temperature;
        vehType = veh_type;
        Load=load;

    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getAssetLoc() {
        return assetLoc;
    }

    public void setAssetLoc(String assetLoc) {
        this.assetLoc = assetLoc;
    }

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

    public String getLocDatetime() {
        return locDatetime;
    }

    public void setLocDatetime(String locDatetime) {
        this.locDatetime = locDatetime;
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

    public String getFL() {
        return fL;
    }

    public void setFL(String fL) {
        this.fL = fL;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }
}
