package com.example.notetakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    View mview;
    TextView textTitle, textTime;


    public NoteViewHolder(View itemView) {

        super(itemView);
        mview = itemView;

        textTitle = mview.findViewById(R.id.note_title);
        textTime = mview.findViewById(R.id.note_time);
    }

    public void setNoteTitle(String title) {
        textTitle.setText(title);
    }

    public void setNoteTime(String time) {
        textTime.setText(time);
    }
}
