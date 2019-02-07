package com.example.sid.meetup;

public class InstantMessage {

    private String message;
    private String author;

    public InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }
// requirement by firebase - no argument constructor and getters
    public InstantMessage() {
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
