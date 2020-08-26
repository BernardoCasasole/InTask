package com.example.chatapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapter.JobAdapter;
import com.example.chatapp.adapter.TimeAdapter;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.example.chatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    View rootView;
    ImageView imageView,verifiedUSerImage;
    TextView name,surname, verifiedUser;
    Button btn_logout,btn_uploadDocument;
    RatingBar ratingBar;
    LinearLayout uploadDocument1;
    RelativeLayout uploadDocument2;
    RecyclerView recyclerViewJob, recyclerViewTime;
    String userID;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle b = getArguments();

        if(b != null) {
            userID = b.getString("id");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = rootView.findViewById(R.id.user_image);
        name = rootView.findViewById(R.id.name_user);
        surname = rootView.findViewById(R.id.surname_user);
        ratingBar = rootView.findViewById(R.id.rating_user);
        uploadDocument1 = rootView.findViewById(R.id.layout_document_1);
        uploadDocument2 = rootView.findViewById(R.id.layout_document_2);
        verifiedUser = rootView.findViewById(R.id.verified_user);
        verifiedUSerImage = rootView.findViewById(R.id.verified_user_image);

        btn_uploadDocument = rootView.findViewById(R.id.user_document_update);
        btn_logout = rootView.findViewById(R.id.logout_id);



        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
                bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
            }
        });
        if(userID.equals(firebaseUser.getUid())) {
            setImageProfile();
           // setSettingLayout();




        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                surname.setText(user.getSurname());
                ratingBar.setRating(user.getAverage_ratings());
                if(!user.getVerified()){
                    verifiedUser.setText("Utente non verificato");
                    verifiedUSerImage.setImageResource(R.drawable.ic_baseline_close_35);
                    btn_uploadDocument.setVisibility(View.GONE);
                    uploadDocument1.setVisibility(View.VISIBLE);
                    uploadDocument2.setVisibility(View.VISIBLE);

                }else{

                    verifiedUser.setText("Utente verificato");
                    verifiedUSerImage.setImageResource(R.drawable.ic_baseline_check_35);
                    btn_uploadDocument.setVisibility(View.VISIBLE);
                    uploadDocument1.setVisibility(View.GONE);
                    uploadDocument2.setVisibility(View.GONE);


                }

                if(user.getSetted_image())
                    uploadImage();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerViewTime = rootView.findViewById(R.id.view_pager_time_off);
        recyclerViewTime.setHasFixedSize(true);
        recyclerViewTime.setLayoutManager(new LinearLayoutManager(recyclerViewTime.getContext()));
        recyclerViewJob = rootView.findViewById(R.id.view_pager_job_off);
        recyclerViewJob.setHasFixedSize(true);
        recyclerViewJob.setLayoutManager(new LinearLayoutManager(recyclerViewJob.getContext()));

        //readAdsJob();
        readAdsTime();


        return rootView;
    }

    /*private void setSettingLayout() {

        TextView mail,password,location;
        Button button;
        String mail_text,password_text, location_text;

        settings = rootView.findViewById(R.id.update_user_data);
        settingView = getLayoutInflater().inflate(R.layout.layout_user_data_update, null);
        settings.addView(settingView);

        mail = settingView.findViewById(R.id.mail_user);
        password = settingView.findViewById(R.id.pwd_user);
        location = settingView.findViewById(R.id.address_user);
        button = settingView.findViewById(R.id.user_data_update_button);

        mail_text = mail.getText().toString();
        password_text = password.getText().toString();





    }*/

    private void setImageProfile() {

        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode != 0) {
            Uri mImageUri = data.getData();
            if(mImageUri != null) {

                databaseReference.child("setted_image").setValue(true);
                StorageReference childRef = storageReference.child("/profile_images/"+userID+".jpg");

                //uploading the image
                UploadTask uploadTask = childRef.putFile(mImageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadImage();
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(getContext(), "Select an image", Toast.LENGTH_SHORT).show();
            }


        }
    }

    protected void uploadImage(){



            File localFile;
            try {
                localFile = File.createTempFile("images", "jpg");


                final File finalLocalFile = localFile;
                storageReference.child("profile_images/"+userID+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Glide.with(getContext()).load(finalLocalFile).into(imageView);
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
    private void readAdsTime() {

        final List<Time> mAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Time");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Time time = snapshot.getValue(Time.class);

                    if(firebaseUser!=null &&(time.getAuthor().equals(firebaseUser.getUid()) ))

                        mAds.add(time);
                    }

                recyclerViewTime.setAdapter(new TimeAdapter(recyclerViewTime.getContext(),mAds,true));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAdsJob(){

        final List<Job> mAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Job");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);

                    if(firebaseUser!=null &&(job.getAuthor().equals(firebaseUser.getUid()) ))
                        mAds.add(job);

                }


                recyclerViewJob.setAdapter(new JobAdapter(recyclerViewJob.getContext(),mAds,true));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}