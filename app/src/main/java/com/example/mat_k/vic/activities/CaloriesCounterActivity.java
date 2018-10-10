package com.example.mat_k.vic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mat_k.vic.CaloriesCounterData;
import com.example.mat_k.vic.R;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class CaloriesCounterActivity extends AppCompatActivity {
   Button menuButton;
   Button addButton;
   EditText weightText;
   TextView kcal;

   String barcode = null;
   int callories = 0;
   int content = -1;
   boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calories_layout);
        kcal = findViewById(R.id.kcal_view);
        Bundle extras = getIntent().getExtras();

        if(savedInstanceState != null){
            callories = savedInstanceState.getInt("Calories");
            content = savedInstanceState.getInt("Barcode");
            barcode = savedInstanceState.getString("Barcode");
            found = savedInstanceState.getBoolean("Found");

            if(barcode != null && found)
                setContentVisible();
        }

        if(extras != null && barcode == null){
            if(extras.containsKey("Barcode")){
                Log.v("BARCODE", extras.getString("Barcode"));
                barcode = extras.getString("Barcode");
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "https://world.openfoodfacts.org/api/v0/product/" + extras.get("Barcode") + ".json";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("RESPONSE", response);
                                try {
                                    JSONObject Jresponse = new JSONObject(response);
                                    if(Objects.equals(Jresponse.getString("status"), "1")){
                                        Toast.makeText(getApplicationContext(), "Product found.", Toast.LENGTH_SHORT).show();
                                        JSONObject product = Jresponse.getJSONObject("product");
                                        JSONObject nutriments = product.getJSONObject("nutriments");
                                        String energy = nutriments.getString("energy_100g");
                                        callories = (int) (Integer.parseInt(energy)/4.184);
                                        found = true;
                                        setContentVisible();
                                        Log.v("energy", energy);
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Product not in Base.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Couldn't parse product. Try Again", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("ErrorResponse", "ERR");
                                Toast.makeText(getApplicationContext(), "Couldn't connect to Base.", Toast.LENGTH_LONG).show();
                            }
                });

                requestQueue.add(stringRequest);
            } else if(extras.containsKey("TEXT")){
                try {
                    callories = Integer.parseInt(extras.getString("TEXT"));
                    barcode = "";
                    found = true;
                    setContentVisible();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Unable to parse ingredients", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addListenerOnMenuButton(this);
        addListenerOnAddButton(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("Calories", callories);
        outState.putInt("Content", content);
        outState.putString("Barcode", barcode);
        outState.putBoolean("Found", found);
    }

    public void addListenerOnMenuButton(final Activity activity) {
        menuButton = (Button) findViewById(R.id.menu_button);

        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(CaloriesCounterActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(CaloriesCounterData.sumOfCal,MainActivity.caloriesCounterData.getSumOfCalories()));
                finish();
            }

        });
    }

    public void addListenerOnAddButton(final Activity activity) {
        addButton = (Button) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            if(content == -1){
                Toast.makeText(getApplicationContext(), "Type in weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity.caloriesCounterData.addCalories((callories*content)/100);
            Toast.makeText(getApplicationContext(), (callories*content)/100 + " kcal added.",Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setContentVisible(){
        findViewById(R.id.add_button).setVisibility(View.VISIBLE);
        weightText= findViewById(R.id.weight_text);
        weightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Objects.equals(weightText.getText().toString(), "")) {
                    kcal.setVisibility(View.INVISIBLE);
                    content = -1;
                    return;
                }

                content = Integer.parseInt(weightText.getText().toString());
                int temp = (callories * content)/100;
                kcal.setText(String.format("That will be %d kcal", temp));
                kcal.setVisibility(View.VISIBLE);
            }
        });
        weightText.setVisibility(View.VISIBLE);
        findViewById(R.id.gramms).setVisibility(View.VISIBLE);
    }
}
