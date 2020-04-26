package com.example.testcreator.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Interface.NameTestCallBack;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.Model.CategoryFirebase;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.QuestionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OnlineDBHelper {

    public static final String TAG = "OnlineDBHelper";

    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private String categoryName;

    private static OnlineDBHelper instance;

    public static synchronized OnlineDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new OnlineDBHelper(context);
        }
        instance.context = context;
        return instance;
    }

    private OnlineDBHelper(Context context) {
        this.context = context;
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void getQuestionsByCategory(final MyCallBack myCallBack, final int categoryID) {
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if (!((QuestionActivity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

        categoryName = Common.getNameCategoryByID(categoryID);
        firebaseFirestore.collection("themes")
                .document(categoryName)
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
    }

    /**
     * @param themesCallBack
     */
    public void getCategories(final ThemesCallBack themesCallBack) {
//        CategoryFirebase categoryFirebase = new CategoryFirebase();
//        List<Category> cat = new ArrayList<>();
//        cat.add(new Category(1, "Music", null));
//        cat.add(new Category(2, "Geography", null));
//        categoryFirebase.setCategoryList(cat);
//
//        firebaseFirestore.collection("themes")
//                .document("categoryID")
//                .set(categoryFirebase);

        firebaseFirestore.collection("themes")
                .document("categoryID")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CategoryFirebase categoryFirebase = documentSnapshot.toObject(CategoryFirebase.class);
                        themesCallBack.setThemes(categoryFirebase.getCategoryList());

                    }
                });


//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (!document.getId().equals("categoryID")) {
//                                    themesLst.add(new Category(document.getId(), ""));
//                                }
////                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                            themesCallBack.setThemes(themesLst);
//                        }
//                    }
//                });
    }

    public void getNamesTest(final NameTestCallBack namesCallBack) {
        final List<String> namesTestLst = new ArrayList<>();
        final List<String> categoriesLst = new ArrayList<>();
        firebaseFirestore.collection("tests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals("tests_names")
                                        && !document.getId().equals("tests") && !document.getId().equals("hereTestsName")) {
                                    namesTestLst.add(document.getId());
                                    QuestionFirebase questionFirebase = document.toObject(QuestionFirebase.class);
                                    String categoryName = Common.getNameCategoryByID(
                                            questionFirebase.getQuestions().get(0).getCategoryID());
                                    categoriesLst.add(categoryName);
                                }
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            namesCallBack.setNamesToAdapter(namesTestLst, categoriesLst);
                        }
                    }
                });
    }

    /**
     * Метод для сохранения вопроса в БД по переданной ссылке.
     *
     * @param docRef        Ссылка на документ в таблице, в который нужно сохранять.
     * @param questionModel Экземпляр класса QuestionModel. Включает в себя всё, что
     *                      относится к вопросу (формулировка, тема вопроса, ответы,
     *                      тип вопроса).
     */
    public void saveQuestionByCategoryAndTest(final DocumentReference docRef, final QuestionModel questionModel) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    QuestionModel question = questionModel;
                    // Если ещё нет ни одного вопроса в тесте то создаём экземпляр класса QuestionFirebase
                    // и добавляем его. Иначе просто добавляем вопрос в существующую коллекцию.
                    if (document != null && document.exists()) {
                        docRef.update("questions", FieldValue.arrayUnion(question));
                    } else {
                        List<QuestionModel> questions = new ArrayList<>();
                        questions.add(question);
                        QuestionFirebase questionsFirebase = new QuestionFirebase(questions);

                        docRef.set(questionsFirebase)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
