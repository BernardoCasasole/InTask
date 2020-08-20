package com.example.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.fragments.FriendsFragment;
import com.example.chatapp.fragments.RequestsFragment;
import com.example.chatapp.fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class FriendActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("myNot","myNot", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragments(new FriendsFragment(),"Friends");
        viewPageAdapter.addFragments(new UsersFragment(),"Users");
        viewPageAdapter.addFragments(new RequestsFragment(),"Requests");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    class ViewPageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}