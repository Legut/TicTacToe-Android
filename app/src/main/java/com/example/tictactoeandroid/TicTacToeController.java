package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class TicTacToeController extends View
{
    private final Paint paint = new Paint();

    private final GameLogic gameLogic;

    private int cellSize = getWidth()/3;

    private final int boardColor;
    private  final int XColor;
    private  final int OColor;
    private  final int winingLineColor;


    private boolean winningLine = false;

    public TicTacToeController(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        gameLogic = new GameLogic();

        TypedArray board_attrib = context.getTheme().obtainStyledAttributes(attrs,R.styleable.TicTacToeBoard,0,0);

        try
        {
            boardColor = board_attrib.getInteger(R.styleable.TicTacToeBoard_boardColor,0);
            XColor = board_attrib.getInteger(R.styleable.TicTacToeBoard_XColor,0);
            OColor = board_attrib.getInteger(R.styleable.TicTacToeBoard_OColor,0);
            winingLineColor = board_attrib.getInteger(R.styleable.TicTacToeBoard_winingLineColor,0);
        }
        finally
        {
            board_attrib.recycle();
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if(action==MotionEvent.ACTION_DOWN)
        {
            int row = (int) Math.ceil(y/cellSize);
            int col = (int) Math.ceil(x/cellSize);

            if(!winningLine)
            {
                if(gameLogic.updateGameBoard(row,col))
                {
                    invalidate();

                    if(gameLogic.winnerCheck())
                    {
                        winningLine = true;
                        invalidate();
                    }

                    if(gameLogic.getPlayer()%2==0)
                    {
                        gameLogic.setPlayer(gameLogic.getPlayer()-1);
                    }
                    else
                    {
                        gameLogic.setPlayer(gameLogic.getPlayer()+1);
                    }
                }


            }



            invalidate();
            return true;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int dimension = Math.min(getMeasuredWidth(),getMeasuredHeight());
        cellSize = dimension/3;

        setMeasuredDimension(dimension,dimension);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        drawGameBoard(canvas);

        drawMarkers(canvas);

        if(winningLine)
        {
            paint.setColor(winingLineColor);
            drawWinningLine(canvas);
        }
    }

    private void drawGameBoard(Canvas canvas)
    {
        paint.setColor(boardColor);
        paint.setStrokeWidth(16);

        for(int i=1;i<3;i++)
        {
            canvas.drawLine(cellSize*i,0,cellSize*i,canvas.getWidth(),paint);
        }

        for(int z=1;z<3;z++)
        {
            canvas.drawLine(0,cellSize*z,canvas.getWidth(),cellSize*z,paint);
        }
    }

    private void drawMarkers(Canvas canvas)
    {
        for (int i = 0;i<3;i++)
        {
            for(int x = 0;x<3;x++)
            {
                if(gameLogic.getGameBoard()[i][x]!=0)
                {
                    if(gameLogic.getGameBoard()[i][x]==1)
                    {
                        drawX(canvas,i,x);
                    }
                    else
                    {
                        drawO(canvas,i,x);
                    }
                }
            }
        }
    }

    private void drawX(Canvas canvas, int row, int col)
    {
        paint.setColor(XColor);

        canvas.drawLine((col+1)*cellSize-cellSize*0.2f,row*cellSize+cellSize*0.2f,col*cellSize+cellSize*0.2f,(row+1)*cellSize-cellSize*0.2f,paint);

        canvas.drawLine(col*cellSize+cellSize*0.2f,row*cellSize+cellSize*0.2f,(col+1)*cellSize-cellSize*0.2f,(row+1)*cellSize-cellSize*0.2f,paint);
    }

    private void drawO(Canvas canvas, int row, int col)
    {
        paint.setColor(OColor);

        canvas.drawOval(col*cellSize+cellSize*0.2f,row*cellSize+cellSize*0.2f,(col*cellSize+cellSize)-cellSize*0.2f,(row*cellSize+cellSize)-cellSize*0.2f,paint);


    }

    private void drawHorizontalLine(Canvas canvas, int row, int col)
    {
        canvas.drawLine(col,row*cellSize+(float)cellSize/2,cellSize*3,row*cellSize+(float)cellSize/2,paint);
    }

    private void drawVerticalLine(Canvas canvas, int row, int col)
    {
        canvas.drawLine(col*cellSize+(float)cellSize/2,row,col*cellSize+(float)cellSize/2,cellSize*3,paint);
    }

    private void drawDiagonalLinePos(Canvas canvas)
    {
        canvas.drawLine(0,cellSize*3,cellSize*3,0,paint);
    }

    private void drawDiagonalLineNeg(Canvas canvas)
    {
        canvas.drawLine(0,0,cellSize*3,cellSize*3,paint);
    }

    public void resetGame()
    {
        gameLogic.resetGame();
        winningLine = false;
    }

    public void setUpGame(Button play_again, TextView player_turn,String[] player_names)
    {
        gameLogic.setPlay_again_button(play_again);
        gameLogic.setPlayer_turn(player_turn);
        gameLogic.setPlayer_names(player_names);

    }

    private void drawWinningLine(Canvas canvas)
    {
        int row = gameLogic.getWinType()[0];
        int col = gameLogic.getWinType()[1];

        switch (gameLogic.getWinType()[2])
        {
            case 1: drawHorizontalLine(canvas,row,col); break;
            case 2: drawVerticalLine(canvas,row,col); break;
            case 3: drawDiagonalLineNeg(canvas); break;
            case 4: drawDiagonalLinePos(canvas); break;
        }
    }

}
