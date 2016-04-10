package com.sbutterfly.smartnotes.dal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAccessObject {

    public static abstract class NoteContract implements BaseColumns {
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

    private final DatabaseHandler databaseHandler;

    public NotesAccessObject(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public void addOrUpdateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteContract.COLUMN_NAME_TITLE, note.getTitle());
        values.put(NoteContract.COLUMN_NAME_BODY, note.getBody());
        values.put(NoteContract.COLUMN_NAME_IMPORTANCE, note.getImportance());

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        long id = db.insertOrThrow(NoteContract.TABLE_NAME, null, values);
        db.close();
        note.setId((int)id);
    }

    public Note getNote(int id) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(NoteContract.TABLE_NAME,
                new String[] { NoteContract._ID, NoteContract.COLUMN_NAME_TITLE, NoteContract.COLUMN_NAME_BODY, NoteContract.COLUMN_NAME_IMPORTANCE, NoteContract.COLUMN_NAME_TIMESTAMP },
                NoteContract._ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Note note = parseNote(cursor);
            return note;
        }
        throw new IllegalArgumentException("Can't find note with id: " + id);
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + NoteContract.TABLE_NAME;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = parseNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
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
        deleteNote(note.getId());
    }

    private void deleteNote(int id) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(NoteContract.TABLE_NAME, NoteContract._ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}
