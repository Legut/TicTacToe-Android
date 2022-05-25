package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Game_Fragment extends Fragment {
    TextView status;
    private AlertDialog dialog;
    private final String noWinnerMessage = "no winner";
    public String turn = "X";
    private String myMark;

    BluetoothService mConnectedThread = null;

    public static String MARK_CHOSEN = "MARK_CHOSEN";
    public int[][] matrix = new int[3][3]; //matrix to know who won

    public static TextView arrayOfButtons[][] = new TextView[3][3];
    //buttons
    private TextView c00 = null;
    private TextView c01 = null;
    private TextView c02 = null;
    private TextView c10 = null;
    private TextView c11 = null;
    private TextView c12 = null;
    private TextView c20 = null;
    private TextView c21 = null;
    private TextView c22 = null;

    private static String IS_SERVER;


    public static Game_Fragment newInstance(String mark, boolean server) {
        Game_Fragment game = new Game_Fragment();
        Bundle bdl = new Bundle(2);
        bdl.putString(MARK_CHOSEN, mark);
        bdl.putBoolean(IS_SERVER, server);

        game.setArguments(bdl);
        return game;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if(!isMatrixFull() && !isWinnerFound() && readMessage.length() == 2) {
                        int i = readMessage.codePointAt(0) - 48;
                        int j = readMessage.codePointAt(1) - 48;
                        if (i < 3 && j < 3 ) {
                            putInMatrix(i, j, turn);
                            updateUI();
                            switchTurn(turn);
                        }
                    }

                    if(readMessage.equals("X")) {
                        dialog = createDialog("O");
                        dialog.show();
                    }
                    if(readMessage.equals("O")) {
                        dialog = createDialog("X");
                        dialog.show();
                    }
                    if(readMessage.equals(noWinnerMessage)) {
                        dialog = createDialog(noWinnerMessage);
                        dialog.show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isServer;

        myMark = getArguments().getString(MARK_CHOSEN);
        isServer = getArguments().getBoolean(IS_SERVER);
        if(isServer) {
            mConnectedThread = Server_Fragment.getBluetoothService(); }
        else {
            mConnectedThread = Client_Fragment.getBluetoothService(); }

        mConnectedThread.putNewHandler(handler);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_game, container, false);
        status = myView.findViewById(R.id.Status);
        status.setText("playing for: " + myMark);

        initButtons(myView);
        buttonsToArray();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                final String col = String.valueOf(i);
                final String row = String.valueOf(j);
                final String colRow = col + row;
                arrayOfButtons[i][j].setOnClickListener(v -> {
                    if(!isMatrixFull() && !isWinnerFound()) { handleCellClick(colRow);
                    } else {
                        checkGameOverCase();
                        Toast.makeText(getActivity(), "game is done", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return myView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnectedThread != null) {
            mConnectedThread.stop();
        }
    }

    private void initButtons(View myView) {
        c00 = myView.findViewById(R.id.cell11);
        c01 = myView.findViewById(R.id.cell12);
        c02 = myView.findViewById(R.id.cell13);
        c10 = myView.findViewById(R.id.cell21);
        c11 = myView.findViewById(R.id.cell22);
        c12 = myView.findViewById(R.id.cell23);
        c20 = myView.findViewById(R.id.cell31);
        c21 = myView.findViewById(R.id.cell32);
        c22 = myView.findViewById(R.id.cell33);
    }

    private void buttonsToArray() {
        arrayOfButtons[0][0] = c00;
        arrayOfButtons[0][1] = c01;
        arrayOfButtons[0][2] = c02;
        arrayOfButtons[1][0] = c10;
        arrayOfButtons[1][1] = c11;
        arrayOfButtons[1][2] = c12;
        arrayOfButtons[2][0] = c20;
        arrayOfButtons[2][1] = c21;
        arrayOfButtons[2][2] = c22;
    }

    private void handleCellClick(String colRow) {
        int col = colRow.codePointAt(0)-48;
        int row = colRow.codePointAt(1)-48;

        if (turn.equals(myMark)) {
            mConnectedThread.write(colRow.getBytes());
            putInMatrix(col, row, myMark);
            updateUI();
            switchTurn(myMark);
            checkGameOverCase();
        } else {
            Toast.makeText(getActivity(), "Not your turn", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkGameOverCase() {
        if(isWinnerFound()) {
            dialog = createDialog(turn);
            dialog.show();
            mConnectedThread.write(turn.getBytes());
        }
        if(isMatrixFull()) {
            dialog = createDialog(noWinnerMessage);
            dialog.show();
            mConnectedThread.write(noWinnerMessage.getBytes());
        }
    }

    private void switchTurn(String currentTurn) {
        if(currentTurn.equals("X")) turn = "O";
        if(currentTurn.equals("O")) turn = "X";
    }

    private void updateUI() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                switch (matrix[i][j]) {
                    case Constants.X: arrayOfButtons[i][j].setText("X");break;
                    case Constants.O: arrayOfButtons[i][j].setText("O");break;
                    case Constants.NONE: arrayOfButtons[i][j].setText("");break;
                }
            }
        }
    }

    public void putInMatrix(int i, int j, String currentMark) {
        if(matrix[i][j] != Constants.X && matrix[i][j] != Constants.O) {
            switch (currentMark) {
                case "X": matrix[i][j] = Constants.X; break;
                case "O": matrix[i][j] = Constants.O;
            }
        }
    }

    private boolean isWinnerFound() {
        if(isWinCombination(matrix[0][0], matrix[1][1], matrix[2][2])) return true;
        if(isWinCombination(matrix[0][2], matrix[1][1], matrix[2][0])) return true;

        for(int i = 0; i < 3; i++) {
            if(isWinCombination(matrix[i][0], matrix[i][1], matrix[i][2])) return true;
            if(isWinCombination(matrix[0][i], matrix[1][i], matrix[2][i])) return true;
        }
        return false;
    }

    private boolean isWinCombination(int a, int b, int c) {

        if(a == Constants.X && b == Constants.X && c == Constants.X) return true;
        if(a == Constants.O && b == Constants.O && c == Constants.O) return true;
        return false;
    }


    private boolean isMatrixFull() {
        int filledCellsCounter = 0;
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(matrix[i][j] == Constants.O || matrix[i][j] == Constants.X)
                    filledCellsCounter++;
            }
        }
        return filledCellsCounter == 9;
    }

    private void cleanMatrix() {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                matrix[i][j] = Constants.NONE;
        }
    }

    public AlertDialog createDialog(final String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Winner is: " + winner);
        builder.setPositiveButton("Play again", (dialog, id) -> {
            cleanMatrix();
            updateUI();
            switchTurn(winner);
            Toast.makeText(getActivity(), "new game", Toast.LENGTH_SHORT).show();
        });
        return builder.create();
    }

}
