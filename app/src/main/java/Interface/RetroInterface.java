package Interface;

import Classes.ModelBreakDown;
import Classes.ModelHistory;
import Classes.ModelList;
import Classes.ModelLogin;
import Classes.ModelLoginSpinnerList;
import Classes.ModelNotifi;
import Classes.ModelReport;
import Classes.ModelSpinnerList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetroInterface {

    //?Str_ID=SG00iX&Str_Model=Android&Str_UserID=free&Str_Pwd=user&Str_Event="Login"&Str_Lat="0"&Str_Long="0"&Str_Long="0"&Str_ProjName="vTrak_SG";
    @GET("Mo_Login.jsp")
    Call<ModelLogin> loginWithCredentials(
            @Query("Str_ID") String id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") String user_id,
            @Query("Str_Pwd") String password,
            @Query("Str_Event") String event,
            @Query("Str_Lat") double latitude,
            @Query("Str_Long") double longitude,
            @Query("Str_gcmid") String gcm_id,
            @Query("Str_ProjName") String proj_name);


    //http://203.125.153.221/tt_tv1/Mo_AssetList.jsp?Str_ID=SG00iX&Str_Model=Android&Str_UserID=62&Str_List=All
    @GET("Mo_AssetList.jsp")
    Call<ModelList> getList(
            @Query("Str_ID") String id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") String user_id,
            @Query("Str_List") String list);

    ///tt_tsg/
    @GET("Mo_Hist_Details.jsp")
    Call<ModelHistory> getHistory(
            @Query("Str_ID") String id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") int user_id,
            @Query("Str_AssetNo") String assetNo,
            @Query("Str_DT_S") String date_s,
            @Query("Str_DT_E") String date_e);

    @GET("Mo_RptList.jsp")
    Call<ModelReport> getReport(
            @Query("Str_ID") String id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") int user_id
    );

    //tt_tsg/Mo_Notify_List.jsp?Str_ID=SG00iX&Str_Model=iPhone&Str_UserID=17&Str_VehNo=ALL
    @GET("Mo_Notify_List.jsp")
    Call<ModelNotifi> getNotifi(
            @Query("Str_ID") String id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") int user_id,
            @Query("Str_VehNo") String vehNo
    );

    //tt_tsg/Mo_BreakList.jsp?Str_ID=10&Str_Model=iPhone&&Str_UserID=FREE&Str_VehNo=SJE5976G
    @GET("Mo_BreakList.jsp")
    Call<ModelSpinnerList> getSpinnerData(
            @Query("Str_ID") int id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") String user_id,
            @Query("Str_VehNo") String vehNo
    );

    @GET("Mo_Breakdown_Request.jsp")
    Call<ModelBreakDown> setBreakDownLocation(
            @Query("Str_ID") int id,
            @Query("Str_Model") String model,
            @Query("Str_UserID") String user_id,
            @Query("Str_VehNo") String vehNo,
            @Query("Service_Required") String serRequired,
            @Query("BkLoc") String bkLoc,
            @Query("BkLoc_Ad") String bkLocAd,
            @Query("BkLoc_Lat") String bkLocLat,
            @Query("BkLoc_Long") String bkLocLong,
            @Query("ContactNo") String contactNo,
            @Query("Job_Notes") String jobNotes
    );

    //?Str_ID=10&Str_Model=Android&Str_Entry=Country
    @GET("Mo_Change_Country.jsp")
    Call<ModelLoginSpinnerList> getLoginSpinnerData(
            @Query("Str_ID") int id,
            @Query("Str_Model") String model,
            @Query("Str_Entry") String strEntry
    );
}