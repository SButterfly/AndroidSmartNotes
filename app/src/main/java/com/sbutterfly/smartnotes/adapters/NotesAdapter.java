package com.sbutterfly.smartnotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.base.RecyclerViewAdapter;
import com.sbutterfly.smartnotes.adapters.interfaces.RecyclerViewOnItemClickListener;
import com.sbutterfly.smartnotes.adapters.viewHolders.NotesViewHolder;
import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.Collection;

public class NotesAdapter extends RecyclerViewAdapter<Note, NotesViewHolder> implements ViewHolderClickListener {

    public interface ImportanceChangedListener {
        void onImportanceChanged(int position);
    }

    private ImportanceChangedListener importanceChangedListener;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public NotesAdapter(Collection<Note> notes) {
        super(notes, R.layout.note_list_footer);
    }
    @Override
    public NotesViewHolder onCreateModelViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        return new NotesViewHolder(v, this);
    }

    @Override
    public void onBindModelViewHolder(NotesViewHolder holder, int position) {
        super.onBindModelViewHolder(holder, position);
        Note note = getItem(position);
        holder.populateView(note);
    }

    public void setImportanceChangedListener(ImportanceChangedListener importanceChangedListener) {
        this.importanceChangedListener = importanceChangedListener;
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public boolean onLongClick(RecyclerView.ViewHolder viewHolder, View view) {
        if (recyclerViewOnItemClickListener != null) {
            int position = viewHolder.getAdapterPosition();
            recyclerViewOnItemClickListener.onItemLongClick(view, position);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, View view) {
        if (view.getId() == R.id.importance) {
            if (importanceChangedListener != null) {
                int position = viewHolder.getAdapterPosition();
                importanceChangedListener.onImportanceChanged(position);
            }
        } else {
            if (recyclerViewOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                recyclerViewOnItemClickListener.onItemClick(view, position);
            }
        }
    }
}