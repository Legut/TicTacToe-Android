package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Game_Fragment extends Fragment {
    private static final String TAG = "Game_Fragment";
    private AlertDialog dialog;
    private String myMark;
    private BluetoothService mConnectedThread = null;
    private String turn = "X";
    private final int[][] matrix = new int[3][3];
    private final TextView[][] arrayOfButtons = new TextView[3][3];


    public static Game_Fragment newInstance(String mark, boolean server) {
        Game_Fragment game = new Game_Fragment();
        Bundle bdl = new Bundle(2);
        bdl.putString(Constants.MARK_CHOSEN, mark);
        bdl.putBoolean(Constants.IS_SERVER, server);
        game.setArguments(bdl);
        return game;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constants.MESSAGE_READ) {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                if (!isMatrixFull() && !isWinnerFound() && readMessage.length() == 2) {
                    int i = readMessage.codePointAt(0) - 48;
                    int j = readMessage.codePointAt(1) - 48;
                    if (i < 3 && j < 3) {
                        putInMatrix(i, j, turn);
                        updateUI();
                        switchTurn(turn);
                    }
                }

                if (readMessage.equals("X")) {
                    dialog = createDialog("X");
                    dialog.show();
                }
                if (readMessage.equals("O")) {
                    dialog = createDialog("O");
                    dialog.show();
                }
                if (readMessage.equals(getResources().getString(R.string.no_winner_msg))) {
                    dialog = createNoWinnerDialog();
                    dialog.show();
                    switchTurn(turn);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isServer;
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        myMark = getArguments().getString(Constants.MARK_CHOSEN);
        isServer = getArguments().getBoolean(Constants.IS_SERVER);
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
        TextView status = myView.findViewById(R.id.Status);
        status.setText("playing for: " + myMark);

        initButtons(myView);

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
        arrayOfButtons[0][0] = myView.findViewById(R.id.cell11);
        arrayOfButtons[0][1] = myView.findViewById(R.id.cell12);
        arrayOfButtons[0][2] = myView.findViewById(R.id.cell13);
        arrayOfButtons[1][0] = myView.findViewById(R.id.cell21);
        arrayOfButtons[1][1] = myView.findViewById(R.id.cell22);
        arrayOfButtons[1][2] = myView.findViewById(R.id.cell23);
        arrayOfButtons[2][0] = myView.findViewById(R.id.cell31);
        arrayOfButtons[2][1] = myView.findViewById(R.id.cell32);
        arrayOfButtons[2][2] = myView.findViewById(R.id.cell33);
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
            informAboutPoints();
        }
        if(isMatrixFull()) {
            dialog = createNoWinnerDialog();
            dialog.show();
            mConnectedThread.write(getResources().getString(R.string.no_winner_msg).getBytes());
            switchTurn(turn);
        }
    }

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    @SuppressLint("NewApi")
    private void informAboutPoints() {
        db.collection("user_data").whereEqualTo("uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int userPoints = Integer.parseInt(String.valueOf(document.getData().get("points"))) + 10;
                            Map<String, Object> data = document.getData();
                            data.replace("points", userPoints);
                            Toast.makeText(getActivity(), data.get("nickname") + " Your points: " + userPoints, Toast.LENGTH_SHORT).show();
                            db.collection("user_data").document(document.getId()).set(data)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "POINTS DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "POINTS Error writing document", e));
                        }
                    } else {
                        Log.w(TAG, "Error getting document", task.getException());
                    }
                });
    }

    private void switchTurn(String currentTurn) {
        if(currentTurn.equals("X")) turn = "O";
        if(currentTurn.equals("O")) turn = "X";
    }

    private void updateUI() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                switch (matrix[i][j]) {
                    case Constants.X: arrayOfButtons[i][j].setText("X"); break;
                    case Constants.O: arrayOfButtons[i][j].setText("O"); break;
                    case Constants.NONE: arrayOfButtons[i][j].setText(""); break;
                }
            }
        }
    }

    public void putInMatrix(int i, int j, String currentMark) {
        if(matrix[i][j] != Constants.X && matrix[i][j] != Constants.O) {
            switch (currentMark) {
                case "X": matrix[i][j] = Constants.X; break;
                case "O": matrix[i][j] = Constants.O; break;
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

    private String getOpposite(String mark) {
        if (mark.equals("O")) return "X";
        else if (mark.equals("X")) return "O";
        else return "Error";
    }

    private AlertDialog createDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Winner is: " + getOpposite(winner));
        builder.setPositiveButton("Play again", (dialog, id) -> {
            cleanMatrix();
            updateUI();
            turn = getOpposite(winner);
            Toast.makeText(getActivity(), "new game", Toast.LENGTH_SHORT).show();
        });
        return builder.create();
    }

    private AlertDialog createNoWinnerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("There is no winner");
        builder.setPositiveButton("Play again", (dialog, id) -> {
            cleanMatrix();
            updateUI();
            Toast.makeText(getActivity(), "new game", Toast.LENGTH_SHORT).show();
        });
        return builder.create();
    }
}
