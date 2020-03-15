package com.example.ridecalculator;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    LatLng latLng;
    ProgressBar progressBar;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

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

        durationDistance = parser.getDuration(s);
        directionsList = parser.parseDirections(s);
        distance = durationDistance.get("distance");
        Log.d("distance 1",distance);

        MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);
        mo.title("Destination");
        mo.snippet("Distance = "+ durationDistance.get("distance")+" & "+"Duration = "+durationDistance.get("duration"));
        Marker marker = mMap.addMarker(mo);
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


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
