package com.sbutterfly.smartnotes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.base.RecyclerViewAdapter;
import com.sbutterfly.smartnotes.adapters.viewHolders.NotesViewHolder;
import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.Collection;

public class NotesAdapter extends RecyclerViewAdapter<Note, NotesViewHolder> {

    private boolean inSelectionMode;

    public NotesAdapter(Collection<Note> notes) {
        super(notes);
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        return new NotesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Note note = getItem(position);
        holder.populateView(note);
        holder.setSelectionMode(inSelectionMode);
    }

    public void setInSelectionMode(boolean value) {
        this.inSelectionMode = value;
        if (getSelectedItemsCount() != 0) {
            clearSelections();
        } else {
            notifyDataSetChanged();
        }
    }
}