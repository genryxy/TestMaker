package com.example.testcreator.Interface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public interface FireBaseConnections
{
    FirebaseAuth authFrbs = FirebaseAuth.getInstance();
    DatabaseReference refFrbs = FirebaseDatabase.getInstance().getReference();
    FirebaseDatabase dataFrbdInstance = FirebaseDatabase.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
}
