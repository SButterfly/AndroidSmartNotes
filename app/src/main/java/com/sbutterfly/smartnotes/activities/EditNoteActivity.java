package com.sbutterfly.smartnotes.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;

public class EditNoteActivity extends AppCompatActivity {

    private Note note;
    private EditText title;
    private EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = new Note();
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                note.populate(bundle);
            }
        } else {
            note.populate(savedInstanceState);
        }

        title = (EditText) findViewById(R.id.title);
        body = (EditText) findViewById(R.id.body);

        title.setText(note.getTitle());
        title.setSelection(title.length());
        body.setText(note.getBody());

        // Show key board
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        note.save(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        title = null;
        body = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_note_menu, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                deleteItem();
                return true;
            case android.R.id.home:
                exit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveItem() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        note.setTitle(title.getText().toString());
        note.setBody(body.getText().toString());
        if (note.getId() != Note.INVALID_ID) {
            notesAccessObject.updateNote(note);
        } else {
            notesAccessObject.addNote(note);
        }
        finish();
    }

    private void deleteItem() {
        final DatabaseHandler databaseHandler = new DatabaseHandler(this);
        final NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        DialogInterface.OnClickListener deleteAlertDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (note.getId() != Note.INVALID_ID) {
                        notesAccessObject.deleteNote(note);
                    }
                    // TODO you should navigate to first activity
                    finish();
                }
            }
        };
        AlertDialog deleteAlertDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.deleteSingleNoteTitle))
                .setPositiveButton(getString(R.string.yes), deleteAlertDialogClickListener)
                .setNegativeButton(getString(R.string.no), deleteAlertDialogClickListener)
                .show();
    }

    private void exit() {
        String newTitle = title.getText().toString();
        String newBody = body.getText().toString();

        if (!newTitle.equals(note.getTitle()) || !newBody.equals(note.getBody())) {

            DialogInterface.OnClickListener alertDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        dialog.dismiss();
                        finish();
                    }
                }
            };
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.exitAlertDialogMessage))
                    .setPositiveButton(getString(R.string.yes), alertDialogClickListener)
                    .setNegativeButton(getString(R.string.no), alertDialogClickListener)
                    .show();
        } else {
            finish();
        }
    }
}
