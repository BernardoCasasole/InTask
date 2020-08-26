package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.Time;
import com.example.chatapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class AdsTimeFragment extends Fragment {

    View rootView;
    String adsID;

    ImageView image;
    TextView title, author, date, hour, description, type, location, distance, verified;
    Button btn_contact;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_ads_time, container, false);
        Bundle b = getArguments();

        if(b != null) {
            adsID = b.getString("id");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Time").child(adsID);
        storageReference = FirebaseStorage.getInstance().getReference();

        image = rootView.findViewById(R.id.user_image);
        title = rootView.findViewById(R.id.title_time_pubb);
        author = rootView.findViewById(R.id.publisher_time_pubb);
        date = rootView.findViewById(R.id.date_time_pubb);
        hour = rootView.findViewById(R.id.hour_time_pubb);
        description = rootView.findViewById(R.id.description_time_pubb);
        type = rootView.findViewById(R.id.category_time_pubb);
        location = rootView.findViewById(R.id.position_time_pubb);
        distance = rootView.findViewById(R.id.hours_number_time_pubb);
        verified = rootView.findViewById(R.id.verified);
        btn_contact = rootView.findViewById(R.id.button_contact);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                final Time time = dataSnapshot.getValue(Time.class);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child(time.getAuthor()).child("surname").getValue().toString().concat(" ")
                                .concat(snapshot.child(time.getAuthor()).child("name").getValue().toString());
                        author.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                author.setText(time.getAuthor());
                title.setText(time.getTitle());
                date.setText(time.getDay());
                hour.setText(time.getTime());
                location.setText(time.getLocation());
                distance.setText(time.getDistance());
                description.setText(time.getDescription());
                type.setText(time.getType());

                switch (time.getType()) {
                    case "Giardinaggio":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_local_florist_24, 0);
                        break;
                    case "Babysitting":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_child_friendly_24, 0);
                        break;
                    case "Cucinare":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_local_pizza_24, 0);
                        break;
                    case "Pulizie":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_local_laundry_service_24, 0);
                        break;
                    case "Ripetizioni":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_menu_book_24, 0);
                        break;
                    case "Trasloco":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_local_shipping_24, 0);
                        break;
                    case "Riparazioni":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_build_24, 0);
                        break;
                    case "Dogsitting":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dog, 0);
                        break;
                    case "Personal Training":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fitness_center_24, 0);
                        break;
                    case "Supporto Informatico":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_computer_24, 0);
                        break;
                    case "Trasporto":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_directions_car_24, 0);
                        break;
                    case "Spesa":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_local_grocery_store_24, 0);
                        break;
                    case "Decimo al Calcetto":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_sports_soccer_24, 0);
                        break;
                    case "Altro":
                        type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_more_horiz_24, 0);
                        break;
                }

                if(!time.getVerified()){
                    verified.setText("Utente non verificato");
                    verified.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close_35, 0);
                }else{
                    verified.setText("Utente verificato");
                    verified.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_35, 0);
                }



                if(time.getAuthor().equals(firebaseUser.getUid())){
                    btn_contact.setVisibility(View.GONE);
                }
                else{
                    btn_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.wtf("LOL","dd");
                        }
                    });
                }

                if(time.getSetted_image())
                    uploadImage();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    protected void uploadImage(){



        File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");


            final File finalLocalFile = localFile;
            storageReference.child("time_images/"+ adsID +".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Glide.with(getContext()).load(finalLocalFile).into(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}