package com.example.darkestmidnight.lykeyfoods.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface FirebaseGetData {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
