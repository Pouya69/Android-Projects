package com.pouyasalehi.dada;

import android.app.Dialog;
import android.content.Intent;
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

public class ExamActivity extends AppCompatActivity {
    private String totalExamID,examid;
    private Button sendAnswerBtn;
    private EditText answerEdt;
    private TextView questiontxt;
    private ChildEventListener totalExamListen;
    private DatabaseReference totalExamDB,dbExam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            totalExamID =(String) b.get("EXAM_ID");

        }else {finish();}

        initializeUIandFirebase();
        listenForNewExam();

        sendAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer=answerEdt.getText().toString();
                if(!answer.isEmpty()){sendAnswer(answer);}

            }
        });


    }



    private void listenForNewExam(){


            try{
                totalExamDB.removeEventListener(totalExamListen);}catch (Exception e){}
            totalExamDB.addChildEventListener(totalExamListen);


    }

    private void sendAnswer(String answer){

        Map answerMap=new HashMap<>();
        answerMap.put("answer",answer);
        dbExam.updateChildren(answerMap);
        sendAnswerBtn.setVisibility(View.GONE);
    }


    private void initializeUIandFirebase(){
        sendAnswerBtn=findViewById(R.id.checkifitiscorrect);
        sendAnswerBtn.setVisibility(View.GONE);
        answerEdt=findViewById(R.id.editTextanswer3);
        questiontxt=findViewById(R.id.quesexample);
        totalExamDB=FirebaseDatabase.getInstance().getReference().child("exam").child(totalExamID);

        totalExamListen=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    if(!dataSnapshot.getKey().equals("users")){
                        if(!dataSnapshot.child("answer").exists()){
                            dbExam=totalExamDB.child(dataSnapshot.getKey());
                            String question=dataSnapshot.child("question").getValue().toString();
                            String finalText=questiontxt.getText().toString()+"\n"+"++  "+question;
                            questiontxt.setText(finalText);
                            sendAnswerBtn.setVisibility(View.VISIBLE);

                        }


                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // custom dialog
                final Dialog dialog = new Dialog(getApplicationContext());
                dialog.setContentView(R.layout.customalertwelcome);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                TextView dialogText=dialog.findViewById(R.id.textAlertDialog);
                dialogText.setText("Your Teacher Just Left");
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
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
