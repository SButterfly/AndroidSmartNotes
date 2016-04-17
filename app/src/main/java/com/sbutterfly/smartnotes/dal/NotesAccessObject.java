package com.sbutterfly.smartnotes.dal;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesAccessObject {

    private static final String TAG = "NotesAccessObject";

    public static abstract class DBContract implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_IMPORTANCE = "importance";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BODY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMPORTANCE + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");";
    }

    public static abstract class BroadcastContract {

        public static final String ACTION = "Notes_db_updated";

        public static final String TYPE_KEY = "Note_db_update_key";
        public static final String TYPE_ADDED = "Note_added";
        public static final String TYPE_DELETED = "Note_deleted";
        public static final String TYPE_UPDATED = "Note_updated";

        public static final String NOTE_KEY = "NOTE_KEY";
    }

    private final Context context;
    private final DatabaseHandler databaseHandler;

    public NotesAccessObject(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    public void addNotes(Collection<Note> notes) {
        for (Note note : notes) {
            addNote(note);
        }
    }

    public void addNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DBContract.COLUMN_NAME_TITLE, note.getTitle());
        values.put(DBContract.COLUMN_NAME_BODY, note.getBody());
        values.put(DBContract.COLUMN_NAME_IMPORTANCE, note.getImportance());

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        long id = db.insertOrThrow(DBContract.TABLE_NAME, null, values);
        db.close();
        note.setId((int)id);

        Log.d(TAG, "Added note: " + note);
        Intent intent = new Intent(BroadcastContract.ACTION)
                .putExtra(BroadcastContract.TYPE_KEY, BroadcastContract.TYPE_ADDED)
                .putExtra(BroadcastContract.NOTE_KEY, note.toBundle());

        context.sendBroadcast(intent);
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DBContract.COLUMN_NAME_TITLE, note.getTitle());
        values.put(DBContract.COLUMN_NAME_BODY, note.getBody());
        values.put(DBContract.COLUMN_NAME_IMPORTANCE, note.getImportance());

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        long id = db.update(DBContract.TABLE_NAME, values, DBContract._ID + "=?",
                new String[] { String.valueOf(note.getId())});

        db.close();

        Log.d(TAG, "Updated note: " + note);
        Intent intent = new Intent(BroadcastContract.ACTION)
                .putExtra(BroadcastContract.TYPE_KEY, BroadcastContract.TYPE_UPDATED)
                .putExtra(BroadcastContract.NOTE_KEY, note.toBundle());

        context.sendBroadcast(intent);
    }

    public Note getNote(int id) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(DBContract.TABLE_NAME,
                new String[] { DBContract._ID, DBContract.COLUMN_NAME_TITLE, DBContract.COLUMN_NAME_BODY, DBContract.COLUMN_NAME_IMPORTANCE, DBContract.COLUMN_NAME_TIMESTAMP },
                DBContract._ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Note note = parseNote(cursor);
            Log.d(TAG, "Got note: " + note);
            return note;
        }
        throw new IllegalArgumentException("Can't find note with id: " + id);
    }

    public List<Note> getSortedNotes() {
        List<Note> notes = getNotes();
        // TODO change comparator to timestamp
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                return rhs.getId() - lhs.getId();
            }
        });

        return notes;
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBContract.TABLE_NAME;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = parseNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        Log.d(TAG, "Got notes: " + notes);
        return notes;
    }

    private Note parseNote(Cursor cursor) {
        int note_id = cursor.getInt(0);
        String title = cursor.getString(1);
        String body = cursor.getString(2);
        int importance = cursor.getInt(3);
        return new Note(note_id, title, body, importance);
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(DBContract.TABLE_NAME, DBContract._ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();

        Log.d(TAG, "Deleted note: " + note);

        Intent intent = new Intent(BroadcastContract.ACTION)
                .putExtra(BroadcastContract.TYPE_KEY, BroadcastContract.TYPE_DELETED)
                .putExtra(BroadcastContract.NOTE_KEY, note.toBundle());

        context.sendBroadcast(intent);
    }
}
