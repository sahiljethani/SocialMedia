package com.example.socialmedia.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Models.User;
import com.example.socialmedia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Userlist_Adapter extends RecyclerView.Adapter<Userlist_Adapter.ViewHolder> implements Filterable {

    private static final String TAG = "Userlist_Adapter";
    private ArrayList<User> UserList_Original;
    private ArrayList<User> UserList_Copy;
    private OnUserListener mOnUserListener;
    private LinearLayout layout;




    private Context context;

    public Userlist_Adapter(ArrayList<User> userList_Original, Context context, OnUserListener OnUserListener, LinearLayout layout) {
        UserList_Original = userList_Original;
        this.context = context;
        UserList_Copy = new ArrayList<>(UserList_Original);
        this.mOnUserListener=OnUserListener;
        this.layout=layout;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_useritem, parent, false);
        ViewHolder holder = new ViewHolder(view,mOnUserListener);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called.");

        User user = UserList_Original.get(position);


        if(!(user.getProfileImageUri().equals("defaultpic")))
            Picasso.get().load(user.getProfileImageUri()).into(holder.userimage);
        else {

            Drawable myDrawable = context.getResources().getDrawable(R.drawable.defaultpic);
            holder.userimage.setImageDrawable(myDrawable);
        }
        holder.user_fullname.setText(user.getFullname());
        holder.username.setText(user.getUsername());



      /*  holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + );

                Toast.makeText(context, mUser.get(position).getFullname(), Toast.LENGTH_SHORT).show();

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return UserList_Original.size();
    }



    // Real time searching logic

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(UserList_Copy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User user : UserList_Copy) {
                    if (user.getFullname().toLowerCase().contains(filterPattern) || user.getUsername().toLowerCase().contains(filterPattern) ) {


                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            UserList_Original.clear();
            UserList_Original=(ArrayList)results.values;
           // UserList_Original.addAll((ArrayList<User>) results.values);
            notifyDataSetChanged();
        }
    };

   // View Holder Class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView userimage;
        TextView user_fullname, username;
        LinearLayout parentLayout;
        OnUserListener mOnUserListener;

        public ViewHolder(View itemView, OnUserListener OnUserListener) {
            super(itemView);

            userimage = itemView.findViewById(R.id.useritem_image);
            user_fullname = itemView.findViewById(R.id.useritem_fullname);
            username = itemView.findViewById(R.id.useritem_username);
          //  parentLayout = itemView.findViewById(R.id.parent_searchLayout);
            parentLayout= layout;
            mOnUserListener=OnUserListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Log.d(TAG, "onClick: "+ getAdapterPosition());
            mOnUserListener.onUserClick(UserList_Original.get(getAdapterPosition()));

        }
    }


    public interface OnUserListener {

        void onUserClick (User user);
        //void onUserClick (int position, ArrayList<User> UserList);

    }



}
