package com.example.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.fragments.FriendsFragment;
import com.example.chatapp.fragments.PublicationChoiceFragment;
import com.example.chatapp.fragments.RequestsFragment;
import com.example.chatapp.fragments.UsersFragment;
import com.example.chatapp.start.StartActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){

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