package com.pouyasalehi.dada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private String usernameholder="";
    private String emailholder="";
    private int usertypeholder=0;

    private EditText emailTV, passwordTV,userTV;
    int h=0;
    private Button loginBtn,goregister;
    private ProgressBar progressBar;
    private DatabaseManager mDatabase;
    private CheckBox checkteacher;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUI();
        emailTV.setHint("Email");
        passwordTV.setHint("Password");
        userTV.setHint("Username");
        mDatabase = new DatabaseManager(this);
        mAuth = FirebaseAuth.getInstance();
        userIsLoggedIn();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
        goregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity( new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
    private void userIsLoggedIn() {
        if(mAuth.getCurrentUser()!=null){
            loadEmployeesFromDatabase();
            if(!usernameholder.isEmpty()){
                if(!emailholder.isEmpty()){
                    if(usertypeholder!=0){




                        startActivity( new Intent(LoginActivity.this, MainActivity.class));
                        finish();}else{mDatabase.deleteEmployee("1");}
                }else{mDatabase.deleteEmployee("1");}
          }else{mDatabase.deleteEmployee("1");}
           }
        else{mDatabase.deleteEmployee("1");}
        }
    private void loadEmployeesFromDatabase() {
        //we are here using the DatabaseManager instance to get all employees
        Cursor cursor = mDatabase.getAllEmployees();

        if (cursor.moveToFirst()) {

            usernameholder= cursor.getString(0);
            emailholder=  cursor.getString(1);
            usertypeholder=cursor.getInt(2);





        }
        if(usernameholder.isEmpty()){
            try{mDatabase.deleteEmployee("0");
                mDatabase.deleteEmployee("1");}catch (Exception e){}
            try{mDatabase.deleteEmployee("0");
                mDatabase.deleteEmployee("1");}catch (Exception e){}

        }else if(usertypeholder==0){  try{mDatabase.deleteEmployee("0");
            mDatabase.deleteEmployee("1");}catch (Exception e){}
            try{mDatabase.deleteEmployee("0");
                mDatabase.deleteEmployee("1");}catch (Exception e){}}
        else if(emailholder.isEmpty()){ try{mDatabase.deleteEmployee("0");
            mDatabase.deleteEmployee("1");}catch (Exception e){}
            try{mDatabase.deleteEmployee("0");
                mDatabase.deleteEmployee("1");}catch (Exception e){}}

    }

    private void loginUserAccount() {


        final String email, password,name3;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        name3=userTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(name3)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Please enter username...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {

                Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);

            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);


                            if(!FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(name3)){
                                Toast.makeText(getApplicationContext(), "Wrong Username", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            }else{
                                if(checkteacher.isChecked()){
                                    h=2;

                                }
                                else{
                                    h=1;
                                }

                                addUserDetails(name3,email,h);




                                startActivity(new Intent(LoginActivity.this,  MainActivity.class));
                                finish();
                            }




                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initializeUI() {
        checkteacher=findViewById(R.id.itisteacher23);
        emailTV = findViewById(R.id.email42);
        passwordTV = findViewById(R.id.password22);
        userTV = findViewById(R.id.userp1);
        loginBtn = findViewById(R.id.login2);
        goregister=findViewById(R.id.gorgster2);
        progressBar = findViewById(R.id.progressBar12);
    }


    private void addUserDetails(String username,String email23,int userType) {





        //adding the employee with the DatabaseManager instance
        if (mDatabase.addEmployee(username, email23, userType))
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();

    }



    }








