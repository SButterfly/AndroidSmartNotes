package com.sbutterfly.smartnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;
import com.sbutterfly.smartnotes.receivers.NotesChangedBroadcastReceiver;

public class ViewNoteActivity extends AppCompatActivity implements NotesChangedBroadcastReceiver.OnNoteChangedListener {

    private Note note = new Note();
    private TextView title;
    private TextView body;
    private NotesChangedBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        note.populate(bundle);

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);

        noteUpdated(note);

        receiver = new NotesChangedBroadcastReceiver();
        receiver.setOnNoteChangedListener(this);
        registerReceiver(receiver, receiver.getIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change:
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtras(note.toBundle());
                startActivity(intent);
                return true;
            case R.id.action_delete:
                // TODO add 'Are you sure to delete?' alert
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
                notesAccessObject.deleteNote(note);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void noteUpdated(Note note) {
        if (note.getId() == this.note.getId()) {
            this.note = note;
            title.setText(note.getTitle());
            body.setText(note.getBody());
        }
    }

    @Override
    public void noteAdded(Note note) {
        // skip
    }

    @Override
    public void noteDeleted(Note note) {
        // skip
    }
}
