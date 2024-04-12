package Model;

/**
 * Created by Aamir on 4/18/2017.
 */

public class ModelException {

    private String ListID="";
    private String ListValue ="";


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected=false;

    public String getTypeOfException() {
        return TypeOfException;
    }

    public void setTypeOfException(String typeOfException) {
        TypeOfException = typeOfException;
    }

    private String TypeOfException="0";

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    private String pCode ="";

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
