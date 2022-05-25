package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Set;

public class Client_Fragment extends Fragment {
    private final String TAG = "Client_Fragment";
    private Button btn_start, btn_device, btn_ready;
    private BluetoothAdapter mBluetoothAdapter =null;
    private BluetoothDevice device;
    private static BluetoothService mChatService = null;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    //when server have the symbol chosen
                    if(readMessage.equals("server decided to be X"))  {
                        transaction.replace(R.id.activity_main, Game_Fragment.newInstance("O", false));
                    } else if(readMessage.equals("server decided to be O"))  {
                        transaction.replace(R.id.activity_main, Game_Fragment.newInstance("X", false));
                    }
                    transaction.addToBackStack(null);
                    transaction.commit();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_client, container, false);
        btn_device = myView.findViewById(R.id.which_device);
        btn_device.setOnClickListener(v -> querypaired());
        btn_start = myView.findViewById(R.id.start_client);
        btn_start.setEnabled(false);
        btn_start.setOnClickListener(v -> { startClient(); });
        btn_ready = myView.findViewById(R.id.ready_game_client);
        btn_ready.setEnabled(false);
        btn_ready.setOnClickListener(v -> sendMessage("choosingDialogQuery"));

        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            btn_start.setEnabled(false);
            btn_device.setEnabled(false);
        }
        Log.v(TAG, "onCreateView");
        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();
        //creating a BluetoothService here
        if(mChatService == null) {
            mChatService = new BluetoothService(getActivity(), handler);
        }
        querypaired();
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            final BluetoothDevice[] blueDev = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i =0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose Bluetooth:");
            builder.setSingleChoiceItems(items, -1, (dialog, item) -> {
                dialog.dismiss();
                if (item >= 0 && item <blueDev.length) {
                    device = blueDev[item];
                    btn_device.setText("device: " + blueDev[item].getName());
                    btn_start.setEnabled(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void startClient() {
        if (device != null) {
            Log.v(TAG, "connecting with: " + device);
            mChatService.connect(device);
            btn_ready.setEnabled(true);
        } else
            Log.v(TAG, "device is null");
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