package general;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.Dash;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import bhskinetic.idee.com.bhskinetic_new.AlarmDialogeActivity;
import bhskinetic.idee.com.bhskinetic_new.AppController;
import bhskinetic.idee.com.bhskinetic_new.ApprovalActivity;
import bhskinetic.idee.com.bhskinetic_new.AssignJobActivity;
import bhskinetic.idee.com.bhskinetic_new.BookingJobActivity;
import bhskinetic.idee.com.bhskinetic_new.BHSNewsActivity;
import bhskinetic.idee.com.bhskinetic_new.DashboardActivity;
import bhskinetic.idee.com.bhskinetic_new.DashboardNotificationActivity;
import bhskinetic.idee.com.bhskinetic_new.DriverIncentiveActivity;
import bhskinetic.idee.com.bhskinetic_new.JobDetailWithPhoto;
import bhskinetic.idee.com.bhskinetic_new.LoginActivity;
import bhskinetic.idee.com.bhskinetic_new.MaintListSupplier;
import bhskinetic.idee.com.bhskinetic_new.MaintananceReqPlanner;
import bhskinetic.idee.com.bhskinetic_new.MaintananceReqSupplier;
import bhskinetic.idee.com.bhskinetic_new.MessageMainActivity;
import bhskinetic.idee.com.bhskinetic_new.MessageNotificationTab;
import bhskinetic.idee.com.bhskinetic_new.MessageSubActivity;
import bhskinetic.idee.com.bhskinetic_new.MiscRequestActivity;
import bhskinetic.idee.com.bhskinetic_new.MovieSeat;
import bhskinetic.idee.com.bhskinetic_new.OrderDetail;
import bhskinetic.idee.com.bhskinetic_new.OrderList;
import bhskinetic.idee.com.bhskinetic_new.PhotoUploadActivity;
import bhskinetic.idee.com.bhskinetic_new.PhotoUploadNew;
import bhskinetic.idee.com.bhskinetic_new.PlannerDashboard;
import bhskinetic.idee.com.bhskinetic_new.R;
import bhskinetic.idee.com.bhskinetic_new.RequestHome;
import bhskinetic.idee.com.bhskinetic_new.RequestLeave;
import bhskinetic.idee.com.bhskinetic_new.ScanActivity;
import bhskinetic.idee.com.bhskinetic_new.SignatureActivity;
import bhskinetic.idee.com.bhskinetic_new.UploadDownloadFiles;
import bhskinetic.idee.com.bhskinetic_new.UserListActivity;
import bhskinetic.idee.com.bhskinetic_new.VehicleUndertakingActivity;
import bhskinetic.idee.com.bhskinetic_new.WHScannerActivity;

/**
 * Created by Aamir on 2/21/2017.
 */
public class APIUtils {
    //Live URL
   // public static String BaseUrl = "http://203.125.153.221/tt_v1/";

