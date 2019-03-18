package com.example.darkestmidnight.lykeyfoods.models;

public class User {
    String firstName;
    String lastName;
    String username;
    String email;
    int userId;

    public User(String fn, String ln, String un, String email, int uid) {
        this.firstName = fn;
        this.lastName = ln;
        this.username = un;
        this.email = email;
        this.userId = uid;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }
}
