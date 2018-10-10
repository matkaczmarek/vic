package com.example.mat_k.vic.activities;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Surface;
import android.widget.FrameLayout;

import com.example.mat_k.vic.MySurfaceHolder;
import com.example.mat_k.vic.R;
import com.google.android.gms.vision.CameraSource;

public class BarcodeRecognitionActivity extends AppCompatActivity {

    private MySurfaceHolder mySurfaceHolder;
    private Camera camera;
    final public static String barcodeMode = "BARCODE_MODE";
    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        startCameraPreview(this, barcodeMode);
    }

    private void startCameraPreview(final Activity activity, String mode){
        int cameraId = findBackFacingCamera();
        int orientation = getCameraDisplayOrientation(activity,cameraId);

        camera = Camera.open(cameraId);
        mySurfaceHolder = new MySurfaceHolder(getApplicationContext(),camera,cameraSource, orientation, mode, this);
        mySurfaceHolder.setForegroundGravity(Gravity.CENTER);
        mySurfaceHolder.setZOrderOnTop(true);
        FrameLayout preview = (FrameLayout)  findViewById(R.id.camera_preview);
        preview.addView(mySurfaceHolder);
        //Snackbar.make(activity.getWindow().getDecorView().findViewById(android.R.id.content), "Found barcode: ", Snackbar.LENGTH_LONG).show();
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    public static int getCameraDisplayOrientation(Activity activity,
                                                  int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }
}
