package com.example.chatapp.adapter.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.fragments.APIService;
import com.example.chatapp.model.User;
import com.example.chatapp.notifications.Client;
import com.example.chatapp.notifications.Data;
import com.example.chatapp.notifications.MyResponse;
import com.example.chatapp.notifications.Notification;
import com.example.chatapp.notifications.Sender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private APIService apiService;
    private boolean notify;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.notify = false;
        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        holder.username.setText(user.getName());

        holder.btn_add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                Toast.makeText(mContext,"Richiesta d'amicizia a "+user.getName()+" inviata!",Toast.LENGTH_SHORT).show();

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("friend_requests_received");
                Map<String, Object> map = new HashMap<>();
                map.put(firebaseUser.getUid(), firebaseUser.getUid());
                reference.updateChildren(map);

                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("friend_requests_sent");
                map = new HashMap<>();
                map.put(user.getId(), user.getId());
                reference.updateChildren(map);

                reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (notify) {

                                String name =dataSnapshot.child(firebaseUser.getUid()).child("username").getValue(String.class);
                                sendNotifications(user.getId(), firebaseUser.getUid(),name);

                            }
                            notify = false;
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



        if(!user.getSetted_image()){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);

        }else{

            File localFile = null;
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
        public Button btn_add_friends;
        public Button btn_remove;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);

            btn_add_friends = itemView.findViewById(R.id.btn_add_friend);
            btn_add_friends.setText(R.string.btn_add_friend);
            btn_remove = itemView.findViewById(R.id.btn_remove_requests);
            btn_remove.setVisibility(View.INVISIBLE);



        }
    }

    private void sendNotifications(final String receiverId, final String userId,final String name){
        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Data data = new Data(userId,receiverId,"Nuova richiesta d'amicizia!",name + " vuole essere tuo amico.");
                    Notification notification = new Notification(name + " vuole essere tuo amico.", "Nuova richiesta d'amicizia!",".FriendActivity");
                    Sender sender = new Sender(data,snapshot.getValue(String.class));

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
