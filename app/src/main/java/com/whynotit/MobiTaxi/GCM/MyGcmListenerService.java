/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whynotit.MobiTaxi.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Activity.PubActivity;
import com.whynotit.MobiTaxi.Models.Localisation;
import com.whynotit.MobiTaxi.Fragment.MapFragment;
import com.whynotit.MobiTaxi.R;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private MainActivity activity;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Log.e("GGGGGGGGGCM",data.toString());
        String subject = data.getString("subject");
        activity = MainActivity.getActivity();
        try {
            if (activity != null) {

                if (subject.contains("requestaccepted")) {
                    activity.setIdCourse(data.getString("idcourse"));
                    activity.setNumeroTaxi(data.getString("numerotaxi"));
                    activity.setComingTaxi(new Localisation(data.getString("idtaxi"), Double.valueOf(data.getString("lat")), Double.valueOf(data.getString("long"))));
                    activity.setTaxiPhone(data.getString("phone"));
                    MapFragment.dismissWaitingDialog();
                    activity.showFragmentIndication();
                } else if (subject.contains("0") && activity.getIdCourse() != null && data.getString("idcourse").compareTo(activity.getIdCourse()) == 0) {
                    if (activity.indicationFragment != null && activity.indicationFragment.isVisible()) {
                        activity.getSupportFragmentManager().popBackStack();
                        activity.showToast(0, "Le Taxi a annulé la course. Appelez un autre.");
                        //Snackbar.make(MapFragment.getRootLayout(),"Le Taxi a annulé la course. Appelez un autre.",Snackbar.LENGTH_LONG).show();
                    }
                } else if (subject.contains("-1") && activity.getIdCourse() != null && data.getString("idcourse").compareTo(activity.getIdCourse()) == 0) {
                    if (activity.indicationFragment != null && activity.indicationFragment.isVisible()) {
                        activity.getSupportFragmentManager().popBackStack();
                        activity.showToast(-1, "Le Taxi ne vous a pas trouvé. Appelez un autre.");
                        //Snackbar.make(MapFragment.getRootLayout(),"Le Taxi ne vous a pas trouvé. Appelez un autre.",Snackbar.LENGTH_LONG).show();
                    }
                } else if (subject.contains("1") && activity.getIdCourse() != null && data.getString("idcourse").compareTo(activity.getIdCourse()) == 0) {
                    if (activity.indicationFragment != null && activity.indicationFragment.isVisible()) {
                        //activity.getSupportFragmentManager().popBackStack();
                        activity.showToast(1, "Le Taxi vous a trouvé.");
                        //Snackbar.make(IndicationFragment.getRootLayout(),"Le Taxi vous a trouvé. Appelez un autre.",Snackbar.LENGTH_LONG).show();
                    }
                } else if (subject.compareTo("pubtime") == 0) {
                    Intent intent = new Intent(this, PubActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("idPub", data.getString("idPub"));
                    intent.putExtra("urlPub", data.getString("urlPub"));

                    startActivity(intent);
                }

            }

        }catch (IllegalStateException e) {
            e.printStackTrace();
        }


/*
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(to, null, message, null, null);*/
        /*if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

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
        //Toast.makeText(MainActivity.getActivity(),message+"!!!!!!",Toast.LENGTH_LONG).show();
        //Log.e("XXXXXXXXXXXXXXX",latitude+":"+longitude);
        //sendNotification(latitude+":"+longitude);

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Bot signals")
                .setContentText("New signal :" + message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void sendUnsubscribedNotification ()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Bot signals")
                .setContentText("New signal : Subscribe to see all exclusive signals.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
