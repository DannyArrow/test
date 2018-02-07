package com.adarkmattercreation.youfoandroid.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.adarkmattercreation.youfoandroid.NotificationBroadcastReceiver;
import com.adarkmattercreation.youfoandroid.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Danilo on 1/31/18.
 */

public class Recievemessage extends FirebaseMessagingService {
    public static String REPLY_ACTION = "com.adarkmattercreation.youfoandroid.service.Recievemessage.REPLY_ACTION";
    public static String KEY_QUICK_REPLY = "key_reply_message";
    private int mNotificationId;
    private int mMessageId;
    String TAG = "hello";
    Bitmap bitmap;
    private String id;
    String user;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String imageUri = remoteMessage.getData().get("photo");
            id = remoteMessage.getData().get("senderid");
            user = remoteMessage.getData().get("title");
            bitmap = getBitmapfromUrl(imageUri);
           String title = remoteMessage.getNotification().getTitle();
           Log.i("title= ", title);
            shownotification(remoteMessage.getData().get("fromusername"),remoteMessage.getData().get("message"),bitmap);
        }
    }

    private void shownotification(String title, String message, Bitmap bitmap){

        mNotificationId = 1;
        mMessageId = 123;
        //the intent



        String replyLabel = "Reply to message";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_QUICK_REPLY).setLabel(replyLabel).build();

            android.support.v4.app.NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, replyLabel,getReplyPendingIntent())
                            .addRemoteInput(remoteInput)
                            .setAllowGeneratedReplies(true).build();



        android.support.v4.app.NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.crop_circle)
                        .setLargeIcon(bitmap)
                        .setShowWhen(true)
                        .addAction(action);


        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, builder.build());


    }


    private Bitmap getBitmapfromUrl(String image){
        try {
            URL url = new URL(image);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    private PendingIntent getReplyPendingIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of this codelab, will be doing a broadcast receiver
            intent = NotificationBroadcastReceiver.getReplyMessageIntent(this, mNotificationId, mMessageId,id,user);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // start your activity
            intent = ReplyActivity.getReplyMessageIntent(this, mNotificationId, mMessageId,id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

        public static CharSequence getReplyMessage(Intent intent) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                return remoteInput.getCharSequence(KEY_QUICK_REPLY);
            }
            return null;

    }
}
