package Model;

/**
 * Created by Aamir on 4/21/2017.
 */

public class ModelMessage {
    private String Str_ID="";
    private String Message="";
    private String DateTime="";
    private String Str_Type="";

    public String getStr_ID() {
        return Str_ID;
    }

    public void setStr_ID(String str_ID) {
        Str_ID = str_ID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getStr_Type() {
        return Str_Type;
    }

    public void setStr_Type(String str_Type) {
        Str_Type = str_Type;
    }
}
