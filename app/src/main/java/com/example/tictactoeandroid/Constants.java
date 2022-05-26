package com.example.tictactoeandroid;

public class Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String MARK_CHOSEN = "MARK_CHOSEN";
    public static final String IS_SERVER = "IS_SERVER";
    public static final String PLAYER_NAME = "PLAYER_NAME";

    public static final int NONE = 1000;
    public static final int X = 100;
    public static final int O = 200;
}
