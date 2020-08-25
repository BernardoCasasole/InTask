package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.FriendActivity;
import com.example.chatapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    View view;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        HomeFragment.ViewPageAdapter viewPageAdapter = new HomeFragment.ViewPageAdapter(getChildFragmentManager());
        Bundle b = getArguments();
        String myAds = "true";
        if(b != null) {
            myAds = b.getString("myAds");
        }
        bundle = new Bundle();
        bundle.putString("type", "time");
        bundle.putString("myAds", myAds);
        Fragment fragment = new HomeAdsFragment();
        fragment.setArguments(bundle);
        viewPageAdapter.addFragments(fragment,"Time");


        bundle = new Bundle();
        bundle.putString("type", "job");
        bundle.putString("myAds", myAds);
        fragment = new HomeAdsFragment();
        fragment.setArguments(bundle);
        viewPageAdapter.addFragments(fragment,"Job");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    return view;
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