package com.pouyasalehi.dada.mainfragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.onesignal.OneSignal;
import com.pouyasalehi.dada.DatabaseManager;
import com.pouyasalehi.dada.LoginActivity;
import com.pouyasalehi.dada.MainActivity;
import com.pouyasalehi.dada.R;
import com.pouyasalehi.dada.SettingsActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {
    private Button refreshpoint;
    private  String usernameholder,emailholder;
    private int usertypeholder,pointdb;
    private TextView points,bio,name;
    private ImageView feeling,goToSettings,profile;
    private DatabaseManager mDatabase;
    public SettingsFragment() {

    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        // Inflate the layout for this fragment
        mDatabase=new DatabaseManager(getContext());
        loadEmployeesFromDatabase();
        refreshpoint=view.findViewById(R.id.btnrefresh);
        feeling = view.findViewById(R.id.feeling3);
        goToSettings = view.findViewById(R.id.gotoSettings);
        profile = view.findViewById(R.id.imageViewProfileMe);
        bio = view.findViewById(R.id.bioProfileMe);
        name = view.findViewById(R.id.idProfileMe);
        name.setText(usernameholder);
        points=view.findViewById(R.id.totalpoints);
        DatabaseReference l2=FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference l=FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(usertypeholder==1){
            l.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.getKey().equals("feeling")){
                            String feeling2 = dataSnapshot.getValue().toString();
                            if (feeling2.equals("confused")){ feeling.setImageResource(R.drawable.confused); }
                            if (feeling2.equals("bad")){feeling.setImageResource(R.drawable.bad);}
                            if (feeling2.equals("dating")){feeling.setImageResource(R.drawable.dating);}
                            if (feeling2.equals("happy")){feeling.setImageResource(R.drawable.happy);}
                            if (feeling2.equals("lucky")){feeling.setImageResource(R.drawable.lucky);}
                            if (feeling2.equals("sick")){feeling.setImageResource(R.drawable.sick);}
                            if (feeling2.equals("studying")){feeling.setImageResource(R.drawable.studying);}
                        }

                        if(dataSnapshot.getKey().equals("bio")){
                        String g=dataSnapshot.getValue().toString();
                        bio.setText(g);
                    }
                        if(dataSnapshot.getKey().equals("profilePic")){
                            String g=dataSnapshot.getValue().toString();
                            Picasso.Builder builder2 = new Picasso.Builder(getContext());
                            builder2.downloader(new OkHttp3Downloader(getContext()));
                            builder2.build().load(g)
                                    .placeholder((R.drawable.image))
                                    .error(R.drawable.ic_launcher_background)
                                    .into(profile);
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
        }else{
            l2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.getKey().equals("feeling")){
                            String feeling2 = dataSnapshot.getValue().toString();
                            if (feeling2.equals("confused")){ feeling.setImageResource(R.drawable.confused); }
                            if (feeling2.equals("bad")){feeling.setImageResource(R.drawable.bad);}
                            if (feeling2.equals("dating")){feeling.setImageResource(R.drawable.dating);}
                            if (feeling2.equals("happy")){feeling.setImageResource(R.drawable.happy);}
                            if (feeling2.equals("lucky")){feeling.setImageResource(R.drawable.lucky);}
                            if (feeling2.equals("sick")){feeling.setImageResource(R.drawable.sick);}
                            if (feeling2.equals("studying")){feeling.setImageResource(R.drawable.studying);}
                        }
                        if(dataSnapshot.getKey().equals("profilePic")){
                            String g=dataSnapshot.getValue().toString();
                            Picasso.Builder builder2 = new Picasso.Builder(getContext());
                            builder2.downloader(new OkHttp3Downloader(getContext()));
                            builder2.build().load(g)
                                    .placeholder((R.drawable.image))
                                    .error(R.drawable.ic_launcher_background)
                                    .into(profile);
                        }
                        if(dataSnapshot.getKey().equals("bio")){
                        String g=dataSnapshot.getValue().toString();
                        bio.setText(g);
                    }}

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
        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().finish();
            }
        });
        refreshpoint.setOnClickListener(new View.OnClickListener() {
            int point;
            @Override
            public void onClick(View v) {
                if(usertypeholder==1){
                DatabaseReference l=FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                l.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getKey().equals("points_user")){point=Integer.parseInt(dataSnapshot.getValue().toString());
                        mDatabase.updateEmployee2(point,usernameholder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }
                else{}
                String hlo=Integer.toString(pointdb);
                points.setText("Your Current Points is : "+hlo);
            }
        });



        return view;
    }




    private void loadEmployeesFromDatabase() {
        //we are here using the DatabaseManager instance to get all employees
        Cursor cursor = mDatabase.getAllEmployees();

        if (cursor.moveToFirst()) {

            usernameholder= cursor.getString(0);
            emailholder=  cursor.getString(1);
            usertypeholder=cursor.getInt(2);
            pointdb=cursor.getInt(3);




        }
    }


}
