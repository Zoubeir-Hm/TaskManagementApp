// NotesActivity.java
package com.example.taskmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementapp.adapters.NoteAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private NoteAdapter notesAdapter;
    private List<note> noteList;

    FloatingActionButton fab;
    private BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Configure Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notes");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewNotes);
        bottomMenu = findViewById(R.id.bottomMenu);
        fab = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        notesAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(notesAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        bottomMenu.setOnNavigationItemSelectedListener(item -> {
            int item_id = item.getItemId();
            if (item_id == R.id.notes) {
                startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                return true;
            } else if (item_id == R.id.tasks) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (item_id == R.id.event) {
                startActivity(new Intent(getApplicationContext(), EventActivity.class));
                return true;
            } else {
                startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                return true;
            }
        });

        loadNotes();
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });
    }

    private void loadNotes() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("user").document(user.getEmail()).collection("notes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                note Note = document.toObject(note.class);
                                Note.setId(document.getId());
                                noteList.add(Note);
                            }
                            notesAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("NotesActivity", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
