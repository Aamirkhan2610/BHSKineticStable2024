package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelLogin {

    @SerializedName("resultvalue")
    @Expose
    private String resultvalue;
    @SerializedName("resultmsg")
    @Expose
    private String resultmsg;
    @SerializedName("Brk_Service")
    @Expose
    private String brkService;

    public String getResultvalue() {
        return resultvalue;
    }

    public void setResultvalue(String resultvalue) {
        this.resultvalue = resultvalue;
    }

    public String getResultmsg() {
        return resultmsg;
    }

    public void setResultmsg(String resultmsg) {
        this.resultmsg = resultmsg;
    }

    public String getBrkService() {
        return brkService;
    }

    public void setBrkService(String brkService) {
        this.brkService = brkService;
    }
}