    //Test URL
    public static String BaseUrl = "http://203.125.153.221/tt_tv1/";
    public static ProgressDialog pDialog;
    public static void sendRequest(final Context mContext, final String TAG, String query, final String redirectionKey) {
        if (!isNetworkConnected(mContext)) {
            boolean netAlertToShow = true;
            if (netAlertToShow) {
                Utils.Alert(mContext.getResources().getString(R.string.alert_internet_notavailable), mContext);
            }
            DatabaseHandlerOfflineURL databaseHandler=new DatabaseHandlerOfflineURL(mContext);
            if(!databaseHandler.checkURL(BaseUrl + query)){
                databaseHandler.updateURL(BaseUrl + query);
            }
            return;
        }
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = BaseUrl + query;
        if(redirectionKey.equalsIgnoreCase("getMapKey")){
            url=query;
        }
        if (url.contains(" ")) {
            url.replaceAll(" ", "%20");
        }

        if (redirectionKey.equalsIgnoreCase("update_distance_eta")) {
            url = query;
        }
        Utils.LogI("API URL", url, mContext);
        boolean progressToShow = true;
        pDialog = new ProgressDialog(mContext,R.style.ProgressDialogStyle);

        pDialog.setMessage(mContext.getResources().getString(R.string.str_progress_loading));


        if (redirectionKey.equalsIgnoreCase("messageList")) {
            progressToShow=false;
        }

        if (redirectionKey.equalsIgnoreCase("MaintList")) {
            progressToShow=true;
            pDialog.setCancelable(false);
        }else{
            pDialog.setCancelable(true);
        }


        if (redirectionKey.equalsIgnoreCase("MSGReadUpdate")) {
            progressToShow=false;
        }


        if(progressToShow){
           try {
               pDialog.show();
           }catch (Exception e){
               e.printStackTrace();
           }
        }

        if (new DatabaseHandlerOfflineURL(mContext).getTotalCount() > 0) {
            if (!Utils.checkServiceRunning(mContext)) {
                mContext.startService(new Intent(mContext, BHSService.class));
            }
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Utils.LogI(TAG, response.toString(), mContext);

                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    //{"recived":1,"POD_URL":"P|D|P|0000"}
                    JSONObject jsonObject=new JSONObject(response.toString());
                    if(jsonObject.has("POD")) {
                        String Ack_Msg = jsonObject.optString("POD");
                        //    Toast.makeText(mContext,Ack_Msg,Toast.LENGTH_SHORT).show();
                        String[] ackArray = Ack_Msg.split("\\|");
                        if (ackArray[1].equalsIgnoreCase("D")) {
                            Utils.setPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),"true",mContext);
                            Ack_Msg=Ack_Msg.replace("D","S");
                            jsonObject.remove("POD");
                            jsonObject.put("POD",Ack_Msg);
                        } else {
                            Utils.setPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),"false",mContext);
                        }

                        response=jsonObject;
                    }else{
                        //    Utils.setPref(mContext.getResources().getString(R.string.pref_isDigitalSignature),"false",mContext);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.i("SERVER RESPONSE", redirectionKey + "\n" + response.toString());

                if (redirectionKey.equalsIgnoreCase("login")) {
                    LoginActivity activity = new LoginActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logout")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("sendLoad")) {
                    MovieSeat activity = new MovieSeat();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutMovieSheet")) {
                    MovieSeat activity = new MovieSeat();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("AttachDeAttach")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("ResourceList")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("DashboardRefresh")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("CheckVehicleValidation")) {
                    VehicleUndertakingActivity activity = new VehicleUndertakingActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }
                else if (redirectionKey.equalsIgnoreCase("OrderList")) {
                    OrderList activity = new OrderList();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutOrderList")) {
                    OrderList activity = new OrderList();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutOrderInner")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("attachmentlist")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("updateOrderPrice")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("exceptionlist")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("UpdateJobStatus")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutLeaveHome")) {
                    RequestHome activity = new RequestHome();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutLeaverequest")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LeaveData")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("ApplyLeave")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LogoutScan")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("ScanStatus")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("SubmitScan")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("exceptionlistStatus")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LogoutPhoto")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("revlist")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LogoutSignature")) {
                    SignatureActivity activity = new SignatureActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LogoutMessage")) {
                    MessageNotificationTab activity = new MessageNotificationTab();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("messageList")) {
                    MessageSubActivity activity = new MessageSubActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("sendMessage")) {
                    MessageSubActivity activity = new MessageSubActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutAssignJob")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("locationlist")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("createJob")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("LogoutDriverIncentive")) {
                    DriverIncentiveActivity activity = new DriverIncentiveActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("IncentiveData")) {
                    DriverIncentiveActivity activity = new DriverIncentiveActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("UpdateIncentiveBill")) {
                    DriverIncentiveActivity activity = new DriverIncentiveActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("UpdateQTY")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("UserList")) {
                    UserListActivity activity = new UserListActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("MessageMainList")) {
                    MessageMainActivity activity = new MessageMainActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutMainMessageList")) {
                    MessageMainActivity activity = new MessageMainActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("logoutUserList")) {
                    UserListActivity activity = new UserListActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("attachmentlistFiles")) {
                    UploadDownloadFiles activity = new UploadDownloadFiles();
                    activity.showResponse(response.toString(), redirectionKey);
                } else if (redirectionKey.equalsIgnoreCase("IncentiveDataSelection")) {
                    DriverIncentiveActivity activity = new DriverIncentiveActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("createJobPhoto")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("getMapKey")) {
                    LoginActivity activity = new LoginActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("GetDriverDetailDialgue")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("UpdateDriverTimeStatus")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("CheckAvailblity")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("createJobAsign")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("customerList")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("PMTRList")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("JobViewUpdate")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("LeaveCancel")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("logoutDetailPhoto")) {
                    JobDetailWithPhoto activity = new JobDetailWithPhoto();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("JobDetailWithPhotoDetail")) {
                    JobDetailWithPhoto activity = new JobDetailWithPhoto();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("logoutPlannerDashboard")) {
                    PlannerDashboard activity = new PlannerDashboard();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("PlannerAttendanceDetail")) {
                    PlannerDashboard activity = new PlannerDashboard();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("PlannerFilterDetail")) {
                    PlannerDashboard activity = new PlannerDashboard();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("logoutApprovalrequest")) {
                    ApprovalActivity activity = new ApprovalActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ApprovalData")) {
                    ApprovalActivity activity = new ApprovalActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("getApprovalDetail")) {
                    ApprovalActivity activity = new ApprovalActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ApprovalRequest")) {
                    ApprovalActivity activity = new ApprovalActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("OTUpdate")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("logoutPlannerMaintanance")) {
                    MaintananceReqPlanner activity = new MaintananceReqPlanner();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("PlannerMaintData")) {
                    MaintananceReqPlanner activity = new MaintananceReqPlanner();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MaintList")) {
                    MaintananceReqPlanner activity = new MaintananceReqPlanner();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("SubmitMaintRequest")) {
                    MaintananceReqPlanner activity = new MaintananceReqPlanner();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("SubmitOrderDetails")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ValidateBarecode")) {
                    ScanActivity activity = new ScanActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("CheckCam")) {
                    LoginActivity activity = new LoginActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ImageHistory")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("HAWBList")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("PMTRListMovieSeat")) {
                    MovieSeat activity = new MovieSeat();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("DashNotificationList")) {
                    DashboardNotificationActivity activity = new DashboardNotificationActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("NotificationReadUpdate")) {
                    DashboardNotificationActivity activity = new DashboardNotificationActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MSGReadUpdate")) {
                    MessageSubActivity activity = new MessageSubActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("BHSNewsList")) {
                    BHSNewsActivity activity = new BHSNewsActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("BHSNewsReaction")) {
                    BHSNewsActivity activity = new BHSNewsActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("SendACKMsg")) {
                    AlarmDialogeActivity activity = new AlarmDialogeActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ServiceTypeMaint")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MaintPopupSubmit")) {
                    RequestLeave activity = new RequestLeave();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MainListSupplier")) {
                    MaintListSupplier activity = new MaintListSupplier();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("maintAttachmentList")) {
                    MaintListSupplier activity = new MaintListSupplier();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("ServiceTypeMaintList")) {
                    MaintananceReqSupplier activity = new MaintananceReqSupplier();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MaintReqSubmitSupplier")) {
                    MaintananceReqSupplier activity = new MaintananceReqSupplier();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("revlistMaintList")) {
                    MaintListSupplier activity = new MaintListSupplier();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("WHMaintList")) {
                    WHScannerActivity activity = new WHScannerActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("SubmitWHScanner")) {
                    WHScannerActivity activity = new WHScannerActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("DashboardRefresh1")) {
                    DashboardNotificationActivity activity = new DashboardNotificationActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("bookingTimeSlot")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MiscType")) {
                    MiscRequestActivity activity = new MiscRequestActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("SubmitMISCData")) {
                    MiscRequestActivity activity = new MiscRequestActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("MiscHistory")) {
                    MiscRequestActivity activity = new MiscRequestActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("LeaveCancelMisc")) {
                    MiscRequestActivity activity = new MiscRequestActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("getAvailableDates")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("locationlist1")) {
                    AssignJobActivity activity = new AssignJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("locationlist1")) {
                    AssignJobActivity activity = new AssignJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("customerListBook")) {
                    BookingJobActivity activity = new BookingJobActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("podList")) {
                    PhotoUploadActivity activity = new PhotoUploadActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("podOTP")) {
                    DashboardActivity activity = new DashboardActivity();
                    activity.showResponse(response.toString(), redirectionKey);
                }else if (redirectionKey.equalsIgnoreCase("App_Lock")) {
                    OrderDetail activity = new OrderDetail();
                    activity.showResponse(response.toString(), redirectionKey);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(mContext.getClass().getSimpleName() + "\n" + TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    //Blocking Map Function
    public static void getAddressFromLatLong(final Context mContext, double LATITUDE, double LONGITUDE, final String redirectKey) {
        JSONObject response = new JSONObject();
        try {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("status", "1");
            HashMap<String, String> hasInner = new HashMap<>();
            hasInner.put("formatted_address", "NA");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(new JSONObject(hasInner));
            response = new JSONObject(hashMap);
            response.put("results", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (redirectKey.equalsIgnoreCase("MovieSeat")) {
                MovieSeat activity = new MovieSeat();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AttachDeAttachAddress")) {
                DashboardActivity activity = new DashboardActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("LoginAddress")) {
                LoginActivity activity = new LoginActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("OrderListAddress")) {
                OrderList activity = new OrderList();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddress")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("attachmentlist")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddressException")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressUpdateStatus")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("LeaveRequestAddress")) {
                RequestLeave activity = new RequestLeave();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("LeaveSubmitAddress")) {
                RequestLeave activity = new RequestLeave();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("OrderListUpdateAddress")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("ScanAddress")) {
                ScanActivity activity = new ScanActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("ScanAddressEx")) {
                ScanActivity activity = new ScanActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("ScanAddressSubmit")) {
                ScanActivity activity = new ScanActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("PhotoAddress")) {
                PhotoUploadActivity activity = new PhotoUploadActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("PhotoAddressNew")) {
                PhotoUploadNew activity = new PhotoUploadNew();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressPhoto")) {
                PhotoUploadActivity activity = new PhotoUploadActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressPhotoNew")) {
                PhotoUploadNew activity = new PhotoUploadNew();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressMessage")) {
                MessageSubActivity activity = new MessageSubActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressSendMessage")) {
                MessageSubActivity activity = new MessageSubActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressAssignJob")) {
                BookingJobActivity activity = new BookingJobActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("DriverIncentiveAddress")) {
                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressBillSubmit")) {
                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("AddressQTY")) {
                OrderDetail activity = new OrderDetail();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("MessageListAddress")) {
                UserListActivity activity = new UserListActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("MessageMainListAddress")) {
                MessageMainActivity activity = new MessageMainActivity();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddressFiles")) {
                UploadDownloadFiles activity = new UploadDownloadFiles();
                activity.showResponse(response.toString(), redirectKey);
            } else if (redirectKey.equalsIgnoreCase("DriverIncentiveAddressSelection")) {
                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("FindDriverDialogue")) {
                DashboardActivity activity = new DashboardActivity();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("AddressDriverTimeUpdate")) {
                DashboardActivity activity = new DashboardActivity();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("AttendanceTypeAddress")) {
                PlannerDashboard activity = new PlannerDashboard();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("ApprovalTypeAddress")) {
                ApprovalActivity activity = new ApprovalActivity();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("PlannerMaintananceAddress")) {
                MaintananceReqPlanner activity = new MaintananceReqPlanner();
                activity.showResponse(response.toString(), redirectKey);
            }else if (redirectKey.equalsIgnoreCase("NotificationReadUpdate")) {
                DashboardNotificationActivity activity = new DashboardNotificationActivity();
                activity.showResponse(response.toString(), redirectKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    public static void getAddressFromLatLong(final Context mContext, double LATITUDE, double LONGITUDE, final String redirectKey) {
        // Tag used to cancel the request
        TrackGPS gps = new TrackGPS(mContext);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
            return;
        }

        String tag_json_obj = "json_obj_req";
        final String[] fulladdress = {""};
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + LATITUDE + "," + LONGITUDE + "&sensor=true&key="+Utils.getPref(mContext.getResources().getString(R.string.map_api_key),mContext);
        Utils.LogI("API URL", url, mContext);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        response=null;
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("status", "1");
                            HashMap<String, String> hasInner = new HashMap<>();
                            hasInner.put("formatted_address", "NA");
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(new JSONObject(hasInner));
                            response = new JSONObject(hashMap);
                            response.put("results", jsonArray);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (redirectKey.equalsIgnoreCase("MovieSeat")) {
                                MovieSeat activity = new MovieSeat();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AttachDeAttachAddress")) {
                                DashboardActivity activity = new DashboardActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("LoginAddress")) {
                                LoginActivity activity = new LoginActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("OrderListAddress")) {
                                OrderList activity = new OrderList();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddress")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("attachmentlist")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddressException")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressUpdateStatus")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("LeaveRequestAddress")) {
                                RequestLeave activity = new RequestLeave();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("LeaveSubmitAddress")) {
                                RequestLeave activity = new RequestLeave();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("OrderListUpdateAddress")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("ScanAddress")) {
                                ScanActivity activity = new ScanActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("ScanAddressEx")) {
                                ScanActivity activity = new ScanActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("ScanAddressSubmit")) {
                                ScanActivity activity = new ScanActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("PhotoAddress")) {
                                PhotoUploadActivity activity = new PhotoUploadActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("PhotoAddressNew")) {
                                PhotoUploadNew activity = new PhotoUploadNew();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressPhoto")) {
                                PhotoUploadActivity activity = new PhotoUploadActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressPhotoNew")) {
                                PhotoUploadNew activity = new PhotoUploadNew();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressMessage")) {
                                MessageSubActivity activity = new MessageSubActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressSendMessage")) {
                                MessageSubActivity activity = new MessageSubActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressAssignJob")) {
                                AssignJobActivity activity = new AssignJobActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("DriverIncentiveAddress")) {
                                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressBillSubmit")) {
                                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("AddressQTY")) {
                                OrderDetail activity = new OrderDetail();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("MessageListAddress")) {
                                UserListActivity activity = new UserListActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("MessageMainListAddress")) {
                                MessageMainActivity activity = new MessageMainActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("OrderInnerAddressFiles")) {
                                UploadDownloadFiles activity = new UploadDownloadFiles();
                                activity.showResponse(response.toString(), redirectKey);
                            } else if (redirectKey.equalsIgnoreCase("DriverIncentiveAddressSelection")) {
                                DriverIncentiveActivity activity = new DriverIncentiveActivity();
                                activity.showResponse(response.toString(), redirectKey);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
*/

    /**
     * To get device consuming netowkr type is 2g,3g,4g
     *
     * @param context
     * @return "2g","3g","4g" as a String based on the network type
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //mobile
        NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();

        //wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
        String status = "";
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            //mobile
            status = "mobile";
        } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            //wifi
            status = "Wifi";
        }

        return status;
    }

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
