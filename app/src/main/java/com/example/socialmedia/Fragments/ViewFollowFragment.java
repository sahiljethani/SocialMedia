package com.example.socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.socialmedia.Adapters.Userlist_Adapter;
import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewFollowFragment extends Fragment implements Userlist_Adapter.OnUserListener {
    private static final String TAG = "ViewFollowFragment";
    private String ListName;
    private DatabaseReference mFollowRef;
    private DatabaseReference mUserRef;
    private User mCurruser;
    private ArrayList <User> mUser = new ArrayList<>();
    private ArrayList <String> UseridList = new ArrayList<>();
    private Userlist_Adapter adapter;
    private RecyclerView recyclerView;
    private Context context;
    private LinearLayout parentlayout;
    private FragmentActionListener fragmentActionListener;
    private String UserId;


    public ViewFollowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        ListName = bundle.getString(FragmentActionListener.KEY_SELECTED_ACTION);
        UserId = bundle.getString(FragmentActionListener.KEY_SELECTED_USERID);
        mFollowRef=FirebaseDatabase.getInstance().getReference("Follow").child(UserId);

       // Log.d(TAG, "onActivityCreated: "+ mCurruser.getFullname());

        getList();
        //getUser(userId);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Called");

        View view=inflater.inflate(R.layout.fragment_view_follow, container, false);

        mUserRef= FirebaseDatabase.getInstance().getReference("Users");

        context = container.getContext();
        recyclerView = view.findViewById(R.id.follow_recyclerview);
        parentlayout=view.findViewById(R.id.parent_FollowLayout);

        // Inflate the layout for this fragment
        return view;
    }

    private void getList() {

        mFollowRef.child(ListName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    UseridList.clear();
                    for ( DataSnapshot snapshot : dataSnapshot.getChildren() )
                    {
                        String userid=snapshot.getKey();
                        UseridList.add(userid);

                        Log.d(TAG, "onDataChange: user add in the list : "+ userid);
                    }

                    Log.d(TAG, "onDataChange: Array Size is " + UseridList.size());


                }

                getUser();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mUser.clear();
                    for ( DataSnapshot snapshot : dataSnapshot.getChildren() )
                    {
                        User newUser=snapshot.getValue(User.class);
                        if(UseridList.contains(newUser.getUserid())) {
                            mUser.add(newUser);
                            Log.d(TAG, "onDataChange: Post user add in the list : "+ newUser.getFullname());
                        }


                    }
                    //    followingListUser.add(mCurruser);
                    Log.d(TAG, "onDataChange: Array Size is " + mUser.size());

                    //showUserPost();
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        adapter= new Userlist_Adapter(mUser,context,this,parentlayout);
        //RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }


    @Override
    public void onUserClick(User user) {
        Log.d(TAG, "onUserClick: Clicked on " +user.getFullname());

        if (fragmentActionListener!=null){
            Bundle bundle = new Bundle();
            bundle.putInt(FragmentActionListener.ACTION_KEY, FragmentActionListener.ACTION_VALUE_USER_SELECTED);
            bundle.putString(FragmentActionListener.KEY_SELECTED_USERID, user.getUserid());
            fragmentActionListener.onActionPerformed(bundle);
        }
    }
}
