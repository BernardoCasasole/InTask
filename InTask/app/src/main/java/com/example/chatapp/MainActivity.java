package com.example.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatapp.fragments.HomeFragment;
import com.example.chatapp.fragments.ads.PublicationChoiceFragment;
import com.example.chatapp.start.StartActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    Fragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("myNot","myNot", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.bottom_nav_home:
                        selectedFragment = homeFragment;
                        break;

                    case R.id.bottom_nav_publish:

                        if(firebaseUser != null) {
                            selectedFragment = new PublicationChoiceFragment();
                        }else{
                            startActivity(new Intent( MainActivity.this, StartActivity.class));
                        }
                        break;

                    case R.id.bottom_nav_profile:

                        if(firebaseUser!=null) {
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                        }else {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                           /* Bundle b = new Bundle();
                            b.putString("userMail", firebaseUser.getEmail());
                            b.putString("userID", firebaseUser.getUid());
                            intent.putExtras(b);*/
                            startActivity(intent);
                            break;
                        }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }

}