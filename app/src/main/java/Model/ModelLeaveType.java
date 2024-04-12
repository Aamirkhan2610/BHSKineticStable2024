package Model;

/**
 * Created by Aamir on 4/18/2017.
 */

public class ModelLeaveType {

    public String getListID() {
        return ListID;
    }

    public void setListID(String listID) {
        ListID = listID;
    }

    public String getListValue() {
        return ListValue;
    }

    public void setListValue(String listValue) {
        ListValue = listValue;
    }

    private String ListID="";
    private String ListValue ="";

    public String getL_Bal() {
        return L_Bal;
    }

    public void setL_Bal(String l_Bal) {
        L_Bal = l_Bal;
    }

    private String L_Bal="";
    private String Req_Status="";

    public String getReq_Status() {
        return Req_Status;
    }

    public void setReq_Status(String req_Status) {
        Req_Status = req_Status;
    }

    public String getListValueWithoutIndex() {
        return ListValueWithoutIndex;
    }

    public void setListValueWithoutIndex(String listValueWithoutIndex) {
        ListValueWithoutIndex = listValueWithoutIndex;
    }

    private String ListValueWithoutIndex="";
}

