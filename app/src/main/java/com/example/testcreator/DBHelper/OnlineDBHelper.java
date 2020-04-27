package com.example.testcreator.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Interface.ResultCallBack;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.Model.CategoryFirebase;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.Model.ResultTestFirebase;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.Model.TestInfoFirebase;
import com.example.testcreator.QuestionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.opencensus.common.ToDoubleFunction;

public class OnlineDBHelper implements FireBaseConnections {

    public static final String TAG = "OnlineDBHelper";

    private FirebaseFirestore db;
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
        this.db = FirebaseFirestore.getInstance();
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
        db.collection("themes")
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

    public void getQuestionsByTest(final MyCallBack myCallBack, final String nameTest) {
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if (!((QuestionActivity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

        db.collection("tests")
                .document(nameTest)
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

    public void getQuestionsByResult(final String key, final ResultCallBack resCallBack) {
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if (!((QuestionActivity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
        final String keyName = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();

        db.collection("users")
                .document(keyName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ResultTestFirebase resFirebase = documentSnapshot.toObject(ResultTestFirebase.class);
                        if (resFirebase != null && resFirebase.getTotalCount() != 0) {
                            resCallBack.setQuestionList(new ArrayList<>(resFirebase.getResultTestsMap().get(key).getQuestionLst()));
                            resCallBack.setUserAnswerList(new ArrayList<>(resFirebase.getResultTestsMap().get(key).getAnswerSheetLst()));
                        } else {
                            resCallBack.setQuestionList(new ArrayList<QuestionModel>());
                            resCallBack.setUserAnswerList(new ArrayList<CurrentQuestion>());
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

    public void getCategories(final ThemesCallBack themesCallBack) {
//        CategoryFirebase categoryFirebase = new CategoryFirebase();
//        List<Category> cat = new ArrayList<>();
//        cat.add(new Category(1, "Music", null));
//        cat.add(new Category(2, "Geography", null));
//        categoryFirebase.setCategoryList(cat);
//
//        db.collection("themes")
//                .document("categoryID")
//                .set(categoryFirebase);

        db.collection("themes")
                .document("categoryID")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CategoryFirebase categoryFirebase = documentSnapshot.toObject(CategoryFirebase.class);
                        themesCallBack.setThemes(categoryFirebase.getCategoryList());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed with: ", e.fillInStackTrace());
                    }
                });
    }

    public void getTestInfos(final TestInfoCallBack infosCallBack) {
        db.collection("tests").document("testInfo")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TestInfoFirebase infoFirebase = documentSnapshot.toObject(TestInfoFirebase.class);
                        infosCallBack.setInfosToAdapter(infoFirebase.getTestInfos());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed with: ", e.fillInStackTrace());
                    }
                });
    }

    public void uploadImage(String nameImage, Uri imgUri) {
        StorageReference childRef = storageRef.child(nameImage);
        childRef.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "Изображение загружено", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "Error CountDownLatch", exception);
                        Toast.makeText(context, "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getImageByName(String pathToImg, final ImageView img, final ProgressBar progressBar) {
        storageRef.child(pathToImg).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(img);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void putNamesTestToSet() {
        Common.namesTestSet.clear();
        db.collection("tests")
                .document("testInfo")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TestInfoFirebase testInfoFirebase = documentSnapshot.toObject(TestInfoFirebase.class);
                        if (testInfoFirebase != null) {
                            for (TestInfo test : testInfoFirebase.getTestInfos()) {
                                Common.namesTestSet.add(test.getName());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed with: ", e.fillInStackTrace());
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

    public void saveTestInfo(final String name, final String pathToImg, final int categoryID) {
        final DocumentReference docRef = db.collection("tests").document("testInfo");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    TestInfo testInfo = new TestInfo(name, categoryID, null, pathToImg,
                            authFrbs.getCurrentUser().getEmail(), new Date());
                    // Если ещё нет ни одного вопроса в тесте то создаём экземпляр класса QuestionFirebase
                    // и добавляем его. Иначе просто добавляем вопрос в существующую коллекцию.
                    if (document != null && document.exists()) {
                        docRef.update("testInfos", FieldValue.arrayUnion(testInfo));
                    } else {
                        List<TestInfo> testInfos = new ArrayList<>();
                        testInfos.add(testInfo);
                        TestInfoFirebase infoFirebase = new TestInfoFirebase(testInfos);

                        docRef.set(infoFirebase)
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

    public void saveResultToDatabase(final ResultTest resultTest) {
        String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();
        final DocumentReference docIdRef = db.collection("users").document(keyUser);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Если у пользователя не было пройденных тестов, то создаём экземпляр ResultTestFirebase
                    //  и добавляем его. Иначе просто добавляем в существующую коллекцию.
                    if (document != null && document.exists()) {
                        ResultTestFirebase result = document.toObject(ResultTestFirebase.class);
                        int newCount = result.getTotalCount() + 1;
                        result.setTotalCount(newCount);
                        resultTest.setResultID(newCount);
                        result.getResultTestsMap().put(String.valueOf(newCount), resultTest);
                        docIdRef.set(result);
                    } else {
                        Map<String, ResultTest> mapResult = new HashMap<>();
                        resultTest.setResultID(1);
                        mapResult.put("1", resultTest);
                        ResultTestFirebase resultTestFirebase = new ResultTestFirebase();
                        resultTestFirebase.setResultTestsMap(mapResult);
                        resultTestFirebase.setTotalCount(1);
                        docIdRef.set(resultTestFirebase)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(context, "Возникла ошибка при добавлении", Toast.LENGTH_SHORT).show();
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
