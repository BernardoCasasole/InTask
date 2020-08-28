package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PopupActivity extends AppCompatActivity {

    EditText day, time;
    Calendar myCalendar;
    RadioGroup radioGroup;
    String userID;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            userID = b.getString("sent");
        }

        day = findViewById(R.id.day);
        time = findViewById(R.id.time);
        myCalendar = Calendar.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        radioGroup = findViewById(R.id.type);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        day.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(PopupActivity.this,date , myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Time time = snapshot.getValue(Time.class);
                    if(time.getAuthor().equals(userID)||time.getAuthor().equals(firebaseUser.getUid())){


                        RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(time.getTitle());
                        radioButton.setTag(time.getKey());//set radiobutton id and store it somewhere
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroup.addView(radioButton, params);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Job").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if(job.getAuthor().equals(userID)||job.getAuthor().equals(firebaseUser.getUid())){


                        RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(job.getTitle());
                        radioButton.setTag(job.getKey());//set radiobutton id and store it somewhere
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroup.addView(radioButton, params);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        day.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkHour(String result){

        int a = ((int) result.charAt(0)) - 48;
        int b = ((int) result.charAt(1)) - 48;
        int c = ((int) result.charAt(3)) - 48;
        if(a < 0 || b < 0 || c < 0) {
            Toast.makeText(PopupActivity.this, "Inserire orario corretto ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if((a > 2 || (a == 2 && b > 3)) || c > 59) {
            Toast.makeText(PopupActivity.this, "Inserire orario corretto", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
}