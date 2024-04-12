package Model;

/**
 * Created by Aamir on 4/20/2017.
 */

public class ModelScanStatus {

    private String ListID="";
    private String ListValue="";

    public String getListValueWithoutIndex() {
        return ListValueWithoutIndex;
    }

    public void setListValueWithoutIndex(String listValueWithoutIndex) {
        ListValueWithoutIndex = listValueWithoutIndex;
    }

    private String ListValueWithoutIndex="";

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
}
