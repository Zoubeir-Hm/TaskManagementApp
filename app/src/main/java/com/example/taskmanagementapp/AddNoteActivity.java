package com.example.taskmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextTitle, editTextDescription;
    private Button buttonAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Références UI
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAddNote = findViewById(R.id.buttonAddNote);

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(AddNoteActivity.this, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                addNoteToFirestore(title, description);
            }
        });
    }

    private void addNoteToFirestore(String title, String description) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Créer une nouvelle note
            Map<String, Object> note = new HashMap<>();
            note.put("title", title);
            note.put("description", description);

            // Ajouter la note à la collection 'notes' sous le document utilisateur
            db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("notes")
                    .add(note)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddNoteActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();

                            // Rediriger vers NotesActivity après succès
                            Intent intent = new Intent(AddNoteActivity.this, NotesActivity.class);
                            startActivity(intent);
                            finish(); // Terminer cette activité pour que l'utilisateur ne puisse pas revenir en arrière
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNoteActivity.this, "Error adding note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AddNoteActivity", "Error adding note", e);
                        }
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
