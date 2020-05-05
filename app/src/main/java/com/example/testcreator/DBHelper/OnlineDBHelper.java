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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testcreator.Adapter.ResultAllAdapter;
import com.example.testcreator.Adapter.ResultDBAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Interface.QuestionCallBack;
import com.example.testcreator.Interface.ResultCallBack;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.Model.Category;
import com.example.testcreator.Model.CategoryFirebase;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionIDFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.Model.ResultAll;
import com.example.testcreator.Model.ResultAllFirebase;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.Model.ResultTestFirebase;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.Model.TestInfoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для организации взаимодействия с удалённой базой данных Firebase.
 * Хранит методы для записи в Cloud Storage и Cloud Firestore, а также для
 * чтения из Cloud Storage и Cloud Firestore.
 */
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

    /**
     * Метод для получения списка вопросов из Cloud Firestore по ID категории.
     * Список вопросов состоит из ID вопросов.
     *
     * @param myCallBack Коллбэк для установки значений из коллекции.
     * @param categoryID ID категории.
     * @param dialog     Диалог, который выводится на экран во время загрузки.
     */
    public void getQuestionsByCategory(final MyCallBack myCallBack, final int categoryID, final AlertDialog dialog) {
        categoryName = Utils.getNameCategoryByID(categoryID);
        db.collection("themes")
                .document(categoryName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QuestionIDFirebase questionFirebase = documentSnapshot.toObject(QuestionIDFirebase.class);

                        if (questionFirebase != null && questionFirebase.getQuestionsID() != null) {
                            myCallBack.setQuestionList(questionFirebase.getQuestionsID());
                        } else {
                            myCallBack.setQuestionList(new ArrayList<Integer>());
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
     * Метод для получения списка вопросов из Cloud Firestore по названию теста.
     * Список вопросов состоит из ID вопросов.
     *
     * @param myCallBack Коллбэк для установки значений из коллекции.
     * @param nameTest   Название теста.
     * @param dialog     Диалог, который выводится на экран во время загрузки.
     */
    public void getQuestionsByTest(final MyCallBack myCallBack, final String nameTest, final AlertDialog dialog) {
        db.collection("tests")
                .document(nameTest)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QuestionIDFirebase questionFirebase = documentSnapshot.toObject(QuestionIDFirebase.class);

                        if (questionFirebase != null && questionFirebase.getQuestionsID() != null) {
                            myCallBack.setQuestionList(questionFirebase.getQuestionsID());
                        } else {
                            myCallBack.setQuestionList(new ArrayList<Integer>());
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
     * Метод для получения списка вопросов из Cloud Firestore по ключу из
     * документа с результатами. Список вопросов состоит из ID вопросов.
     * В коллбэке кроме индексов вопросов устанавливаются ещё ответы пользователя.
     *
     * @param key         Ключ, по которому получают результат из словаря.
     * @param resCallBack Коллбэк для установки значений из коллекции (вопросов + ответов пользователя).
     * @param dialog      Диалог, который выводится на экран во время загрузки.
     */
    public void getQuestionsByResult(final String key, final ResultCallBack resCallBack, final AlertDialog dialog) {
        final String keyName = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();

        db.collection("users")
                .document(keyName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final ResultTestFirebase resFirebase = documentSnapshot.toObject(ResultTestFirebase.class);
                        if (resFirebase != null && resFirebase.getTotalCount() != 0) {
                            resCallBack.setQuestionList(new ArrayList<>(resFirebase.getResultTestsMap().get(key).getQuestionsIDLst()),
                                    new ArrayList<>(resFirebase.getResultTestsMap().get(key).getAnswerSheetLst()));
                        } else {
                            resCallBack.setQuestionList(new ArrayList<Integer>(), new ArrayList<CurrentQuestion>());
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
     * Метод для получения списка вопросов из Cloud Firestore по списку, состоящему из ID вопросов,
     * которые нужно получить. Список вопросов состоит из экземпляров класса QuestionModel.
     *
     * @param questionsIDLst Список с ID вопросов.
     * @param idCallBack     Коллбэк для установки значений из коллекции.
     */
    public void getQuestionsByID(final List<Integer> questionsIDLst, final QuestionCallBack idCallBack) {
        db.collection("questions").document("questionsAll")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QuestionFirebase questionFirebase = documentSnapshot.toObject(QuestionFirebase.class);
                        List<QuestionModel> questionsLst = new ArrayList<>();
                        for (Integer id : questionsIDLst) {
                            questionsLst.add(questionFirebase.getQuestions().get(id));
                        }
                        idCallBack.setQuestionList(questionsLst);
                    }
                });
    }

    /**
     * Метод для получения названий всех категорий, имеющихся в БД.
     *
     * @param themesCallBack Коллбэк для установки значений из коллекции.
     */
    public void getCategories(final ThemesCallBack themesCallBack) {
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

    /**
     * Метод для получения подробной информации о всех созданных тестах.
     *
     * @param infosCallBack Коллбэк для установки значений из коллекции.
     */
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

    /**
     * Метод для загрузки изображение в Cloud Storage.
     *
     * @param nameImage Название загружаемого изображения.
     * @param imgUri    Уникальный идентификатор изображения.
     */
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

    /**
     * Метод для получения изображения из Cloud Storage и загрузки
     * в указанный элемент интерфейса ImageView.
     *
     * @param pathToImg   Путь до изображения в БД.
     * @param img         Элемент интерфейса, в который будет помещена загруженная картинка.
     * @param progressBar Диалог, который выводится на экран во время загрузки
     */
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

    /**
     * Метод для получения названий тестов, которые уже имеются в БД. Полученные
     * значения помещаются в Common.namesTestSet.
     */
    public void putNamesTestToSet() {
        Common.namesTestSet.clear();
        db.collection("tests")
                .document("testInfo")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TestInfoFirebase testInfoFirebase = documentSnapshot.toObject(TestInfoFirebase.class);
                        if (testInfoFirebase != null && testInfoFirebase.getTestInfos() != null) {
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
     * Метод для получения всех результатов для одного пользователя. Ключ, по которому определяется
     * пользователь, создаётся с помощью email авторизованного пользователя, а также уникального
     * идентификатора, присваемого при регистрации.
     * Затем создаётся адаптер и устанавливается для RecyclerView.
     *
     * @param resultDBRecycler RecyclerView со всем результатами одного пользователя.
     * @param dialog           Диалог, который выводится на экран во время загрузки.
     */
    public void getResultByUserKey(final RecyclerView resultDBRecycler, final AlertDialog dialog) {
        final String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(keyUser);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ResultTestFirebase resultTestFirebase = documentSnapshot.toObject(ResultTestFirebase.class);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (resultTestFirebase == null) {
                            Toast.makeText(context, "Вы ещё не проходили тесты!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ResultDBAdapter adapter = new ResultDBAdapter(context,
                                new ArrayList<>(resultTestFirebase.getResultTestsMap().values()));
                        resultDBRecycler.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "Не удалось загрузить результаты", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Метод для получения результатов всех пользователей для теста, имя которого указано.
     * Затем создаётся адаптер и устанавливается для RecyclerView.
     *
     * @param nameTest          Название теста.
     * @param resultAllRecycler RecyclerView с результатами всех пользователей за конкретный тест.
     * @param dialog            Диалог, который выводится на экран во время загрузки.
     */
    public void getResultByNameTest(final String nameTest, final RecyclerView resultAllRecycler, final AlertDialog dialog) {
        DocumentReference docRef = db.collection("results").document(nameTest);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ResultAllFirebase resAllFb = documentSnapshot.toObject(ResultAllFirebase.class);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (resAllFb == null) {
                            Toast.makeText(context, "Этот тест никто не проходил!", Toast.LENGTH_SHORT).show();
                            resAllFb = new ResultAllFirebase();
                            resAllFb.setResultAllList(new ArrayList<ResultAll>());
                        }
                        ResultAllAdapter adapter = new ResultAllAdapter(context, resAllFb.getResultAllList());
                        resultAllRecycler.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "Не удалось загрузить результаты", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Метод для сохранения экземпляра вопроса в БД по переданной ссылке.
     *
     * @param docRef        Ссылка на документ в таблице, в который нужно сохранять.
     * @param questionModel Экземпляр класса QuestionModel. Включает в себя всё, что
     *                      относится к вопросу (формулировка, тема вопроса, ответы,
     *                      тип вопроса).
     */
    public void saveQuestionModelDB(final DocumentReference docRef, final QuestionModel questionModel, final String nameTest) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    QuestionFirebase questionFirebase = document.toObject(QuestionFirebase.class);
                    QuestionModel question = questionModel;
                    if (questionFirebase == null || questionFirebase.getQuestions() == null) {
                        questionFirebase = new QuestionFirebase();
                        List<QuestionModel> questionsLst = new ArrayList<>();
                        questionFirebase.setQuestions(questionsLst);
                    }
                    // Нумерация начинается с нуля.
                    question.setQuestionID(questionFirebase.getQuestions().size());
                    questionFirebase.getQuestions().add(question);
                    docRef.set(questionFirebase)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                    final DocumentReference docRefTheme = db.collection("themes")
                            .document(Utils.getNameCategoryByID(question.getCategoryID()));
                    saveQuestionIDDB(docRefTheme, question.getQuestionID());

                    final DocumentReference docRefName = db.collection("tests")
                            .document(nameTest);
                    saveQuestionIDDB(docRefName, question.getQuestionID());
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    /**
     * Метод для сохранения ID вопроса в БД по переданной ссылке.
     *
     * @param docRef     Ссылка на документ в таблице, в который нужно сохранять.
     * @param questionID ID добавляемого вопроса.
     */
    private void saveQuestionIDDB(final DocumentReference docRef, final Integer questionID) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Если ещё нет ни одного вопроса в тесте то создаём экземпляр класса QuestionIDFirebase
                    // и добавляем его. Иначе просто добавляем вопрос в существующую коллекцию.
                    if (document != null && document.exists()) {
                        docRef.update("questionsID", FieldValue.arrayUnion(questionID));
                    } else {
                        List<Integer> questionsID = new ArrayList<>();
                        questionsID.add(questionID);
                        QuestionIDFirebase idFirebase = new QuestionIDFirebase(questionsID);

                        docRef.set(idFirebase)
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

    /**
     * Метод для сохранения информации о созданном тесте в БД по переданной ссылке.
     *
     * @param name       Название теста.
     * @param pathToImg  Строка, содержащая путь до картинки, которая является "логотипом"
     *                   теста. null - если нет картинки
     * @param categoryID Число, показывающее к какой категории относится созданный тест.
     */
    public void saveTestInfoDB(final String name, final String pathToImg, final int categoryID) {
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

    /**
     * Метод для сохранения результатов прохождения теста пользователем в БД.
     * Сохранение происходит для отдельного пользователя, а также в документ
     * конкретного теста.
     *
     * @param resultTest Класс для хранения результата за конкретный тест, пройденный пользователем.
     * @param resultAll  Класс для хранения результата. Нужен для вывода результатов всех
     *                   пользователей по указанному тесту.
     */
    public void saveResultDB(final ResultTest resultTest, final ResultAll resultAll) {
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
        if (Common.selectedTest != null) {
            final DocumentReference docRef = db.collection("results").document(Common.selectedTest);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // Если ещё нет ни одного результата, то создаём экземпляр класса ResultAll
                        // и добавляем его. Иначе просто добавляем результат в существующую коллекцию.
                        if (document != null && document.exists()) {
                            docRef.update("resultAllList", FieldValue.arrayUnion(resultAll));
                        } else {
                            List<ResultAll> resultAlls = new ArrayList<>();
                            resultAlls.add(resultAll);
                            ResultAllFirebase resAllFb = new ResultAllFirebase(resultAlls);

                            docRef.set(resAllFb)
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

    /**
     * Метод для сохранения новой категории в БД.
     *
     * @param newCategory Экземпляр класса новой категории.
     */
    public void saveCategoryDB(final Category newCategory) {
        final DocumentReference docRef = db.collection("themes").document("categoryID");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Если ещё нет ни одного вопроса в тесте, то создаём экземпляр класса QuestionFireBase
                    // и добавляем его. Иначе просто добавляем вопрос в существующую коллекцию.
                    if (document != null && document.exists()) {
                        docRef.update("categoryList", FieldValue.arrayUnion(newCategory));
                    } else {
                        List<Category> categoryLst = new ArrayList<>();
                        categoryLst.add(newCategory);
                        CategoryFirebase categoryFirebase = new CategoryFirebase(categoryLst);

                        docRef.set(categoryFirebase)
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
