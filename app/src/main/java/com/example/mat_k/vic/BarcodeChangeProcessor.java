package com.example.mat_k.vic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.example.mat_k.vic.activities.CaloriesCounterActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeChangeProcessor implements Detector.Processor<Barcode> {

    Activity activity;

    public BarcodeChangeProcessor(Activity activity){//View view){
        this.activity = activity;
    }

    boolean started = false;


    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> items = detections.getDetectedItems();
        if (items.size() != 0 ) {
            Barcode barcode = items.valueAt(0);
            if(!started) {
                started = true;
                Log.v("BARCODE", barcode.displayValue);
                activity.startActivity(new Intent(activity, CaloriesCounterActivity.class).putExtra("Barcode", barcode.displayValue));
            }
        }
    }
}
