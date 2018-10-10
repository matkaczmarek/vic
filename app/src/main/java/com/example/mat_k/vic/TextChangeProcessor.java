package com.example.mat_k.vic;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.example.mat_k.vic.activities.CaloriesCounterActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Source;

public class TextChangeProcessor implements Detector.Processor<TextBlock> {

    public TextChangeProcessor(Activity activity) {
        this.activity = activity;
    }

    Activity activity;
    boolean found = false;

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        if(found)
            return;
        final SparseArray<TextBlock> items = detections.getDetectedItems();
        if (items.size() != 0 ){
            for (int i = 0; i < items.size(); i++) {
                TextBlock textBlock = items.valueAt(i);
                String text = textBlock.getValue();
                String[] split = text.split("\n");
                System.out.println(Arrays.toString(split));
                for (String s: split) {
                    Pattern p = Pattern.compile("(\\d+).*?kcal");
                    Matcher m = p.matcher(s);
                    m.find();
                    if(m.matches()) {
                        found = true;
                        activity.startActivity(new Intent(activity, CaloriesCounterActivity.class).putExtra("TEXT", m.group(0).split(" ")[0]));
                        return;
                    }
                }
            }
        }
    }
}
