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

}

