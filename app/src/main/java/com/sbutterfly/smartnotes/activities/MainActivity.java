package com.sbutterfly.smartnotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.sbutterfly.smartnotes.adapters.NotesAdapter;
import com.sbutterfly.smartnotes.adapters.interfaces.RecyclerViewOnItemClickListener;
import com.sbutterfly.smartnotes.dal.DatabaseHandler;
import com.sbutterfly.smartnotes.dal.NotesAccessObject;
import com.sbutterfly.smartnotes.dal.model.Note;
import com.sbutterfly.smartnotes.receivers.NotesChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener,
        NotesChangedBroadcastReceiver.OnNoteChangedListener, NotesAdapter.ImportanceChangedListener {

    private SelectionMode selectionMode = SelectionMode.DISABLE;

    private RecyclerView.LayoutManager layoutManager;
    private NotesAdapter adapter;
    private RecyclerView recyclerView;

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
        placeDefaultNotesToDataBaseIfNessary();

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

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        List<Note> notes = notesAccessObject.getSortedNotes();

        adapter = new NotesAdapter(notes);
        adapter.setImportanceChangedListener(this);
        adapter.setRecyclerViewOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        // HACK to disable change animation
        recyclerView.getItemAnimator().setChangeDuration(0);

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
    public void onItemClick(View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            Note note = adapter.getItem(position);
            Bundle noteBundle = note.toBundle();

            Intent intent = new Intent(this, ViewNoteActivity.class);
            intent.putExtras(noteBundle);
            startActivity(intent);
        } else {
            adapter.toggleSelection(position);
            setEnabledMenuItem(changeMenuItem, adapter.getSelectedItemsCount() == 1);
            setEnabledMenuItem(deleteMenuItem, adapter.getSelectedItemsCount() != 0);

            updateAppBarTitle();
        }
    }

    @Override
    public void onItemLongClick(View clickedView, int position) {
        if (selectionMode == SelectionMode.DISABLE) {
            enterSelectionMode();
        }
        adapter.setSelected(position);
        updateAppBarTitle();
    }

    private void enterSelectionMode() {
        selectionMode = SelectionMode.ABLE;
        fab.hide();
        changeMenuItem.setVisible(true);
        deleteMenuItem.setVisible(true);
        setEnabledMenuItem(changeMenuItem, true);
        setEnabledMenuItem(deleteMenuItem, true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorSelectedPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorSelectedPrimaryDark));
        }
    }

    private void exitSelectionMode() {
        selectionMode = SelectionMode.DISABLE;
        fab.show();
        changeMenuItem.setVisible(false);
        deleteMenuItem.setVisible(false);
        adapter.clearSelections();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
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
    public void onImportanceChanged(int position) {
        Note note = adapter.getItem(position);
        note.setImportance(Note.Importance.next(note.getImportance()));
        final DatabaseHandler databaseHandler = new DatabaseHandler(this);
        final NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);
        notesAccessObject.updateNote(note);
    }

    @Override
    public void noteUpdated(Note note) {
        for (int i = 0; i < adapter.getModelItemCount(); i++) {
            if (adapter.getItem(i).getId() == note.getId()) {
                adapter.setItem(i, note);
                layoutManager.scrollToPosition(i);
                break;
            }
        }
    }

    @Override
    public void noteAdded(Note note) {
        adapter.insertItem(0, note);
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void noteDeleted(Note note) {
        for (int i = 0; i < adapter.getModelItemCount(); i++) {
            if (adapter.getItem(i).getId() == note.getId()) {
                adapter.deleteItem(i);
                break;
            }
        }

        if (adapter.getModelItemCount() == 0 && selectionMode == SelectionMode.ABLE) {
            exitSelectionMode();
        }
        updateAppBarTitle();
    }

    private void updateAppBarTitle() {
        String title;

        if (selectionMode == SelectionMode.DISABLE) {
            title = getString(R.string.app_name);
        } else {
            String format = getString(R.string.selectedItemsCountFormat);
            title = String.format(format, adapter.getSelectedItemsCount());
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    private void setEnabledMenuItem(MenuItem menuItem, boolean enabled) {
        menuItem.setEnabled(enabled);
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            icon.setAlpha(enabled ? 255 : 64);
        }
    }

    private void placeDefaultNotesToDataBaseIfNessary() {
        final String PREFS_NAME = "NOTES_PREFERENCES";
        final String FIRST_TIME_LAUNCH_KEY = "first_time_launch";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstTimeLaunch = settings.getBoolean(FIRST_TIME_LAUNCH_KEY, true);

        if (firstTimeLaunch) {
            ArrayList<Note> notes = new ArrayList<>();

            notes.add(new Note("Hello", "Welcome to my app!", Note.Importance.NONE));
            notes.add(new Note("Simple instructions:", "1. To view note tap on it", Note.Importance.NONE));
            notes.add(new Note(null, "2. To edit note press it, than tap edit icon", Note.Importance.NONE));
            notes.add(new Note(null, "3. To delete note press it, than tap delete icon", Note.Importance.NONE));
            notes.add(new Note(null, "5. You could delete several items as well", Note.Importance.NONE));
            notes.add(new Note("6. To change importance, click on star -->", null, Note.Importance.MIDDLE));
            notes.add(new Note("Funny facts", "You didn't notice that 4 option is missed", Note.Importance.NONE));
            notes.add(new Note(null, "You check it", Note.Importance.NONE));
            notes.add(new Note(null, "You smile :)", Note.Importance.NONE));
            notes.add(new Note(null, "You smile again C:", Note.Importance.NONE));
            notes.add(new Note("Have a nice day!!", "Yo. Man, seriously. You deserved it.", Note.Importance.NONE));

            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            NotesAccessObject notesAccessObject = new NotesAccessObject(this, databaseHandler);

            for (int i = notes.size() - 1; i >= 0; i--) {
                notesAccessObject.addNote(notes.get(i));
            }

            settings.edit()
                    .putBoolean(FIRST_TIME_LAUNCH_KEY, false)
                    .apply();
        }
    }
}