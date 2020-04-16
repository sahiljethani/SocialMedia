package com.example.socialmedia.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.socialmedia.Models.User;
import com.example.socialmedia.PostViewHolder;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private User mCurruser;
    private FloatingActionButton fab_post;
    private RecyclerView userpostList;
    Boolean isLiked=false;


    //Firebase Variables
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    String currentUserId;
    private DatabaseReference mPostRef, mLikeRef;
    FirebaseRecyclerAdapter<Posts,PostViewHolder> adapter;


    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurruser=((UserClient)(getActivity().getApplicationContext())).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);

        //Initializing Variables
        fab_post= view.findViewById(R.id.fab_post);
        userpostList=view.findViewById(R.id.user_post_recyclerview);

        //Firebase init
        mAuth=FirebaseAuth.getInstance();
        mUserRef=FirebaseDatabase.getInstance().getReference("Users");
        mPostRef= FirebaseDatabase.getInstance().getReference("Posts");
        mLikeRef=FirebaseDatabase.getInstance().getReference("Likes");
        currentUserId=mAuth.getCurrentUser().getUid();


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

                final String postkey=getRef(position).getKey();
                holder.txtfullname.setText(model.getFullname());
                holder.txtdesc.setText(model.getPostDescription());
                holder.txtdate.setText(model.getDate());
                holder.txttime.setText(model.getTime());
                holder.setProfileImage(model.getProfileImage(),getContext());
                holder.setPostImageUrl(model.getPostImageUrl());
                holder.setLikeButtonInfo(postkey);

                holder.like_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLiked=true;
                        mLikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(isLiked.equals(true)) {
                                    if(dataSnapshot.child(postkey).hasChild(currentUserId)) {

                                        mLikeRef.child(postkey).child(currentUserId).removeValue();
                                        isLiked=false;

                                    }
                                    else {
                                        mLikeRef.child(postkey).child(currentUserId).setValue(true);
                                        isLiked=false;

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });


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
        fragmentTransaction.add(R.id.mainContainer, fragment);
        fragmentTransaction.addToBackStack("posts");
        fragmentTransaction.commit();

    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
