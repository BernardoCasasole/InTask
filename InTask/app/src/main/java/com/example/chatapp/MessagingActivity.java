package com.example.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.adapter.MessageAdapter;
import com.example.chatapp.fragments.APIService;
import com.example.chatapp.model.Chat;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {


    String userID;

    ImageButton btn_send, btn_close;
    CircleImageView image;
    TextView name;
    Button btn_organize;
    EditText text_send;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private APIService apiService;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MessagingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("myNot","myNot", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MessagingActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.i("FCM Token", token);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                if(firebaseUser!=null)
                    reference.child(firebaseUser.getUid()).setValue(token);
            }
        });

        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            userID = b.getString("sent");
        }

        recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        mChat = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        showMessage();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name_surname);
        btn_organize = findViewById(R.id.chat_with);
        btn_close = findViewById(R.id.top_pane_back_button);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                name.setText(user.getName() + " " + user.getSurname());

                if(user.getSetted_image()){

                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final File finalLocalFile = localFile;
                        storageReference.child("profile_images/"+user.getId()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Glide.with(getApplicationContext()).load(finalLocalFile).into(image);
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
                btn_organize.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(MessagingActivity.this, PopupActivity.class);
                        Bundle b = new Bundle();
                        b.putString("sent", userID);
                        intent.putExtras(b);
                        startActivity(intent);


                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = text_send.getText().toString();
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(),userID,message);
                    text_send.setText("");
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


    }

    private void sendMessage(final String sender, final String receiver, final String message){

       reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();

        HashMap<String,Object> map = new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",message);
        map.put("type", "none");
        map.put("ads","");
        map.put("key",reference.getKey());
        map.put("date","");
        map.put("title","");

        reference.setValue(map);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                sendNotifications(receiver,sender,user.getName(),message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void showMessage(){

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                mChat.add(new Chat());
                for(DataSnapshot snapshot: dataSnapshot.getChildren() ){

                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID) ||
                        chat.getReceiver().equals(userID)&&chat.getSender().equals(firebaseUser.getUid())){
                        mChat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(getApplicationContext(),mChat);
                recyclerView.setAdapter(messageAdapter);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                text_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotifications(final String receiverId, final String userId,final String name, final String message){


        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = token.orderByKey().equalTo(receiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Data data = new Data(userId,receiverId, "Nuovo messaggio da "+ name +"!",message);
                    Notification notification = new Notification(message, "Nuovo messaggio da "+ name +"!",".MessagingActivity");
                    Sender sender = new Sender(data,snapshot.getValue(String.class));

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Log.wtf("Errore","notifica");
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
