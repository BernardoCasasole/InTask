package com.example.chatapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class JobSvFormFragment extends Fragment {

    EditText title, description, day, time, duration,location, houseNumber, street;
    RadioGroup type;
    RadioButton radioButton, verified;
    Button btn_publish;
    View rootView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_job_sv_form, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Ads");

        title = rootView.findViewById(R.id.proposal_title);
        description = rootView.findViewById(R.id.proposal_description);
        location = rootView.findViewById(R.id.proposal_location);
        houseNumber = rootView.findViewById(R.id.proposal_house_number);
        street = rootView.findViewById(R.id.proposal_street);
        day = rootView.findViewById(R.id.proposal_day);
        time = rootView.findViewById(R.id.proposal_time);
        duration = rootView.findViewById(R.id.proposal_duration);
        type = rootView.findViewById(R.id.proposal_type);


        btn_publish = rootView.findViewById(R.id.proposal_btn_publish);


        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioButton = rootView.findViewById(type.getCheckedRadioButtonId());
                verified = rootView.findViewById(R.id.proposal_verified);

                Map<String, Object> map = new HashMap<>();
                map.put("user", firebaseUser.getUid());
                map.put("title", title.getText().toString());
                map.put("description", description.getText().toString());
                map.put("location", location.getText().toString());
                map.put("houseNumber", houseNumber.getText().toString());
                map.put("street", street.getText().toString());
                map.put("day", day.getText().toString());
                map.put("time", time.getText().toString());
                map.put("duration", duration.getText().toString());
                map.put("type", radioButton.getText().toString());
                map.put("verified", verified.isChecked());

                databaseReference.push().setValue(map);



            }
        });

        return rootView;
    }
}