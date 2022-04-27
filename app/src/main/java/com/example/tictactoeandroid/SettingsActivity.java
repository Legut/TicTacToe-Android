package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.widget.Button;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity
{
    SwitchCompat ThemeSwitch;
    SharedPreferences sharedPreferences = null;
    Button AudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_settings);
        ThemeSwitch = findViewById(R.id.Theme);

        sharedPreferences = getSharedPreferences("night",0);
        Boolean boleanValue = sharedPreferences.getBoolean("night_mode",false);

        if(!boleanValue)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            ThemeSwitch.setChecked(false);
            ThemeSwitch.setText("Enable Dark Mode");
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            ThemeSwitch.setChecked(true);
            ThemeSwitch.setText("Disable Dark Mode");
        }


        ThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ThemeSwitch.setChecked(true);
                    ThemeSwitch.setText("Disable Dark Mode");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ThemeSwitch.setChecked(false);
                    ThemeSwitch.setText("Enable Dark Mode");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });


        AudioList = findViewById(R.id.AudioListButton);
        AudioList.setOnClickListener(task -> startActivity(new Intent(getApplicationContext(), BackgroundMusicActivity.class)));


    }
}