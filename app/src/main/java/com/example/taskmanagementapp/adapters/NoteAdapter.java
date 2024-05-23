// NoteAdapter.java
package com.example.taskmanagementapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementapp.R;
import com.example.taskmanagementapp.note;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<note> notes;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;

    public NoteAdapter(List<note> notes, Context context) {
        this.notes = notes;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        note note = notes.get(position);
        holder.textViewTitle.setText(note.getTitle());
        holder.textViewDescription.setText(note.getDescription());

        holder.deleteIcon.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                db.collection("user").document(user.getEmail()).collection("notes").document(note.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            notes.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, notes.size());
                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error deleting note", Toast.LENGTH_SHORT).show();
                            Log.e("NoteAdapter", "Error deleting note", e);
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDescription;
        public FloatingActionButton deleteIcon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
