package bhskinetic.idee.com.bhskinetic_new;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;

import general.Utils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Aamir on 4/19/2017.
 */

public class ScanActivityDefault extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private ImageView imgFlash;
    private boolean isFlashOn=false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.scannerview_fullscreen);
        mScannerView = findViewById(R.id.mScannerView);
        imgFlash=findViewById(R.id.img_flash);
        imgFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    isFlashOn=false;
                    imgFlash.setImageResource(R.drawable.ic_flash_off);
                    mScannerView.setFlash(false);
                }else{
                    isFlashOn=true;
                    imgFlash.setImageResource(R.drawable.ic_flash_on);
                    mScannerView.setFlash(true);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.i("SCAN_RESULT", rawResult.getText()); // Prints scan results
        Log.i("SCAN_RESULT", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Utils.ScanResult=rawResult.getText();
        ScanActivityDefault.this.finish();
      //  mScannerView.resumeCameraPreview(this);
    }
}