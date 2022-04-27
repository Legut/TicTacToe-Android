package com.example.tictactoeandroid;

import android.content.Intent;
import android.media.MediaPlayer;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;



import java.lang.reflect.Field;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BackgroundMusicActivity extends AppCompatActivity
{
    ListView listView;
    TrackModel[] tracList;
    TrackAdapter adapter;
    static MediaPlayer mediaPlayer;
    Field[] fields;
    int trackID;
    Button mute_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_music);

        listView = findViewById(R.id.track_list_view);
        mute_button = findViewById(R.id.Mute);

        fields=R.raw.class.getFields();
        tracList = new TrackModel[fields.length];

        loadTracks();

        mute_button.setOnClickListener(v -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                loadTracks();

                TrackModel track = tracList[i];
                trackID = track.getId();

                if(mediaPlayer!=null)
                {
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        track.setPlaying(false);
                    }

                }

                try
                {


                    mediaPlayer = MediaPlayer.create(BackgroundMusicActivity.this,track.getId());
                    mediaPlayer.setLooping(true);

                    if(mediaPlayer.isPlaying())
                    {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        track.setPlaying(false);
                    }
                    else
                    {
                        mediaPlayer.start();
                        track.setPlaying(true);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.getMessage());
                }

            }
        });

    }

    public void loadTracks()
    {


        for(int count=0; count < fields.length; count++)
        {
            int id = getResources().getIdentifier(fields[count].getName(), "raw", getPackageName());
           tracList[count] = new TrackModel(id,fields[count].getName(),false);

        }



        adapter = new TrackAdapter(BackgroundMusicActivity.this,tracList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

       if(mediaPlayer!=null)
        {
            //if(mediaPlayer.isPlaying())
           // {
             //   mediaPlayer.stop();
              //  mediaPlayer.reset();

            //}
        }

    }
}