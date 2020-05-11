package com.pouyasalehi.dada;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private Button btnchangeusername,changeprofilePicture,saveChanges,updatebio;
    private  String usernameholder,emailholder,feel,bio1,oldfeeling,oldbio;
    private int usertypeholder,pointdb;
    private Dialog dialog;
    private StorageReference riversRef2;
    private ImageView imgView,nextEmoji,prevEmoji,emoji;
    private int listPosition=0;
    private StorageReference mStorageRef;
    private DatabaseManager mDatabase;
    private Uri imageURI;
    private ArrayList<String> feelinglist= new ArrayList<>();
    private EditText txtusername,bioGet;
    private TextView btnsignoutpls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDatabase=new DatabaseManager(SettingsActivity.this);
        loadEmployeesFromDatabase();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        initializeFeelingList();

        bioGet = findViewById(R.id.bioGet);

        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user");
        final DatabaseReference teacherDB = FirebaseDatabase.getInstance().getReference().child("teacher");
        if(usertypeholder==1){userDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    }
                    for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                        if(childsnapshot.getKey().equals("bio")){
                            oldbio=childsnapshot.getValue().toString();
                            bioGet.setText(childsnapshot.getValue().toString());
                        }if(childsnapshot.getKey().equals("feeling")){
                            setFeeling(childsnapshot.getValue().toString());
                            oldfeeling = childsnapshot.getValue().toString();
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
        });}else{ teacherDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    }
                    for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                        if(childsnapshot.getKey().equals("bio")){
                            oldbio=childsnapshot.getValue().toString();
                            bioGet.setText(childsnapshot.getValue().toString());
                        }if(childsnapshot.getKey().equals("feeling")){
                            setFeeling(childsnapshot.getValue().toString());
                            oldfeeling = childsnapshot.getValue().toString();
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
        });}





        changeprofilePicture=findViewById(R.id.btnchangePic);
        changeprofilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                dialog = new Dialog(SettingsActivity.this);
                dialog.setContentView(R.layout.customalertwaiting);
                Button dialogButtonPick = dialog.findViewById(R.id.dialogButtonPickImage);
                Button dialogButtonOK = dialog.findViewById(R.id.dialogButtonOK2);
                final TextView txtsa = dialog.findViewById(R.id.pickimageprofile);
                imgView = dialog.findViewById(R.id.profilePicturePick);

                dialog.show();
                // if button is clicked, close the custom dialog
                dialogButtonPick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePic();
                    }
                });
                dialogButtonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            upload(imageURI);
                            dialog.dismiss();
                        }catch (Exception e){
                            Toast.makeText(SettingsActivity.this,"Pic an image first..",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        nextEmoji = findViewById(R.id.nextEmoji);
        prevEmoji = findViewById(R.id.previousEmoji);
        saveChanges = findViewById(R.id.buttonSaveChanges);
        updatebio = findViewById(R.id.btnchangebio);
        emoji = findViewById(R.id.emojiSettings);
        btnsignoutpls=findViewById(R.id.signoutTxt);
        txtusername=findViewById(R.id.edtnewusr);
        txtusername.setText(usernameholder);
        btnchangeusername=findViewById(R.id.btnchangeusername);
        prevEmoji.setVisibility(View.INVISIBLE);
        updatebio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bio = bioGet.getText().toString();

                if(!bio.isEmpty()){
                    if(bio.length()>45){
                        Toast.makeText(getApplicationContext(),"Max bio length : 45",Toast.LENGTH_LONG).show();

                    }else{
                        if(usertypeholder==1){
                            Map map = new HashMap<>();
                            if(!bio.equals(oldbio)){map.put("bio",bio);
                                userDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                            }else{
                                Toast.makeText(getApplicationContext(),"Change your bio",Toast.LENGTH_LONG).show();
                            }


                        }else{
                            Map map = new HashMap<>();
                            if(!bio.equals(oldbio)){map.put("bio",bio);
                                teacherDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                            }else{
                                Toast.makeText(getApplicationContext(),"Change your bio",Toast.LENGTH_LONG).show();
                            }
                        }
                    }


                }else{Toast.makeText(getApplicationContext(),"Empty bio",Toast.LENGTH_LONG).show();}
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!feel.isEmpty()){
                    if(usertypeholder==1){

                        Map map = new HashMap<>();
                        if(!feel.equals(oldfeeling)){ map.put("feeling",feel);
                            userDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                        }else{
                            Toast.makeText(getApplicationContext(),"Change your feeling status",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Map map = new HashMap<>();
                        if(!feel.equals(oldfeeling)){ map.put("feeling",feel);
                            teacherDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map);
                        }else{
                            Toast.makeText(getApplicationContext(),"Change your feeling status",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        nextEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPosition+=1;
                if(listPosition==6){nextEmoji.setVisibility(View.INVISIBLE);}else{nextEmoji.setVisibility(View.VISIBLE);}
                if(listPosition==0){prevEmoji.setVisibility(View.INVISIBLE);}else{prevEmoji.setVisibility(View.VISIBLE);}
                feel = feelinglist.get(listPosition);
                setFeeling(feel);
            }
        });

        prevEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPosition-=1;
                if(listPosition==6){nextEmoji.setVisibility(View.INVISIBLE);}else{nextEmoji.setVisibility(View.VISIBLE);}
                if(listPosition==0){prevEmoji.setVisibility(View.INVISIBLE);}else{prevEmoji.setVisibility(View.VISIBLE);}
                feel = feelinglist.get(listPosition);
                setFeeling(feel);
            }
        });
        btnchangeusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateusername();
            }
        });
        btnsignoutpls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.deleteEmployee("0");
                try{  mDatabase.deleteEmployee("1");
                    mDatabase.deleteEmployee("2");}catch (Exception e){}

                OneSignal.setSubscription(false);
                //check next line :D
                OneSignal.logoutEmail();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
    private void setFeeling(String feel){
        if (feel.equals("confused")){ emoji.setImageResource(R.drawable.confused); }
        if (feel.equals("bad")){emoji.setImageResource(R.drawable.bad);}
        if (feel.equals("dating")){emoji.setImageResource(R.drawable.dating);}
        if (feel.equals("happy")){emoji.setImageResource(R.drawable.happy);}
        if (feel.equals("lucky")){emoji.setImageResource(R.drawable.lucky);}
        if (feel.equals("sick")){emoji.setImageResource(R.drawable.sick);}
        if (feel.equals("studying")){emoji.setImageResource(R.drawable.studying);}
    }
    private void initializeFeelingList() {
        feelinglist.add("confused");
        feelinglist.add("bad");
        feelinglist.add("lucky");
        feelinglist.add("sick");
        feelinglist.add("happy");
        feelinglist.add("dating");
        feelinglist.add("studying");


    }


    private void imagePic(){
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

    }
    private void upload(final Uri file){
        riversRef2 = mStorageRef.child("profileImages/" + usernameholder+"/"+"profile" + ".jpg");
        try{riversRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }


        });

            UploadTask uploadTask = riversRef2.putFile(file);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if(usertypeholder==1){
                                DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map imageURL = new HashMap<>();
                                imageURL.put("profilePic",uri.toString());
                                db.updateChildren(imageURL);

                            }else{
                                DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map imageURL = new HashMap<>();
                                imageURL.put("profilePic",uri.toString());
                                db.updateChildren(imageURL);
                            }


                        }
                    });

                }
            });

        }catch (Exception e){
            UploadTask uploadTask = riversRef2.putFile(file);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            if(usertypeholder==1){
                                DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map imageURL = new HashMap<>();
                                imageURL.put("profilePic",uri.toString());
                                db.updateChildren(imageURL);

                            }else{
                                DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map imageURL = new HashMap<>();
                                imageURL.put("profilePic",uri.toString());
                                db.updateChildren(imageURL);
                            }


                        }
                    });

                }
            });
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                // currImageURI is the global variable I'm using to hold the content:// URI of the image




                // if button is clicked, close the custom dialog

                imageURI = data.getData();
                ImageView imgView2 = (ImageView)dialog.findViewById(R.id.profilePicturePick);
                imgView2.setImageURI(imageURI);
            }
        }
    }
    private void updateusername(){
        String newUsername=txtusername.getText().toString();
        FirebaseUser user2345 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user2345.getUid());
        mDatabase.updateEmployee(newUsername);
        Map<String, Object> userMap = new HashMap<>();
        if(!newUsername.equals(usernameholder)){  userMap.put("name",newUsername);
            mUserDB.updateChildren(userMap);}else{
            Toast.makeText(getApplicationContext(),"Enter a new username",Toast.LENGTH_LONG).show();
        }

        return;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        finish();
    }

}
