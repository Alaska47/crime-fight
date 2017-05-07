package com.crimefighter.crimefighter.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.utils.UnCaughtException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class AnalyticsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_MAP = 1337;
    private static Location userLoc;
    private MapView mMapView;
    private static GoogleMap mMap;
    private Bundle mBundle;

    final String host = "71.171.96.88";
    final int portNumber = 914;

    private static ArrayList<Double[]> dubs = new ArrayList<>();

    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Log.e("mapview", "", e);
        }

        Typeface mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        Button mReportButton = (Button) findViewById(R.id.report_button1234);
        mReportButton.setTypeface(mTypeface);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        mMapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


      /*
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      } else {
          mMap.setMyLocationEnabled(true);
      }
      */

        Log.d("DashboardFragment", "map ready");

        if(checkPermission()) {
            Log.d("Dash", "good");
            SmartLocation.with(this).location()
                    .oneFix()
                    .start(
                            new OnLocationUpdatedListener() {
                                @Override
                                public void onLocationUpdated(Location location) {
                                    userLoc = location;
                                    //Log.d("Got location", location.toString());
                                }
                            });

            new Thread(
                    new Runnable() {
                        public void run() {
                            long startTime = System.currentTimeMillis();
                            while (userLoc == null) {
                                try {
                                    Thread.sleep(100);
                                    if(System.currentTimeMillis() - startTime > 5000) {
                                        userLoc = new Location("");
                                        userLoc.setLatitude( -8.783195);
                                        userLoc.setLongitude(-124.508523);
                                        break;
                                    }
                                    Log.d("WatchActivity", "searching");

                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            LatLng newLatLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
                            LatLngBounds bounds = new LatLngBounds.Builder().
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 0)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 90)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 180)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 270)).build();
                            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                            String fin = "";
                            try {
                                fin = new MapItemsRetriever().execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            String[] fin1 = fin.split(",");
                            for(int i = 0; i < fin1.length; i+= 2) {
                                Double[] d = new Double[2];
                                d[0] = Double.parseDouble(fin1[i]);
                                d[1] = Double.parseDouble(fin1[i+1]);
                                dubs.add(d);
                            }
                            final BitmapDescriptor currentLocation = BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.current_location), 0.4f));
                            final BitmapDescriptor redPin = BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.red_pin), 0.4f));
                            runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            mMap.moveCamera(cameraUpdate);
                                            mMap.moveCamera(cameraUpdate);
                                            Marker home = mMap.addMarker(new MarkerOptions().position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude())).icon(currentLocation));
                                            for(Double[] a : dubs) {
                                                mMap.addMarker(new MarkerOptions().position(new LatLng(a[0], a[1])).icon(redPin));
                                            }
                                        }
                                    });
                        }
                    }).start();
        }
        else {

            new Thread(
                    new Runnable() {
                        public void run() {

                            userLoc = new Location("");
                            userLoc.setLatitude( -8.783195);
                            userLoc.setLongitude(-124.508523);

                            LatLng newLatLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
                            LatLngBounds bounds = new LatLngBounds.Builder().
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 0)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 90)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 180)).
                                    include(SphericalUtil.computeOffset(newLatLng, 1.5 * 1609.344d, 270)).build();
                            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                            String fin = "";
                            try {
                                fin = new MapItemsRetriever().execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            String[] fin1 = fin.split(",");
                            for(int i = 0; i < fin1.length; i+= 2) {
                                Double[] d = new Double[2];
                                d[0] = Double.parseDouble(fin1[i]);
                                d[1] = Double.parseDouble(fin1[i+1]);
                                dubs.add(d);
                            }
                            final BitmapDescriptor currentLocation = BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.current_location), 0.4f));
                            final BitmapDescriptor redPin = BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.red_pin), 0.4f));
                            runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            mMap.moveCamera(cameraUpdate);
                                            mMap.moveCamera(cameraUpdate);
                                            Marker home = mMap.addMarker(new MarkerOptions().position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude())).icon(currentLocation));
                                            for(Double[] a : dubs) {
                                                mMap.addMarker(new MarkerOptions().position(new LatLng(a[0], a[1])).icon(redPin));
                                            }
                                        }
                                    });
                        }
                    }).start();

            Log.d("Dash", "bad");
        }
        mMap.getUiSettings().setScrollGesturesEnabled(true);

    }


    private void getUserLocation() {
        if (selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) || selfPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            SmartLocation.with(this).location()
                    .oneFix()
                    .start(
                            new OnLocationUpdatedListener() {
                                @Override
                                public void onLocationUpdated(Location location) {
                                    userLoc = location;
                                    Log.d("Got location", location.toString());
                                }
                            });
        }
    }


    public List<String> readLine(String path) {
        List<String> mLines = new ArrayList<>();

        AssetManager am = getApplicationContext().getAssets();

        try {
            InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLines;
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            }
            else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getData(String key) {
        return getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).getString(key, "");
    }

    public Bitmap bitmapSizeByScale(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    class MapItemsRetriever extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground (Void...params){
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            String message = "";
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                String commandStr = "7," + Double.toString(userLoc.getLatitude()) + "," + Double.toString(userLoc.getLongitude());
                Log.d("commandStr", commandStr);
                oos.writeObject(commandStr);
                message = (String) ois.readObject();
                ois.close();
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("DashboardFragment", "Permission result");
        switch (requestCode) {

            case PERMISSIONS_MAP: {
                Log.d("DashboardFragment", "Permission result");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    userLoc = new Location("");
                    userLoc.setLatitude( -8.783195);
                    userLoc.setLongitude(-124.508523);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
