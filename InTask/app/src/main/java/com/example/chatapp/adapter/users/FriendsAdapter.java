package com.example.chatapp.adapter.users;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public FriendsAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_item,parent,false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        holder.username.setText(user.getName());


        holder.btn_x.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("friends").child(user.getId());
                reference.removeValue();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("friends").child(firebaseUser.getUid());
                reference.removeValue();

            }
        });

        holder.btn_view_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("userMail",user.getMail());
                b.putString("userID",user.getId());
                intent.putExtras(b);
                mContext.startActivity(intent);

            }
        });

        holder.btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessagingActivity.class);
                Bundle b = new Bundle();
                b.putString("sent",user.getId());
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });


        if(!user.getSetted_image()){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);

        }else{

            File localFile ;
            try {
                localFile = File.createTempFile("images", "jpg");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final File finalLocalFile = localFile;
                storageReference.child("profile_images/"+user.getMail()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Glide.with(mContext).load(finalLocalFile).into(holder.profile_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public Button btn_x;
        public Button btn_view_profile;
        public Button btn_chat;

        public ViewHolder(View itemView){

            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);

            btn_x = itemView.findViewById(R.id.btn_remove_friend);
            btn_view_profile = itemView.findViewById(R.id.btn_view_profile);
            btn_chat = itemView.findViewById(R.id.btn_chat);


        }
    }

}
