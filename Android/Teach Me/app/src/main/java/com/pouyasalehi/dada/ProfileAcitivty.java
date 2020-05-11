package com.pouyasalehi.dada;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class ProfileAcitivty extends AppCompatActivity {

    private String username,userUID,profile;
    private TextView txtBIO,txtname,points;
    private ImageView imgprofile,feeling;
    private DatabaseReference userDB,teacherDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imgprofile = findViewById(R.id.imageViewProfile);
        txtBIO = findViewById(R.id.bioProfile);
        txtname = findViewById(R.id.idProfile);
        points = findViewById(R.id.pointsProfile);
        feeling = findViewById(R.id.feelingProfile);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            username =(String) b.get("USER_NAME");
            userUID = (String) b.get("USER_UID");
            profile = (String) b.get("PROFILE_IMAGE");

        }else {finish();}
            txtname.setText(username);
            Picasso.Builder builder2 = new Picasso.Builder(getApplicationContext());
            builder2.downloader(new OkHttp3Downloader(getApplicationContext()));
            builder2.build().load(profile)
                    .placeholder((R.drawable.image))
                    .error(R.drawable.ic_launcher_background)
                    .into(imgprofile);
            userDB = FirebaseDatabase.getInstance().getReference().child("user");
            teacherDB = FirebaseDatabase.getInstance().getReference().child("teacher");

                userDB.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.getKey().equals(userUID)){
                                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                                    if(childsnapshot.getKey().equals("bio")){
                                        txtBIO.setText(childsnapshot.getValue().toString());
                                    }
                                    if(childsnapshot.getKey().equals("points_user")){
                                        points.setText("Points : "+childsnapshot.getValue().toString());
                                    }else if(childsnapshot.getKey().equals("feeling")){
                                        getFeeling(childsnapshot.getValue().toString());
                                    }
                                }
                            }


                            }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            teacherDB.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.getKey().equals(userUID)){
                            for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                                if(childsnapshot.getKey().equals("bio")){
                                    txtBIO.setText(childsnapshot.getValue().toString());
                                }else if(childsnapshot.getKey().equals("feeling")){
                                    getFeeling(childsnapshot.getValue().toString());
                                }

                            }
                        }


                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void getFeeling(String feeling2){
        if (feeling2.equals("confused")){ feeling.setImageResource(R.drawable.confused); }
        if (feeling2.equals("bad")){feeling.setImageResource(R.drawable.bad);}
        if (feeling2.equals("dating")){feeling.setImageResource(R.drawable.dating);}
        if (feeling2.equals("happy")){feeling.setImageResource(R.drawable.happy);}
        if (feeling2.equals("lucky")){feeling.setImageResource(R.drawable.lucky);}
        if (feeling2.equals("sick")){feeling.setImageResource(R.drawable.sick);}
        if (feeling2.equals("studying")){feeling.setImageResource(R.drawable.studying);}
    }

}
