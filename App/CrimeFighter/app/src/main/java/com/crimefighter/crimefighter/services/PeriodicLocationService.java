package com.crimefighter.crimefighter.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by anees on 3/26/2017.
 */

public class PeriodicLocationService extends IntentService {

    final String host = "71.171.96.88";
    final int portNumber = 914;

    // Must create a default constructor
    public PeriodicLocationService() {
        // Used to name the worker thread, important only for debugging.
        super("periodic-location-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SmartLocation.with(this).location()
                .oneFix()
                .start(
                        new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                new LocationUpdater().execute(location);
                                //Log.d("Got location", location.toString());
                            }
                        });
    }

    class LocationUpdater extends AsyncTask<Location, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground (Location...params){
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            String message = "";
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                String commandStr = "1," + getData("UserID") + "," + Double.toString(params[0].getLatitude()) + "," + Double.toString(params[0].getLongitude());
                oos.writeObject(commandStr);
                ois = new ObjectInputStream(socket.getInputStream());
                message = (String) ois.readObject();
                ois.close();
                oos.close();
                socket.close();
            } catch (ClassNotFoundException e) {
                return "errorcla";
            } catch (UnknownHostException e) {
                return "erroruhe";
            } catch (SocketTimeoutException e) {
                return "errorste";
            } catch (Exception e) {
                return "errorio";
            }
            return message;
        }

        @Override
        protected void onPostExecute (String msg){

        }
    }

    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getData(String key) {
        return getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).getString(key, "");
    }
}

