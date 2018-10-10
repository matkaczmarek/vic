package com.example.mat_k.vic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mat_k.vic.CaloriesCounterData;
import com.example.mat_k.vic.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button textRecognitionButton;
    Button barcodeButton;
    static CaloriesCounterData caloriesCounterData;

    private static final int requestPermissionID = 101;
    private ProgressBar progressBar;
    private TextView textView;
    SharedPreferences shrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.progressView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(2500);
        checkCaloriesData();
        handleSharedPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkExtras();
        checkPermissions();
        addTextRecognizerListenerOnButton(this);
        addBarcodeListenerOnButton(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CaloriesCounterData.sumOfCal, caloriesCounterData.getSumOfCalories());
    }

    private void checkExtras(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey(CaloriesCounterData.sumOfCal)){
                caloriesCounterData = new CaloriesCounterData(extras.getInt(CaloriesCounterData.sumOfCal), Calendar.getInstance().get(Calendar.DAY_OF_WEEK), progressBar);
            }
        }
        progressBar.setProgress(0);
        progressBar.setProgress(caloriesCounterData.getSumOfCalories());
        textView.setText(String.format("%d/%d kcal", caloriesCounterData.getSumOfCalories(), progressBar.getMax()));
        shrd.edit().putInt(CaloriesCounterData.dateString, caloriesCounterData.getDate()).apply();
        shrd.edit().putInt(CaloriesCounterData.sumOfCal, caloriesCounterData.getSumOfCalories()).apply();
    }

    private void checkCaloriesData(){
        if(caloriesCounterData == null){
            caloriesCounterData = new CaloriesCounterData(0,Calendar.getInstance().get(Calendar.DAY_OF_WEEK),progressBar);
        }
    }

    private void handleSharedPreferences(){
        shrd = getPreferences(MODE_PRIVATE);
        if(caloriesCounterData.getDate() != shrd.getInt(CaloriesCounterData.dateString, 0)){
            caloriesCounterData.setSumOfCalories(0);
        }else {
            caloriesCounterData.setSumOfCalories(shrd.getInt(CaloriesCounterData.sumOfCal, 0));
        }
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    requestPermissionID);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    requestPermissionID + 1);
        }
    }

    public void addTextRecognizerListenerOnButton(final Activity activity) {
        textRecognitionButton = (Button) findViewById(R.id.textrecognition_button);

        textRecognitionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, TextRecognitionActivity.class));
            }

        });
    }

    public void addBarcodeListenerOnButton(final Activity activity) {
        barcodeButton = (Button) findViewById(R.id.barcode_button);

        barcodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, BarcodeRecognitionActivity.class));
            }

        });
    }
}