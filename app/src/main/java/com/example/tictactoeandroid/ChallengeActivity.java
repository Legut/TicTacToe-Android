package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChallengeActivity extends AppCompatActivity
{


    private TicTacToeController ticTacToeController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        Button playagain = findViewById(R.id.playagainbutton);
        TextView playerturn = findViewById(R.id.textView_playerturn);

        playagain.setVisibility((View.GONE));

        //TODO TUTAJ MUSI BRAC IMIONA GRACZY
        String[] player_names = {"Player1","Player2"};

        if(player_names!=null)
        {
            playerturn.setText(player_names[0]+"'s Turn");
        }

        ticTacToeController = findViewById(R.id.ticTacToeBoard);

        ticTacToeController.setUpGame(playagain,playerturn,player_names);
    }

    public void playAgainButtonClick(View view)
    {
        ticTacToeController.resetGame();
        ticTacToeController.invalidate();
    }


}