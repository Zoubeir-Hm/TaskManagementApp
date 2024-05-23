// Note.java
package com.example.taskmanagementapp;

public class note {
    private String id;
    private String title;
    private String description;

    public note() {
        // Public no-arg constructor needed for Firestore deserialization
    }

    public note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
