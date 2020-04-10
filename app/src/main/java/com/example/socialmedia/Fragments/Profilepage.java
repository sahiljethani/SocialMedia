package com.example.socialmedia.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profilepage extends Fragment {

    private static final String TAG = "Profile Page Fragment";


    private User mCurruser;
    private TextView mUsername,mFullname,mBio;
    private CircleImageView mProfile_image;




    public Profilepage() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurruser=((UserClient)(getActivity().getApplicationContext())).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profilepage, container, false);

        //Initializing Profile Details

        mFullname= view.findViewById(R.id.user_fullname);
        mUsername= view.findViewById(R.id.username);
        mBio= view.findViewById(R.id.user_bio);
        mProfile_image = view.findViewById(R.id.profile_pic);

        //Set the layout
        setLayout();


        // Inflate the layout for this fragment
        return view ;
    }

    private void setLayout() {

        mFullname.setText(mCurruser.getFullname());
        mUsername.setText(mCurruser.getUsername());
        mBio.setText(mCurruser.getUserbio());
        Picasso.get().load(mCurruser.getProfileImageUri()).into(mProfile_image);

    }

}
