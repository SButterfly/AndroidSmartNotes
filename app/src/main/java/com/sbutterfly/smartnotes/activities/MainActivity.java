package com.sbutterfly.smartnotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import com.sbutterfly.smartnotes.receivers.NotesChangedBroadcastReceiver;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener,
        NotesChangedBroadcastReceiver.OnNoteChangedListener {

    private SelectionMode selectionMode = SelectionMode.DISABLE;
    private NotesAdapter adapter;

    private MenuItem changeMenuItem;
    private MenuItem deleteMenuItem;

    private FloatingActionButton fab;
    private NotesChangedBroadcastReceiver receiver;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notes_recycle_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new ItemTouchListenerAdapter(recyclerView, this));

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        List<Note> notes = notesAccessObject.getNotes();
        adapter = new NotesAdapter(notes);
        recyclerView.setAdapter(adapter);

        receiver = new NotesChangedBroadcastReceiver();
        receiver.setOnNoteChangedListener(this);
        registerReceiver(receiver, receiver.getIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver.setOnNoteChangedListener(null);
        receiver = null;
    }

    @Override
    public void onItemClick(RecyclerView parent, View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            Note note = adapter.getItem(position);
            Bundle noteBundle = note.toBundle();

            Intent intent = new Intent(this, ViewNoteActivity.class);
            intent.putExtras(noteBundle);
            startActivity(intent);
        } else {
            NotesAdapter adapter = (NotesAdapter) parent.getAdapter();
            adapter.toggleSelection(position);
            changeMenuItem.setEnabled(adapter.getSelectedItemsCount() == 1);
            deleteMenuItem.setEnabled(adapter.getSelectedItemsCount() != 0);
            updateAppBarTitle();
        }
    }

    @Override
    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            enterSelectionMode();
        }

        NotesAdapter adapter = (NotesAdapter) parent.getAdapter();
        adapter.setSelected(position);
        updateAppBarTitle();
    }

    private void enterSelectionMode() {
        selectionMode = SelectionMode.ABLE;
        fab.hide();
        adapter.setInSelectionMode(true);
        changeMenuItem.setVisible(true);
        deleteMenuItem.setVisible(true);
        changeMenuItem.setEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void exitSelectionMode() {
        selectionMode = SelectionMode.DISABLE;
        fab.show();
        adapter.setInSelectionMode(false);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (selectionMode == SelectionMode.ABLE) {
                exitSelectionMode();
                updateAppBarTitle();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change:
                Note note = adapter.getSelectedItems().get(0);

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
                            List<Note> selectedItems = adapter.getSelectedItems();
                            for (Note selectedNote : selectedItems) {
                                notesAccessObject.deleteNote(selectedNote);
                            }
                        }
                    }
                };
                AlertDialog deleteAlertDialog = new AlertDialog.Builder(this)
                        .setMessage(getString(adapter.getSelectedItemsCount() == 1 ? R.string.deleteSingleNoteTitle : R.string.deleteMultiNoteTitle))
                        .setPositiveButton(getString(R.string.yes), deleteAlertDialogClickListener)
                        .setNegativeButton(getString(R.string.no), deleteAlertDialogClickListener)
                        .show();
                return true;
            case android.R.id.home:
                if (selectionMode == SelectionMode.ABLE) {
                    exitSelectionMode();
                    updateAppBarTitle();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void noteUpdated(Note note) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).getId() == note.getId()) {
                adapter.setItem(i, note);
                break;
            }
        }
    }

    @Override
    public void noteAdded(Note note) {
        adapter.insertItem(0, note);
    }

    @Override
    public void noteDeleted(Note note) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).getId() == note.getId()) {
                adapter.deleteItem(i);
                break;
            }
        }

        if (adapter.getItemCount() == 0 && selectionMode == SelectionMode.ABLE) {
            exitSelectionMode();
        }
        updateAppBarTitle();
    }

    private void updateAppBarTitle() {
        String title = selectionMode == SelectionMode.DISABLE || adapter.getSelectedItemsCount() == 0 ? getString(R.string.app_name) : String.valueOf(adapter.getSelectedItemsCount());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }
}