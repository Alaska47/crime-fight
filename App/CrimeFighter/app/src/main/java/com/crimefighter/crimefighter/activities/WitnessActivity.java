package com.crimefighter.crimefighter.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class WitnessActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_MAP = 1337;
    private static Location userLoc;
    private MapView mMapView;
    private static GoogleMap mMap;
    private Bundle mBundle;

    final String host = "71.171.96.88";
    final int portNumber = 914;

    private Typeface mTypeface;
    private TextView mTitle;
    private EditText mTime;
    private EditText mDescription;
    private Button mReportButton;

    String sTitle;
    String sTime;
    String sDescription;

    private String stealID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witness);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                stealID = null;
            } else {
                stealID = extras.getString("stealID");
            }
        } else {
            stealID = (String) savedInstanceState.getSerializable("stealID");
        }

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitle = (TextView)findViewById(R.id.title);
        mTitle.setTypeface(mTypeface);
        mTime = (EditText)findViewById(R.id.time_edit_text);
        mTime.setTypeface(mTypeface);
        mDescription = (EditText)findViewById(R.id.description_edit_text);
        mDescription.setTypeface(mTypeface);
        mReportButton = (Button) findViewById(R.id.report_button);
        mReportButton.setTypeface(mTypeface);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReportButton.setEnabled(false);
                sTitle = mTitle.getText().toString();
                if(sTitle.isEmpty()) sTitle = "Default Title";
                sTime = mTime.getText().toString();
                if(sTime.isEmpty()) sTime = "4:20 PM";
                sDescription = mDescription.getText().toString();
                if(sDescription.isEmpty()) sDescription = "Default Description";

                try {
                    new WitnessActivitySender().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                runOnUiThread(
                        new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_SHORT).show();
                            }
                        });
                Intent a = new Intent(getApplicationContext(), MainActivity.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
            }
        });

        if (!selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) || !selfPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_MAP);
        }
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Log.e("mapview", "", e);
        }

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

        if(selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) || selfPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
                            runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            mMap.moveCamera(cameraUpdate);
                                            mMap.moveCamera(cameraUpdate);
                                            Marker home = mMap.addMarker(new MarkerOptions().position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.red_pin), 0.4f))));
                                        }
                                    });
                        }
                    }).start();
        }
        else {

            new Thread(
                    new Runnable() {
                        public void run() {
                            getUserLocation();
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
                            runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            mMap.moveCamera(cameraUpdate);
                                            mMap.moveCamera(cameraUpdate);
                                            Marker home = mMap.addMarker(new MarkerOptions().position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.red_pin), 0.4f))));
                                        }
                                    });
                        }
                    }).start();

            Log.d("Dash", "bad");
        }
        mMap.getUiSettings().setScrollGesturesEnabled(false);

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

    @Override
    public void onStop() {
        super.onStop();
    }

    class WitnessActivitySender extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground (Void...params){
            Socket socket = null;
            ObjectOutputStream oos = null;
            String message = "";
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                String commandStr = "6," + stealID + "," + Double.toString(userLoc.getLatitude())+ "," + Double.toString(userLoc.getLongitude()) + "," + sTitle.replaceAll(","," ") + "," + sDescription.replaceAll(","," ") + "," + sTime.replaceAll(","," ");
                Log.d("commandStr", commandStr);
                oos.writeObject(commandStr);
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
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
