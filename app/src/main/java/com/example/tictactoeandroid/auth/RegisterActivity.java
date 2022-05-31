package com.example.tictactoeandroid.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoeandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText nicknameInput, emailInput, passwordInput, repeatPasswordInput;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private Toast lastToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nicknameInput = findViewById(R.id.nickname_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        repeatPasswordInput = findViewById(R.id.repeat_password_input);
        Button registerButton = findViewById(R.id.register_button);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        registerButton.setOnClickListener(v -> startRegistrationProcess());
    }

    private void startRegistrationProcess() {
        String nickname = nicknameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String repeatPassword = repeatPasswordInput.getText().toString().trim();

        if (!dataIsCorrect(nickname, email, password, repeatPassword)) {
            return;
        }

        db.collection("user_data").get().addOnCompleteListener(getUserDataTask -> {
            if (getUserDataTask.isSuccessful()) {
                for (QueryDocumentSnapshot document : getUserDataTask.getResult()) {
                    String dbNickname = Objects.requireNonNull(document.getData().get("nickname")).toString();
                    if (dbNickname.equals(nickname)) {
                        quickToast("Nickname is already in use!");
                        return;
                    }
                }
                createNewUser(email, password, nickname);
            } else {
                Log.d(TAG, "getUserDataTask: Task is not successful", getUserDataTask.getException());
            }
        });
    }

    private void createNewUser(String email, String password, String nickname) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createUserTask -> {
            if (createUserTask.isSuccessful()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", fAuth.getUid());
                userData.put("nickname", nickname);
                userData.put("friends", FieldValue.arrayUnion(""));
                userData.put("points", 0);
                insertAdditionalUserData(userData);
            } else {
                Log.w(TAG, "createUserTask: Task is not successful", createUserTask.getException());
                quickToast(Objects.requireNonNull(createUserTask.getException()).getMessage());
            }
        });
    }

    private void insertAdditionalUserData(Map<String, Object> userData) {
        db.collection("user_data").add(userData)
                .addOnSuccessListener(addUserDataTask -> {
                    quickToast("User created!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "addUserDataTask: Task is not successful", e);
                    quickToast("Error creating user!");
                    Objects.requireNonNull(fAuth.getCurrentUser()).delete();
                });
    }

    private void quickToast(String text) {
        if (lastToast != null) {
            lastToast.cancel();
        }
        if (!text.equals("")) {
            lastToast = Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT);
            lastToast.show();
        }
    }

    private boolean dataIsCorrect(String nickname, String email, String password, String repeatPassword) {
        if (TextUtils.isEmpty(nickname)) {
            quickToast("Nickname is required!");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            quickToast("Email is required!");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            quickToast("Something is wrong with your email!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            quickToast("Password is required!");
            return false;
        }
        if (password.length() < 6) {
            quickToast("Password must be at least 6 characters long!");
            return false;
        }
        if (TextUtils.isEmpty(repeatPassword)) {
            quickToast("Password validation is required!");
            return false;
        }
        if (!TextUtils.equals(password, repeatPassword)) {
            quickToast("Passwords do not match!");
            return false;
        }
        return true;
    }
}