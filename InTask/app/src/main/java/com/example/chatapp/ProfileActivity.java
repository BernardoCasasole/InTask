package com.example.chatapp;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends BaseActivity {

    TextView username,mail,ratings;
    ImageView image;
    RatingBar average_ratings;
    Button btn_change_image;
    String userMail;
    String userID;



    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();

        if(b != null) {
            userMail = b.getString("userMail");
            userID = b.getString("userID");
        }
        username = findViewById(R.id.username_profile);
        mail = findViewById(R.id.mail_profile);
        ratings = findViewById(R.id.ratings_profile);
        average_ratings = findViewById(R.id.rating_bar_profile);
        image = findViewById(R.id.imageProfile);
        btn_change_image = findViewById(R.id.btn_change_image);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        if(!userMail.equals(firebaseUser.getEmail())) {
            btn_change_image.setVisibility(View.INVISIBLE);
        }

        btn_change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                User user = dataSnapshot.child(userID).getValue(User.class);
                username.setText(user.getUsername());
                mail.setText(user.getMail());
                ratings.setText(user.getRatings());
                average_ratings.setRating(Float.parseFloat(user.getAverage_ratings()));
                uploadImage(user.getSetted_image());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode != 0) {
            Uri mImageUri = data.getData();
            if(mImageUri != null) {

                reference.child(firebaseUser.getUid()).child("setted_image").setValue("true");
                StorageReference childRef = storageReference.child("/profile_images/"+userMail+".jpg");

                //uploading the image
                UploadTask uploadTask = childRef.putFile(mImageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadImage(true);
                        Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(ProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
            }


        }
    }

    protected void uploadImage(Boolean uploaded){

        if(!uploaded){
            image.setImageResource(R.mipmap.ic_launcher);

        }else{

            File localFile;
            try {
                localFile = File.createTempFile("images", "jpg");


                final File finalLocalFile = localFile;
                storageReference.child("profile_images/"+userMail+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Glide.with(getApplicationContext()).load(finalLocalFile).into(image);
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

}