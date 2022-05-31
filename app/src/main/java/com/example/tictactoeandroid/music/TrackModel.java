package com.example.tictactoeandroid.music;

public class TrackModel {
    int id;
    String name;
    boolean isPlaying;

    public TrackModel(int id, String name, boolean isPlaying) {
        this.id = id;
        this.name = name;
        this.isPlaying = isPlaying;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
