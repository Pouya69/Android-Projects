package com.pouyasalehi.dada;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ExamerActivity extends AppCompatActivity {
    private ChildEventListener totalExamListen,answersListen,userListen;
    private DatabaseReference totalExamDB,dbExam;
    private int finalPoint=0;
    private String totalExamID;
    private Button sendQuestionBtn,plusPointBtn,finishExamBtn,minesPointBtn;
    private EditText questionEdt;
    private TextView answertxt,showPointstxt;
    private DatabaseManager mDatabase;
    private String examId,momp,usernameholder,emailholder;
    private int usertypeholder;
    private String otherUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examer);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            totalExamID =(String) b.get("EXAM_ID");
           if(totalExamID.isEmpty()){finish();}
        }else {finish();}




        initializeUIandFirebase();
//starting the activity
        totalExamDB.child("users").addChildEventListener(userListen);

        //if(otherUser.isEmpty()){finish();}

        sendQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String question=questionEdt.getText().toString();
             if(!question.isEmpty()){
                 if(question.endsWith("?")){
                     newQuestion(question);
             }}

            }
        });

        finishExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishExam();
            }
        });
        plusPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusPoint();
            }
        });
        minesPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalPoint-=1;
                showPointstxt.setText(Integer.toString(finalPoint));
            }
        });



    }
    private void initializeUIandFirebase(){
        finishExamBtn=findViewById(R.id.buttonFinishExam);
        questionEdt=findViewById(R.id.editTextquest);
        answertxt=findViewById(R.id.textViewAnswer);
        showPointstxt=findViewById(R.id.textViewShowPoints);
        sendQuestionBtn=findViewById(R.id.buttonsendquest);
        plusPointBtn=findViewById(R.id.buttonPlusPoint);
        minesPointBtn=findViewById(R.id.buttonMinesPoint);

        totalExamDB= FirebaseDatabase.getInstance().getReference().child("exam").child(totalExamID);

        answersListen=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if (dataSnapshot.exists()){

                      if(dataSnapshot.getKey().equals("answer")){   String answer= dataSnapshot.getValue().toString();
                          String f=answertxt.getText().toString()+"\n"+answer;
                          answertxt.setText(f);}



               }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){

                    if(dataSnapshot.getKey().equals("answer")){   String answer= dataSnapshot.getValue().toString();
                    String f=answertxt.getText().toString()+"\n"+answer;
                        answertxt.setText(f);}



                }
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
       userListen= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    if(!dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                       otherUser=dataSnapshot.getValue().toString();
                       String f=answertxt.getText().toString()+"\n"+"** Your student joined!";
                       answertxt.setText(f);
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // custom dialog
                try{}catch (Exception e){}

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };





    }



   private void listenForAnswer(){
       try{
           dbExam.removeEventListener(answersListen);}catch (Exception e){}
           dbExam.addChildEventListener(answersListen);

   }




    private void newQuestion(String question){
        String key=totalExamDB.push().getKey();
        dbExam=totalExamDB.child(key);
        Map questionMap= new HashMap<>();
        questionMap.put("question",question);
        dbExam.updateChildren(questionMap);
        listenForAnswer();
    }

    private void finishExam(){
        try{if(finalPoint!=0){  FirebaseDatabase.getInstance().getReference().child("user").child(otherUser).child("points_user").setValue(Integer.toString(finalPoint));}}catch (Exception e){}


        FirebaseDatabase.getInstance().getReference().child("exam").child(totalExamID).removeValue();
         finish();
    }

    private void plusPoint(){
        finalPoint+=1;
        showPointstxt.setText(Integer.toString(finalPoint));
    }



}
