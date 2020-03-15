package com.example.testcreator.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.QuestionActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class OnlineDBHelper {

    //    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private Context context;

//    private DatabaseReference ref;

    public OnlineDBHelper(Context context, /*FirebaseDatabase firebaseDatabase,*/ FirebaseFirestore firebaseFirestore) {
//        this.firebaseDatabase = firebaseDatabase;
        this.context = context;
//        ref = firebaseDatabase.getReference("themes");
        this.firebaseFirestore = firebaseFirestore;
    }

    public void readData(final MyCallBack myCallBack, String category) {
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if (!((QuestionActivity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

        firebaseFirestore.collection("themes")
                .document(category)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QuestionFirebase questionFirebase = documentSnapshot.toObject(QuestionFirebase.class);

                        if (questionFirebase != null) {
                            myCallBack.setQuestionList(questionFirebase.getQuestion());
                        } else {
                            myCallBack.setQuestionList(new ArrayList<QuestionModel>());
                        }

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//        ref.child(category)
//                .child("question")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        List<QuestionModel> questionList = new ArrayList<>();
//                        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                            questionList.add(questionSnapshot.getValue(QuestionModel.class));
//                        }
//                        myCallBack.setQuestionList(questionList);
//
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
}
