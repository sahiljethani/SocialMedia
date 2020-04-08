package com.example.socialmedia.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword, mUsername;
    String email;
    String username;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.emailId);
        mPassword = findViewById(R.id.Password);
        mUsername = findViewById(R.id.username);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void createAccount(View view) {

        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        username = mUsername.getText().toString();

        if (!validateForm(email, password, username)) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            sendUsertoSetup();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: I AM HERE");
            }
        });

    }

private void sendUsertoSetup() {

    Intent intent = new Intent(RegisterActivity.this, SetupProfileActivity.class);
    intent.putExtra("username",username);
    startActivity(intent);
    finish();
}

    private boolean validateForm(String email, String password, String username) {
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
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Required.");
            valid = false;
        } else {
            mUsername.setError(null);
        }
        return valid;
    }

}
