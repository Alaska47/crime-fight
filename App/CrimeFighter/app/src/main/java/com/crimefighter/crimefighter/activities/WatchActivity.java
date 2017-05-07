package com.crimefighter.crimefighter.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.utils.SingleShotLocationProvider;
import com.crimefighter.crimefighter.utils.UnCaughtException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class WatchActivity extends BaseActivity {

    private static Location userLoc;

    private static final int PERMISSIONS_MAP = 1337;

    private Typeface mTypeface;
    private TextView mTitle;
    private EditText mItemName;
    private EditText mTime;
    private EditText mDescription;

    private String sItemName;
    private String sTime;
    private String sDescription;

    private Button mReportButton;
    private Button mCameraButton;
    private static ImageView cameraView;

    private static final int REQUEST_RUNTIME_PERMISSION = 1;

    private StorageReference mStorageRef;

    final String host = "71.171.96.88";
    final int portNumber = 914;

    private static final int CAMERA_REQUEST = 1888;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.crimefighter.crimefighter.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitle = (TextView)findViewById(R.id.title);
        mTitle.setTypeface(mTypeface);
        mItemName = (EditText)findViewById(R.id.name_edit_text);
        mItemName.setTypeface(mTypeface);
        mTime = (EditText)findViewById(R.id.time_edit_text);
        mTime.setTypeface(mTypeface);
        mDescription = (EditText)findViewById(R.id.description_edit_text);
        mDescription.setTypeface(mTypeface);
        mReportButton = (Button) findViewById(R.id.report_button);
        mReportButton.setTypeface(mTypeface);

        cameraView = (ImageView) findViewById(R.id.camera_view);

        mCameraButton = (Button) findViewById(R.id.camera_button);
        mCameraButton.setTypeface(mTypeface);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageFromCamera(view);
            }
        });

        getUserLocation();

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(
                        new Runnable() {
                            public void run() {
                                mReportButton.setEnabled(false);
                                getUserLocation();
                                long startTime = System.currentTimeMillis();
                                while (userLoc == null) {
                                    try {
                                        Thread.sleep(100);
                                        if(System.currentTimeMillis() - startTime > 7500) {
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

                                    }
                                    catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d("WatchActivity", "got location");
                                runOnUiThread(
                                        new Runnable() {
                                            public void run() {
                                                sItemName = mItemName.getText().toString();
                                                if(sItemName.isEmpty()) sItemName = "Default Title";
                                                sTime = mTime.getText().toString();
                                                if(sTime.isEmpty()) sTime = "4:20 PM";
                                                sDescription = mDescription.getText().toString();
                                                if(sDescription.isEmpty()) sDescription = "Default Description";
                                            }
                                        });
                                try {
                                    new WatchRequestSubmit().execute().get();
                                    storeData("createdWatch", "true");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                Log.d("WatchActivity", "sent");

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

        checkPermission();


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

    void checkPermission() {
        //select which permission you want
        final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(WatchActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(WatchActivity.this, permission)) {
            } else {
                ActivityCompat.requestPermissions(WatchActivity.this, new String[]{permission}, REQUEST_RUNTIME_PERMISSION);
            }
        }
    }

    public Bitmap bitmapSizeByScale(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class WatchRequestSubmit extends AsyncTask<Void, Void, String> {
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
            File path = new File(getFilesDir(), "crime_images/");
            if (!path.exists()) path.mkdirs();
            File image = new File(path, "image.jpg");
            Log.d("WatchActivity", ""+image.exists());
            try {
                socket = new Socket(host, portNumber);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                String commandStr = "3," + getData("UserID") + "," + sItemName.replaceAll(","," ") + "," + sDescription.replaceAll(","," ") + "," + Double.toString(userLoc.getLatitude()) + "," + Double.toString(userLoc.getLongitude());
                Log.d("commandStr", commandStr);
                oos.writeObject(commandStr);
                message = (String) ois.readObject();
                Log.d("WatchActivity", "image_id: " + message);
                image = saveBitmapToFile(image);
                Uri file = Uri.fromFile(image);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl("gs://crimefighter-162707.appspot.com/").child(message + ".jpg");
                storageReference.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Log.d("WatchActivity", "" + downloadUrl.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("WatchActivity", "image NOT sent");
                            }
                        });
                oos.close();
                socket.close();
                image.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
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

    public void takeImageFromCamera(View view) {
        File path = new File(getFilesDir(), "crime_images/");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, "image.jpg");
        Uri imageUri = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, image);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "called");
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                File path = new File(getFilesDir(), "crime_images/");
                if (!path.exists()) path.mkdirs();
                File imageFile = new File(path, "image.jpg");
                // use imageFile to open your image
                Log.d("MainActivity", "" + imageFile.exists());
                Bitmap bitmap = decodeSampledBitmapFromFile(imageFile.getAbsolutePath(), 1000, 700);
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                cameraView.setImageDrawable(d);
                cameraView.setVisibility(View.VISIBLE);
                mCameraButton.setVisibility(View.GONE);
            }
        }
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
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
