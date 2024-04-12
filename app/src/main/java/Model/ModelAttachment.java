package Model;

/**
 * Created by Aamir on 4/17/2017.
 */

public class ModelAttachment {
    private String FileName="";
    private String ImgID="";

    public String getImgID() {
        return ImgID;
    }

    public void setImgID(String imgID) {
        ImgID = imgID;
    }

    public String getFileNameWithoutIndexing() {
        return FileNameWithoutIndexing;
    }

    public void setFileNameWithoutIndexing(String fileNameWithoutIndexing) {
        FileNameWithoutIndexing = fileNameWithoutIndexing;
    }

    private String FileNameWithoutIndexing="";
    private String Str_URL="";
    private String Type="";

    private String Sent_By="";

    public String getSent_By() {
        return Sent_By;
    }

    public void setSent_By(String sent_By) {
        Sent_By = sent_By;
    }

    public String getSent_Date() {
        return Sent_Date;
    }

    public void setSent_Date(String sent_Date) {
        Sent_Date = sent_Date;
    }

    public String getStr_Status() {
        return Str_Status;
    }

    public void setStr_Status(String str_Status) {
        Str_Status = str_Status;
    }

    private String Sent_Date="";
    private String Str_Status="";

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getStr_URL() {
        return Str_URL;
    }

    public void setStr_URL(String str_URL) {
        Str_URL = str_URL;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

}
