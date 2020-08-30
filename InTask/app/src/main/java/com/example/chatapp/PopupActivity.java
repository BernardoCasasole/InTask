package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chatapp.fragments.APIService;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.example.chatapp.model.User;
import com.example.chatapp.notifications.Client;
import com.example.chatapp.notifications.Data;
import com.example.chatapp.notifications.MyResponse;
import com.example.chatapp.notifications.Notification;
import com.example.chatapp.notifications.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupActivity extends AppCompatActivity {

    EditText day, time;
    Calendar myCalendar;
    RadioGroup radioGroup;
    String userID;
    FirebaseUser firebaseUser;
    Button btn_send;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            userID = b.getString("sent");
        }

        day = findViewById(R.id.day);
        time = findViewById(R.id.time);
        myCalendar = Calendar.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        radioGroup = findViewById(R.id.type);
        btn_send = findViewById(R.id.proposal_btn_send);

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
                DatePickerDialog datePickerDialog =

                new DatePickerDialog(PopupActivity.this,date , myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Time time = snapshot.getValue(Time.class);
                    if(!time.getPending() && !time.getAchieved() &&(time.getAuthor().equals(userID)||time.getAuthor().equals(firebaseUser.getUid()))){


                        RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(time.getTitle());
                        radioButton.setTag("Time@".concat(time.getKey()));//set radiobutton id and store it somewhere
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroup.addView(radioButton, params);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Job").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if(!job.getPending() && !job.getAchieved() &&(job.getAuthor().equals(userID)||job.getAuthor().equals(firebaseUser.getUid()))){


                        RadioButton radioButton = new RadioButton(getApplicationContext());
                        radioButton.setText(job.getTitle());
                        radioButton.setTag("Job@".concat(job.getKey()));//set radiobutton id and store it somewhere
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroup.addView(radioButton, params);
                    }

                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (checkHour(time.getText().toString()) &&
                                    findViewById(radioGroup.getCheckedRadioButtonId()) != null &&
                                    day.getText() != null) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                                        String tag = (String) radioButton.getTag();
                                        String[] attributes = tag.split("@");
                                        String message = user.getName() + " ha fatto una proposta per " +
                                                radioButton.getText().toString() + " in data "+ day.getText().toString() +
                                                ", ora " + time.getText().toString() + ".";
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("sender", firebaseUser.getUid());
                                        map.put("receiver", userID);
                                        map.put("message", message);
                                        map.put("type", attributes[0]);
                                        map.put("ads", attributes[1]);
                                        map.put("key",reference.getKey());
                                        map.put("title",radioButton.getText().toString());
                                        map.put("date",day.getText().toString());

                                        reference.setValue(map);
                                        sendNotifications(userID, user.getId(), user.getName(), message);
                                        FirebaseDatabase.getInstance().getReference(attributes[0]).child(attributes[1]).child("pending").setValue(true);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });


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

    private void sendNotifications(final String receiverId, final String userId,final String name, final String message){


        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = token.orderByKey().equalTo(receiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Data data = new Data(userId,receiverId, "Nuovo messaggio da "+ name +"!",message);
                    Notification notification = new Notification(message, "Nuovo messaggio da "+ name +"!",".MessagingActivity");
                    Sender sender = new Sender(data,snapshot.getValue(String.class));

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}