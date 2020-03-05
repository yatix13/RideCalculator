package com.example.ridecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class CalculateActivity extends AppCompatActivity {

    private TextView tv_distance, tv_petrolprice, tv_result;
    private EditText tf_average;
    private double distance, avg, petrolPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        tv_distance = findViewById(R.id.TV_distance);
        tv_petrolprice = findViewById(R.id.TV_petrol_price);
        tf_average = findViewById(R.id.TF_average);
        tv_result = findViewById(R.id.TV_result);

        String d = getIntent().getStringExtra("distance");
        distance = Double.parseDouble(d.substring(0, d.length()-3));
        petrolPrice = 80.53;
        tv_distance.setText(d);
        tv_petrolprice.setText(petrolPrice+"");

    }

    public void onClick(View v){
        if(v.getId() == R.id.B_calculate){
            String a = tf_average.getText().toString();
            avg = Double.parseDouble(a);
            DecimalFormat df = new DecimalFormat("#.##");
            double lits = distance/avg;
            double cost = lits*petrolPrice;
            cost = Double.parseDouble(df.format(cost));

            tv_result.setText("Trip cost = ₹ "+cost);
        }

    }



}
