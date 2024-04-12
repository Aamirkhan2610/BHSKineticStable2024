package Model;

import org.json.JSONArray;

/**
 * Created by Aamir on 4/16/2017.
 */

public class ModelOrderList {

    private String Display="";
    private JSONArray sublist=null;
    private String Trip_No="";

    public String getMovie_Milestone() {
        return Movie_Milestone;
    }

    public void setMovie_Milestone(String movie_Milestone) {
        Movie_Milestone = movie_Milestone;
    }

    private String Movie_Milestone="";
    public String getAppLock() {
        return AppLock;
    }

    public void setAppLock(String appLock) {
        AppLock = appLock;
    }

    private String AppLock="";
    public String getJobNo() {
        return JobNo;
    }

    public void setJobNo(String jobNo) {
        JobNo = jobNo;
    }

    private String JobNo="";
    private String Movie_Seats="";

    public String getMovie_Seats() {
        return Movie_Seats;
    }

    public void setMovie_Seats(String movie_Seats) {
        Movie_Seats = movie_Seats;
    }

    public String getApps_Status() {
        return Apps_Status;
    }

    public void setApps_Status(String apps_Status) {
        Apps_Status = apps_Status;
    }

    public String getVeh_Status() {
        return Veh_Status;
    }

    public void setVeh_Status(String veh_Status) {
        Veh_Status = veh_Status;
    }

    private String Apps_Status="OFF";
    private String Veh_Status="OFF";
    public String getViewed() {
        return Viewed;
    }

    public void setViewed(String viewed) {
        Viewed = viewed;
    }

    private String Viewed="";

    public String getERP_No() {
        return ERP_No;
    }

    public void setERP_No(String ERP_No) {
        this.ERP_No = ERP_No;
    }

    private String ERP_No="";

    public String getvTrip_Tm() {
        return vTrip_Tm;
    }

    public void setvTrip_Tm(String vTrip_Tm) {
        this.vTrip_Tm = vTrip_Tm;
    }

    private String vTrip_Tm="";
    public String getvTrip_Dt() {
        return vTrip_Dt;
    }

    public void setvTrip_Dt(String vTrip_Dt) {
        this.vTrip_Dt = vTrip_Dt;
    }

    private String vTrip_Dt="";
    private String ZONE="";

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        Display = display;
    }

    public JSONArray getSublist() {
        return sublist;
    }

    public void setSublist(JSONArray sublist) {
        this.sublist = sublist;
    }

    public String getTrip_No() {
        return Trip_No;
    }

    public void setTrip_No(String trip_No) {
        Trip_No = trip_No;
    }


    public String getZONE() {
        return ZONE;
    }

    public void setZONE(String ZONE) {
        this.ZONE = ZONE;
    }
}
