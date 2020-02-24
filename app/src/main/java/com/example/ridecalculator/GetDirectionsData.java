package com.example.ridecalculator;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object, String, String> {


    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            googleDirectionsData = downloadUrl.readUrl(url);
            Log.d("googleDirectionsData",googleDirectionsData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String , String> durationDistance;

        String[] directionsList;
        DataParser parser = new DataParser();

        //durationDistance = parser.getDuration(s);
        directionsList = parser.parseDirections(s);
        Log.d("directionsList",directionsList.toString());
        displayDirections(directionsList);
    }

    public void displayDirections(String[] directionsList){

        int count = directionsList.length;
        for(int i=0; i<count; i++){

            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(options);
        }
    }





}
