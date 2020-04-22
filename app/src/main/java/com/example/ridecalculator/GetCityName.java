package com.example.ridecalculator;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetCityName extends AsyncTask<Object, String, String> {


    GoogleMap mMap;
    String url;
    static String cityName;
    LatLng latLng;
    static String address;
    static String stateName;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            cityName = downloadUrl.readUrl(url);
            Log.d("googleCityData",cityName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    protected void onPostExecute(String s) {


        DataParser parser = new DataParser();

        cityName = parser.getCityName(s);
        address = parser.getAddress(s);
        stateName = parser.getStateName(s);


    }



}
