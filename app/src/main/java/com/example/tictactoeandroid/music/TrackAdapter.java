package com.example.tictactoeandroid.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tictactoeandroid.R;

public class TrackAdapter extends BaseAdapter {
    Context context;
    TrackModel[] tracks;

    TrackAdapter(Context context, TrackModel[] tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public int getCount() {
        return tracks.length;
    }

    @Override
    public Object getItem(int i) {
        return tracks[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TrackModel track = (TrackModel) getItem(i);
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.track_item, viewGroup, false);
            holder = new ViewHolder();
            holder.titleText = view.findViewById(R.id.track_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.titleText.setText(track.getName());
        return view;
    }

    static class ViewHolder {
        TextView titleText;
    }
}
