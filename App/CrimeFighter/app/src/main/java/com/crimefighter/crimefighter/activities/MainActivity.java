package com.crimefighter.crimefighter.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.services.AlarmReceiver;
import com.crimefighter.crimefighter.services.RegistrationIntentService;
import com.crimefighter.crimefighter.utils.Item;
import com.crimefighter.crimefighter.utils.QuickstartPreferences;
import com.crimefighter.crimefighter.utils.RVAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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

public class MainActivity extends AppCompatActivity {

    public static Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mRemNumTextView;
    private TextView mRemTextView;
    private TextView mSolvNumTextView;
    private TextView mSolvTextView;
    private Button mButton;
    private FloatingActionButton fab;
    private List<Item> items;
    private RecyclerView rv;
    private static Location userLoc;

    final String host = "71.171.96.88";
    final int portNumber = 914;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.main_title);
        mTitleTextView.setTypeface(mTypeface);
        mRemNumTextView = (TextView)findViewById(R.id.main_remaining_number);
        mRemNumTextView.setTypeface(mTypeface);
        mRemTextView = (TextView)findViewById(R.id.main_remaining);
        mRemTextView.setTypeface(mTypeface);
        mSolvNumTextView = (TextView)findViewById(R.id.main_solved_number);
        mSolvNumTextView.setTypeface(mTypeface);
        mSolvTextView = (TextView)findViewById(R.id.main_solved);
        mSolvTextView.setTypeface(mTypeface);

        fab = (FloatingActionButton) findViewById(R.id.fabio);
        fab.setImageBitmap(textAsBitmap("!", 40, Color.WHITE));

//        boolean testAd = true;
//
//        if(testAd) {
//            MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713"); //ca-app-pub-3940256099942544~3347511713 DEMO
//            AdView mAdView = (AdView) findViewById(R.id.adView);
//            AdRequest request = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                    .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                    .build();
//            mAdView.loadAd(request);
//        }
//        else {
//            MobileAds.initialize(getApplicationContext(), "ca-app-pub-3670882123396960~3295541137"); //ca-app-pub-3940256099942544~3347511713 DEMO
//            AdView mAdView = (AdView) findViewById(R.id.adView);
//            AdRequest request = new AdRequest.Builder()
//                    .build();
//            mAdView.loadAd(request);
//        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                startActivity(intent);
            }
        });

        getUserLocation();
        //scheduleAlarm();

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
                        String mess = "";
                        try {
                            mess = new RecentItemsRetreiver().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        Log.d("RecentItems", mess);
                        initializeData(mess);
                    }
                }).start();



        //TODO: remove temp

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        //initializeData();
        //initializeAdapter();


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

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis + 60*1000,
                60*1000L, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

    }

    private void initializeData(String input) {
        String[] values = input.split(" ");
        int num = Integer.parseInt(values[0]);
        for(int i=0; i<num; i++) {
            //num, name, distance, description, lat, long
            Location ad = userLoc;
            ad.setLatitude(Double.parseDouble(values[i*6+4]));
            ad.setLongitude(Double.parseDouble(values[i*6+5]));
            items.add(new Item((i+1), values[i*6+1], Double.parseDouble(values[i*6+3]), values[i*6+2],ad));
        }
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(items, getApplicationContext());
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
                String commandStr = "2," + getData("UserID") + "," + Double.toString(userLoc.getLatitude()) + "," + Double.toString(userLoc.getLongitude());
                Log.d("commandStr", commandStr);
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
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                return sw.toString();
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