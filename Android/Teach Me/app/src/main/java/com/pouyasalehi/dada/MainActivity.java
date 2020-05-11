package com.pouyasalehi.dada;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pouyasalehi.dada.mainfragments.MainFragment;
import com.pouyasalehi.dada.mainfragments.SettingsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;

    private StorageReference riversRef2;
    private StorageReference mStorageRef;
    private ImageView imgView;
    private int exit_int=0;
    private  String usernameholder,emailholder;
    private Context mContext;
    private AppCompatActivity mActivity;
    private Uri imageURI;
    private int usertypeholder;



    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(" Read Contacts and Read External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                },
                                123
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE

                        },
                        123
                );
            }
        } else {
            // Do something, when permissions are already granted
            // startService(new Intent(this, MyService.class));

            // JobIntentService for background task

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // When request is cancelled, the results array are empty
                if (
                        (grantResults.length > 0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ) {
                    // Permissions are granted
                    // startService(new Intent(this, MyService.class));

                    // JobIntentService for background task


                } else {
                    // Permissions are denied

                }
                return;
            }
        }
    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        DatabaseReference dbImage=FirebaseDatabase.getInstance().getReference().child("user");
        DatabaseReference dbImage2=FirebaseDatabase.getInstance().getReference().child("teacher");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ChildEventListener listenImage=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        if(!dataSnapshot.child("profilePic").exists()){
                            final Dialog dialog = new Dialog(MainActivity.this);
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
                                    checkPermission();
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
                                        Toast.makeText(getApplicationContext(),"Pic an image first..",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{}



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
        };
        if(usertypeholder==1){
            dbImage.addChildEventListener(listenImage);
        }
        else{
            dbImage2.addChildEventListener(listenImage);
        }









        mDatabase = new DatabaseManager(getApplicationContext());
        loadEmployeesFromDatabase();

        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setBehavior(new BottomNavigationBehavior());

     // load the store fragment by default





        Fragment fragment2;
        fragment2 = new MainFragment();
        loadFragment2(fragment2);






}
    private void imagePic(){
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

    }
    private void upload(Uri file){
        riversRef2 = mStorageRef.child("profileImages/" + usernameholder+"/"+ "profile" + ".jpg");

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                // currImageURI is the global variable I'm using to hold the content:// URI of the image
                imageURI = data.getData();
                imgView.setImageURI(imageURI);
            }
        }
    }

    // And to convert the image URI to the direct file system path of the image file

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_favorites:
                    fragment = new MainFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.action_nearby:
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;


            }

            return false;
        }
    };




    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void loadFragment2(Fragment fragment) {
        // load fragment
        this.getFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit_int=exit_int+1;
        if(exit_int==2){finish();}
        else{
            Toast.makeText(this, "Press Back To Exit", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    exit_int=0;
                }
            }, 1000);
        }
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
