package com.example.tictactoeandroid;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private Button registerButton;
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
        registerButton = findViewById(R.id.register_button);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> startRegistrationProcess());
    }

    /**
     * Starts the registration process. Collects all the data provided via inputs, verifies data and
     * moves the process further by calling {@link #createNewUser this} method.
     */
    private void startRegistrationProcess() {
        String nickname = nicknameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String repeatPassword = repeatPasswordInput.getText().toString().trim();

        if(!dataIsCorrect(nickname, email, password, repeatPassword)){ return; }

        // Check if nickname is free to use
        db.collection("user_data").get().addOnCompleteListener(getUserDataTask -> {
            if (getUserDataTask.isSuccessful()) {
                // If nickname is already taken stop registration process and inform user
                for (QueryDocumentSnapshot document : getUserDataTask.getResult()) {
                    String dbNickname = Objects.requireNonNull(document.getData().get("nickname")).toString();
                    if (dbNickname.equals(nickname)) {
                        quickToast("Nickname is already in use!");
                        return;
                    }
                }

                // If nickname is free to use create new user via firebase authentication module
                createNewUser(email, password, nickname);
            }
            else {
                Log.d(TAG, "getUserDataTask: Task is not successful", getUserDataTask.getException());
            }
        });
    }

    /**
     * Creates new user via firebase authentication module using the data provided to the function.
     * If user creation succeeds registration process goes further by calling
     * {@link #insertAdditionalUserData this} method.
     * @param email string representing user email.
     * @param password string representing user password.
     * @param nickname string representing user nickname.
     */
    private void createNewUser(String email, String password, String nickname) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createUserTask -> {
            if (createUserTask.isSuccessful()) {
                // If user was created insert additional user data to user_data table
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

    /**
     * Inserts additional user data to user_data table. If insertion fails, then it removes current
     * user data from authentication module of firebase. If insertion succeed, then registration
     * activity is being finished.
     * @param userData Map containing user data. Key is a db field name and value is a db field value
     */
    private void insertAdditionalUserData(Map<String, Object> userData) {
        db.collection("user_data").add(userData)
                .addOnSuccessListener(addUserDataTask -> {
                    // If all data has been stored finish activity
                    quickToast("User created!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    // If there was a problem remove user from authentication database
                    Log.d(TAG, "addUserDataTask: Task is not successful", e);
                    quickToast("Error creating user!");
                    Objects.requireNonNull(fAuth.getCurrentUser()).delete();
                });
    }

    /**
     * Displays toast with a given text. Makes sure toasts aren't overlapping each other.
     * @param text defines what the massage inside a toast should say.
     */
    private void quickToast(String text) {
        if(lastToast != null) { lastToast.cancel(); }
        if(!text.equals("")) {
            lastToast = Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT);
            lastToast.show();
        }
    }

    /**
     * Verifies whether provided data is correct or not. Checks if it's empty or too short and so on.
     * @param nickname string representing the nickname of the user.
     * @param email string representing the email of the user.
     * @param password string representing the password of the user.
     * @param repeatPassword string representing the repeated password of the user.
     * @return true if provided data is correct and false if it's not.
     */
    private boolean dataIsCorrect(String nickname, String email, String password, String repeatPassword) {
        if(TextUtils.isEmpty(nickname)){
            quickToast("Nickname is required!");
            return false;
        }
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
        if(password.length() < 6) {
            quickToast("Password must be at least 6 characters long!");
            return false;
        }
        if(TextUtils.isEmpty(repeatPassword)){
            quickToast("Password validation is required!");
            return false;
        }
        if(!TextUtils.equals(password, repeatPassword)){
            quickToast("Passwords do not match!");
            return false;
        }

        return true;
    }
}