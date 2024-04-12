package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelBreakDown {
    @SerializedName("recived")
    @Expose
    private Integer recived;
    @SerializedName("Ack_Msg")
    @Expose
    private String ackMsg;

    public Integer getRecived() {
        return recived;
    }

    public void setRecived(Integer recived) {
        this.recived = recived;
    }

    public String getAckMsg() {
        return ackMsg;
    }

    public void setAckMsg(String ackMsg) {
        this.ackMsg = ackMsg;
    }
}
