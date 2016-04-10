package com.sbutterfly.smartnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.ItemTouchListenerAdapter;
import com.sbutterfly.smartnotes.adapters.NotesAdapter;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    private List<Note> notes;

    private SelectionMode selectionMode = SelectionMode.DISABLE;

    private MenuItem changeMenuItem;
    private MenuItem deleteMenuItem;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    private enum SelectionMode {
        DISABLE,
        ABLE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent viewNoteActivity = new Intent(this, EditNoteActivity.class);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(viewNoteActivity);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.notes_recycle_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new ItemTouchListenerAdapter(recyclerView, this));

        Note note1 = new Note(0, "Mama", "Hi", Note.Importance.NONE);
        Note note2 = new Note(1, "Papa", "Hi", Note.Importance.NONE);
        Note note3 = new Note(2, "Gradma and all my family to test long title sadasdfasdfasdfasdfad fadsf asdf", "Hi", Note.Importance.NONE);
        Note note4 = new Note(3, "Gradpa", "Hiasdfa sdf asdf asdf asdf asdf adsga sdfa sdfasdf asdf asdf adsf asdf asdf asdf ", Note.Importance.NONE);
        Note note5 = new Note(4, "Mea dfa df asdfasdf adfgva dfasdf adf adf aed adsfvaw evcadcvawefd adfva df gads", "Hiadf asdf asdf asdf asdf asdf asdf asdf asdf asdfa dsf asdfa sdf asdf asdf asdf", Note.Importance.NONE);

        notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        notes.add(note5);

        NotesAdapter notesAdapter = new NotesAdapter(notes);
        recyclerView.setAdapter(notesAdapter);
    }

    @Override
    public void onItemClick(RecyclerView parent, View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            Note note = notes.get(position);
            Bundle noteBundle = note.toBundle();

            Intent intent = new Intent(this, ViewNoteActivity.class);
            intent.putExtras(noteBundle);
            startActivity(intent);
        } else {
            NotesAdapter adapter = (NotesAdapter)parent.getAdapter();
            adapter.toggleSelection(position);
            changeMenuItem.setEnabled(adapter.getSelectedItemCount() == 1);
        }
    }

    @Override
    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            enterSelectionMode();
        }

        NotesAdapter adapter = (NotesAdapter)parent.getAdapter();
        adapter.setSelected(position);
    }

    private void enterSelectionMode() {
        selectionMode = SelectionMode.ABLE;
        fab.hide();
        NotesAdapter adapter = (NotesAdapter)recyclerView.getAdapter();
        adapter.setSelectionMode(true);
        changeMenuItem.setVisible(true);
        deleteMenuItem.setVisible(true);
        changeMenuItem.setEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void exitSelectionMode() {
        selectionMode = SelectionMode.DISABLE;
        fab.show();
        NotesAdapter adapter = (NotesAdapter)recyclerView.getAdapter();
        adapter.setSelectionMode(false);
        changeMenuItem.setVisible(false);
        deleteMenuItem.setVisible(false);
        changeMenuItem.setEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_edit_mode_menu, menu);
        changeMenuItem = menu.findItem(R.id.action_change);
        deleteMenuItem = menu.findItem(R.id.action_delete);
        changeMenuItem.setVisible(false);
        deleteMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NotesAdapter notesAdapter = (NotesAdapter) recyclerView.getAdapter();
        switch (item.getItemId()) {
            case R.id.action_change:
                Note note = notesAdapter.getSelectedItems().get(0);

                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtras(note.toBundle());
                startActivity(intent);
                return true;
            case R.id.action_delete:
                // TODO add 'Are you sure to delete?' alert
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                NotesAccessObject notesAccessObject = new NotesAccessObject(databaseHandler);

                List<Note> selectedItems = notesAdapter.getSelectedItems();
                for (Note selectedNote : selectedItems) {
                    notesAccessObject.deleteNote(selectedNote);
                }
                return true;
            case android.R.id.home:
                if (selectionMode == SelectionMode.ABLE) {
                    exitSelectionMode();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
