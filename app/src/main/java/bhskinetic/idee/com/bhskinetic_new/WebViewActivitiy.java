package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;


/**
 * Created by Aamir on 4/18/2017.
 */

public class WebViewActivitiy extends Activity {
    private TextView tv_header;
    private ImageView img_logout;
    private ImageView img_refresh;
    private String isGoogleDrive="1";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);
        Init();
    }

    private void Init() {
        WebView webview = (WebView) findViewById(R.id.web_document);
        webview.getSettings().setJavaScriptEnabled(true);
        Bundle b=getIntent().getExtras();
        String pdf = "";
        String title="";
        if(b!=null){
            pdf=b.getString("DocumentURL");
            title=b.getString("DocumentTitle");
            if(b.getString("isGoogleDrive")!=null){
                isGoogleDrive=b.getString("isGoogleDrive");
            }
        }

        if(isGoogleDrive.equalsIgnoreCase("0")){
            webview.loadUrl(pdf);
        }else {
            webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
        }
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        tv_header=(TextView)findViewById(R.id.tv_header);
        img_logout=(ImageView)findViewById(R.id.img_logout);
        img_refresh=(ImageView)findViewById(R.id.img_refresh);
        img_refresh.setVisibility(View.GONE);
        img_logout.setVisibility(View.GONE);
        tv_header.setText(title);

        img_refresh.setVisibility(View.VISIBLE);
        img_refresh.setImageResource(R.drawable.ic_back);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
