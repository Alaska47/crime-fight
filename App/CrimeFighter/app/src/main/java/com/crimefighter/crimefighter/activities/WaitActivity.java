package com.crimefighter.crimefighter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crimefighter.crimefighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class WaitActivity extends AppCompatActivity {

    public static Typeface mTypeface;
    private TextView mNameTextView;
    private TextView mDistanceTextView;
    private TextView mDescriptionTextView;
    private Button mButtonHere;
    private Button mButtonGone;

    final String host = "71.171.96.88";
    final int portNumber = 914;

    private static String id;
    private static String[] realLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id = null;
            } else {
                id = extras.getString("id");
            }
        } else {
            id = (String) savedInstanceState.getSerializable("id");
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

        String[] realLocation;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                realLocation = null;
            } else {
                realLocation = extras.getStringArray("location");
            }
        } else {
            realLocation = (String[]) savedInstanceState.getSerializable("location");
        }


        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mNameTextView = (TextView)findViewById(R.id.name);
        mNameTextView.setTypeface(mTypeface);
        mNameTextView.setText(name);
        final ImageView cameraView = (ImageView) findViewById(R.id.wait_picture);
        /*
        ImageView cameraView = (ImageView) findViewById(R.id.wait_picture);
        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");
        try {
            if (b != null || b.length != 0) {
                Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
                cameraView.setImageBitmap(bmp);
            }
        } catch (NullPointerException e) {

        }
        */

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://crimefighter-162707.appspot.com/").child(id + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(3*ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                cameraView.setImageBitmap(bitmap);
                findViewById(R.id.progress).setVisibility(View.GONE);
                cameraView.setVisibility(View.VISIBLE);
            }
        });

        mDistanceTextView = (TextView)findViewById(R.id.distance);
        mDistanceTextView.setTypeface(mTypeface);
        if(distance.contains("Near")) {
            mDistanceTextView.setText("Near you");
        } else {
            mDistanceTextView.setText(distance.split(" ")[0] + " miles away");
        }
        mDescriptionTextView = (TextView)findViewById(R.id.description);
        mDescriptionTextView.setTypeface(mTypeface);
        mDescriptionTextView.setText(description);
        mButtonHere = (Button)findViewById(R.id.here);
        mButtonHere.setTypeface(mTypeface);
        mButtonHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new WaitItemVerifier().execute("y").get();
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
        mButtonGone = (Button)findViewById(R.id.gone);
        mButtonGone.setTypeface(mTypeface);
        mButtonGone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new WaitItemVerifier().execute("n").get();
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

    }

    class WaitItemVerifier extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground (String...params){
            Socket socket = null;
            ObjectOutputStream oos = null;
            String message = "";
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                String commandStr = "4," + id + "," + params[0];
                Log.d("commandStr", commandStr);
                oos.writeObject(commandStr);
                oos.close();
                socket.close();
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
            return "";
        }
    }
}
