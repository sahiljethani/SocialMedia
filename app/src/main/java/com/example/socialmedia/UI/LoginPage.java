package com.example.socialmedia.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private String mUserid;
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword;
    private ProgressBar mPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.password);
        mPb= findViewById(R.id.progressBar);
   //     setProgressBar(R.id.progressBar);


    }
    @Override
    public void onStart() {
        Log.d(TAG, "onStart: I am here");
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



    public void createAccount(View view) {
        Log.d(TAG, "createAccount: I AM HERE");
        Intent intent = new Intent(LoginPage.this, NewProfile.class);
        startActivity(intent);
        finish();


    }

    public void signIn(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        Log.d(TAG, "signIn:" + email);
        if (!validateForm(email,password)) {
            return;
        }


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mPb.setVisibility(View.VISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END sign_in_with_email]
    }


    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {

        Log.d(TAG, "updateUI: I am here");

        if (user != null) {


            mPb.setVisibility(View.VISIBLE);


            mUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = mFirebaseDatabase.getReference();


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User newuser = new User();
                        newuser = ds.child(mUserid).getValue(User.class);
                        Log.d(TAG, "onComplete: User is added " + newuser.getUsername());

                       /* newuser.setUsername(ds.child(mUserid).getValue(User.class).getUsername());
                        newuser.setEmail(ds.child(mUserid).getValue(User.class).getEmail();
                        newuser.setUserid(mUserid);
                        if(ds.child(mUserid).getValue(User.class).getProfileImageUri()!=null)
                            newuser.setProfileImageUri(ds.child(mUserid).getValue(User.class).getProfileImageUri());
                        else
                            newuser.setProfileImageUri(null);*/
                        ((UserClient) (getApplicationContext())).setUser(newuser);

                    }


                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }


}
