package com.example.socialmedia.UI;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Fragments.FragmentActionListener;
import com.example.socialmedia.Fragments.HomeFragment;
import com.example.socialmedia.Fragments.SearchFragment;
import com.example.socialmedia.Fragments.ViewProfileFragment;
import com.example.socialmedia.Models.User;
import com.example.socialmedia.Fragments.Profilepage;
import com.example.socialmedia.R;
import com.example.socialmedia.UserClient.UserClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements FragmentActionListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference mUserRef;
    private TextView nav_username;
    private CircleImageView nav_profile_image;
    private User mCurrUser;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId= mAuth.getCurrentUser().getUid();

        //Initialing Variables
        toolbar = findViewById(R.id.home_appbar_layout);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawable_layout);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        nav_username=navView.findViewById(R.id.nav_username);
        nav_profile_image=navView.findViewById(R.id.nav_profile_image);

        //Initializing Layout
        setupActionBar();
        setupDrawerLayout();
        setupNavigationHeader();
        gotoHome();

        //Setting the Current User for the whole app and setting the navigation Details

        mCurrUser=((UserClient)(getApplicationContext())).getUser();

        if( (mCurrUser ==null)
             || (!mCurrUser.getUserid().equals(currentUserId)) )
            getCurrUser();
        else
            setNavigationDetails();


    }

    private void setNavigationDetails() {
        String username=mCurrUser.getUsername();
        String profileData=mCurrUser.getProfileImageUri();
        nav_username.setText(username);
        if(!profileData.equals("defaultpic"))
        Picasso.get().load(profileData).into(nav_profile_image);
    }

    private void getCurrUser() {

        mUserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mCurrUser=dataSnapshot.getValue(User.class);
                    ((UserClient)(getApplicationContext())).setUser(mCurrUser);
                    Log.d(TAG, "onDataChange: Curr User is " + mCurrUser.getFullname());
                   setNavigationDetails();
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
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser==null) {
            sendUsertoLogin();
        }
        else {
            checkUserExists();
        }
    }

    private void checkUserExists() {
        final String currentUser_id= mAuth.getCurrentUser().getUid();
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUser_id)) {
                    sendUsertoSetup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void menuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                gotoHome();
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                gotoProfile();
                Toast.makeText(this, "Profile Page", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                signOut();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_search:
                gotoSearch();
                Toast.makeText(this, "Search Page", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void gotoSearch() {


        Log.d(TAG, "inflating search page " );

        SearchFragment fragment = new SearchFragment();
        FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        fragment.setFragmentActionListener(this);
        transaction.commit();
        getSupportActionBar().setTitle("Search Page");

    }

    private void gotoProfile() {

            Log.d(TAG, "inflating profile page " );

            Profilepage fragment = new Profilepage();
            FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainContainer, fragment);
            transaction.commit();
            getSupportActionBar().setTitle("Profile Page");

    }

    private void gotoHome() {


        Log.d(TAG, "inflating home page " );

        HomeFragment fragment = new HomeFragment();
        FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        transaction.commit();
        getSupportActionBar().setTitle("Home Page");

    }

    public void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }

    private void setupDrawerLayout() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationHeader() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuSelector(menuItem);
                return false;
            }
        });
    }

    private void sendUsertoLogin() {
        Intent intent= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendUsertoSetup() {
        Intent intent= new Intent(MainActivity.this,SetupProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActionPerformed(Bundle bundle) {
        int actionPerformed = bundle.getInt(FragmentActionListener.ACTION_KEY);
        switch (actionPerformed){
            case FragmentActionListener.ACTION_VALUE_USER_SELECTED:
                gotoViewProfile (bundle);
                break;
        }
    }

    private void gotoViewProfile(Bundle bundle) {


        Log.d(TAG, "inflating View Profile " );

        ViewProfileFragment fragment = new ViewProfileFragment();
        FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment);
        transaction.addToBackStack(null);
        fragment.setArguments(bundle);
        transaction.commit();


    }
}
