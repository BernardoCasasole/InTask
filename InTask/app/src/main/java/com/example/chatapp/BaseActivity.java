package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.start.StartActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public abstract class BaseActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;
    int selectedItem;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageReference storageReference;

    public void initToolbar(){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(selectedItem);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_nav_publish:

                        if(firebaseUser !=null) {
                            startActivity(new Intent(BaseActivity.this, PublicationChoiceActivity.class));
                        }else{
                            startActivity(new Intent( BaseActivity.this, StartActivity.class));
                        }
                        return true;

                    case R.id.bottom_nav_profile:

                        if(firebaseUser!=null) {
                            startActivity(new Intent(BaseActivity.this, StartActivity.class));
                        }else {
                            Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                           /* Bundle b = new Bundle();
                            b.putString("userMail", firebaseUser.getEmail());
                            b.putString("userID", firebaseUser.getUid());
                            intent.putExtras(b);*/
                            startActivity(intent);
                            return true;
                        }
                }
                return false;
            }
        });

    }
}

