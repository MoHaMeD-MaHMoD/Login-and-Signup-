package com.example.trackyourhealth.LogInAndSignUp;

import com.example.trackyourhealth.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Helper {
    DatabaseReference mDatabase  = FirebaseDatabase.getInstance().getReference();

    public void writeNewUser(String userId,String photoUri, String name, String email, Boolean isVerifyed) {
        User user = new User(photoUri ,name, email,isVerifyed);

        mDatabase.child("users").child(userId).setValue(user);
    }


    public void updateIsVerified(String userId, Boolean isVerifyed) {
        mDatabase.child("users").child(userId).child("isVerifyed").setValue(isVerifyed);

    }




}
