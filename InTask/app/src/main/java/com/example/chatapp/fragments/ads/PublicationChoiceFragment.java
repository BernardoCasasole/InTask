package com.example.chatapp.fragments.ads;

import android.os.Bundle;

import com.example.chatapp.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class PublicationChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_publication_choice, container, false);

        Button btnPublishJob = view.findViewById(R.id.button_publish_job);
        Button btnPublishTime = view.findViewById(R.id.button_publish_time);


        btnPublishJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new JobSvFormFragment(), "JOB_FRAGMENT").commit();
            }
        });

        btnPublishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PublicationTimeFragment(),"TIME_FRAGMENT").commit();
            }
        });
        return view;
    }
}