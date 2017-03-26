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
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;
import com.google.android.gms.common.SignInButton;
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

import java.text.DecimalFormat;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class StolenActivity extends AppCompatActivity  implements OnMapReadyCallback {

    public static Typeface mTypeface;
    private TextView mNameTextView;
    private TextView mDistanceTextView;
    private TextView mDescriptionTextView;
    private Button mButton;

    private static final int PERMISSIONS_MAP = 1337;
    private static Location userLoc;
    private static String[] reallyLocation;
    private MapView mMapView;
    private static GoogleMap mMap;
    private Bundle mBundle;

    private String stealID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stolen);

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

        String name;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                name = null;
            } else {
                name = extras.getString("name");
            }
        } else {
            name = (String) savedInstanceState.getSerializable("name");
        }

        String description;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                description = null;
            } else {
                description = extras.getString("description");
            }
        } else {
            description = (String) savedInstanceState.getSerializable("description");
        }

        String distance;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                distance = null;
            } else {
                distance = extras.getString("distance");
            }
        } else {
            distance = (String) savedInstanceState.getSerializable("distance");
        }

        Log.d("guface_distance", distance);

        double fu = Double.parseDouble(distance);
        int lu = ((int) (fu * 10000.0));
        double fd = ((double) lu) / 10000.0;

        distance = Double.toString(fd);

        String realLocation;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                realLocation = null;
            } else {
                realLocation = extras.getString("location");
            }
        } else {
            realLocation = (String) savedInstanceState.getSerializable("location");
        }
        Log.d("guface", realLocation);

        reallyLocation = realLocation.split(",");

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mNameTextView = (TextView)findViewById(R.id.name);
        mNameTextView.setTypeface(mTypeface);
        mNameTextView.setText(name);
        mDistanceTextView = (TextView)findViewById(R.id.distance);
        mDistanceTextView.setTypeface(mTypeface);

        if(distance.contains("Near") || distance.equals("0.0")) {
            mDistanceTextView.setText("Near you");
        } else {
            mDistanceTextView.setText(distance + " miles away");
        }

        mDescriptionTextView = (TextView)findViewById(R.id.description);
        mDescriptionTextView.setTypeface(mTypeface);

        mDescriptionTextView.setText(description);

        mButton = (Button)findViewById(R.id.button);
        mButton.setTypeface(mTypeface);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advance();
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

    public void advance() {
        Intent intent = new Intent(this, WitnessActivity.class);
        intent.putExtra("stealID", stealID);
        startActivity(intent);
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
                            while (userLoc == null) {
                                try {
                                    Thread.sleep(100);
                                    //Log.d("Got location", "searching");
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            userLoc.setLatitude(Double.parseDouble(reallyLocation[0]));
                            userLoc.setLongitude(Double.parseDouble(reallyLocation[1]));
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
}
