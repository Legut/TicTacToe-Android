package com.example.tictactoeandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Set;

public class Start_Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private BluetoothAdapter mBluetoothAdapter = null;

    public interface OnFragmentInteractionListener {
        void onButtonSelected(int id);
    }

    public void startbt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(),"This device does not support bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getActivity(),"There is bluetooth, but turned off.", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityIntent.launch(enableBtIntent);
        } else {
            Toast.makeText(getActivity(),"The bluetooth is ready to use.", Toast.LENGTH_SHORT).show();
            querypaired();
        }
    }

    @SuppressLint("MissingPermission")
    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            final BluetoothDevice[] blueDev = new BluetoothDevice[pairedDevices.size()];
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                i++;
            }

        } else {
            Toast.makeText(getActivity(),"There are no paired devices.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_start, container, false);
        Button btn_client = myView.findViewById(R.id.button2);
        btn_client.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onButtonSelected(2);
        });
        Button btn_server = myView.findViewById(R.id.button1);
        btn_server.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onButtonSelected(1);
        });

        startbt();
        return myView;
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    querypaired();
                } else {
                    Toast.makeText(getActivity(),"Please turn the bluetooth on.", Toast.LENGTH_SHORT).show();
                }
    });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
