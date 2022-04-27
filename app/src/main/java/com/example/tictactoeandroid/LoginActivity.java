package com.example.tictactoeandroid;

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

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth fAuth;
    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private Toast lastToast;


    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("night",0);
        Boolean boleanValue = sharedPreferences.getBoolean("night_mode",false);

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
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Initialize Firebase Auth
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

    /**
     * Clears inputs after pausing activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        emailInput.setText("");
        passwordInput.setText("");
    }

    /**
     * Verifies whether provided data is correct or not. Checks if it's empty or matches email regex.
     * @param email string representing the email of the user.
     * @param password string representing the password of the user.
     * @return true if provided data is correct and false if it's not.
     */
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

    /**
     * Displays toast with a given text. Makes sure toasts aren't overlapping each other.
     * @param text defines what the massage inside a toast should say.
     */
    private void quickToast(String text) {
        if(lastToast != null) { lastToast.cancel(); }
        if(!text.equals("")) {
            lastToast = Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT);
            lastToast.show();
        }
    }
}
