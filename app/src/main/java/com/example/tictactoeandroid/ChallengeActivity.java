package com.example.tictactoeandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tictactoeandroid.game.ClientFragment;
import com.example.tictactoeandroid.game.ServerFragment;
import com.example.tictactoeandroid.game.StartFragment;

public class ChallengeActivity extends AppCompatActivity implements StartFragment.OnFragmentInteractionListener {
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, new StartFragment()).commit();
    }

    @Override
    public void onButtonSelected(int id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (id == 2) {
            transaction.replace(R.id.activity_main, new ClientFragment());
        } else {
            transaction.replace(R.id.activity_main, new ServerFragment());
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }
}