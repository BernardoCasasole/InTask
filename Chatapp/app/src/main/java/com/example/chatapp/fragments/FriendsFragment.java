package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.adapter.users.FriendsAdapter;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;

    private List<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        recyclerView = view.findViewById(R.id.recycler_view_friends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mUsers = new ArrayList<>();

        readUsers();

        return view;
    }


    private void readUsers(){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                Map<String, String> friends = (HashMap<String,String>) dataSnapshot.child(firebaseUser.getUid()).child("friends").getValue();

                List<String> values = new ArrayList<>();

                if(friends!=null) {
                    values.addAll(friends.values());
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String userID = snapshot.child("id").getValue(String.class);
                    if(!userID.equals(firebaseUser.getUid())&values.contains(userID)){
                        mUsers.add(snapshot.getValue(User.class));

                    }

                }
                friendsAdapter = new FriendsAdapter(recyclerView.getContext(),mUsers);
                recyclerView.setAdapter(friendsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}