package com.example.chatapp.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button btn_login, btn_register;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /*if(firebaseUser!=null){

            Intent intent = new Intent( StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/

        btn_login = findViewById(R.id.btn_start_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));

            }
        });

        btn_register = findViewById(R.id.btn_start_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(StartActivity.this, RegisterActivity.class));

            }
        });
    }
}