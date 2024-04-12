package activities;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Classes.ModelReport;
import Classes.RecyclerAdapter;
import Classes.UtilFunctions;
import bhskinetic.idee.com.bhskinetic_new.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> listRepName;
    List<String> listRepValue;

    Spinner spinner;
    ImageView ivLoadUrl;
    WebView webView;
    TextView tvDate;
    ImageView ivBack;

    Calendar calendar;

    int mYear, mMonth, mDay;
    String startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UtilFunctions.showDialog(ReportActivity.this);
        setContentView(R.layout.activity_report);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(mToolbar);

        spinner = (Spinner) findViewById(R.id.spinner);
        ivLoadUrl = (ImageView) findViewById(R.id.ivGo);
        webView = (WebView) findViewById(R.id.webView);
        tvDate = (TextView) findViewById(R.id.tvDate);
        ivBack = (ImageView) findViewById(R.id.ivBack);


        Call<ModelReport> call = UtilFunctions.retroInterface.getReport("SG00iX", "Android",17);
        call.enqueue(new Callback<ModelReport>() {
            @Override
            public void onResponse(Call<ModelReport> call, Response<ModelReport> response) {
                listRepName = new ArrayList<String>();
                listRepValue = new ArrayList<String>();
                for (int i = 0; i < response.body().getData().size(); i++) {
                    listRepName.add(response.body().getData().get(i).getReportName());
                    listRepValue.add(response.body().getData().get(i).getReportValue());
                }
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(ReportActivity.this, android.R.layout.simple_spinner_item, listRepName);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterSpinner);
                UtilFunctions.hideDialog();
            }

            @Override
            public void onFailure(Call<ModelReport> call, Throwable t) {
                UtilFunctions.hideDialog();
                UtilFunctions.showAlert(ReportActivity.this, "Can't load data, please try again later");
                getSupportFragmentManager().popBackStackImmediate();
            }
        });

        ivLoadUrl.setOnClickListener(ReportActivity.this);
        tvDate.setOnClickListener(ReportActivity.this);
        ivBack.setOnClickListener(ReportActivity.this);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyyHH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        tvDate.setText(sdf1.format(date));
        startDate = sdf.format(date);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tvDate) {


            DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                    calendar.setTimeInMillis(0);
                    calendar.set(year, monthOfYear, dayOfMonth);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyyHH:mm");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
                    tvDate.setText(sdf1.format(date));
                    startDate = sdf.format(date);
                }
            }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }

        if (view.getId() == R.id.ivBack) {

            super.onBackPressed();
        }
        if (view.getId() == R.id.ivGo) {

            String repVal = null;
            String assetNum = null;
            for (int i = 0; i < listRepName.size(); i++) {
                if (spinner.getSelectedItem().toString().equalsIgnoreCase(listRepName.get(i))) {
                    repVal = listRepValue.get(i);
                }
            }

            Bundle b = getIntent().getExtras();
            if (b != null) {
                assetNum = b.getString(RecyclerAdapter.KEY_VEH_NUM);
            }

            Log.i("url info : ", Integer.parseInt(VtrakLoginActivity.sharedPreferences.getString(VtrakLoginActivity.KEY_RESULT_MSG, null)) + " " + repVal + " " + assetNum + " " + startDate);
            //String url = "http://203.125.153.221/tt_tsg/Mo_Reports.jsp?Str_ID=SG00iX&Str_Model=Android&Str_UserID=17&Str_Rpt_Name=Location&Str_AssetNo=GBE8185Y&Str_DT_S=07-Nov-201700:00&Str_DT_E=07-Nov-201723:59";
                          //http://203.125.153.221/tt_tsg/Mo_RptList.jsp?Str_ID=SG00iX&Str_Model=Android&Str_UserID=17
            String url = UtilFunctions.BASE_URL+"/Mo_Reports.jsp?Str_ID=SG00iX&Str_Model=Android&Str_UserID=" + Integer.parseInt(VtrakLoginActivity.sharedPreferences.getString(VtrakLoginActivity.KEY_RESULT_MSG, null)) + "&Str_Rpt_Name=" + repVal + "&Str_AssetNo=" + assetNum + "&Str_DT_S=" + tvDate.getText() + "&Str_DT_E=07-Nov-201723:59";
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().getBuiltInZoomControls();
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    UtilFunctions.showDialog(ReportActivity.this);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    UtilFunctions.hideDialog();
                    super.onPageFinished(view, url);
                }
            });
            /*webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == keyEvent.KEYCODE_BACK && webView.canGoBack()) {
                            webView.goBack();
                        }
                    } else {
                        finish();
                    }
                    return true;
                }
            });*/
            webView.loadUrl(url);
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else
            super.onBackPressed();
    }
}