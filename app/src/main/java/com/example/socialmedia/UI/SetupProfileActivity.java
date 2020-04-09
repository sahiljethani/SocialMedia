package com.example.socialmedia.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfileActivity extends AppCompatActivity {

    private EditText user_fullname;
    private EditText user_bio;
    private EditText user_name;
    private Button saveUserbt;
    private CircleImageView user_profile_pic;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private StorageReference mUserProfileImageRef;
    String currentUserId;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        //Initializing Variables
        user_fullname = findViewById(R.id.user_fullname);
        user_bio = findViewById(R.id.user_bio);
        saveUserbt = findViewById(R.id.saveUserbt);
        user_profile_pic= findViewById(R.id.profile_image);
        user_name=findViewById(R.id.user_name);

        //Firebase Variables
        mAuth= FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        mUserRef= FirebaseDatabase.getInstance().getReference("Users");
        mUserProfileImageRef= FirebaseStorage.getInstance().getReference().child("profile_images");

        saveUserbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        user_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                 resultUri = result.getUri();
                 try {
                     Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                     user_profile_pic.setImageBitmap(bitmap);
                 }   catch (IOException e) {
                     e.printStackTrace();
                 }

            } else {
                Toast.makeText(this, "Error Occurred: Image cant be cropped, Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void uploadImage() {
        if(resultUri==null) return;

        final StorageReference filePath = mUserProfileImageRef.child(currentUserId + ".jpg");
        filePath.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("SetupProfile", "Image Uploaded: " + taskSnapshot.getMetadata().getPath());

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("SetupProfile", "Image url is :" + uri);
                                saveUserInfo(uri.toString());

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SetupProfile","Failed to upload Image"+e.getMessage());

            }
        });

    }


    private void saveUserInfo(String profileImageUrl) {

        FirebaseUser currentUser= mAuth.getCurrentUser();
        String fullname=user_fullname.getText().toString();
        String userbio=user_bio.getText().toString();
        String email= currentUser.getEmail();
        String userid= currentUser.getUid();
        String username=user_name.getText().toString();



        if (!validateForm(fullname, userbio,username)) {
            return;
        }

        User user= new User();
        user.setEmail(email);
        user.setFullname(fullname);
        user.setUserbio(userbio);
        user.setUserid(userid);
        user.setUsername(username);
        user.setProfileImageUri(profileImageUrl);

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

    private boolean validateForm(String fullname, String userbio, String username) {

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
        if (TextUtils.isEmpty(username)) {
            user_name.setError("Required.");
            valid = false;
        } else {
            user_name.setError(null);
        }
        return valid;
    }
}
