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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    public User mCurruser;
    private FloatingActionButton fab_post;
    private RecyclerView userpostList;
    Boolean isLiked=false;
    private ArrayList<String> followingList=new ArrayList<>();
    private ArrayList<User> followingListUser=new ArrayList<>();



    //Firebase Variables
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    String currentUserId;
    private DatabaseReference mPostRef, mLikeRef,mFollowingRef;
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
        mFollowingRef=FirebaseDatabase.getInstance().getReference("Follow").child(currentUserId).child("Following");


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

        getFollowing();

        return view;

    }


    private void showUserPost() {

////////////////HERE MAIN PART///////////////////////////////////////////////////////////
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Posts>()
                    .setQuery(mPostRef, Posts.class).build();

            adapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Posts model) {

                    if (followingList.contains(model.getUserid())) {

                        User user = new User();
                        for (User newuser : followingListUser) {

                            if (newuser.getUserid().equals(model.getUserid())) {
                                user = newuser;

                            }

                        }

                        //else if (model.getUserid().equals(currentUserId)) {
                        //  user=mCurruser;
                        //  }
                        final String postkey = getRef(position).getKey();
                        holder.txtfullname.setText(user.getFullname());
                        holder.txtdesc.setText(model.getPostDescription());
                        holder.txtdate.setText(model.getDate());
                        holder.txttime.setText(model.getTime());
                        holder.setProfileImage(user.getProfileImageUri(), getContext());
                        holder.setPostImageUrl(model.getPostImageUrl());
                        holder.setLikeButtonInfo(postkey);

                        holder.like_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isLiked = true;
                                mLikeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (isLiked.equals(true)) {
                                            if (dataSnapshot.child(postkey).hasChild(currentUserId)) {

                                                mLikeRef.child(postkey).child(currentUserId).removeValue();
                                                isLiked = false;

                                            } else {
                                                mLikeRef.child(postkey).child(currentUserId).setValue(true);
                                                isLiked = false;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                            }
                        });
                    }

                }


                @NonNull
                @Override
                public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_post, parent, false);
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

    private void getFollowing() {

        mFollowingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        followingList.clear();
                        for ( DataSnapshot snapshot : dataSnapshot.getChildren() )
                        {
                            String userid=snapshot.getKey();
                            followingList.add(userid);

                            Log.d(TAG, "onDataChange: Post user add in the list : "+ userid);
                        }
                      //  followingList.add(mCurruser.getUserid());
                        Log.d(TAG, "onDataChange: Array Size is " + followingList.size());

                        getFollowingUser();
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowingUser() {

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    followingListUser.clear();
                    for ( DataSnapshot snapshot : dataSnapshot.getChildren() )
                    {
                        User newUser=snapshot.getValue(User.class);
                        if(followingList.contains(newUser.getUserid())) {
                            followingListUser.add(newUser);
                        }

                        Log.d(TAG, "onDataChange: Post user add in the list : "+ newUser.getFullname());
                    }
                //    followingListUser.add(mCurruser);
                    Log.d(TAG, "onDataChange: Array Size is " + followingListUser.size());

                    showUserPost();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
