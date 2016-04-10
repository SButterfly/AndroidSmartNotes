package com.sbutterfly.smartnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.adapters.ItemTouchListenerAdapter;
import com.sbutterfly.smartnotes.adapters.NotesAdapter;
import com.sbutterfly.smartnotes.dal.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent viewNoteActivity = new Intent(this, EditNoteActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(viewNoteActivity);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notes_recycle_view);
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
        Note note = notes.get(position);
        Bundle noteBundle = note.toBundle();

        Intent intent = new Intent(this, ViewNoteActivity.class);
        intent.putExtras(noteBundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
        // enter selection mode
    }
}
