package com.example.socialmedia.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfileFragment extends Fragment {


    private static final String TAG = "ViewProfileFragment";


    private String userId;
    private User user;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private TextView User_Fullname, User_bio, Username;
    private CircleImageView User_image;
    private Button mBtnFollow;
    private Context context;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=container.getContext();
        View view=inflater.inflate(R.layout.fragment_view_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");

        //Initializing

        User_Fullname= view.findViewById(R.id.ViewProfile_fragment_fullname);
        User_bio=view.findViewById(R.id.userProfile_fragment_bio);
        Username= view.findViewById(R.id.ViewProfile_fragment_username);
        User_image= view.findViewById(R.id.ViewProfile_fragment_image);
        mBtnFollow= view.findViewById(R.id.btnFollow);

        mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowAction();
            }
        });

        return view;
    }

    private void FollowAction() {

        Drawable myDrawable = context.getResources().getDrawable(R.drawable.btnfollow);

        mBtnFollow.setBackground(myDrawable);
        mBtnFollow.setText("Following");
        mBtnFollow.setTextColor(Color.BLACK);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        userId = bundle.getString(FragmentActionListener.KEY_SELECTED_USERID);
        getUser(userId);
    }

    private void getUser(String userId) {


        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    user=dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: Curr User is " + user.getFullname());
                    setLayout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setLayout() {

        User_Fullname.setText(user.getFullname());
        Username.setText(user.getUsername());
        if(!(user.getUserbio().equals("")))
            User_bio.setText(user.getUserbio());
        else
            User_bio.setText(getResources().getString(R.string.Default_bio));
        if(!user.getProfileImageUri().equals("defaultpic"))
            Picasso.get().load(user.getProfileImageUri()).into(User_image);



    }


}
