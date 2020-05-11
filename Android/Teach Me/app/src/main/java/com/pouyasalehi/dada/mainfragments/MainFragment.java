package com.pouyasalehi.dada.mainfragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.pouyasalehi.dada.ChatListAdapter;
import com.pouyasalehi.dada.ChatObject;
import com.pouyasalehi.dada.DatabaseManager;
import com.pouyasalehi.dada.FindUserActivity;
import com.pouyasalehi.dada.LoginActivity;
import com.pouyasalehi.dada.MainActivity;
import com.pouyasalehi.dada.R;
import com.pouyasalehi.dada.Things;
import com.pouyasalehi.dada.UserObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    public MainFragment() {
        // Required empty public constructor
    }
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private RecyclerView mChatList;
    private  String usernameholder,emailholder;
    private int usertypeholder,imageStatus;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;
    ArrayList<ChatObject> chatList;
    FloatingActionButton btngouser;
    private DatabaseManager mDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chats, container, false);
        mChatList= view.findViewById(R.id.chatList);
        initializeRecyclerView();
        mDatabase = new DatabaseManager(getContext());
        loadEmployeesFromDatabase();



        OneSignal.startInit(getContext()).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if(usertypeholder==1){ FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(userId);}
                else{FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(userId);}
            }
        });

        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        Fresco.initialize(getContext());
        TextView welcome=view.findViewById(R.id.welcomeback);
        welcome.setText("Welcome Back "+usernameholder+" !");
        btngouser=view.findViewById(R.id.findUser);
        btngouser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindUserActivity.class);
                startActivity(intent);

            }
        });
        try{
        if(usertypeholder==2){getUserChatList2();}
        else if(usertypeholder==1){getUserChatList();}}catch (Exception e){
            mDatabase.deleteEmployee("1");
        }
        return view;
    }



    private void getUserChatList2(){
        DatabaseReference mUserChatDB2 = FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat");


        mUserChatDB2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                       // ChatObject mChat2 = new ChatObject(childSnapshot.child("users").getValue().toString());
                        boolean  exists = false;
                        for (ChatObject mChatIterator : chatList){
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList.add(mChat);
                        getChatData(mChat.getChatId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }






    private void getUserChatList(){
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat");









        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        try {
                            ChatObject mChat = new ChatObject(childSnapshot.getKey());

                            ChatObject mChat2 = new ChatObject(childSnapshot.child("users").getValue().toString());

                            boolean exists = false;
                            for (ChatObject mChatIterator : chatList) {
                                if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                    exists = true;
                            }
                            if (exists)
                                continue;
                            chatList.add(mChat2);
                            getChatData(mChat.getChatId());
                        }catch  (NullPointerException d){
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatData(String chatId) {
        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("info");
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String chatId = "";

                    if(dataSnapshot.child("id").getValue() != null)
                        chatId = dataSnapshot.child("id").getValue().toString();

                    for(DataSnapshot userSnapshot : dataSnapshot.child("users/").getChildren()){
                        for(ChatObject mChat : chatList){
                            Things things=new Things();





                            if(mChat.getChatId().equals(chatId)){
                                UserObject mUser = new UserObject(userSnapshot.getKey());
                                mChat.addUserToArrayList(mUser);

                                    if(usertypeholder==2){getUserData2(mUser);}
                                else{getUserData(mUser);}
                            }
                        }
                    }




                    for(DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()){
                        for(ChatObject mChat : chatList){
                            Things things=new Things();





                            if(mChat.getChatId().equals(chatId)){
                                UserObject mUser2 = new UserObject(userSnapshot.getKey());
                                mChat.addUserToArrayList(mUser2);

                                if(usertypeholder==2){getUserData2(mUser2);}
                                else{getUserData(mUser2);}
                            }
                        }
                    }





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void getUserData2(UserObject mUser) {
        final Things g=new Things();

            DatabaseReference mUserDb2 = FirebaseDatabase.getInstance().getReference().child("teacher").child(mUser.getuid());
            mUserDb2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserObject mUser = new UserObject(dataSnapshot.getKey());

                    if(dataSnapshot.child("notificationKey").getValue() != null)
                        mUser.setNotificationKey(dataSnapshot.child("notificationKey").getValue().toString());
                    if(dataSnapshot.child("name").getValue() != null)
                        mUser.setName(dataSnapshot.child("name").getValue().toString());
                        g.chattingwith=mUser.getName();


                    for(ChatObject mChat : chatList){
                        for (UserObject mUserIt : mChat.getUserObjectArrayList()){
                            if(mUserIt.getuid().equals(mUser.getuid())){
                                mUserIt.setNotificationKey(mUser.getnotificationKey());
                            }
                        }
                    }
                    mChatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });






    }







    private void getUserData(UserObject mUser) {
        Things g=new Things();
            DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("user").child(mUser.getuid());
            mUserDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserObject mUser = new UserObject(dataSnapshot.getKey());

                    if(dataSnapshot.child("notificationKey").getValue() != null)
                        mUser.setNotificationKey(dataSnapshot.child("notificationKey").getValue().toString());

                    for(ChatObject mChat : chatList){
                        for (UserObject mUserIt : mChat.getUserObjectArrayList()){
                            if(mUserIt.getuid().equals(mUser.getuid())){
                                mUserIt.setNotificationKey(mUser.getnotificationKey());
                            }
                        }
                    }
                    mChatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });






    }

    private void initializeRecyclerView() {
        chatList = new ArrayList<>();
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }








    private void loadEmployeesFromDatabase() {
        //we are here using the DatabaseManager instance to get all employees
        Cursor cursor = mDatabase.getAllEmployees();

        if (cursor.moveToFirst()) {

            usernameholder= cursor.getString(0);
            emailholder=  cursor.getString(1);
            usertypeholder=cursor.getInt(2);





        }
    }

    }


