// Note.java
package com.example.taskmanagementapp;

public class note {
    private String title;
    private String description;

    public note() {
        // Public no-arg constructor needed for Firestore deserialization
    }

    public note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
