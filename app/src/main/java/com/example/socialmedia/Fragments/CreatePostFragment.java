package com.example.socialmedia.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment {


    private static final String TAG = "CreatePostFragment";

    private User mCurruser;
    private DatabaseReference mUserRef;
    private DatabaseReference mPostRef;
    private ImageView postImage;
    private Button post_bt;
    private Button preview_text_bt;
    private EditText caption_field;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postName;
    private String current_user_id;


    //Firebase Variables

    private StorageReference postImageRef;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurruser=((UserClient)(getActivity().getApplicationContext())).getUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_create_post, container, false);

       //Initializing Variables
        postImage=view.findViewById(R.id.postImage);
        post_bt=view.findViewById(R.id.post_bt);
        preview_text_bt=view.findViewById(R.id.preview_text_bt);
        caption_field=view.findViewById(R.id.caption_field);

        //Firebase
        mAuth= FirebaseAuth.getInstance();
        current_user_id = mCurruser.getUserid();
        postImageRef = FirebaseStorage.getInstance().getReference();
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mUserRef= FirebaseDatabase.getInstance().getReference("Users");

        //Methods
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        post_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri==null) {
                    Toast.makeText(getActivity(), "Please choose an Image", Toast.LENGTH_SHORT).show();
                } else uploadToStorage();
            }
        });


       return view;
    }

    private void uploadToStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postName = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = postImageRef.child("Post Images").child(imageUri.getLastPathSegment() + postName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {

                    Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            SavingPostToDatabase(uri.toString());
                        }
                    });

                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(), "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

  private void SavingPostToDatabase(String postImageUrl) {

        String postDescription = caption_field.getText().toString();

        Posts post= new Posts();

        post.setUserid(mCurruser.getUserid());
        post.setPostDescription(postDescription);
        post.setPostImageUrl(postImageUrl);
        post.setDate(saveCurrentDate);
        post.setTime(saveCurrentTime);

       mPostRef.child(postName).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if(task.isSuccessful())
               {
                  sendUsertoHomeFragment();
                   Toast.makeText(getActivity(), "New Post is uploaded successfully.", Toast.LENGTH_SHORT).show();

               }
               else
               {
                   Toast.makeText(getActivity(), "Error Occured while updating your post", Toast.LENGTH_SHORT).show();

               }

           }
       });

    }

    private void sendUsertoHomeFragment() {

        Log.d(TAG, "inflating Home Fragment " );
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }


    private void pickImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK&& data!=null) {
            imageUri=data.getData();
            postImage.setImageURI(imageUri);
        }
    }

  /*  private Bitmap texttoImage(String text) {
        Paint paint= new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(2);
        paint.setColor(Color.BLUE);
        paint.setTextAlign(Paint.Align.CENTER);
        float baseline=-paint.ascent();
        int width= (int)(paint.measureText(text)+0.5f);
        int height= (int)(baseline+paint.descent()+0.5f);
        Bitmap image=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(image);
        canvas.drawText(text,0,baseline,paint);
        return image;

    }
    */
}
