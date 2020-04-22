package com.example.ridecalculator;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalculateActivity extends AppCompatActivity {

    private TextView tv_distance, tv_fuelPrice, tv_result;
    private EditText tf_average;
    private double distance, avg, fuelPrice;
    private ProgressBar progressBar;
    private String cityName, stateName;
    private TextView tv_cityname;
    private RadioButton  rb_petrol, rb_diesel, rb_CNG;
    private String fuelType;
    private VideoView videoView;
    private Spinner spinnerCityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        tv_distance = findViewById(R.id.TV_distance);
        tv_fuelPrice = findViewById(R.id.TV_fuelPrice);
        tf_average = findViewById(R.id.TF_average);
        tv_result = findViewById(R.id.TV_result);
        progressBar = findViewById(R.id.progressBar);
        tv_cityname = findViewById(R.id.TV_cityName);
        /*videoView = (VideoView)findViewById(R.id.VehicleIV);
        String path = "android.resource://"+getPackageName()+"/"+R.raw.car_loop;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();

         */
        spinnerCityList = findViewById(R.id.cityList);

        /*
        1. average fuel consumption
        2. Distance -> getting from server
        3. Number of people
        4. City name ->
        5. Petrol Price ->
         */
        String d = getIntent().getStringExtra("distance");
        Log.d("dhere",d);
        //Converting the distance from string to double for calculation purpose
        if(d.indexOf(',') != -1) //to check if distance string contains (comma)
        {
            int index = d.indexOf(',');
            d = d.substring(0,index)+d.substring(index+1,d.length());
        }
        distance = Double.parseDouble(d.substring(0, d.length()-3));
        cityName = getIntent().getStringExtra("cityName");
        stateName = getIntent().getStringExtra("stateName");
        tv_cityname.setText(cityName);
        tv_distance.setText(d);


    }

    public void fetchPetrolPrice()
    {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<FuelPrice> call = apiService.fetchFuelPrice(cityName, stateName, fuelType);
        call.enqueue(new Callback<FuelPrice>() {
            @Override
            public void onResponse(Call<FuelPrice> call, Response<FuelPrice> response) {
                String responseStr = response.body().getResponse();
                Log.d("response1", responseStr);
                if(responseStr.equalsIgnoreCase("cityFound"))
                {
                    Log.d("cityName", "Found");
                    fuelPrice = response.body().getPrice();
                    progressBar.setVisibility(View.GONE);
                    tv_fuelPrice.setText("₹ "+fuelPrice+"");
                    Log.d("fuelPrice",fuelPrice+"");

                }
                else if(responseStr.equalsIgnoreCase("cityNotFound")){
                    Log.d("cityName","NotFound");
                    spinnerCityList.setVisibility(View.VISIBLE);
                    tv_cityname.setVisibility(View.GONE);
                    final ArrayList<FuelPrice> cityPriceList = response.body().getCityList();
                    ArrayList<String> cityList = new ArrayList<>();
                    for(int i=0;i<cityPriceList.size();i++)
                    {
                        String city = cityPriceList.get(i).getCity();
                        cityList.add(city);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, cityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCityList.setAdapter(adapter);
                    spinnerCityList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String cityName = parent.getItemAtPosition(position).toString();
                            Log.d("fetched city", cityName);

                            fuelPrice = 0;
                            for(int i = 0;i<cityPriceList.size();i++)
                            {
                                if(cityPriceList.get(i).getCity().equalsIgnoreCase(cityName))
                                {
                                    fuelPrice = cityPriceList.get(i).getPrice();
                                    break;
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            tv_fuelPrice.setText("₹ "+fuelPrice+"");
                            Log.d("fuelPrice",fuelPrice+"");


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else //state not found
                {
                    progressBar.setVisibility(View.GONE);
                    Log.d("state","NotFound");
                    Toast.makeText(getApplicationContext(), "CNG is not available in your state", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<FuelPrice> call, Throwable t) {

            }
        });
    }

    public void onRadioButtonClicked(View v){

        boolean isSelected = ((RadioButton)v).isChecked();
        switch (v.getId()){
            case R.id.RB_petrol: if(isSelected){
                fuelType = "Petrol";
                fetchPetrolPrice();

            }
            break;
            case R.id.RB_diesel: if(isSelected){
                fuelType = "Diesel";
                fetchPetrolPrice();

            }
                break;
            case R.id.RB_CNG: if(isSelected){
                fuelType = "CNG";
                fetchPetrolPrice();

            }
                break;
        }
    }

    public void onClick(View v){
        if(v.getId() == R.id.B_calculate){
            RadioButton rb1 = findViewById(R.id.RB_petrol);
            RadioButton rb2 = findViewById(R.id.RB_diesel);
            RadioButton rb3 = findViewById(R.id.RB_CNG);

            if(!rb1.isChecked() && !rb2.isChecked() && !rb3.isChecked())
            {
                Toast.makeText(getApplicationContext(), "Select fuel type", Toast.LENGTH_SHORT).show();
            }
            else if(tf_average.getText().toString().matches(""))
            {
                Toast.makeText(getApplicationContext(),"Enter vehicle average", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String a = tf_average.getText().toString();
                avg = Double.parseDouble(a);
                DecimalFormat df = new DecimalFormat("#.##");
                double lits = distance/avg;
                double cost = lits*fuelPrice;
                cost = Double.parseDouble(df.format(cost));

                tv_result.setText("₹ "+cost);
            }
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
                        String type = object.getString("type");
                        if(city.equalsIgnoreCase("Mumbai") && fuelType.equalsIgnoreCase(type)){

                            fuelPrice = object.getDouble("price");
                            progressBar.setVisibility(View.GONE);
                            tv_fuelPrice.setText("₹ "+fuelPrice+"");
                            Log.d("fuelPrice",fuelPrice+"");

                            String a = tf_average.getText().toString();
                            avg = Double.parseDouble(a);
                            DecimalFormat df = new DecimalFormat("#.##");
                            double lits = distance/avg;
                            double cost = lits*fuelPrice;
                            cost = Double.parseDouble(df.format(cost));

                            tv_result.setText("₹ "+cost);

                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
