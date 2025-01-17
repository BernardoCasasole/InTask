package com.example.chatapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatapp.JavaMailAPI;
import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChat;
    private FirebaseUser firebaseUser;
    public static final int MSG_START_TYPE = 2;
    public static final int MSG_RIGHT_TYPE = 1;
    public static final int MSG_LEFT_TYPE = 0;


    public MessageAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == MSG_START_TYPE)
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_start, parent, false);
        else if (viewType == MSG_RIGHT_TYPE)
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        return new MessageAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        if(position !=0) {
            final Chat chat = mChat.get(position);
            holder.show_message.setText(chat.getMessage());
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (chat.getType().equals("none") || chat.getSender().equals(firebaseUser.getUid())) {
                holder.accept.setVisibility(View.GONE);
                holder.decline.setVisibility(View.GONE);
            } else {

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getKey()).child("type").setValue("none");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("sender", chat.getReceiver());
                        map.put("receiver", chat.getSender());
                        map.put("message", getApplicationContext().getResources().getString(R.string.proposta_accettata));
                        map.put("type", "none");
                        map.put("ads", "");
                        map.put("key", reference.getKey());

                        reference.setValue(map);

                        FirebaseDatabase.getInstance().getReference(chat.getType()).child(chat.getAds()).child("pending").setValue(false);
                        FirebaseDatabase.getInstance().getReference(chat.getType()).child(chat.getAds()).child("achieved").setValue(true);

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender()).child("rateable").push();

                        map = new HashMap<>();
                        map.put("user", chat.getReceiver());
                        map.put("date", chat.getDate());
                        map.put("key", reference1.getKey());
                        map.put("title", chat.getTitle());
                        reference1.setValue(map);

                        reference1 = FirebaseDatabase.getInstance().getReference("Users").child(chat.getReceiver()).child("rateable").push();

                        map = new HashMap<>();
                        map.put("user", chat.getSender());
                        map.put("date", chat.getDate());
                        map.put("key", reference1.getKey());
                        map.put("title", chat.getTitle());

                        reference1.setValue(map);

                        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userSender = snapshot.child(chat.getSender()).getValue(User.class);
                                User userReceiver = snapshot.child(chat.getReceiver()).getValue(User.class);
                                String message_to_sender = getApplicationContext().getResources().getString(R.string.complimenti) + " " + userSender.getName() +
                                        getApplicationContext().getResources().getString(R.string._agreement_) + " " + chat.getTitle()  + " " + getApplicationContext().getResources().getString(R.string.in_data) + " " + chat.getDate() + " " + getApplicationContext().getResources().getString(R.string.time) + " " + chat.getTime() + "." +
                                        getApplicationContext().getResources().getString(R.string.agreement_corp) + " " + userReceiver.getName() +"." +
                                        getApplicationContext().getResources().getString(R.string.agreement_end);
                                String message_to_reciver = getApplicationContext().getResources().getString(R.string.complimenti) + " " + userReceiver.getName() +
                                        getApplicationContext().getResources().getString(R.string._agreement_) + " " + chat.getTitle()  + " " + getApplicationContext().getResources().getString(R.string.in_data) + " " + chat.getDate() + " " + getApplicationContext().getResources().getString(R.string.time) + " " + chat.getTime() + "." +
                                        getApplicationContext().getResources().getString(R.string.agreement_corp) + " " + userSender.getName() +"." +
                                        getApplicationContext().getResources().getString(R.string.agreement_end);
                                String sender_mail = userSender.getMail();
                                String reciver_mail = userReceiver.getMail();
                                senEmail(sender_mail, message_to_sender);
                                senEmail(reciver_mail, message_to_reciver);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getKey()).child("type").setValue("none");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("sender", chat.getReceiver());
                        map.put("receiver", chat.getSender());
                        map.put("message", getApplicationContext().getResources().getString(R.string.proposta_rifiutata));
                        map.put("type", "none");
                        map.put("ads", "");
                        map.put("key", reference.getKey());

                        reference.setValue(map);
                        FirebaseDatabase.getInstance().getReference(chat.getType()).child(chat.getAds()).child("pending").setValue(false);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(position == 0)
            return MSG_START_TYPE;
        else if (mChat.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_RIGHT_TYPE;
        else
            return MSG_LEFT_TYPE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public Button accept, decline;


        public ViewHolder(View itemView) {

            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);


        }
    }

    private void senEmail(String mEmail, String mMessage) {
        String mSubject = getApplicationContext().getResources().getString(R.string.accordo_con_utente_raggiunto);

        JavaMailAPI javaMailAPI = new JavaMailAPI(mContext, mEmail, mSubject, mMessage);

        javaMailAPI.execute();
    }
}