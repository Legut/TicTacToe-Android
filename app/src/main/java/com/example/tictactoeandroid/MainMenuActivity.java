package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity {
    private static final String TAG = "MainMenuActivity";
    private TextView smallTitleTextView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button challengeButton = findViewById(R.id.challenge_user_button);
        Button friendsButton = findViewById(R.id.friends_button);
        Button settingsButton = findViewById(R.id.settings_button);
        Button logoutButton = findViewById(R.id.logout_button);
        smallTitleTextView = findViewById(R.id.small_title);
        mAdView = findViewById(R.id.adView);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() == null) {
            finish();
        }

        challengeButton.setOnClickListener(task -> startActivity(new Intent(getApplicationContext(), ChallengeActivity.class)));
        friendsButton.setOnClickListener(task -> startActivity(new Intent(getApplicationContext(), FriendsActivity.class)));
        settingsButton.setOnClickListener(task -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
        logoutButton.setOnClickListener(task -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        loadAdBanner();
        printNicknameToSmallTitle();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void printNicknameToSmallTitle() {
        db.collection("user_data").whereEqualTo("uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            smallTitleTextView.setText("Hello " + document.getData().getOrDefault("nickname", "user") +
                                    "\nPoints: " + document.getData().getOrDefault("points", "Error..."));
                        }
                    } else {
                        Log.w(TAG, "Error getting document", task.getException());
                    }
                });
    }

    private void loadAdBanner() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "loadAdBanner: Ad loaded successfully!");
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.d(TAG, "loadAdBanner: Something went wrong while loading the ad. " + adError);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        printNicknameToSmallTitle();
    }
}