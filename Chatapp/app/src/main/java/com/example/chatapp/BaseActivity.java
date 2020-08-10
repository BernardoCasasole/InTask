package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatapp.model.User;
import com.example.chatapp.start.StartActivity;
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

public abstract class BaseActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageReference storageReference;

    public void initToolbar(int toolbar){


        androidx.appcompat.widget.Toolbar myToolbar = findViewById(toolbar);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imageView = findViewById(R.id.imageProfileToolbar);
        username = findViewById(R.id.usernameToolbar);
        imageView.setVisibility(View.INVISIBLE);
        username.setVisibility(View.INVISIBLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(BaseActivity.this, StartActivity.class));
            finish();
            return true;
        }else if(id == R.id.friends){
            startActivity(new Intent(BaseActivity.this,FriendActivity.class));
            return true;

        }else if(id == R.id.profile){

            Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
            Bundle b = new Bundle();
            b.putString("userMail",firebaseUser.getEmail());
            b.putString("userID",firebaseUser.getUid());
            intent.putExtras(b);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    protected void showUsernameToolbar(String userId){

        imageView.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(!user.getSetted_image()){
                    imageView.setImageResource(R.mipmap.ic_launcher);

                }else{

                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");


                        final File finalLocalFile = localFile;
                        storageReference.child("profile_images/"+user.getMail()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Glide.with(getApplicationContext()).load(finalLocalFile).into(imageView);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
