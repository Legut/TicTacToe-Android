package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Server_Fragment extends Fragment {
    private static String TAG = "Server_Fragment";
    private FragmentManager fragmentManager;
    private static BluetoothService mChatService = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_WRITE: break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.equals("choosingDialogQuery"))  {
                        AlertDialog dialog = createDialog();
                        dialog.show();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    if (null != activity)
                        Toast.makeText(activity, "Connected to ", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity)
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        //creating a BluetoothService here
        if(mChatService == null) {
            mChatService = new BluetoothService(getActivity(), handler);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_server, container, false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Choose your symbol: ").setTitle("The player 2 is ready");
        builder.setPositiveButton("X", (dialog, id) -> {
            sendMessage("server decided to be X");
            fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.activity_main, Game_Fragment.newInstance("X", true));
            transaction.addToBackStack(null);
            transaction.commit();
            Toast.makeText(getActivity(), "X symbol has been chosen", Toast.LENGTH_SHORT).show();

        });
        builder.setNegativeButton("O", (dialog, id) -> {
            sendMessage("server decided to be O");
            fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.activity_main, Game_Fragment.newInstance("O", true));
            transaction.addToBackStack(null);
            transaction.commit();
            Toast.makeText(getActivity(), "O symbol has been chosen", Toast.LENGTH_SHORT).show();
        });
        return builder.create();
    }

    public void sendMessage(String msg) {
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
       mChatService.write(msg.getBytes());
    }

    static public BluetoothService getBluetoothService() {
        return mChatService;
    }
}

