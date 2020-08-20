package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatapp.start.RegisterActivity;

import android.view.View;
import android.widget.Button;

public class PublicationChoiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_choice);

        selectedItem = R.id.bottom_nav_publish;
        initToolbar();

        Button btnPublishJob = findViewById(R.id.button_publish_job);
        Button btnPublishTime = findViewById(R.id.button_publish_time);


        btnPublishJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PublicationChoiceActivity.this, JobSvFormActivity.class));
            }
        });

        btnPublishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PublicationChoiceActivity.this, PublicationTimeActivity.class));
            }
        });
    }
}