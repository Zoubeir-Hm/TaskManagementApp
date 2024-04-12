package com.example.taskmanagementapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class login extends AppCompatActivity {



    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Assurez-vous que le fichier layout est nommé activity_login

        // Initialisation de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Connectez les éléments de l'interface utilisateur
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Listener pour le bouton de connexion
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                loginUser(email, password);
            }
        });

        // Listener pour le bouton d'enregistrement
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrir l'activité d'inscription
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Email or password fields cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        // Tentative de connexion avec Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Connexion réussie, mise à jour UI avec les informations de l'utilisateur connecté
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Redirection ou mise à jour de l'UI
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Intent to go to another activity or update UI
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Échec de la connexion
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /*S@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }*/
}