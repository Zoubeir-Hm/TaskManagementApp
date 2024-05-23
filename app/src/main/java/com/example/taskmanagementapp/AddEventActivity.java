// AddEventActivity.java
package com.example.taskmanagementapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextEventName;
    private Button buttonSelectDate, buttonSelectTime, buttonAddEvent;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Références UI
        editTextEventName = findViewById(R.id.editTextEventName);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        buttonAddEvent = findViewById(R.id.buttonAddEvent);

        buttonSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        selectedYear = year1;
                        selectedMonth = monthOfYear;
                        selectedDay = dayOfMonth;
                    }, year, month, day);
            datePickerDialog.show();
        });

        buttonSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                    (view, hourOfDay, minute1) -> {
                        selectedHour = hourOfDay;
                        selectedMinute = minute1;
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        buttonAddEvent.setOnClickListener(v -> {
            String eventName = editTextEventName.getText().toString().trim();

            if (TextUtils.isEmpty(eventName)) {
                Toast.makeText(AddEventActivity.this, "Event Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            addEventToFirestore(eventName, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        });
    }

    private void addEventToFirestore(String eventName, int year, int month, int day, int hour, int minute) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Créer un nouvel événement
            Map<String, Object> event = new HashMap<>();
            event.put("eventName", eventName);
            event.put("year", year);
            event.put("month", month);
            event.put("day", day);
            event.put("hour", hour);
            event.put("minute", minute);

            // Ajouter l'événement à la collection 'events' sous le document utilisateur
            db.collection("user").document(user.getEmail()).collection("events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                        setAlarm(eventName, year, month, day, hour, minute);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddEventActivity.this, "Error adding event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlarm(String eventName, int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);

        long alarmTime = calendar.getTimeInMillis();

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventName", eventName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }
}
