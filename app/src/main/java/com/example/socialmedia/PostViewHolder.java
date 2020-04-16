package com.example.socialmedia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView txtfullname;
    public TextView txtdate;
    public TextView txttime;
    public TextView txtdesc;
    public TextView like_number;
    public DatabaseReference mLikeRef;
    public ImageButton like_bt;
    String currentUserId;
    int LikesCount;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        txtfullname=itemView.findViewById(R.id.post_username);
        txtdate=itemView.findViewById(R.id.post_date);
        txttime=itemView.findViewById(R.id.post_time);
        txtdesc=itemView.findViewById(R.id.post_desc);
        like_number=itemView.findViewById(R.id.like_number);
        like_bt=itemView.findViewById(R.id.like_bt);
        mLikeRef= FirebaseDatabase.getInstance().getReference("Likes");
        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
    public void setLikeButtonInfo(final String postkey) {

        mLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postkey).hasChild(currentUserId)) {

                    LikesCount= (int) dataSnapshot.child(postkey).getChildrenCount();
                    like_bt.setImageResource(R.drawable.ic_favorite_red_24dp);
                    like_number.setText(Integer.toString(LikesCount)+(" Likes"));

                }
                else {

                    LikesCount= (int) dataSnapshot.child(postkey).getChildrenCount();
                    like_bt.setImageResource(R.drawable.ic_favorite_black_24dp);
                    like_number.setText(Integer.toString(LikesCount)+(" Likes"));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void setProfileImage(String profileImage, Context ctx) {
        CircleImageView imageView= itemView.findViewById(R.id.user_profile_img);
        if(!(profileImage.equals("defaultpic"))) {
            Picasso.get().load(profileImage).into(imageView);

        } else {

                Drawable myDrawable = ctx.getResources().getDrawable(R.drawable.defaultpic);
                imageView.setImageDrawable(myDrawable);
            }
        }

    public void setPostImageUrl(String postImageUrl) {
        ImageView postImage=itemView.findViewById(R.id.post_image);
        Picasso.get().load(postImageUrl).into(postImage);

    }
}


