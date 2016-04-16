package com.sbutterfly.smartnotes.adapters.viewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.model.Note;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public TextView bodyTextView;
    public ImageView importanceImageView;

    public NotesViewHolder(View v) {
        super(v);
        titleTextView = (TextView) v.findViewById(R.id.title);
        bodyTextView = (TextView) v.findViewById(R.id.body);
        importanceImageView = (ImageView) v.findViewById(R.id.importance);
    }

    public void populateView(Note note) {
        titleTextView.setText(note.getTitle());
        bodyTextView.setText(note.getBody());
        // TODO set importance icon
    }

    public void setSelectionMode(boolean selectionMode) {
        if (selectionMode && itemView.isActivated()) {
            itemView.setBackgroundColor(Color.GRAY);
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
