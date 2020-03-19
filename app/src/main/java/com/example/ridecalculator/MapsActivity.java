package com.example.ridecalculator;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.internal.impl.net.pablo.FindAutocompletePredictionsPabloResponse;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private SearchView searchBar;
    private FloatingSearchView materialSearchBar;
    private FloatingSearchView fromSearchBar;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private List<AutocompletePrediction> predictionList;
    private Location lastLocation;
    private Button searchButton;
    private Marker currentLocationMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<String> suggestionList;
    private List<SearchSuggestion> suggestionList2;
    private final int REQUEST_LOCATION_CODE = 99;
    private double endLatitude, endLongitude, startLatitude, startLongitude;
    private String searchResult;
    private double distance;
    private String cityName;
    private GetDirectionsData directionsData;
    private String fromSuggestion, toSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchButton = findViewById(R.id.B_search);

        //searchBar = findViewById(R.id.search_bar);
        materialSearchBar = findViewById(R.id.toSearchBar);
        fromSearchBar = findViewById(R.id.fromSearchBar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        Places.initialize(MapsActivity.this, "AIzaSyBRYvFByo5BOJ7PvJqdmNSI4oSj1WZ56RM");

        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        final PlacesClient placesClient = Places.createClient(MapsActivity.this);


        /*
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountry("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                Log.d("autocomplete request", request.toString());

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse response = task.getResult();
                            Log.d("suggestionResponse", response.toString());
                            if(response != null){
                                predictionList = response.getAutocompletePredictions();
                                suggestionList = new ArrayList<>();
                                for(int i=0; i<predictionList.size(); i++){

                                    AutocompletePrediction prediction = predictionList.get(i);
                                    String one = prediction.getFullText(null).toString();
                                    suggestionList.add(one);
                                }
                                materialSearchBar.updateLastSuggestions(suggestionList);
                                if(!materialSearchBar.isSuggestionsVisible())
                                {
                                    materialSearchBar.showSuggestionsList();
                                }

                            }
                        }
                        else{
                            Log.d("error","Auto complete request fail");
                        }
                    }
                });



                materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                    @Override
                    public void OnItemClickListener(int position, View v) {
                        if(position > suggestionList.size())
                            return;
                        AutocompletePrediction selectedPrediction = predictionList.get(position);
                        String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                        materialSearchBar.clearSuggestions();
                        materialSearchBar.hideSuggestionsList();
                        materialSearchBar.setText(suggestion);
                        searchResult = suggestion;

                        hideKeyboard(MapsActivity.this, v);
                    }

                    @Override
                    public void OnItemDeleteListener(int position, View v) {

                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


         */
        materialSearchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountry("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(newQuery)
                        .build();
                Log.d("autocomplete request", request.toString());

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse response = task.getResult();
                            Log.d("suggestionResponse", response.toString());
                            if(response != null){
                                predictionList = response.getAutocompletePredictions();
                                suggestionList2 = new ArrayList<>();
                                for(int i=0; i<predictionList.size(); i++){

                                    AutocompletePrediction prediction = predictionList.get(i);
                                    final String one = prediction.getFullText(null).toString();
                                    SearchSuggestion ss = new SearchSuggestion() {
                                        @Override
                                        public String getBody() {
                                            return one;
                                        }

                                        @Override
                                        public int describeContents() {
                                            return 0;
                                        }

                                        @Override
                                        public void writeToParcel(Parcel dest, int flags) {

                                        }
                                    };
                                    suggestionList2.add(ss);
                                }

                                materialSearchBar.swapSuggestions(suggestionList2);


                            }
                        }
                        else{
                            Log.d("error","Auto complete request fail");
                        }
                    }
                });

                materialSearchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
                    @Override
                    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                        String suggestion = searchSuggestion.getBody();
                        toSuggestion = suggestion;
                        Log.d("suggestion",toSuggestion);
                        materialSearchBar.setSearchText(toSuggestion);
                        //fromSearchBar.clearSuggestions();
                        searchResult = suggestion;

                        hideKeyboard(MapsActivity.this, materialSearchBar);
                    }

                    @Override
                    public void onSearchAction(String currentQuery) {

                    }
                });


            }
        });
        fromSearchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountry("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(newQuery)
                        .build();
                Log.d("autocomplete request", request.toString());

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse response = task.getResult();
                            Log.d("suggestionResponse", response.toString());
                            if(response != null){
                                predictionList = response.getAutocompletePredictions();
                                suggestionList2 = new ArrayList<>();
                                for(int i=0; i<predictionList.size(); i++){

                                    AutocompletePrediction prediction = predictionList.get(i);
                                    final String one = prediction.getFullText(null).toString();
                                    SearchSuggestion ss = new SearchSuggestion() {
                                        @Override
                                        public String getBody() {
                                            return one;
                                        }

                                        @Override
                                        public int describeContents() {
                                            return 0;
                                        }

                                        @Override
                                        public void writeToParcel(Parcel dest, int flags) {

                                        }
                                    };
                                    suggestionList2.add(ss);
                                }

                                fromSearchBar.swapSuggestions(suggestionList2);


                            }
                        }
                        else{
                            Log.d("error","Auto complete request fail");
                        }
                    }
                });

                fromSearchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
                    @Override
                    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                        String suggestion = searchSuggestion.getBody();
                        fromSuggestion = suggestion;
                        Log.d("suggestion",fromSuggestion);
                        fromSearchBar.setSearchText(fromSuggestion);
                        //fromSearchBar.clearSuggestions();
                        searchResult = suggestion;

                        hideKeyboard(MapsActivity.this, fromSearchBar);
                    }

                    @Override
                    public void onSearchAction(String currentQuery) {

                    }
                });


            }
        });

        /*
        fromSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountry("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                Log.d("autocomplete request", request.toString());

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse response = task.getResult();
                            Log.d("suggestionResponse", response.toString());
                            if(response != null){
                                predictionList = response.getAutocompletePredictions();
                                suggestionList = new ArrayList<>();
                                for(int i=0; i<predictionList.size(); i++){

                                    AutocompletePrediction prediction = predictionList.get(i);
                                    String one = prediction.getFullText(null).toString();
                                    suggestionList.add(one);
                                }
                                fromSearchBar.updateLastSuggestions(suggestionList);
                                if(!fromSearchBar.isSuggestionsVisible())
                                {
                                    fromSearchBar.showSuggestionsList();
                                }

                            }
                        }
                        else{
                            Log.d("error","Auto complete request fail");
                        }
                    }
                });



                fromSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                    @Override
                    public void OnItemClickListener(int position, View v) {
                        if(position > suggestionList.size())
                            return;
                        fromSearchBar.hideSuggestionsList();

                        AutocompletePrediction selectedPrediction = predictionList.get(position);
                        String suggestion = fromSearchBar.getLastSuggestions().get(position).toString();
                        fromSearchBar.setText(suggestion);
                        fromSearchBar.clearSuggestions();
                        fromSearchBar.hideSuggestionsList();
                        searchResult = suggestion;

                        hideKeyboard(MapsActivity.this, v);
                    }

                    @Override
                    public void OnItemDeleteListener(int position, View v) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

         */
    }

    public void hideKeyboard(Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    private String getDirectionsUrl(){
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+startLatitude+","+startLongitude);
        googleDirectionsUrl.append("&destination="+endLatitude+","+endLongitude);
        googleDirectionsUrl.append("&key="+"AIzaSyBRYvFByo5BOJ7PvJqdmNSI4oSj1WZ56RM");
        Log.d("directions url = ",googleDirectionsUrl.toString());
        return googleDirectionsUrl.toString();
    }

    public void onOKClick(View v){

        if(v.getId()==R.id.B_ok){
            Intent intent = new Intent(MapsActivity.this, CalculateActivity.class);
            intent.putExtra("distance", directionsData.distance);
            intent.putExtra("cityName", GetCityName.cityName);
            startActivity(intent);
        }
    }
    public void onClick(View v){
        Object dataTransfer[]= new Object[3];
        directionsData = new GetDirectionsData();

        if(v.getId() == R.id.B_search){

            String toLocation = toSuggestion;
            String fromLocation = fromSuggestion;
            List<Address> addressList=null;
            List<Address> fromAddressList = null;
            MarkerOptions mo = new MarkerOptions();
            LatLng latlng;

            if(!toLocation.equals("")){
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(toLocation, 5);
                }catch(IOException e){
                    e.printStackTrace();
                }


                for(int i=0; i<addressList.size(); i++)
                {
                    Address myAddress = addressList.get(i);
                    latlng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());


                    mo.position(latlng);
                    mo.title("Your search results");
                    //mMap.addMarker(mo);mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                }

                endLatitude = addressList.get(0).getLatitude();
                endLongitude = addressList.get(0).getLongitude();

                LatLng fromlatlng = null;
                if(!fromLocation.equals(""))
                {
                    if(!fromLocation.equalsIgnoreCase("current location"))
                    {
                        try {
                            fromAddressList = geocoder.getFromLocationName(fromLocation, 5);
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        for(int i=0; i<fromAddressList.size(); i++)
                        {
                            Address myAddress = fromAddressList.get(i);
                            fromlatlng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());


                            mo.position(fromlatlng);
                            mo.title("Your search results");
                            //mMap.addMarker(mo);mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                        }

                        startLatitude = fromAddressList.get(0).getLatitude();
                        startLongitude = fromAddressList.get(0).getLongitude();
                    }

                }

                float results[] = new float[10];
                Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
                fetchCityNameFromLatLng(fromlatlng);

                latlng = new LatLng(endLatitude, endLongitude);

                mo.position(latlng);


                //mMap.addMarker(mo);
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));


                dataTransfer[0] = mMap;
                dataTransfer[1] = getDirectionsUrl();
                dataTransfer[2] = latlng;


                directionsData.execute(dataTransfer);

                final Button B_OK = findViewById(R.id.B_ok);

                Activity act = (Activity)this;
                act.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        B_OK.setVisibility(View.VISIBLE);

                    } });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_LOCATION_CODE:if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //pemission granted
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(client == null){
                        buildGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } //permission denied
            else
            {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();

            }
            return;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //hey call nahi karat app run hotay na phone var.. ithech bol .. mic on discod var y rokkar
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
    }

    protected synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
        //created clients and connected them to GP services.
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if(currentLocationMarker != null)//created locationMarker object and check if it's null,if not remove marker.
        {
            currentLocationMarker.remove();

        }

        startLatitude = location.getLatitude();
        startLongitude = location.getLongitude();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String startLocation = startLatitude+","+startLongitude;
        //fromSearchBar.setText("Current Location");

        //fetching city name
        //fetchCityNameFromLatLng(latLng);



        MarkerOptions markerOptions = new MarkerOptions();//to set properties of marker.
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);//added marker on map and passed properties.

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }


    }

    private String getCityNameUrl(){
        StringBuilder googleCityNameUrl = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?");
        googleCityNameUrl.append("latlng="+startLatitude+","+startLongitude);
        googleCityNameUrl.append("&key="+"AIzaSyBRYvFByo5BOJ7PvJqdmNSI4oSj1WZ56RM");
        Log.d("city name url = ",googleCityNameUrl.toString());
        return googleCityNameUrl.toString();
    }

    public void fetchCityNameFromLatLng(LatLng latlng)
    {
        GetCityName getCityName = new GetCityName();
        Object dataTransfer[] = new Object[3];
        dataTransfer[0] = mMap;
        dataTransfer[1] = getCityNameUrl();
        dataTransfer[2] = latlng;

        getCityName.execute(dataTransfer);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** TODO : fix this -> from search bar shows lat lang, instead it should show address -> this is causing error
        String address = GetCityName.address;
        fromSearchBar.setText(address);
        Log.d("addresshere",address);
        **/

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(1000);
    locationRequest.setFastestInterval(1000);
    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//created object and set interval and priority.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this); //calling requestlocationupdate menthod and passing values.


        }

    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }

            return false;
        }
        else return true;
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
