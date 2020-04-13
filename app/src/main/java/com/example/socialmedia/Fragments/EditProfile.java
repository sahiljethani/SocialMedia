package com.example.socialmedia.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.MainActivity;
import com.example.socialmedia.UI.SetupProfileActivity;
import com.example.socialmedia.UserClient.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class EditProfile extends Fragment {


    private static final String TAG = "EditProfile";

    private EditText user_bio;
    private Button saveUserbt;
    private CircleImageView user_profile_pic;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private StorageReference mUserProfileImageRef;
    private User mCurruser;
    String currentUserId;

    Uri resultUri;

    public EditProfile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurruser=((UserClient)(getActivity().getApplicationContext())).getUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //Initializing Variables
        user_bio = view.findViewById(R.id.edit_user_bio);
        saveUserbt = view.findViewById(R.id.edit_changeBtn);
        user_profile_pic= view.findViewById(R.id.editProfile_image);
        //user_name=view.findViewById(R.id.user_name);

        //Firebase Variables
        mAuth= FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        mUserRef= FirebaseDatabase.getInstance().getReference("Users");
        mUserProfileImageRef= FirebaseStorage.getInstance().getReference().child("profile_images");

        setLayout();

        saveUserbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CLicked");
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

        return view;
    }


    private void setLayout() {

        if(!(mCurruser.getUserbio().equals("")))
            user_bio.setText(mCurruser.getUserbio());
        if(!mCurruser.getProfileImageUri().equals("defaultpic"))
            Picasso.get().load(mCurruser.getProfileImageUri()).into(user_profile_pic);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    user_profile_pic.setImageBitmap(bitmap);
                }   catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "onActivityResult: Image can not be cropped ");
           }

        }
    }


    private void uploadImage() {
        if(resultUri==null)

        {  saveUserInfo(mCurruser.getProfileImageUri());
            return;

        }

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

        String userbio=user_bio.getText().toString();
        String userid= mCurruser.getUserid();

        User user= new User();
        user.setEmail(mCurruser.getEmail());
        user.setFullname(mCurruser.getFullname());
        user.setUserbio(userbio);
        user.setUserid(userid);
        user.setUsername(mCurruser.getUsername());
        user.setProfileImageUri(profileImageUrl);
        ((UserClient)(getActivity().getApplicationContext())).setUser(user);


        mUserRef.child(userid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    SendUserToProfilePage();
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

    private void SendUserToProfilePage() {

        Log.d(TAG, "inflating Profile Page " );
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();

    }


}
