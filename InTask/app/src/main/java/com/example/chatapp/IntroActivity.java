package com.example.chatapp;
import androidx.appcompat.app.AppCompatActivity;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import retrofit2.http.Url;

public class IntroActivity extends AppCompatActivity {
    Button button;
    PDFView mPdfView;
    CheckBox agreed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_intro2);
                button = findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setContentView(R.layout.activity_intro3);
                        button = findViewById(R.id.button);
                        button.setClickable(false);
                        button.setActivated(false);
                        button.setEnabled(false);
                        mPdfView = (PDFView) findViewById(R.id.pdfView);
                        mPdfView.fromAsset("privacy_and_condition.pdf").load();
                        agreed = findViewById(R.id.accept);
                        agreed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(agreed.isChecked()){
                                    button.setClickable(true);
                                    button.setActivated(true);
                                    button.setEnabled(true);
                                }
                            }
                        });
                        Button lib_fam_but = findViewById(R.id.libr_fam);
                        lib_fam_but.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://www.inps.it/nuovoportaleinps/default.aspx?itemdir=51098"));
                                startActivity(intent);
                            }
                        });



                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
}