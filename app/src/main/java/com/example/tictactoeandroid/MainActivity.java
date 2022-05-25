package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements Start_Fragment.OnFragmentInteractionListener {
    private static final String TAG = "ChallengeActivity";
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Uruchomiony");
        setContentView(R.layout.activity_main);

        //fragment for start or join the game
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, new Start_Fragment()).commit();
    }

    @Override
    public void onButtonSelected(int id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.e("Cokolwiek", "haha");
        // Replace whatever is in the fragment_container view with this fragment,
        if (id == 2) { //client
            transaction.replace(R.id.activity_main, new Client_Fragment());
        } else { //server
            transaction.replace(R.id.activity_main, new Server_Fragment());
        }
        // and add the transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
}