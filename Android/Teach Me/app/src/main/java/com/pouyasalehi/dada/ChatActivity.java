package com.pouyasalehi.dada;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Things h=new Things();
    private boolean sender;
    private RecyclerView mMedia;
    private Dialog dialog;
    private int id=1;
    private RecyclerView chatrecyclerView;
    private MessageAdapter chatrecyclerViewAdapter;
    private RecyclerView.Adapter mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;
    private String picURL;
    private Button checkready;
    int usercount=0;
    ArrayList<MessageObject> messageList;
    private String examId,momp,usernameholder,emailholder;
    private int usertypeholder;
    ChatObject mChatObject;
    private DatabaseManager mDatabase;
    DatabaseReference mChatMessagesDb;
    DatabaseReference mChatMessagesDb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
///use for exam too!
        TextView chattingwithg=findViewById(R.id.chatting);
        chattingwithg.setVisibility(View.GONE);







        mDatabase = new DatabaseManager(getApplicationContext());
        loadEmployeesFromDatabase();
        dialog = new Dialog(ChatActivity.this);

        if(usertypeholder==1){
            DatabaseReference imgDB=FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            imgDB.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()){
                        if(dataSnapshot.getKey().equals("profilePic")) {
                            picURL=dataSnapshot.getValue().toString();
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
        }else {
            DatabaseReference imgDB = FirebaseDatabase.getInstance().getReference().child("teacher").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            imgDB.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getKey().equals("profilePic")) {
                            picURL = dataSnapshot.getValue().toString();
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

        checkready=findViewById(R.id.readyex);
        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");
        try{
            chattingwithg.setText(mChatObject.getChatId());}
        catch (Exception p){chattingwithg.setText("Unknown");}
        mChatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");
        examId = mChatObject.getChatId();
        mChatMessagesDb2= FirebaseDatabase.getInstance().getReference().child("exam").child(examId).child("users");
        Button mSend = findViewById(R.id.send);
        Button mAddMedia = findViewById(R.id.addMedia);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        DatabaseReference usersChat = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("info").child("users");

        usersChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usercount+=1;
                if(usercount==2){
                    checkready.setVisibility(View.VISIBLE);
                }
                if(usercount>2){
                    checkready.setVisibility(View.GONE);
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
        if(usercount==2){
            checkready.setVisibility(View.VISIBLE);
        }
        initializeMessage();
        initializeMedia();
        getChatMessages();
         checkready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent classchange = new Intent(ChatActivity.this, ExamActivity.class);
                Intent classchange2 = new Intent(ChatActivity.this, ExamerActivity.class);
//attach the key value pair using putExtra to this intent
                classchange.putExtra("EXAM_ID",mChatObject.getChatId());
                classchange2.putExtra("EXAM_ID",mChatObject.getChatId());

                reqexamTheUser();
                if(usertypeholder==1){
                    startActivity(classchange);}
                else{startActivity(classchange2);}
                final Map newMessageMap2 = new HashMap<>();


                newMessageMap2.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseDatabase.getInstance().getReference().child("exam").child(mChatObject.getChatId()).child("users").updateChildren(newMessageMap2);
            }
        });

    }



    private void reqexamTheUser(){



           // FirebaseDatabase.getInstance().getReference().child("exam").child(mChatObject.getChatId()).removeValue();







            for(UserObject mUser : mChatObject.getUserObjectArrayList()){
                if(!mUser.getuid().equals(FirebaseAuth.getInstance().getUid())){
                    //check here!
                    new SendNotification("exam with "+usernameholder, "New Message", mUser.getnotificationKey());
                }
            }





    }






    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@android.support.annotation.Nullable String s) {
                if(dataSnapshot.exists()){
                    String  text = "",
                            creatorID = "",profilePic="",creatorUID="";
                    ArrayList<String> mediaUrlList = new ArrayList<>();

                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("profilePic").getValue() != null)
                        profilePic = dataSnapshot.child("profilePic").getValue().toString();
                    if(dataSnapshot.child("creatorUID").getValue() != null)
                        creatorUID = dataSnapshot.child("creatorUID").getValue().toString();
                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();
                        if(creatorID.equals(usernameholder)){sender=true;
                            Things ko=new Things();
                            ko.sender=sender;}
                        else{sender=false;
                        Things ko=new Things();
                        ko.sender=sender;}

                    if(dataSnapshot.child("media").getChildrenCount() > 0)
                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapshot.getValue().toString());
                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text, mediaUrlList,sender,profilePic,ChatActivity.this,id++,creatorUID);

                    messageList.add(mMessage);
                    //addMoreCoinsToTheList(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    chatrecyclerViewAdapter.notifyDataSetChanged();
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

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;
    private void sendMessage(){
        mMessage = findViewById(R.id.messageInput);

        String messageId = mChatMessagesDb.push().getKey();
        final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);

        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", usernameholder);
        newMessageMap.put("creatorUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        newMessageMap.put("profilePic",picURL);
        if(!mMessage.getText().toString().isEmpty())

            newMessageMap.put("text", mMessage.getText().toString());


        if(!mediaUriList.isEmpty()){
            for (String mediaUri : mediaUriList){
                String mediaId = newMessageDb.child("media").push().getKey();
                mediaIdList.add(mediaId);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat/").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());

                                totalMediaUploaded++;
                                if(totalMediaUploaded == mediaUriList.size())
                                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);

                            }
                        });
                    }
                });
            }
        }else{
            if(!mMessage.getText().toString().isEmpty())
                updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
        }
    }


    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUploaded=0;
        mMediaAdapter.notifyDataSetChanged();

        String message;

        if(newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();
        else
            message = "Sent Media";

        for(UserObject mUser : mChatObject.getUserObjectArrayList()){
            if(!mUser.getuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                new SendNotification(message, "New Message From : "+usernameholder, mUser.getnotificationKey());
            }
        }
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        chatrecyclerView= findViewById(R.id.messageList);
        chatrecyclerViewAdapter = new MessageAdapter(messageList,ChatActivity.this);
        //chatrecyclerViewAdapter = new RecyclerViewAdapter(modelArrayList);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        chatrecyclerView.setLayoutManager(mChatLayoutManager);
        chatrecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatrecyclerView.setHasFixedSize(false);
        chatrecyclerView.setAdapter(chatrecyclerViewAdapter);
        chatrecyclerView.setNestedScrollingEnabled(false);
        //mChat.setLayoutManager(mChatLayoutManager);

       // mChat.setAdapter(mChatAdapter);

    }



    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    private void addMoreCoinsToTheList(MessageObject msg) {
        ArrayList<MessageObject> models = new ArrayList<>();

        for (MessageObject model : messageList) {
            if(model!=null){models.add(model.clone());}

        }
        models.add(msg);
        //models.add(new MessageObject(i++, "Tron", 1));
        //models.add(new MessageObject(i++, "Ripple", 5));
        //models.add(new Model(i++, "NEO", 100));
       // models.add(new Model(i++, "OMG", 20));


        chatrecyclerViewAdapter.setData(models);

    }
    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onBackPressed() {
        Things lo=new Things();
        lo.chattingwith="";
        finish();
        return;
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
