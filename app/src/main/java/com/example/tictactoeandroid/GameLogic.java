package com.example.tictactoeandroid;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameLogic
{
    private  int [][] gameBoard;

    private int player = 1;

    private String[] player_names = {"Player1","Player2"};

    private TextView player_turn;
    private Button play_again_button;

    private int[] winType ={-1,-1,-1};


    GameLogic()
    {
        gameBoard = new int [3][3];
        for (int i = 0;i<3;i++)
        {
            for(int x = 0;x<3;x++)
            {
                gameBoard[i][x]=0;
            }
        }
    }

    public boolean updateGameBoard(int row, int col)
    {
        if(gameBoard[row-1][col-1] == 0)
        {
            gameBoard[row-1][col-1] = player;

            if(player==1)
            {
                player_turn.setText((player_names[1]+"'s Turn"));
            }
            else
            {
                player_turn.setText((player_names[0]+"'s Turn"));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean winnerCheck()
    {
        boolean isWiner = false;

        for(int i=0;i<3;i++)
        {
            if(gameBoard[i][0]==gameBoard[i][1] && gameBoard[i][0] == gameBoard[i][2]  && gameBoard[i][0]!=0)
            {
                winType = new int[] {i,0,1};
                isWiner = true;
            }
        }

        for(int x=0;x<3;x++)
        {
            if(gameBoard[0][x]==gameBoard[1][x] && gameBoard[2][x] == gameBoard[0][x]  && gameBoard[0][x]!=0)
            {
                winType = new int[] {0,x,2};
                isWiner = true;
            }
        }

        if(gameBoard[0][0] == gameBoard[1][1] && gameBoard[0][0] == gameBoard[2][2] && gameBoard[0][0]!=0)
        {
            winType = new int[] {0,2,3};
            isWiner = true;
        }

        if(gameBoard[2][0] == gameBoard[1][1] && gameBoard[2][0] == gameBoard[0][2] && gameBoard[2][0]!=0)
        {
            winType = new int[] {2,2,4};
            isWiner = true;
        }

        int boardFilled=0;

        for(int i=0;i<3;i++)
        {
            for(int x=0;x<3;x++)
            {
                if(gameBoard[i][x] !=0)
                {
                    boardFilled+=1;
                }
            }
        }

        if(isWiner)
        {
            play_again_button.setVisibility(View.VISIBLE);
            player_turn.setText(player_names[player-1]+" Won");
            return true;
        }
        else if(boardFilled == 9)
        {
            play_again_button.setVisibility(View.VISIBLE);
            player_turn.setText("Tie Game");
            return true;
        }
        else
        {
            return false;
        }

    }

    public void setPlayer(int player)
    {
        this.player = player;
    }

    public int getPlayer()
    {
        return player;
    }

    public int[][] getGameBoard()
    {
        return gameBoard;
    }

    public void resetGame()
    {
        for (int i = 0;i<3;i++)
        {
            for(int x = 0;x<3;x++)
            {
                gameBoard[i][x]=0;
            }
        }

        player = 1;

        play_again_button.setVisibility(View.GONE);
        player_turn.setText(player_names[0]+"'s Turn");
    }

    public void setPlay_again_button(Button play_again_button)
    {
        this.play_again_button = play_again_button;
    }

    public void setPlayer_turn(TextView playerturn)
    {
        this.player_turn = playerturn;
    }

    public void setPlayer_names(String[] player_names)
    {
        this.player_names = player_names;
    }

    public int[] getWinType()
    {
        return winType;
    }

}
