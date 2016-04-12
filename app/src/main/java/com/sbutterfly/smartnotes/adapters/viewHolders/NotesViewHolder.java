package com.sbutterfly.smartnotes.adapters.viewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.model.Note;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public TextView bodyTextView;
    public ImageView importanceImageView;
    private CheckBox checkBox;
    private FrameLayout checkFrameLayout;

    public NotesViewHolder(View v) {
        super(v);
        titleTextView = (TextView) v.findViewById(R.id.title);
        bodyTextView = (TextView) v.findViewById(R.id.body);
        importanceImageView = (ImageView) v.findViewById(R.id.importance);
        checkBox = (CheckBox)v.findViewById(R.id.checkbox);
        checkFrameLayout = (FrameLayout)v.findViewById(R.id.checkboxlayout);
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
        // TODO add animation
        checkFrameLayout.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        checkBox.setChecked(itemView.isActivated());
    }
}
