package com.sbutterfly.smartnotes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;

public class EditNoteActivity extends AppCompatActivity {

    private Note note = new Note();
    private EditText title;
    private EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            note.populate(bundle);
        }

        title = (EditText) findViewById(R.id.title);
        body = (EditText) findViewById(R.id.body);

        title.setText(note.getTitle());
        body.setText(note.getBody());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        switch (item.getItemId()) {
            case R.id.action_save:
                note.setTitle(title.getText().toString());
                note.setBody(body.getText().toString());
                if (note.getId() != -1) {
                    notesAccessObject.updateNote(note);
                } else {
                    notesAccessObject.addNote(note);
                }
                finish();
                return true;
            case R.id.action_delete:
                // TODO add 'Are you sure to delete?' alert
                notesAccessObject.deleteNote(note);
                // TODO you should navigate to first activity
                finish();
                return true;
            case android.R.id.home:
                // TODO add 'Are you sure to exit?' alert
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
