package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChat;

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
        if (viewType == MSG_RIGHT_TYPE)
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        return new MessageAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mChat.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_RIGHT_TYPE;
        else
            return MSG_LEFT_TYPE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;


        public ViewHolder(View itemView) {

            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);

        }
    }
}