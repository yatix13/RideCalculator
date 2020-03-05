package com.example.ridecalculator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.ProgressBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ServerRequest {
    ProgressDialog progressDialog;
    public static final String ServerAddress = "https://polyphyodont-bets.000webhostapp.com/";

    public ServerRequest(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Data...");
        progressDialog.setMessage("Please Wait...");
    }
    public void fetchDataInBackground(PetrolPrice petrolPrice, GetUserCallback callback) {

        progressDialog.show();
    }

    public class FetchDataAsyncTask extends AsyncTask<Void, Void, PetrolPrice>{
        PetrolPrice petrolPrice;
        GetUserCallback callback;

        public FetchDataAsyncTask(PetrolPrice petrolPrice, GetUserCallback callback){

            this.petrolPrice = petrolPrice;
            this.callback = callback;
        }

        @Override
        protected PetrolPrice doInBackground(Void... voids) {
            try {
                URL url= new URL(ServerAddress);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }
    }
}


