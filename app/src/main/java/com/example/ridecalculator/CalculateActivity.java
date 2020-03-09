package com.example.ridecalculator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class CalculateActivity extends AppCompatActivity {

    private TextView tv_distance, tv_petrolprice, tv_result;
    private EditText tf_average;
    private double distance, avg, petrolPrice;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        tv_distance = findViewById(R.id.TV_distance);
        tv_petrolprice = findViewById(R.id.TV_petrol_price);
        tf_average = findViewById(R.id.TF_average);
        tv_result = findViewById(R.id.TV_result);
        progressBar = findViewById(R.id.progressBar);

        String d = getIntent().getStringExtra("distance");
        distance = Double.parseDouble(d.substring(0, d.length()-3));
        tv_distance.setText(d);
        getJSON("https://polyphyodont-bets.000webhostapp.com/fetch_petrol_price.php");
        Log.d("petrolprice",petrolPrice+"");


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

    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("here",s);
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for(int i=0; i<jsonArray.length();i++){

                        JSONObject object = jsonArray.getJSONObject(i);
                        String city = object.getString("city");
                        if(city.equalsIgnoreCase("Mumbai")){

                            petrolPrice = object.getDouble("price");
                            progressBar.setVisibility(View.GONE);
                            tv_petrolprice.setText(petrolPrice+"");

                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader;
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        Log.d("json",json);
                        sb.append(json + "\n");
                    }


                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }



}
