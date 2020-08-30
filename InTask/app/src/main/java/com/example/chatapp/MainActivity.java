package com.example.chatapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.chatapp.fragments.ChatsFragment;
import com.example.chatapp.fragments.HomeAdsFragment;
import com.example.chatapp.fragments.HomeFragment;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.fragments.ads.PublicationChoiceFragment;
import com.example.chatapp.start.StartActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    Fragment homeFragment;
    BottomNavigationView bottomNavigationView;
    boolean close = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.i("FCM Token", token);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                if(firebaseUser!=null)
                    reference.child(firebaseUser.getUid()).setValue(token);
            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("myNot","myNot", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        Bundle bundle = new Bundle();
        bundle.putString("myAds", "false");
        homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Bundle bundle;
                close = false;
                switch (item.getItemId()){
                    case R.id.bottom_nav_message:

                        if(firebaseUser != null) {
                            selectedFragment = new ChatsFragment();
                        }else{
                            startActivity(new Intent( MainActivity.this, StartActivity.class));
                            return true;
                        }
                        break;
                    case R.id.bottom_nav_my_ads:
                        if(firebaseUser==null) {
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        return true;
                    }else {
                        bundle = new Bundle();
                        bundle.putString("myAds", "true");
                        selectedFragment = new HomeFragment();
                        selectedFragment.setArguments(bundle);
                        }
                        break;
                    case R.id.bottom_nav_home:
                        bundle = new Bundle();
                        bundle.putString("myAds", "false");
                        selectedFragment = new HomeFragment();
                        selectedFragment.setArguments(bundle);
                        break;

                    case R.id.bottom_nav_publish:

                        if(firebaseUser != null) {
                            selectedFragment = new PublicationChoiceFragment();
                        }else{
                            startActivity(new Intent( MainActivity.this, StartActivity.class));
                            return true;
                        }
                        break;

                    case R.id.bottom_nav_profile:

                        if(firebaseUser==null) {
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            return true;
                        }else {
                            bundle = new Bundle();
                            bundle.putString("id", firebaseUser.getUid());
                            selectedFragment = new ProfileFragment();
                            selectedFragment.setArguments(bundle);
                            break;


                        }

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }
    @Override
    public void onBackPressed() {
        if(close)
            super.onBackPressed();
        else {
            close =true;
            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }
    }
}