package com.example.socialmedia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView txtfullname;
    public TextView txtdate;
    public TextView txttime;
    public TextView txtdesc;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        txtfullname=itemView.findViewById(R.id.post_username);
        txtdate=itemView.findViewById(R.id.post_date);
        txttime=itemView.findViewById(R.id.post_time);
        txtdesc=itemView.findViewById(R.id.post_desc);

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


