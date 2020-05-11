package com.pouyasalehi.dada;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindUserActivity extends AppCompatActivity {
private Things f=new Things();
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    private  String usernameholder,emailholder;
    private int usertypeholder;
    DatabaseManager mDatabase;
    ArrayList<UserObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        mDatabase = new DatabaseManager(this);
        loadEmployeesFromDatabase();

        userList= new ArrayList<>();

        Button mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createChat();
                    Toast.makeText(getApplicationContext(),"Chat Created!",Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e){}
            }
        });




        initializeRecyclerView();
        Things kok=new Things();

        if (usertypeholder==2){getUserDetails();}
        else{getTeacherDetails();}
    }

    private void createChat(){
        ///here!! too!!!

            String names=usernameholder;
            for(UserObject mUser3 : userList){names=names+","+mUser3.getName();}
            DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(names).child("info");
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            DatabaseReference userDb2 = FirebaseDatabase.getInstance().getReference().child("teacher");
            HashMap newChatMap = new HashMap();
            HashMap newChatMap2 = new HashMap();
            newChatMap.put("id", names);
            //check
            newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);
            Boolean validChat = false;
            for(UserObject mUser2 : userList){
                for(UserObject mUser : userList){

                    if(mUser.getSelected()){
                        validChat = true;
                        //checktoo
                        newChatMap.put("users/" + mUser.getuid(), true);
                        if(usertypeholder==2){
                            userDb.child(mUser.getuid()).child("chat").child(names).setValue(true);

                        }else {
                            userDb2.child(mUser.getuid()).child("chat").child(names).setValue(true);
                        }
                    }
                }
                if(usertypeholder==2){
                    DatabaseReference loo=userDb.child(mUser2.getuid()).child("chat").child(names);
                    HashMap m=new HashMap();
                    m.put("users",names);
                    loo.updateChildren(m);

                }else {
                    DatabaseReference loo=userDb2.child(mUser2.getuid()).child("chat").child(names);
                    HashMap m=new HashMap();
                    m.put("users",names);
                    loo.updateChildren(m);
                }
            }
            newChatMap2.put("users",names);
/////here 2!!!!!
            if(validChat){
                chatInfoDb.updateChildren(newChatMap);
                if(usertypeholder==2){
                    userDb2.child(FirebaseAuth.getInstance().getUid()).child("chat").child(names).setValue(true);
                    DatabaseReference loo=userDb2.child(FirebaseAuth.getInstance().getUid()).child("chat").child(names);
                    loo.updateChildren(newChatMap2);}
                else{
                    userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(names).setValue(true);
                    DatabaseReference loo=userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(names);
                    loo.updateChildren(newChatMap2);
                }
            }



    }



    private void getUserDetails() {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  email = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("email").getValue()!=null)
                            email = childSnapshot.child("email").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, email);





                        userList.add(mUser);




                        mUserListAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void getTeacherDetails() {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("teacher");
        Query query = mUserDB.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  email = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("email").getValue()!=null)
                            email = childSnapshot.child("email").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, email);








                            userList.add(mUser);
                            mUserListAdapter.notifyDataSetChanged();



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }



    private void initializeRecyclerView() {
        mUserList= findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
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

