package com.example.testcreator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface FireBaseConnections
{
    FirebaseAuth authFrbs = FirebaseAuth.getInstance();
    DatabaseReference refFrbs = FirebaseDatabase.getInstance().getReference();

}
