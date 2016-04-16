package com.sbutterfly.smartnotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;

public class ViewNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private Note note;
    private TextView title;
    private TextView body;
    private ImageView importance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.view));

        note = new Note();
        if (savedInstanceState != null) {
            note.populate(savedInstanceState);
        } else {
            Bundle bundle = getIntent().getExtras();
            note.populate(bundle);
        }

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);

        importance = (ImageView) findViewById(R.id.importance);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);

        note = notesAccessObject.getNote(note.getId());
        title.setText(note.getTitle());
        body.setText(note.getBody());
        setImportanceIcon(note, importance);
        importance.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        importance.setOnClickListener(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        note.save(outState);
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
                final DatabaseHandler databaseHandler = new DatabaseHandler(this);
                final NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
                DialogInterface.OnClickListener deleteAlertDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            notesAccessObject.deleteNote(note);
                            finish();
                        }
                    }
                };
                AlertDialog deleteAlertDialog = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.deleteSingleNoteTitle))
                        .setPositiveButton(getString(R.string.yes), deleteAlertDialogClickListener)
                        .setNegativeButton(getString(R.string.no), deleteAlertDialogClickListener)
                        .show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        final DatabaseHandler databaseHandler = new DatabaseHandler(this);
        final NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);

        note.setImportance(Note.Importance.next(note.getImportance()));
        notesAccessObject.updateNote(note);
        setImportanceIcon(note, importance);
    }

    public void setImportanceIcon(Note note, ImageView imageView) {
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

        imageView.setImageResource(imageResourceId);
    }
}
