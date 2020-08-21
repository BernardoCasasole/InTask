package com.example.chatapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JobSvFormFragment extends Fragment {

    EditText title, description, day, time, duration,location, houseNumber, street;
    RadioGroup type;
    ImageView imageView;
    RadioButton radioButton, verified;
    Button btn_publish;
    View rootView;

    Uri mImageUri;
    Boolean uploaded;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_job_sv_form, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Job");
        storageReference = FirebaseStorage.getInstance().getReference();


        title = rootView.findViewById(R.id.proposal_title);
        description = rootView.findViewById(R.id.proposal_description);
        location = rootView.findViewById(R.id.proposal_location);
        houseNumber = rootView.findViewById(R.id.proposal_house_number);
        street = rootView.findViewById(R.id.proposal_street);
        day = rootView.findViewById(R.id.proposal_day);
        time = rootView.findViewById(R.id.proposal_time);
        duration = rootView.findViewById(R.id.proposal_duration);
        type = rootView.findViewById(R.id.proposal_type);
        imageView = rootView.findViewById(R.id.image);
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);

            }
        });


        btn_publish = rootView.findViewById(R.id.proposal_btn_publish);


        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioButton = rootView.findViewById(type.getCheckedRadioButtonId());
                verified = rootView.findViewById(R.id.proposal_verified);

                String title_text, description_text, day_text, time_text, duration_text, location_text, houseNumber_text, street_text;

                title_text = title.getText().toString();
                description_text = description.getText().toString();
                location_text = location.getText().toString();
                houseNumber_text = houseNumber.getText().toString();
                street_text = street.getText().toString();
                day_text = day.getText().toString();
                time_text = time.getText().toString();
                duration_text =  duration.getText().toString();

                if( !title_text.equals("")  &&
                        !description_text.equals("") &&
                        !location_text.equals("") &&
                        !houseNumber_text.equals("") &&
                        !street_text.equals("") &&
                        !day_text.equals("") &&
                        !time_text.equals("") &&
                        !duration_text.equals("") &&
                        radioButton !=null) {
                Map<String, Object> map = new HashMap<>();
                map.put("author", firebaseUser.getUid());
                map.put("title", title_text);
                map.put("description", description_text);
                map.put("location", location_text);
                map.put("houseNumber", houseNumber_text);
                map.put("street", street_text);
                map.put("day", day_text);
                map.put("time", time_text);
                map.put("duration", duration_text);
                map.put("type", radioButton.getText().toString());
                map.put("verified", verified.isChecked());
                map.put("setted_image",uploaded);
                    Toast.makeText(getContext(),"Annuncio pubblicato con successo",Toast.LENGTH_SHORT).show();
                    if(uploaded){

                        StorageReference childRef = storageReference.child("/job_images/"+databaseReference.push().getKey()+".jpg");
                        childRef.putFile(mImageUri);
                    }
                    databaseReference.push().setValue(map);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PublicationChoiceFragment()).commit();

                }else
                    Toast.makeText(getContext(),"Devi riempire tutti i campi",Toast.LENGTH_SHORT).show();



            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode != 0 ) {
            mImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
                imageView.setImageBitmap(bitmap);
                uploaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
