package com.sbutterfly.smartnotes.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;

public class NotesChangedBroadcastReceiver extends BroadcastReceiver {

    private OnNoteChangedListener listener;

    public interface OnNoteChangedListener {
        void noteUpdated(Note note);
        void noteAdded(Note note);
        void noteDeleted(Note note);
    }

    public IntentFilter getIntentFilter() {
        return new IntentFilter(NotesAccessObject.BroadcastContract.ACTION);
    }

    public void setOnNoteChangedListener(OnNoteChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            String receive_type = intent.getStringExtra(NotesAccessObject.BroadcastContract.TYPE_KEY);
            Bundle note_bundle = intent.getBundleExtra(NotesAccessObject.BroadcastContract.NOTE_KEY);

            Note note = new Note();
            note.populate(note_bundle);

            switch (receive_type){
                case NotesAccessObject.BroadcastContract.TYPE_ADDED:
                    listener.noteAdded(note);
                    return;
                case NotesAccessObject.BroadcastContract.TYPE_DELETED:
                    listener.noteDeleted(note);
                    return;
                case NotesAccessObject.BroadcastContract.TYPE_UPDATED:
                    listener.noteUpdated(note);
                    return;
                default:
                    throw new RuntimeException("Don't know type: " + receive_type);
            }
        }
    }
}
