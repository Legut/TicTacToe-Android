package com.example.tictactoeandroid.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.tictactoeandroid.MainMenuActivity;
import com.example.tictactoeandroid.R;
import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth fAuth;
    private EditText emailInput, passwordInput;
    private Toast lastToast;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("night",0);
        boolean boleanValue = sharedPreferences.getBoolean("night_mode",false);
        if(!boleanValue)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        fAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if(!dataIsCorrect(email, password)) { return; }

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    quickToast("Authenticated successfully!");
                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                } else {
                    Log.w(TAG, "signInWithEmailAndPassword: failure", task.getException());
                    quickToast("Authentication failed!");
                }
            });
        });
        registerButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        emailInput.setText("");
        passwordInput.setText("");
    }

    private boolean dataIsCorrect(String email, String password) {
        if(TextUtils.isEmpty(email)){
            quickToast("Email is required!");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            quickToast("Something is wrong with your email!");
            return false;
        }
        if(TextUtils.isEmpty(password)){
            quickToast("Password is required!");
            return false;
        }
        return true;
    }

    public static boolean dataIsCorrectStatic(String email, String password) {
        if(email == null || email.length() == 0)
            return false;
        if(password == null || password.length() == 0)
            return false;
        return true;
    }

    private void quickToast(String text) {
        if(lastToast != null) { lastToast.cancel(); }
        if(!text.equals("")) {
            lastToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            lastToast.show();
        }
    }
}
