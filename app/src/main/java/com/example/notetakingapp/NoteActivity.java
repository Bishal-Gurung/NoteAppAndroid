package com.example.notetakingapp;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etTitle, etContent;
    private Toolbar mToolbar;

    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;

    private Menu mainMenu;
    private String noteId = "no";

    private boolean isExist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        btnCreate = findViewById(R.id.new_note_btnCreate);
        etTitle = findViewById(R.id.new_note_title);
        etContent = findViewById(R.id.new_note_content);
        mToolbar = findViewById(R.id.new_note_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                } else {

                    Snackbar.make(v, "fill empty fields", Snackbar.LENGTH_SHORT).show();


                }
            }
        });

        try {
            noteId = getIntent().getStringExtra("noteId");
            if (noteId.equals("no")) {
                mainMenu.getItem(0).setVisible(false);
                isExist=false;
            }else {
                isExist=true;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        putData();
    }

    private void putData() {

        if (isExist) {
            fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void createNote(String title, String content) {


        if (fAuth.getCurrentUser() != null) {

            if (isExist) {
                //update notes
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);
                fNotesDatabase.child(noteId).updateChildren(updateMap);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();

            } else {
                //create notes
                final DatabaseReference newNoteRef = fNotesDatabase.push();
                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);
                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {

                                    Toast.makeText(NoteActivity.this, "Note added to Database", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NoteActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                mainThread.start();
            }


        } else {
            Toast.makeText(this, "user not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_note_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.new_note_delete_btn:


                if (!noteId.equals("no")) {
                    deleteNote();

                }
                break;

        }
        return true;
    }

    private void deleteNote() {

        fNotesDatabase.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NoteActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                    noteId = "no";
                    finish();
                } else {

                    Log.e("NoteActivity", task.getException().toString());
                    Toast.makeText(NoteActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


