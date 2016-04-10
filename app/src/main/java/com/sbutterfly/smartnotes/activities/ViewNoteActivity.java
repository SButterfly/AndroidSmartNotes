package com.sbutterfly.smartnotes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sbutterfly.smartnotes.R;
import com.sbutterfly.smartnotes.dal.model.Note;

public class ViewNoteActivity extends AppCompatActivity {

    private Note note = new Note();
    private TextView title;
    private TextView body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Bundle bundle = getIntent().getExtras();
        note.populate(bundle);

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);

        title.setText(note.getTitle());
        body.setText(note.getBody());
    }
}
