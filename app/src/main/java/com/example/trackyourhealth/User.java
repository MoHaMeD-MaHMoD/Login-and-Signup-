package com.example.trackyourhealth;

public class User {
    public String photoUri;
    public String name;
    public String email;
    public Boolean isVerifyed;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String photoUri,String username, String email, Boolean isVerifyed) {
        this.name = username;
        this.email = email;
        this.isVerifyed=isVerifyed;
        this.photoUri=photoUri;
    }

}
