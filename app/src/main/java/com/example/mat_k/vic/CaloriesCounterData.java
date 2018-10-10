package com.example.mat_k.vic;


import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;

public class CaloriesCounterData {
    ProgressBar progressBar;
    private int sumOfCalories;
    private int date = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    public static final String sumOfCal = "SUM_OF_CALORIES";
    public static final String dateString = "DATE";

    public CaloriesCounterData(int sumOfCalories, int date, ProgressBar progressBar) {
        this.sumOfCalories = sumOfCalories;
        this.date = date;
        this.progressBar = progressBar;
        System.out.println("DATE" + date);
    }

    public void setSumOfCalories(int sumOfCallories) {
        this.sumOfCalories = sumOfCallories;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getSumOfCalories() {
        return sumOfCalories;
    }

    public int getDate() {
        return date;
    }

    public void addCalories(int add){
        sumOfCalories += add;
        if(date != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            sumOfCalories = 0;
            add = 0;
        }
        progressBar.setProgress(add);
    }
}
