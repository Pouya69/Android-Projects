package com.pouyasalehi.dada;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTV, passwordTV,userrg;
    private Button regBtn,gologin;
    private CheckBox checkteacher;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        initializeUI();
        emailTV.setHint("Email");
        passwordTV.setHint("Password");
        userrg.setHint("Username");
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
        gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,  LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        final String email, password,userl;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        userl=userrg.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(userl)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            final FirebaseUser user3 = FirebaseAuth.getInstance().getCurrentUser();

                                if(!checkteacher.isChecked()){
                                    final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user3.getUid());
                                                mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(!dataSnapshot.exists()){
                                                Map<String, Object> userMap = new HashMap<>();
                                                FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
                                                userMap.put("email", user2.getEmail());
                                                userMap.put("points_user","0");
                                                userMap.put("name", userl);
                                                userMap.put("feeling", "confused");
                                                userMap.put("bio", "A new user");
                                                FirebaseUser user4=FirebaseAuth.getInstance().getCurrentUser();
                                                mUserDB.updateChildren(userMap);

                                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                        .setDisplayName(userl).build();

                                                                user4.updateProfile(profileUpdates);



                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else{
                                    final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("teacher").child(user3.getUid());
                                    mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.exists()){
                                                Map<String, Object> userMap = new HashMap<>();
                                                FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
                                                userMap.put("email", user2.getEmail());
                                                userMap.put("points_teacher","0");
                                                userMap.put("name", userl);
                                                userMap.put("feeling", "confused");
                                                userMap.put("bio", "A new teacher");
                                                mUserDB.updateChildren(userMap);
                                                FirebaseUser user4=FirebaseAuth.getInstance().getCurrentUser();

                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(userl).build();

                                                    user4.updateProfile(profileUpdates);



                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }










                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email8);
        passwordTV = findViewById(R.id.password8);
        userrg = findViewById(R.id.userrg1);
        regBtn = findViewById(R.id.register);
        checkteacher=findViewById(R.id.itisteacher);
        gologin=findViewById(R.id.golgn);
        progressBar = findViewById(R.id.progressBar8);
    }
}