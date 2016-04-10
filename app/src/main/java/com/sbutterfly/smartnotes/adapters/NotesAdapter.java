package com.sbutterfly.smartnotes.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.base.RecyclerViewAdapter;
import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.List;

public class NotesAdapter extends RecyclerViewAdapter<Note, NotesAdapter.ViewHolder> {

    private List<Note> notes;
    private boolean selectionMode;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView bodyTextView;
        public ImageView importanceImageView;

        public ViewHolder(View v) {
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

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Note note = notes.get(position);
        holder.populateView(note);
        holder.setSelectionMode(selectionMode);
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setSelectionMode(boolean value) {
        this.selectionMode = value;
        clearSelections();
    }
}