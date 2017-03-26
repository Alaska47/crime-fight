package com.crimefighter.crimefighter.gcm;

/**
 * Created by anees on 3/26/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.activities.FoundActivity;
import com.crimefighter.crimefighter.activities.MainActivity;
import com.crimefighter.crimefighter.activities.StolenActivity;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Callandroid.R.attred when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        PowerManager.WakeLock screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        screenOn.acquire(3000);
        String message = data.getString("message");
        Log.d("NotificationShits", message);

        //format message to your liking
        /*
        String[] mess = message.split("\\*");
        if(!mess[0].equals(message)) {
        	String badname = mess[0];
        	String classname = PreferenceManager.getDefaultSharedPreferences(this).getString(badname, "");
        	if(!classname.isEmpty())
        		message = classname + " " + mess[1];
        }
        */

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        try {
            String[] cse = message.split(",");
            if(cse[0].equalsIgnoreCase("recovery")) {
                sendNotificationRecovery(cse);
                Log.d("NotificationShits", "recovery");
            }
            else if(cse[0].equalsIgnoreCase("stolen")) {
                sendNotificationStolen(cse);
                Log.d("NotificationShits", "stolen");
            }
            else
                sendNotificationFound();
        }
        catch(Exception e) {}
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotificationStolen(String[] message) {
        Intent intent = new Intent(this, StolenActivity.class); //updateintent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", message[1]);
        intent.putExtra("description", message[2]);
        intent.putExtra("distance", message[3]);
        intent.putExtra("stealID", message[4]);
        intent.putExtra("location", new String[] {message[5],message[6]});
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Log.d("NotificationShits", "finished extras");
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Stolen Item Alert")
                .setContentText(
                        message[1]+" was stolen.") //update message
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.mini_logo) //update icon
                .setContentIntent(pendingIntent);

        Log.d("NotificationShits", "built notification");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        Log.d("NotificationShits", "notified");
    }

    private void sendNotificationRecovery(String[] message) {
        Intent intent = new Intent(this, MainActivity.class); //updateintent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Your Item Missing")
                .setContentText("Your "+message[1]+" dissappeared.") //update message
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.mini_logo) //update icon
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotificationFound() {
        Intent intent = new Intent(this, FoundActivity.class); //updateintent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Your Item Found")
                .setContentText("Your item has been found!") //update message
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.ic_action_name) //update icon
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
