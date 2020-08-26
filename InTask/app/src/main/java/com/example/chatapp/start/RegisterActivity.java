package com.example.chatapp.start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.User;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText surname, name, mail, password;
    Button btn_register;


    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);



        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if no view has focus:
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                reference.addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String text_name = name.getText().toString();
                        String text_surname = surname.getText().toString();
                        String text_mail = mail.getText().toString();
                        String text_password = password.getText().toString();

                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        boolean found = false;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            if (data.getValue(User.class).getMail().equals(text_mail)) {
                                Toast.makeText(getApplicationContext(), "Mail già esistente!", Toast.LENGTH_SHORT).show();
                                found = true;
                            }
                        }
                        if (!found) {

                            if (TextUtils.isEmpty(text_mail) | TextUtils.isEmpty(text_name) | TextUtils.isEmpty(text_surname) | TextUtils.isEmpty(text_password)) {
                                Toast.makeText(getApplicationContext(), "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
                            } else if (text_password.length() < 8) {
                                Toast.makeText(getApplicationContext(), "La password deve essere almeno di 8 caratteri!", Toast.LENGTH_SHORT).show();
                            } else if (text_name.contains(" ") | text_surname.contains(" ")) {
                                Toast.makeText(getApplicationContext(), "Non usare spazi", Toast.LENGTH_SHORT).show();
                            } else {
                                register(text_name, text_surname, text_mail, text_password);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        });


    }

    private void register(final String name, final String surname, final String mail, String password) {

        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("name", name);
                    hashMap.put("surname", surname);
                    hashMap.put("mail", mail);
                    hashMap.put("setted_image", false);
                    hashMap.put("verified",false);
                    hashMap.put("ratings", 0);
                    hashMap.put("average_ratings", 0);
                    hashMap.put("location","");
                    hashMap.put("typeReg","Mail");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Non puoi registarti con questa mail e/o passowrd", Toast.LENGTH_SHORT).show();

                }
            }

        });


    }


}