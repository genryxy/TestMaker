package com.example.testcreator.Interface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Интерфейс для хранения ссылок на экземпляр для авторизации, а также на хранилище
 * с изображениями.
 */
public interface FireBaseConnections {
    FirebaseAuth authFrbs = FirebaseAuth.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
}
