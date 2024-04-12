package Model;

/**
 * Created by Aamir on 4/18/2017.
 */

public class ModelLeaveHistory {
    private String Req_Name="";
    private String Req_Status="";
    private String VehicleNo="";

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getPONo() {
        return PONo;
    }

    public void setPONo(String PONo) {
        this.PONo = PONo;
    }

    private String PONo="";

    public String getComplete_Status() {
        return Complete_Status;
    }

    public void setComplete_Status(String complete_Status) {
        Complete_Status = complete_Status;
    }

    private String Complete_Status="0";

    public String getReq_StartDate() {
        return Req_StartDate;
    }

    public void setReq_StartDate(String req_StartDate) {
        Req_StartDate = req_StartDate;
    }

    private String Req_StartDate="";
    public String getListID() {
        return ListID;
    }

    public void setListID(String listID) {
        ListID = listID;
    }

    private String ListID="";
    public int getVisibleDetailItem() {
        return visibleDetailItem;
    }

    public void setVisibleDetailItem(int visibleDetailItem) {
        this.visibleDetailItem = visibleDetailItem;
    }

    private int visibleDetailItem=-1;
    public String getReq_Display() {
        return Req_Display;
    }

    public void setReq_Display(String req_Display) {
        Req_Display = req_Display;
    }

    private String Req_Display="";
    public String getReq_Name() {
        return Req_Name;
    }

    public void setReq_Name(String req_Name) {
        Req_Name = req_Name;
    }

    public String getReq_Status() {
        return Req_Status;
    }

    public void setReq_Status(String req_Status) {
        Req_Status = req_Status;
    }
}
