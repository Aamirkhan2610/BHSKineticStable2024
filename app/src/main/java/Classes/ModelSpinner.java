package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelSpinner {
    @SerializedName("ListID")
    @Expose
    private String listID;
    @SerializedName("ListValue")
    @Expose
    private String listValue;

    public ModelSpinner(String listId, String listValue) {
        this.listID = listId;
        this.listValue = listValue;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getListValue() {
        return listValue;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}
