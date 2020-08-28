package com.example.chatapp.adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;
import com.example.chatapp.fragments.AdsJobFragment;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.User;
import com.example.chatapp.start.StartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mChat;

    public ChatAdapter(Context mContext, List<String> mAds) {
        this.mContext = mContext;
        this.mChat = mAds;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_person_item,parent,false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String friend = mChat.get(position);


        FirebaseDatabase.getInstance().getReference("Users").child(friend).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                  final User user = snapshot.getValue(User.class);
                  holder.name.setText(user.getName() + "\n" + user.getSurname());
                if(user.getSetted_image()){

                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final File finalLocalFile = localFile;
                        storageReference.child("profile_images/"+user.getId()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Glide.with(mContext).load(finalLocalFile).into(holder.image);
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
                holder.openChat.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(mContext, MessagingActivity.class);
                        Bundle b = new Bundle();
                        b.putString("sent", user.getId());
                        intent.putExtras(b);
                        mContext.startActivity(intent);


                    }
                });



                   }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.deleteChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren() ) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(friend) ||
                                    chat.getReceiver().equals(friend)&&chat.getSender().equals(firebaseUser.getUid())){

                                FirebaseDatabase.getInstance().getReference("Chats").child(snapshot.getKey()).removeValue();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView image;
        public TextView name;
        public Button openChat;
        public ImageButton deleteChat;

        public ViewHolder(View itemView){

            super(itemView);
            name = itemView.findViewById(R.id.name_surname);
            openChat = itemView.findViewById(R.id.chat_with);
            deleteChat = itemView.findViewById(R.id.delete_chat);

            image = itemView.findViewById(R.id.profile_image);



        }
    }
}