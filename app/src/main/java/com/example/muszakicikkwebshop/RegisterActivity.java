package com.example.muszakicikkwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private FirebaseAuth mAuth;
    SharedPreferences preferences;

    EditText userName;
    EditText userEmail;
    EditText userPassword;
    EditText userPasswordAgain;
    TextView regisztraciosHibakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.editTextRegisterEmail);
        userPassword = findViewById(R.id.editTextRegisterPassword);
        userPasswordAgain = findViewById(R.id.editTextRegisterPasswordAgain);
        userName = findViewById(R.id.editTextRegisterUserName);
        regisztraciosHibakText = (TextView) findViewById(R.id.textViewHibak);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();
    }

    public void regisztralas(View view) {
        ArrayList<String> regisztraciosHibak = new ArrayList<>();

        String usersEmail = userEmail.getText().toString();
        String usersPassword = userPassword.getText().toString();
        String usersPasswordAgain = userPasswordAgain.getText().toString();
        String usersName = userName.getText().toString();
        if(usersName.length() < 6){
            regisztraciosHibak.add("- Túl rövid a felhasználónév");
        }
        if(usersPassword.length() < 8){
            regisztraciosHibak.add("- Túl rövid a jelszó");
        }
        if(!usersPassword.equals(usersPasswordAgain)){
            regisztraciosHibak.add("- A jelszó és a jelszó \nmegerősítése nem egyeznek");
        }
        if(!usersEmail.contains("@") || !usersEmail.contains(".") || usersEmail.length() < 8){
            regisztraciosHibak.add("- E-mail nem megfelelő\n");
        }
        if(regisztraciosHibak.size() > 0){
            String uzenet = "";
            for (int i=0;i<regisztraciosHibak.size(); i++){
                uzenet = uzenet + "\n" + regisztraciosHibak.get(i);
            }
            regisztraciosHibakText.setText(uzenet);
        } else{
            mAuth.createUserWithEmailAndPassword(usersEmail, usersPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.i(LOG_TAG,"Siker");
                        AuthResult authResult = task.getResult();
                        String email = authResult.getUser().getEmail();
                        Log.d(LOG_TAG, "Halo: "+email);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userEmail", email);
                        editor.apply();
                        finish();
                    } else{
                        // hiba üzenet
                        Log.i(LOG_TAG,"HIBA");
                    }
                }
            });
        }
    }
    public void cancelRegister(View view) {
        finish();
    }
}