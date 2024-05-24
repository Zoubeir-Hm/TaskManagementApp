package com.example.taskmanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementapp.Event;
import com.example.taskmanagementapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private FirebaseFirestore db;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getDay() + "/" + (event.getMonth() + 1) + "/" + event.getYear());
        holder.eventTimeTextView.setText(String.format("%02d:%02d", event.getHour(), event.getMinute()));

        holder.deleteIcon.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                CollectionReference eventsRef = db.collection("user").document(userEmail).collection("events");

                eventsRef.whereEqualTo("eventName", event.getEventName())
                        .whereEqualTo("year", event.getYear())
                        .whereEqualTo("month", event.getMonth())
                        .whereEqualTo("day", event.getDay())
                        .whereEqualTo("hour", event.getHour())
                        .whereEqualTo("minute", event.getMinute())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    eventsRef.document(document.getId()).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                eventList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, eventList.size());
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle the error
                                            });
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setEvents(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDateTextView;
        TextView eventTimeTextView;
        View deleteIcon;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventName);
            eventDateTextView = itemView.findViewById(R.id.eventDate);
            eventTimeTextView = itemView.findViewById(R.id.eventTime);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
