package bhskinetic.idee.com.bhskinetic_new;

/**
 * Created by Admin on 7/5/2018.
 */

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import general.Utils;

class PreviewWithoutSign extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    MySurfaceViewWithoutSignature mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    PreviewWithoutSign(Context context, MySurfaceViewWithoutSignature sv) {
        super(context);

        mSurfaceView = sv;
//        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            // get Camera parameters
            Camera.Parameters params = mCamera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters
                mCamera.setParameters(params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    // if (Build.MODEL.equalsIgnoreCase("moto g(9)")) {

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera != null) {
            if (Build.MODEL.trim().contains("SM-J")) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                requestLayout();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } else {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                // You need to choose the most appropriate previewSize for your app
                 Toast.makeText(getContext(), Build.MODEL,Toast.LENGTH_SHORT).show();


                if(Utils.getPref(getContext().getString(R.string.pref_cam_option),getContext()).equalsIgnoreCase("0")) {
                    if (Build.MODEL.equalsIgnoreCase("SM-N960F")) {
                        parameters.setPreviewSize(previewSizes.get(1).width, previewSizes.get(1).height);
                    } else if (Build.MODEL.equalsIgnoreCase("SM-G965F")) {
                        parameters.setPreviewSize(previewSizes.get(1).width, previewSizes.get(1).height);
                    } else if (Build.MODEL.equalsIgnoreCase("vivo 1902")) {
                        parameters.setPreviewSize(previewSizes.get(1).width, previewSizes.get(1).height);
                    } else if (Build.MODEL.equalsIgnoreCase("moto g(9)")) {
                        parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            parameters.setPreviewSize(previewSizes.get(2).width, previewSizes.get(2).height);
                        } else {
                            parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
                        }

                    }
                }else{

                    String camOption=Utils.getPref(getContext().getString(R.string.pref_cam_option),getContext());
                    int option=0;
                    if(camOption.equalsIgnoreCase("Camera Option 0")){
                        option=0;
                    }else if(camOption.equalsIgnoreCase("Camera Option 1")){
                        option=1;
                    }else if(camOption.equalsIgnoreCase("Camera Option 2")){
                        option=2;
                    }else if(camOption.equalsIgnoreCase("Camera Option 3")){
                        option=3;
                    }else if(camOption.equalsIgnoreCase("Camera Option 4")){
                        option=4;
                    }else if(camOption.equalsIgnoreCase("Camera Option 5")){
                        option=5;
                    }
                    parameters.setPreviewSize(previewSizes.get(option).width, previewSizes.get(option).height);

                }
                requestLayout();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }

            mSurfaceView.setZOrderMediaOverlay(true);

        }
    }

}