package com.example.chatapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class ProfileFragment extends Fragment {

    View rootView;
    ImageView imageView,verifiedUSerImage, documentImage;
    TextView name,surname, verifiedUser,titleJob,titleTime, numOfRatings, location;
    Button btn_logout,btn_uploadDocument, btn_updateAddress, btn_updateDocument, getPosition;
    RatingBar ratingBar;
    LinearLayout uploadDocument1, loginLayout,addressLayout,jobLayout,timeLayout, verifiedLayout;
    RelativeLayout uploadDocument2;
    RecyclerView recyclerViewJob, recyclerViewTime;
    EditText addressUser;
    String userID;
    boolean myProfile;
    double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;
    private static final int GALLERY2 = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle b = getArguments();

        if(b != null) {
            userID = b.getString("id");
        }
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.wtf("Boh","accendiGPS");
            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);

        }
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        storageReference = FirebaseStorage.getInstance().getReference();
        myProfile = firebaseUser != null&& userID.equals(firebaseUser.getUid());

        imageView = rootView.findViewById(R.id.user_image);
        documentImage = rootView.findViewById(R.id.document_image);
        name = rootView.findViewById(R.id.name_user);
        surname = rootView.findViewById(R.id.surname_user);
        ratingBar = rootView.findViewById(R.id.rating_user);
        uploadDocument1 = rootView.findViewById(R.id.layout_document_1);
        uploadDocument2 = rootView.findViewById(R.id.layout_document_2);
        verifiedUser = rootView.findViewById(R.id.verified_user);
        verifiedUSerImage = rootView.findViewById(R.id.verified_user_image);
        loginLayout = rootView.findViewById(R.id.login_layout);
        verifiedLayout = rootView.findViewById(R.id.layout_verified);
        addressUser = rootView.findViewById(R.id.address_user);
        addressLayout = rootView.findViewById(R.id.address_input);
        titleJob = rootView.findViewById(R.id.title_job);
        titleTime = rootView.findViewById(R.id.title_time);
        jobLayout = rootView.findViewById(R.id.layout_job);
        timeLayout = rootView.findViewById(R.id.layout_time);
        numOfRatings = rootView.findViewById(R.id.number_of_reviews);
        location = rootView.findViewById(R.id.text_location);
        loginLayout.setVisibility(View.GONE);
        addressUser.setVisibility(View.GONE);
        getPosition = rootView.findViewById(R.id.position_button);
        getPosition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    databaseReference.child("location").setValue(address);
                    location.setText(address);
                    Toast.makeText(getContext(),"Indirizzo modificato!",Toast.LENGTH_SHORT).show();
                } catch (IOException | IndexOutOfBoundsException e) {
                    Toast.makeText(getContext(), "Indirizzo non trovato", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_uploadDocument = rootView.findViewById(R.id.user_document_upload);
        btn_updateDocument = rootView.findViewById(R.id.user_document_update);
        btn_logout = rootView.findViewById(R.id.logout_id);
        btn_updateAddress = rootView.findViewById(R.id.user_address_update_button);
        btn_updateDocument.setVisibility(View.GONE);
        if(!myProfile){
            uploadDocument1.setVisibility(View.GONE);
            btn_uploadDocument.setVisibility(View.GONE);
            uploadDocument2.setVisibility(View.GONE);
            addressLayout.setVisibility((View.GONE));
            btn_logout.setVisibility(View.GONE);

        }

        btn_updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressUser.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);
                btn_updateAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(addressUser.getText().toString().equals("")){
                            Toast.makeText(getContext(),"Riempi il campo!",Toast.LENGTH_SHORT).show();
                        }else{

                            databaseReference.child("location").setValue(addressUser.getText().toString());
                            Toast.makeText(getContext(),"Indirizzo modificato!",Toast.LENGTH_SHORT).show();
                            addressUser.setVisibility(View.GONE);
                            location.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });



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
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                surname.setText(user.getSurname());
                ratingBar.setRating(user.getAverage_ratings());
                numOfRatings.setText(String.valueOf(user.getRatings()));
                location.setText(user.getLocation());
                if(!user.getVerified()){
                    if(!myProfile) {
                        verifiedUser.setText("Utente non verificato");
                        verifiedUSerImage.setImageResource(R.drawable.ic_baseline_close_35);
                    }else{
                        verifiedLayout.setVisibility(View.GONE);
                        btn_uploadDocument.setVisibility(View.VISIBLE);
                        uploadDocument1.setVisibility(View.VISIBLE);
                        uploadDocument2.setVisibility(View.VISIBLE);
                        documentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setImageDocument();
                            }
                        });
                        btn_uploadDocument.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(btn_uploadDocument.getTag().equals("default"))
                                    setImageDocument();
                                else
                                    databaseReference.child("verified").setValue(true);
                            }
                        });
                    }
                }else{
                    verifiedLayout.setVisibility(View.VISIBLE);
                    verifiedUser.setText("Utente verificato");
                    verifiedUSerImage.setImageResource(R.drawable.ic_baseline_check_35);
                    if (myProfile) {
                        btn_uploadDocument.setVisibility(View.GONE);
                        btn_updateDocument.setVisibility(View.VISIBLE);
                        btn_updateDocument.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (Image != null)
                                    Image.recycle();
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY2);
                            }
                        });
                        uploadDocument1.setVisibility(View.GONE);
                        uploadDocument2.setVisibility(View.GONE);

                    }

                }

                if(user.getSetted_image())
                    uploadImage("profile_images/"+userID+".jpg", imageView);


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

        readAdsJob();
        readAdsTime();


        return rootView;
    }

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

    private void setImageDocument() {

                documentImage.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY2);

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
                        uploadImage("/profile_images/"+userID+".jpg",imageView);
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


        } else if (requestCode == GALLERY2) {
            Uri mImageUri = data.getData();
            if(mImageUri != null) {

                StorageReference childRef = storageReference.child("/document_images/"+userID+".jpg");

                //uploading the image
                UploadTask uploadTask = childRef.putFile(mImageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadImage("/document_images/"+userID+".jpg", documentImage);
                        btn_uploadDocument.setTag("changed");
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }
    }

    protected void uploadImage(String path, final ImageView image){



            File localFile;
            try {
                localFile = File.createTempFile("images", "jpg");


                final File finalLocalFile = localFile;
                storageReference.child(path).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
    private void readAdsTime() {

        final List<Time> mAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Time");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAds.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Time time = snapshot.getValue(Time.class);
                    if (time.getAuthor().equals(userID))
                        mAds.add(time);
                }
                if(mAds.isEmpty())
                    timeLayout.setVisibility(View.GONE);
                if (myProfile)
                    recyclerViewTime.setAdapter(new TimeAdapter(recyclerViewTime.getContext(), mAds, true));
                else {
                    titleTime.setText("Disponibilità a lavorare");
                    recyclerViewTime.setAdapter(new TimeAdapter(recyclerViewTime.getContext(), mAds, false));
                }
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
                    if (job.getAuthor().equals(userID))
                        mAds.add(job);

                }
                if(mAds.isEmpty())
                    jobLayout.setVisibility(View.GONE);
                if (myProfile)
                    recyclerViewJob.setAdapter(new JobAdapter(recyclerViewJob.getContext(), mAds, true));
                else{
                    titleJob.setText("Offerte di lavoro");
                    recyclerViewJob.setAdapter(new JobAdapter(recyclerViewJob.getContext(), mAds, false));
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}