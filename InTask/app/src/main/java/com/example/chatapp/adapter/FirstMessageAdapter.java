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

import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class FirstMessageAdapter extends RecyclerView.Adapter<FirstMessageAdapter.ViewHolder> {

    private Context mContext;


    public FirstMessageAdapter(Context mContext) {
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public FirstMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        Log.wtf("DADA11","dadada");
        view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_start, parent, false);

        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.wtf("DADA","dadada");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            Log.wtf("DADA11","dadada");
        }
    }
}

