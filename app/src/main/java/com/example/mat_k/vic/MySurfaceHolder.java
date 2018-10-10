package com.example.mat_k.vic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.example.mat_k.vic.activities.BarcodeRecognitionActivity;
import com.example.mat_k.vic.activities.TextRecognitionActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by mat_k on 14.08.2018.
 */

public class MySurfaceHolder extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    private CameraSource cameraSource;
    private Activity activity;
    TextRecognizer textRecognizer;
    BarcodeDetector barcodeDetector;
    String mode;
    int orientation;

    public MySurfaceHolder(Context context, Camera camera, CameraSource cameraSource, int orientation, String mode, Activity activity){
        super(context);
        this.cameraSource = cameraSource;
        this.mContext = context;
        this.mCamera = camera;
        this.orientation = orientation;
        this.mode = mode;
        this.activity = activity;
        this.mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        if(Objects.equals(mode, TextRecognitionActivity.textRecognitionMode))
            this.textRecognizer = new TextRecognizer.Builder(mContext).build();

        if(Objects.equals(mode, BarcodeRecognitionActivity.barcodeMode))
            this.barcodeDetector = new BarcodeDetector.Builder(mContext).build();

        mHolder = getHolder();
        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressLint("MissingPermission")
    public void surfaceCreated(SurfaceHolder holder) {
        if(cameraSource != null){
            cameraSource.stop();
        }

        try {
            if(Objects.equals(mode, TextRecognitionActivity.textRecognitionMode)) {
                startTextRecognizer();
            }else if(Objects.equals(mode, BarcodeRecognitionActivity.barcodeMode)){
                startBarcodeRecognizer();
            }
        } catch (Exception e){
            Log.e("ERR", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }

        if(cameraSource != null){
            cameraSource.stop();
        }

        try {
            if(Objects.equals(mode, TextRecognitionActivity.textRecognitionMode)) {
                startTextRecognizer();
            }else if(Objects.equals(mode, BarcodeRecognitionActivity.barcodeMode)){
                startBarcodeRecognizer();
            }
        } catch (Exception e){
            Log.e("ERR", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        if (mPreviewSize!=null) {
            float ratio;
            if(mPreviewSize.height >= mPreviewSize.width)
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
            else
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

            if(orientation == 90 || orientation == 270) { //Portrait
                setMeasuredDimension(width, (int) (width * ratio));
            }else {
                setMeasuredDimension(width, (int) ((width/ratio)));
            }
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    @SuppressLint("MissingPermission")
    private void startTextRecognizer() throws IOException {
        textRecognizer.setProcessor(new TextChangeProcessor(activity));
        cameraSource = new CameraSource.Builder(mContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(mPreviewSize.width,mPreviewSize.height)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
                .build();
        cameraSource.start(mHolder);
    }

    @SuppressLint("MissingPermission")
    private void startBarcodeRecognizer() throws IOException {
        barcodeDetector.setProcessor(new BarcodeChangeProcessor(activity));
        cameraSource = new CameraSource.Builder(mContext, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(mPreviewSize.width,mPreviewSize.height)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
                .build();
        cameraSource.start(mHolder);
    }
}