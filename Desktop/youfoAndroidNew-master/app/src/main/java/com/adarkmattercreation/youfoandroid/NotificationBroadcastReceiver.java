package com.adarkmattercreation.youfoandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.adarkmattercreation.youfoandroid.miscActivities.Comment;
import com.adarkmattercreation.youfoandroid.miscActivities.Userinformation;
import com.adarkmattercreation.youfoandroid.service.Recievemessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.adarkmattercreation.youfoandroid.service.Recievemessage.REPLY_ACTION;

/**
 * Created by Danilo on 2/2/18.
 */
//import static com.segunfamisa.notification.directreply.NotificationService.REPLY_ACTION;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    private static Userinformation userinformation;
    private static Userinformation currentuser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    CharSequence message;
    private static String KEY_NOTIFICATION_ID = "key_noticiation_id";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static String Sender_id = "sender_id";
    private static String Usernameid = "user_id";
    static Context context1;
    DatabaseReference myref = firebaseDatabase.getReference();
    DatabaseReference checkref = firebaseDatabase.getReference().child("Users").child(user.getUid()).child("privatemessage");
    String key;
    String id;
    private static String username;


    public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId, String id, String user) {

        context1 = context;
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.putExtra(Sender_id, id);
        intent.putExtra(Usernameid,user);
        return intent;
    }

    public NotificationBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (REPLY_ACTION.equals(intent.getAction())) {
            context1 = context;
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            message = Recievemessage.getReplyMessage(intent);
            int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);
            id = intent.getStringExtra(Sender_id);
            username = intent.getStringExtra(Usernameid);

            Toast.makeText(context, "Message ID: " + messageId + "\nMessage: " + message,
                    Toast.LENGTH_SHORT).show();

            // update notification
            int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
            updateNotification(context, notifyId);
        }
    }

    private void updateNotification(Context context, int notifyId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_send_black_24dp)
                .setContentText("sent");

        notificationManager.notify(notifyId, builder.build());
        sendmessagetocloud();
    }

    private void sendmessagetocloud() {

        myref.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentuser = dataSnapshot.getValue(Userinformation.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              userinformation = dataSnapshot.getValue(Userinformation.class);
                HashMap<String,String> notification = new HashMap<>();
                notification.put("fromusername",currentuser.getUser());
                notification.put("message", String.valueOf(message));
                notification.put("photo", currentuser.getProfile());
                notification.put("senderid",user.getUid());
                notification.put("username", userinformation.getUser());

               myref.child("notificationRequests").push().setValue(notification);
               // getkey();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void getkey() {
        checkref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eachmessage : dataSnapshot.getChildren()) {
                    Log.i("key --", eachmessage.getKey());

                    if (eachmessage.getKey().equalsIgnoreCase(id)) {
                        key = eachmessage.getValue(String.class);
                        Log.i("lobbyid", key);
                        break;
                    } else {
                        //key = userrefrence.push().getKey();
                    }
                }
               // sendmessagedatabase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    private void sendmessagedatabase(){
        Comment comment = new Comment(currentuser.getUser(), String.valueOf(message), user.getUid(), user.getUid());

        myref.child("Users").child(id).child("privatemessage").child(user.getUid()).setValue(key);
        myref.child("Users").child(user.getUid()).child("privatemessage").child(id).setValue(key);
        myref.child("Users").child(id).child("messangecount").child(user.getUid()).push().setValue("");
        myref.child("privateid").child(key).push().setValue(comment);

    }
}

