package com.example.ridecalculator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DataParser {

    public String getAddress(String jsonData)
    {
        String address = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for(int i = 0;i<jsonArray.length();i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d("types",object.getString("types"));
                if(object.getString("types").indexOf("\"route\"") != -1)
                {
                    address = object.getString("long_name");
                    break;
                }
            }
            Log.d("address",address);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address;

    }

    public String getStateName(String jsonData)
    {
        String stateName = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonData);
            Log.d("jsonData",jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for(int i = 0;i<jsonArray.length();i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d("types",object.getString("types"));
                if(object.getString("types").indexOf("\"administrative_area_level_1\"") != -1)
                {
                    stateName = object.getString("long_name");
                    break;
                }
            }
            Log.d("short_name",stateName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("stateName = ",stateName);

        return stateName;
    }

    public String getCityName(String jsonData)
    {
        String cityName = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            Log.d("jsonData",jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for(int i = 0;i<jsonArray.length();i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d("types",object.getString("types"));
                if(object.getString("types").indexOf("\"locality\"") != -1)
                {
                    cityName = object.getString("short_name");
                    break;
                }
            }
            Log.d("short_name",cityName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityName;
    }


    public HashMap<String, String> getDuration(String jsonData)
    {
        JSONArray googleDirectionJson = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            googleDirectionJson = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HashMap<String, String>  googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";

        try {
            duration = googleDirectionJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration", duration);
            googleDirectionsMap.put("distance", distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleDirectionsMap;

    }

    public String[] parseDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepsJson)
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];
        for(int i =0; i<count; i++){
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return polylines;
    }

    public String getPath(JSONObject googlePathsJson){

        String polyline = "";
        try {
            polyline = googlePathsJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
