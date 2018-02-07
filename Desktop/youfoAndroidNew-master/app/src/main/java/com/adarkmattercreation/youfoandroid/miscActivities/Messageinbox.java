package com.adarkmattercreation.youfoandroid.miscActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.adarkmattercreation.youfoandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Danilo on 1/14/18.
 */

public class Messageinbox extends AppCompatActivity{
    HashMap<String,ArrayList<Object>> hashMap;
    Intent intent;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView txtname;
    String name;
    Comment messages;
    RecyclerView recyclerView;

    MAdapter mAdapter;
    DatabaseReference privatelobby;
    ArrayList<String> userkey;
    ArrayList<String> privateid;
    ArrayList<String> profilelist;
    ArrayList<String> namelist;
    ArrayList<Comment> lastcomment;
    ArrayList<Mailmessage> mailmessageArrayList;
    DatabaseReference lobbyref;
    private HashMap<String, Object> data;


    protected void onCreate(Bundle savedInstanceState) {
        mailmessageArrayList = new ArrayList<>();
        userkey = new ArrayList<>();
        privateid = new ArrayList<>();
        profilelist = new ArrayList<>();
        namelist = new ArrayList<>();
        lastcomment = new ArrayList<Comment>();
        hashMap = new HashMap<>();
        firebaseDatabase =  FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbyref = database.getReference().child("Users").child(firebaseUser.getUid()).child("privatemessage");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);


      intent = getIntent();
      /*
       hashMap = (HashMap<String, ArrayList<Object>>) intent.getSerializableExtra("hash");
       */
      name =  intent.getStringExtra("name");

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.back);
            txtname = (TextView) actionBar.getCustomView().findViewById(R.id.textView8);
            txtname.setText(name+ " Mailbox");
            actionBar.show();
        }



        databaseReference.child("Users").child(firebaseUser.getUid()).child("messangecount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mailmessageArrayList.clear();
                userkey.clear();
                privateid.clear();
                profilelist.clear();
                namelist.clear();
                lastcomment.clear();
                hashMap.clear();

                for(DataSnapshot child: dataSnapshot.getChildren()){
                    data = (HashMap<String, Object>) child.getValue();
                    String  datakey = child.getKey();

                    //countofmsg += (int) child.getChildrenCount();


                    Object[] arr =  data.keySet().toArray();
                    ArrayList<Object> arrayList = new ArrayList<Object>(Arrays.asList(arr));
                    hashMap.put(datakey,arrayList);
                }
                getprivateid();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void getprivateid(){
        lobbyref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot children: dataSnapshot.getChildren()){
                    String value = children.getValue(String.class);
                    String key = children.getKey();

                    userkey.add(key);
                    privateid.add(value);
                }
                for(int i = 0; i < privateid.size(); i ++){
                    privatelobby = firebaseDatabase.getReference().child("privateid").child(privateid.get(i));
                    getlastmessage();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getlastmessage(){
        privatelobby.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ///lastcomment.clear();
                for(DataSnapshot children: dataSnapshot.getChildren()){
                    messages = children.getValue(Comment.class);

                }
                Log.i("last = "," g" +messages.getComment());
                lastcomment.add(messages);




                if(lastcomment.size() == privateid.size()){
                    for(int i = 0; i < privateid.size(); i++){
                        userinfo(i);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void userinfo(int i) {
        databaseReference.child("Users").child(userkey.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Userinformation userinformation = dataSnapshot.getValue(Userinformation.class);
             profilelist.add(userinformation.getProfile());
             namelist.add(userinformation.getUser());
                if(((namelist.size() == lastcomment.size())  &&  (privateid.size() == lastcomment.size()))){
                    createobject ();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createobject() {
        for (int i = 0; i < privateid.size(); i++){
     Mailmessage mailmessage = new Mailmessage(firebaseUser.getUid(), privateid.get(i), namelist.get(i), lastcomment.get(i).getMessageTime(), profilelist.get(i), lastcomment.get(i).getComment(),userkey.get(i));
     mailmessageArrayList.add(mailmessage);
            Collections.sort(mailmessageArrayList);
            Collections.sort(mailmessageArrayList, Collections.reverseOrder());
            Log.i("mail list", String.valueOf(mailmessageArrayList.size()));
            LinearLayoutManager layoutmanager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mAdapter = new MAdapter(mailmessageArrayList, hashMap,getApplicationContext());
            recyclerView.setLayoutManager(layoutmanager);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();







        }
    }
}
