package com.example.socialmedia.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupProfileActivity extends AppCompatActivity {

    private EditText user_fullname;
    private EditText user_bio;
    private Button saveUserbt;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        //Initializing Variables
        user_fullname = findViewById(R.id.user_fullname);
        user_bio = findViewById(R.id.user_bio);
        saveUserbt = findViewById(R.id.saveUserbt);

        //Firebase Variables
        mAuth= FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference("Users");

        saveUserbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }

    private void saveUserInfo() {

        FirebaseUser currentUser= mAuth.getCurrentUser();
        String fullname=user_fullname.getText().toString();
        String userbio=user_bio.getText().toString();
        String email= currentUser.getEmail();
        String userid= currentUser.getUid();
        Intent intent=getIntent();
        final String value_username=intent.getStringExtra("username");

        if (!validateForm(fullname, userbio)) {
            return;
        }

        User user= new User();
        user.setEmail(email);
        user.setFullname(fullname);
        user.setUserbio(userbio);
        user.setUserid(userid);
        user.setUsername(value_username);

      mUserRef.child(userid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                  SendUserToMainActivity();
                  Log.d("Setup", "onComplete: User added");

              } else
                  Log.d("Setup", "onComplete: Not able to add user");

          }

      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
          }
      });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private boolean validateForm(String fullname, String userbio) {

        boolean valid = true;
        if (TextUtils.isEmpty(fullname)) {
            user_fullname.setError("Required.");
            valid = false;
        } else {
            user_fullname.setError(null);
        }
        if (TextUtils.isEmpty(userbio)) {
            user_bio.setError("Required.");
            valid = false;
        } else {
            user_bio.setError(null);
        }
        return valid;
    }
}
