package com.example.mytaskmate;

public class User {
    public String name;
    public String email;
    public String role;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}