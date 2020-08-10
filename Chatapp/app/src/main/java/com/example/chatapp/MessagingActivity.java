package com.example.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.MessageAdapter;
import com.example.chatapp.fragments.APIService;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.User;
import com.example.chatapp.notifications.Client;
import com.example.chatapp.notifications.Data;
import com.example.chatapp.notifications.MyResponse;
import com.example.chatapp.notifications.Notification;
import com.example.chatapp.notifications.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends BaseActivity {


    String userID;

    ImageButton btn_send;
    EditText text_send;

    private APIService apiService;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            userID = b.getString("sent");
        }

        initToolbar(R.id.start_toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showUsernameToolbar(userID);

        recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        mChat = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        showMessage();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

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

       reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> map = new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",message);

        reference.child("Chats").push().setValue(map);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                sendNotifications(receiver,sender,user.getUsername(),message);
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
                for(DataSnapshot snapshot: dataSnapshot.getChildren() ){

                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID) ||
                        chat.getReceiver().equals(userID)&&chat.getSender().equals(firebaseUser.getUid())){
                        mChat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(getApplicationContext(),mChat);
                recyclerView.setAdapter(messageAdapter);
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
                    Sender sender = new Sender(data,snapshot.getValue(String.class),notification);

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
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
