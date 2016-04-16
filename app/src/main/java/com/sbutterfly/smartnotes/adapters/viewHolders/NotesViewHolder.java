package com.sbutterfly.smartnotes.adapters.viewHolders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.ViewHolderClickListener;
import com.sbutterfly.smartnotes.dal.model.Note;

public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView titleTextView;
    public TextView bodyTextView;
    public ImageView importanceImageView;

    private final ViewHolderClickListener viewHolderClickListener;

    public NotesViewHolder(View v, @NonNull ViewHolderClickListener viewHolderClickListener) {
        super(v);
        this.viewHolderClickListener = viewHolderClickListener;
        titleTextView = (TextView) v.findViewById(R.id.title);
        bodyTextView = (TextView) v.findViewById(R.id.body);
        importanceImageView = (ImageView) v.findViewById(R.id.importance);
        importanceImageView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void populateView(Note note) {
        titleTextView.setText(note.getTitle());
        bodyTextView.setText(note.getBody());

        int imageResourceId;

        switch (note.getImportance()) {
            case Note.Importance.HIGH:
                imageResourceId = R.drawable.ic_star_red;
                break;
            case Note.Importance.MIDDLE:
                imageResourceId = R.drawable.ic_star_yellow;
                break;
            case Note.Importance.LOW:
                imageResourceId = R.drawable.ic_star_green;
                break;
            case Note.Importance.NONE:
            default:
                imageResourceId = R.drawable.ic_star_none;
                break;
        }

        importanceImageView.setImageResource(imageResourceId);
    }

    public void setSelectionMode(boolean selectionMode) {
        if (selectionMode && itemView.isActivated()) {
            itemView.setBackgroundColor(Color.GRAY);
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View v) {
        viewHolderClickListener.onClick(this, v);
    }

    @Override
    public boolean onLongClick(View v) {
        return viewHolderClickListener.onLongClick(this, v);
    }
}
