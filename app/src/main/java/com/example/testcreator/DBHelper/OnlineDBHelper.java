package com.example.testcreator.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.QuestionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OnlineDBHelper {

    private FirebaseFirestore firebaseFirestore;
    private Context context;

    public OnlineDBHelper(Context context, FirebaseFirestore firebaseFirestore) {
        this.context = context;
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

                        if (questionFirebase != null && questionFirebase.getQuestions() != null) {
                            myCallBack.setQuestionList(questionFirebase.getQuestions());
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

    public void readThemes(final ThemesCallBack themesCallBack) {
        final List<String> themesLst = new ArrayList<>();
        firebaseFirestore.collection("themes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                themesLst.add(document.getId());
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            themesCallBack.setThemesToAdapter(themesLst);
                        } else {
                        }
                    }
                });
    }
}
