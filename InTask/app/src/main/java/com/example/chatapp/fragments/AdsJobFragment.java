package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class AdsJobFragment extends Fragment {

    View rootView;
    String adsID;

    ImageView image;
    TextView title, author, when, reward, description, type, location, duration, verified;
    Button btn_contact;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView =  inflater.inflate(R.layout.fragment_ads_job, container, false);
        Bundle b = getArguments();

        if(b != null) {
            adsID = b.getString("id");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Job").child(adsID);
        storageReference = FirebaseStorage.getInstance().getReference();

        image = rootView.findViewById(R.id.user_image);
        title = rootView.findViewById(R.id.title_job_pubb);
        author = rootView.findViewById(R.id.publisher_job_pubb);
        when = rootView.findViewById(R.id.date_hour_job_pubb);
        reward = rootView.findViewById(R.id.amount_job_pubb);
        description = rootView.findViewById(R.id.description_job_pubb);
        type = rootView.findViewById(R.id.category_job_pubb);
        location = rootView.findViewById(R.id.position_job_pubb);
        duration = rootView.findViewById(R.id.hours_number_job_pubb);
        verified = rootView.findViewById(R.id.verified);
        btn_contact = rootView.findViewById(R.id.button_contact);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                Job job = dataSnapshot.getValue(Job.class);

                author.setText(job.getAuthor());
                title.setText(job.getTitle());
                when.setText(job.getDay().concat(", ").concat(job.getTime()));
                reward.setText(job.getReward());
                location.setText(job.getLocation());
                duration.setText(job.getDuration());
                description.setText(job.getDescription());
                if(!job.getVerified()){
                    verified.setText("Utente non verificato");
                    //verified.  setImageResource(R.drawable.ic_baseline_close_35);


                }else{

                    verified.setText("Utente verificato");
                    //verifiedUSerImage.setImageResource(R.drawable.ic_baseline_check_35);



                }

                //if(job.getSetted_image())
                    //uploadImage();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }
}