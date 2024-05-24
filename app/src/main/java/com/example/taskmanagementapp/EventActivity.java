package com.example.taskmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementapp.adapters.EventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String userEmail = "user@example.com"; // Replace with the current user's email

    FloatingActionButton fab;
    private BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Events");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventAdapter = new EventAdapter(new ArrayList<>());
        recyclerView.setAdapter(eventAdapter);

        bottomMenu = findViewById(R.id.bottomMenu); // Initialize bottomMenu before using it
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

        fab = findViewById(R.id.fab);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            loadEvents(userEmail);
        } else {
            // Redirect the user to the login screen
        }

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(EventActivity.this, AddEventActivity.class);
            startActivity(intent);
        });
    }

    private void loadEvents(String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("user").document(userEmail).collection("events");

        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Event> eventList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Event event = document.toObject(Event.class);
                        eventList.add(event);
                    }
                    // Pass this list to your RecyclerView adapter
                    eventAdapter.setEvents(eventList);
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
