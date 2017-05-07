package com.crimefighter.crimefighter.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.game.Main1Activity;
import com.crimefighter.crimefighter.game.MyGame;
import com.crimefighter.crimefighter.services.AlarmReceiver;
import com.crimefighter.crimefighter.services.RegistrationIntentService;
import com.crimefighter.crimefighter.utils.Item;
import com.crimefighter.crimefighter.utils.QuickstartPreferences;
import com.crimefighter.crimefighter.utils.RVAdapter;
import com.crimefighter.crimefighter.utils.UnCaughtException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends BaseActivity {

    public static Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mRemNumTextView;
    private TextView mRemTextView;
    private TextView mSolvNumTextView;
    private TextView mSolvTextView;
    private TextView mEmptyText;
    private Button mButton;
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;


    private List<byte[]> images = new ArrayList<byte[]>();
    private List<Item> items = new ArrayList<Item>();
    private static RecyclerView rv;
    private static RVAdapter adapter;
    private static Location userLoc;

    final String host = "71.171.96.88";
    final int portNumber = 914;

    private static final int PERMISSIONS_MAP = 1337;

    private SwipeRefreshLayout swipeContainer;


    public static Context context1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.main_title);
        mTitleTextView.setTypeface(mTypeface);

        mEmptyText = (TextView)findViewById(R.id.empty_view);
        mEmptyText.setTypeface(mTypeface);

        mTitleTextView.setOnTouchListener(new View.OnTouchListener() {
            Handler handler = new Handler();

            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout() + 500) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

                        if (numberOfTaps == 3) {
                            Intent intent = new Intent(getApplicationContext(), Main1Activity.class);
                            startActivity(intent);
                        }
                }

                return true;
            }
        });

        mRemNumTextView = (TextView)findViewById(R.id.main_remaining_number);
        mRemNumTextView.setTypeface(mTypeface);
        mRemTextView = (TextView)findViewById(R.id.main_remaining);
        mRemTextView.setTypeface(mTypeface);
        mSolvNumTextView = (TextView)findViewById(R.id.main_solved_number);
        mSolvNumTextView.setTypeface(mTypeface);
        mSolvTextView = (TextView)findViewById(R.id.main_solved);
        mSolvTextView.setTypeface(mTypeface);



        fab = (FloatingActionButton) findViewById(R.id.fabio1);
        fab.setImageBitmap(textAsBitmap("!", 40, Color.WHITE));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                startActivity(intent);
            }
        });

        fab1 = (FloatingActionButton) findViewById(R.id.fabio);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WatchActivity.class);
                startActivity(intent);
            }
        });

        fab2 = (FloatingActionButton) findViewById(R.id.fabio2);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalyticsActivity.class);
                startActivity(intent);
            }
        });

        getUserLocation();
        //scheduleAlarm();

        context1 = getApplicationContext();




        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(
                        new Runnable() {
                            public void run() {
                                if(checkPermission()) {
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
                                } else {
                                    userLoc = new Location("");
                                    userLoc.setLatitude( -8.783195);
                                    userLoc.setLongitude(-124.508523);
                                }
                                Log.d("WatchActivity", "got location");
                                runOnUiThread(
                                        new Runnable() {
                                            public void run() {
                                                adapter.clear();
                                            }
                                        });

                                String mess = "";
                                try {
                                    mess = new RecentItemsRetreiver().execute().get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                Log.d("WatchActivity", mess);
                                final String gg = mess;
                                runOnUiThread(
                                        new Runnable() {
                                            public void run() {
                                                initializeData(gg);
                                                initializeAdapter();
                                                swipeContainer.setRefreshing(false);
                                            }
                                        });

                            }
                        }).start();

            }
        };

        swipeContainer.setOnRefreshListener(swipeRefreshListner);



        //TODO: remove temp

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapter();
        //

        swipeContainer.post(new Runnable() {
            @Override public void run() {
                swipeContainer.setRefreshing(true);
                // directly call onRefresh() method
                swipeRefreshListner.onRefresh();
            }
        });
    }

    public boolean itemAlreadyServed(Item a, List<Item> b) {
        for(Item c : b) {
            if(c.itemName.equals(a.itemName) || c.id == a.id) {
                return true;
            }
        }
        return false;
    }



    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

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

    private void initializeData(String input) {
        Log.d("crimefighter", input);
        if(input.equals(",")) {
            swipeContainer.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            swipeContainer.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
            String[] values = input.split(",");
            int num = Integer.parseInt(values[0]);
            for (int i = 0; i < num; i++) {
                //num, name, distance, description, lat, long
                Location ad = userLoc;
                ad.setLatitude(Double.parseDouble(values[i * 6 + 4]));
                ad.setLongitude(Double.parseDouble(values[i * 6 + 5]));
                Item addition = new Item(Integer.parseInt(values[i * 6 + 6]), values[i * 6 + 1], Double.parseDouble(values[i * 6 + 3]), values[i * 6 + 2], ad);
                if(!itemAlreadyServed(addition,items)) {
                    try {
                        items.add(new Item(Integer.parseInt(values[i * 6 + 6]), values[i * 6 + 1], Double.parseDouble(values[i * 6 + 3]), values[i * 6 + 2], ad));
                    } catch (Exception e) {
                        items.add(new Item(Integer.parseInt(values[i * 6 + 6]), values[i * 6 + 1], Double.parseDouble(values[i * 6 + 3]), values[i * 6 + 2], ad));
                    }
                }
            }
        }
    }

    private void initializeAdapter(){
        adapter = new RVAdapter(items, getApplicationContext());
        rv.setAdapter(adapter);
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    class RecentItemsRetreiver extends AsyncTask<Void, Void, String> {
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
                String commandStr = "2," + getData("UserID") + "," + Double.toString(userLoc.getLatitude()) + "," + Double.toString(userLoc.getLongitude()) + "," + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("regID", "");
                Log.d("commandStr", commandStr);
                oos.writeObject(commandStr);
                ois = new ObjectInputStream(socket.getInputStream());
                message = (String) ois.readObject();
                String[] values = message.split(",");
                int num = Integer.parseInt(values[0]);
                ois.close();
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
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
