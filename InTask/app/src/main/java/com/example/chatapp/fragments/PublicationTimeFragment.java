package com.example.chatapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PublicationTimeFragment extends Fragment {

    EditText title, description, day, time, distance,location, houseNumber, street;
    ImageView imageView;
    RadioGroup type;
    RadioButton radioButton, verified;
    Button btn_publish;
    Uri mImageUri;
    Boolean uploaded;

    View rootView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_publication_time, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Time");
        storageReference = FirebaseStorage.getInstance().getReference();

        title = rootView.findViewById(R.id.title);
        description = rootView.findViewById(R.id.description);
        location = rootView.findViewById(R.id.location);
        houseNumber = rootView.findViewById(R.id.house_number);
        street = rootView.findViewById(R.id.street);
        day = rootView.findViewById(R.id.day);
        time = rootView.findViewById(R.id.time);
        distance = rootView.findViewById(R.id.distance);
        type = rootView.findViewById(R.id.type);

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

        btn_publish = rootView.findViewById(R.id.btn_publish);

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioButton = rootView.findViewById(type.getCheckedRadioButtonId());
                verified = rootView.findViewById(R.id.verified);

                String title_text, description_text, day_text, time_text, distance_text, location_text, houseNumber_text, street_text;


                title_text = title.getText().toString();
                description_text = description.getText().toString();
                location_text = location.getText().toString();
                houseNumber_text = houseNumber.getText().toString();
                street_text = street.getText().toString();
                day_text = day.getText().toString();
                time_text = time.getText().toString();
                distance_text =  distance.getText().toString();

                if( !title_text.equals("")  &&
                !description_text.equals("") &&
                !location_text.equals("") &&
                !houseNumber_text.equals("") &&
                !street_text.equals("") &&
                !day_text.equals("") &&
                !time_text.equals("") &&
                !distance_text.equals("") &&
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
                    map.put("distance", distance_text);
                    map.put("type", radioButton.getText().toString());
                    map.put("verified", verified.isChecked());
                    map.put("setted_image",uploaded);
                    Toast.makeText(getContext(),"Annuncio pubblicato con successo",Toast.LENGTH_SHORT).show();
                if(uploaded){

                    StorageReference childRef = storageReference.child("/time_images/"+databaseReference.push().getKey()+".jpg");
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