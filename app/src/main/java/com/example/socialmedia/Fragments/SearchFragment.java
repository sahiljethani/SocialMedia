package com.example.socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.socialmedia.Adapters.Userlist_Adapter;
import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchFragment extends Fragment implements Userlist_Adapter.OnUserListener {


    private static final String TAG = "SearchFragment";
    private ArrayList <User> mUser = new ArrayList<>();
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private RecyclerView userpostList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference mUserRef;
    private TextView nav_username;
    private CircleImageView nav_profile_image;
    private User mCurrUser;
    private Context context;
    private RecyclerView recyclerView;
    private Userlist_Adapter adapter;

    FragmentActionListener fragmentActionListener;

    String currentUserId;




    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCurrUser=((UserClient)(getActivity().getApplicationContext())).getUser();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");

        setHasOptionsMenu(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.userlist_Recyclerview);
        getUsers();
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return view;
    }


    private void getUsers()
    {
        Log.d(TAG, "getUsers: Getting users");

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mUser.clear();
                    for ( DataSnapshot snapshot : dataSnapshot.getChildren() )
                    {
                        User user = snapshot.getValue(User.class);
                        if(!(user.getUserid().equals(mCurrUser.getUserid())))
                            mUser.add(user);
                        Log.d(TAG, "onDataChange: User add in the list : "+ user.getFullname());
                    }

                    Log.d(TAG, "onDataChange: Array Size is " + mUser.size());

                    initRecyclerView();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
         adapter= new Userlist_Adapter(mUser,context,this);
        //RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: Called");
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();



        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search for friends");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

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
