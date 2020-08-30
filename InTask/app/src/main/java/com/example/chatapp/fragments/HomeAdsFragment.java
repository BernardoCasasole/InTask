package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.adapter.JobAdapter;
import com.example.chatapp.adapter.TimeAdapter;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
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

public class HomeAdsFragment extends Fragment {

    String typeAds, myAds;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_ads, container, false);
        Bundle b = getArguments();
        if(b != null) {
            typeAds = b.getString("type");
            myAds = b.getString("myAds");
        }

        recyclerView = view.findViewById(R.id.recycler_view_ads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));


        if(typeAds.equals("job"))
            readAdsJob();
        else
            readAdsTime();

        return view;
    }

    private void readAdsTime() {

        final List<Time> mAds = new ArrayList<>();
        final List<Time> allAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Time");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAds.clear();
                allAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Time time = snapshot.getValue(Time.class);

                    if(!time.getPending() && !time.getAchieved())
                        if(firebaseUser==null ||
                            ( !time.getAuthor().equals(firebaseUser.getUid()))){
                        allAds.add(time);

                    }else
                        mAds.add(time);

                }

                if(Boolean.parseBoolean(myAds))
                    recyclerView.setAdapter(new TimeAdapter(recyclerView.getContext(),mAds,Boolean.parseBoolean(myAds)));
                else
                    recyclerView.setAdapter(new TimeAdapter(recyclerView.getContext(),allAds,Boolean.parseBoolean(myAds)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAdsJob(){

        final List<Job> mAds = new ArrayList<>();
        final List<Job> allAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Job");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAds.clear();
                allAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);

                   if(!job.getPending() && !job.getAchieved())
                       if(firebaseUser==null ||
                           ( !job.getAuthor().equals(firebaseUser.getUid()))){
                        allAds.add(job);

                    }
                   else
                       mAds.add(job);

                }

                if(Boolean.parseBoolean(myAds))
                    recyclerView.setAdapter(new JobAdapter(recyclerView.getContext(),mAds,Boolean.parseBoolean(myAds)));
                else
                    recyclerView.setAdapter(new JobAdapter(recyclerView.getContext(),allAds,Boolean.parseBoolean(myAds)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
