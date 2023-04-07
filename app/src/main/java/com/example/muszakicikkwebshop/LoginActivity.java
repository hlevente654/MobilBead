package com.example.muszakicikkwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    private static final String PREF_KEY = LoginActivity.class.getPackage().toString();

    SharedPreferences preferences;

    EditText userLoginEmail;
    EditText userLoginPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userLoginEmail = findViewById(R.id.editTextLoginEmail);
        userLoginPassword = findViewById(R.id.editTextLoginPassword);


        mAuth = FirebaseAuth.getInstance();


        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

    }

    public void cancelLogin(View view) {
        finish();
    }

    public void logingIn(View view) {
        String usersLoginPassword = userLoginPassword.getText().toString();
        String usersLoginEmail = userLoginEmail.getText().toString();

        mAuth.signInWithEmailAndPassword(usersLoginEmail, usersLoginPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Bejelentkezés sikeres");
                    AuthResult authResult = task.getResult();
                    String email = authResult.getUser().getEmail();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userEmail", email);
                    editor.apply();
                    finish();
                } else {
                    Log.d(LOG_TAG, "Bejelentkezés sikertelen");
                }
            }
        });
    }

}