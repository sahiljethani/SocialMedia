package com.example.socialmedia.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.PostViewHolder;
import com.example.socialmedia.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FloatingActionButton fab_post;
    private RecyclerView userpostList;
    private DatabaseReference mPostRef;
    FirebaseRecyclerAdapter<Posts,PostViewHolder> adapter;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);

        //Initializing Variables
        fab_post= view.findViewById(R.id.fab_post);
        userpostList=view.findViewById(R.id.user_post_recyclerview);
        mPostRef= FirebaseDatabase.getInstance().getReference("Posts");

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userpostList.setLayoutManager(linearLayoutManager);
        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCreatePost();
            }
        });

        showUserPost();
        return view;

    }

    private void showUserPost() {

        FirebaseRecyclerOptions options= new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(mPostRef,Posts.class).build();

        adapter= new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Posts model) {
                holder.txtfullname.setText(model.getFullname());
                holder.txtdesc.setText(model.getPostDescription());
                holder.txtdate.setText(model.getDate());
                holder.txttime.setText(model.getTime());
                holder.setProfileImage(model.getProfileImage(),getContext());
                holder.setPostImageUrl(model.getPostImageUrl());

            }
           @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.display_post,parent,false);
                return new PostViewHolder(view);
            }
        };

       adapter.startListening();
       adapter.notifyDataSetChanged();
       userpostList.setAdapter(adapter);

    }

    private void gotoCreatePost() {

        Log.d(TAG, "inflating Create Post" );
        CreatePostFragment fragment = new CreatePostFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.createPost_container, fragment);
        fragmentTransaction.addToBackStack("posts");
        fragmentTransaction.commit();

    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
