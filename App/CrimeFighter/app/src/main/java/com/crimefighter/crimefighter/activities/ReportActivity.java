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
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Text;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class ReportActivity extends BaseActivity implements OnMapReadyCallback {

    private static Location userLoc;
    private MapView mMapView;
    private static GoogleMap mMap;
    private Bundle mBundle;

    private Typeface mTypeface;
    private TextView mTitle;
    private EditText mItemName;
    private EditText mTime;
    private EditText mDescription;
    private Button mReportButton;

    private String sItemName;
    private String sTime;
    private String sDescription;


    final String host = "71.171.96.88";
    final int portNumber = 914;

    private static final int PERMISSIONS_MAP = 1337;

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {//Can add more as per requirement

            return false;
        }
        return true;
    }

    private void getUserLocation() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(ReportActivity.this));

        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/montserrat.ttf");
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setTypeface(mTypeface);
        mItemName = (EditText) findViewById(R.id.name_edit_text);
        mItemName.setTypeface(mTypeface);
        mTime = (EditText) findViewById(R.id.time_edit_text);
        mTime.setTypeface(mTypeface);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        mDescription.setTypeface(mTypeface);
        mReportButton = (Button) findViewById(R.id.report_button);
        mReportButton.setTypeface(mTypeface);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(
                        new Runnable() {
                            public void run() {
                                getUserLocation();
                                long startTime = System.currentTimeMillis();
                                while (userLoc == null) {
                                    try {
                                        Thread.sleep(100);
                                        if (System.currentTimeMillis() - startTime > 7500) {
                                            userLoc = new Location("");
                                            userLoc.setLatitude( -8.783195);
                                            userLoc.setLongitude(-124.508523);
                                            runOnUiThread(
                                                    new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Using default location", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            break;

                                        }
                                        Log.d("WatchActivity", "searching");

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                runOnUiThread(
                                        new Runnable() {
                                            public void run() {
                                                sItemName = mItemName.getText().toString();
                                                if (sItemName.isEmpty())
                                                    sItemName = "Default Title";
                                                sTime = mTime.getText().toString();
                                                if (sTime.isEmpty()) sTime = "4:20 PM";
                                                sDescription = mDescription.getText().toString();
                                                if (sDescription.isEmpty())
                                                    sDescription = "Default Description";
                                            }
                                        });

                                boolean cont = false;
                                String lastSentTimeStr = getData("lastSentTime");
                                String numSent = getData("numSent");
                                if (lastSentTimeStr.equals("")) {
                                    storeData("lastSentTime", System.currentTimeMillis() + "");
                                    storeData("numSent", "1");
                                    cont = true;
                                } else {
                                    long lastSentTime = Long.parseLong(lastSentTimeStr);
                                    if (Integer.parseInt(numSent) < 4 && System.currentTimeMillis() - lastSentTime < 24 * 60 * 60 * 1000) {
                                        storeData("lastSentTime", System.currentTimeMillis() + "");
                                        storeData("numSent", (Integer.parseInt(numSent) + 1) + "");
                                        cont = true;
                                    } else if (System.currentTimeMillis() - lastSentTime > 24 * 60 * 60 * 1000) {
                                        storeData("lastSentTime", System.currentTimeMillis() + "");
                                        storeData("numSent", "1");
                                        cont = true;
                                    } else {
                                    }
                                }
                                if (cont) {
                                    try {
                                        new StolenItemSender().execute().get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
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
                        }).start();
            }
        });

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

        if (selfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) || selfPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
                                    if (System.currentTimeMillis() - startTime > 5000) {
                                        userLoc = new Location("");
                                        userLoc.setLatitude( -8.783195);
                                        userLoc.setLongitude(-124.508523);
                                        break;
                                    }
                                    Log.d("WatchActivity", "searching");

                                } catch (InterruptedException e) {
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
        } else {

            new Thread(
                    new Runnable() {
                        public void run() {
                            getUserLocation();
                            long startTime = System.currentTimeMillis();
                            while (userLoc == null) {
                                try {
                                    Thread.sleep(100);
                                    if (System.currentTimeMillis() - startTime > 5000) {
                                        userLoc = new Location("");
                                        userLoc.setLatitude( -8.783195);
                                        userLoc.setLongitude(-124.508523);
                                        break;
                                    }
                                    Log.d("WatchActivity", "searching");

                                } catch (InterruptedException e) {
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
        mMap.getUiSettings().setScrollGesturesEnabled(true);

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
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
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

    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
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

    class StolenItemSender extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            Socket socket = null;
            ObjectOutputStream oos = null;
            String message = "";
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                String commandStr = "5," + getData("UserID") + "," + Double.toString(userLoc.getLatitude()) + "," + Double.toString(userLoc.getLongitude()) + "," + sItemName.replaceAll(",", " ") + "," + sDescription.replaceAll(",", " ") + "," + sTime.replaceAll(",", " ");
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


}
