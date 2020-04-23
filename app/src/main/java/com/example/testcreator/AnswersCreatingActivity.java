package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testcreator.Adapter.AnswerViewListAdapter;
import com.example.testcreator.Enum.TypeAnswer;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.AnswerView;
import com.example.testcreator.Model.QuestionFirebase;
import com.example.testcreator.Model.QuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersCreatingActivity extends AppCompatActivity implements FireBaseConnections {
    private static final String TAG = "AnswersCreatingActivity";
    private final String answerText = "Текст ответа";
    private Button addNewVariantBtn;
    private Button createNextQuestionBtn;
    private Button endCreatingTestBtn;
    private String typeAnswer;
    private String nameTest;
    private String nameImage;
    private String questionText;
    private int keyNameTest;
    private int questionNumber;
    private int answersNumber;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private TestInfo testInfo;
    private QuestionFirebase questionsFirebase;

    // Если не из другой кнопки вызов, то открыть
    // intent из обработчика события.
    private boolean isFromEndCreatingBtn;
    private boolean isFromNextQuestionBtn;
    // Словарь, состоящий из пар (номер вопроса, вариант ответа).
    private Map<Integer, String> lettersMap = new HashMap<>();
    // Список с вариантами ответов на вопросы. Варианты ответов
    // вводит пользователь.
    private List<AnswerView> lstAnswers;
    // Список с вариантами ответов на вопросы. Варианты ответов
    // вводит пользователь.
    private List<String> lstAnswersToDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);
        ListView lstView = findViewById(R.id.answersLstView);
        findElementsViewById();
        getInfoFromPreviousIntent();

        // Создаём список с ответами.
        lstAnswers = new ArrayList<>();
        for (int i = 0; i < answersNumber; i++) {
            lstAnswers.add(new AnswerView(answerText, false));
        }

        // Заполняем словарь.
        for (int i = 0; i < 10; i++) {
            lettersMap.put(i, String.valueOf((char) ('A' + i)));
        }

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter
                (this, R.layout.layout_creating_answer, lstAnswers);
        lstView.setAdapter(adapter);

        addNewVariantBtnClickListen(adapter);
        createNextQuestionBtnClickListen();
        endCreatingTestBtnClickListen();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание");
        builder.setMessage("Изменения не будут сохранены ");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.show();
    }

    /**
     * Создаёт список вопросов, состоящий из одного вопроса с заданными параметрами.
     *
     * @param rightAnsBuilder Правильный ответ в строковом представлении.
     * @param rightAnsNumber  Количество правильных ответов.
     * @return Список вопросов
     */
    private List<QuestionModel> createQuestion(StringBuilder rightAnsBuilder, int rightAnsNumber) {
        processLstAnswers();
//        Question question = new Question(questionText, typeAnswer.equals(TypeAnswer.OwnAnswer.name())
//                ? TypeAnswer.OwnAnswer : TypeAnswer.OneOrManyAnswers, lstAnswers.size(),
//                lstAnswersToDatabase, rightAnsNumber, rightAnsBuilder.toString());
        List<QuestionModel> questions = new ArrayList<>();
        QuestionModel question = new QuestionModel(questionNumber, questionText, null, lstAnswersToDatabase,
                rightAnsBuilder.toString(), false, 0);
        questions.add(question);
        return questions;
    }

    /**
     * Метод для обработки вариантов ответов. Каждый ответ должен начинаться с
     * заглавной буквы латинского алфавита, которая означает номер ответа.
     * Потом следует точка. Ещё в каждом вопросе должно быть 10 вариантов ответа
     * для правильного вывода возможных ответов на вопрос, поэтому дозаполняем
     * оставшиеся варианты ответов строкой "Z".
     * Для отображения ответов при заполнении используется класс AnswerView, но
     * в базе данных достаточно только формулировки ответа, поэтому переносим
     * все ответы в другой список, использующий встроенный класс String.
     */
    private void processLstAnswers() {
        lstAnswersToDatabase = new ArrayList<>();
        String tmp;
        int size = lstAnswers.size();
        for (int i = 0; i < size; i++) {
            tmp = lettersMap.get(i) + ". " + lstAnswers.get(i).getAnswerText();
            lstAnswersToDatabase.add(tmp);
        }

        // Теперь дозаполним оставшиеся ответы.
        for (int i = size; i < lettersMap.size(); i++) {
            lstAnswersToDatabase.add("Z");
        }
    }

    /**
     * Метод для подсчёта количества правильных ответов и запоминания
     * номеров правильных ответов.
     *
     * @return Возвращается пара (количество верных ответов,
     * строка с номерами верных ответов)
     */
    private Pair<Integer, StringBuilder> saveRightAnswers() {
        int rightAnsNumber = 0;
        StringBuilder rightAnsBuilder = new StringBuilder();
        for (int i = 0; i < lstAnswers.size(); i++) {
            if (lstAnswers.get(i).getSelected()) {
                rightAnsNumber++;
                rightAnsBuilder.append(lettersMap.get(i));
                rightAnsBuilder.append(",");
            }
        }
        // (Длина - 1), чтобы не было висячей запятой.
        if (rightAnsBuilder.length() > 0) {
            rightAnsBuilder.deleteCharAt(rightAnsBuilder.length() - 1);
        }
        return new Pair<>(rightAnsNumber, rightAnsBuilder);
    }

    /**
     * Метод для получения значений, переданных с предыдущей страницы.
     */
    private void getInfoFromPreviousIntent() {
        Intent prevIntent = getIntent();
        questionNumber = prevIntent.getIntExtra("questionNumber", 1);
        answersNumber = Integer.valueOf(prevIntent.getStringExtra("answersNumberEdt"));
        typeAnswer = prevIntent.getStringExtra("typeAnswer");
        questionText = prevIntent.getStringExtra("questionTextEdt");
        typeAnswer = prevIntent.getStringExtra("typeAnswer");
        nameTest = prevIntent.getStringExtra("nameTestEdt");
        nameImage = prevIntent.getStringExtra("nameImage");
        keyNameTest = prevIntent.getIntExtra("keyNameTestEdt", 1);
    }

    /**
     * Добавляет в адаптер новое поле для записи ответа. Добавляемый элемент
     * включает checkbox, textBox для ввода ответа, кнопку для удаления ответа.
     *
     * @param adapter Адаптер для вывода коллекции на экране.
     */
    private void addNewVariantBtnClickListen(final AnswerViewListAdapter adapter) {
        addNewVariantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (typeAnswer.equals(TypeAnswer.OwnAnswer.name()) && lstAnswers.size() != 0) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Вы выбрали ответ в свободной форме!", Toast.LENGTH_SHORT).show();
                } else if (lstAnswers.size() >= 10) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Не может быть больше 10 ответов", Toast.LENGTH_SHORT).show();

                } else {
                    lstAnswers.add(new AnswerView(answerText, false));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Обработчик события для кнопки для завершения создания теста.
     * Если создаётся первый вопрос, то необходимо отметить хотя бы один
     * правильный ответ в вопросе. Иначе делегируем создание вопроса кнопке
     * createNextQuestionBtn (вызываем искусственное нажатие).
     * Если пользователь нажал на кнопку "Завершить создание" и уже есть хотя
     * бы один созданный вопрос, то проверяем что выбран хотя бы один ответ в
     * вопросе и переходим на MainActivity.
     */
    private void endCreatingTestBtnClickListen() {
        endCreatingTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<Integer, StringBuilder> rightAns = saveRightAnswers();
                // Если создаётся не первый вопрос, то вызвать нажатие
                // на кнопку для создания следующего вопроса.
                if (questionNumber > 1) {
                    // Если был выбран хотя бы один ответ и была нажата кнопка
                    // "Завершить создание", то запоминаем последний вопрос перед
                    // завершением создания теста. Иначе переходим сразу на
                    // следующую страницу.
                    if (rightAns.first > 0) {
                        isFromEndCreatingBtn = true;
                        createNextQuestionBtn.callOnClick();
                    }
                } else {
                    if (rightAns.first == 0) {
                        Toast.makeText(AnswersCreatingActivity.this,
                                "Нужно отметить правильный ответ, чтобы добавить тест " +
                                        "из одного вопроса", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<QuestionModel> questions = createQuestion(rightAns.second, rightAns.first);
//                    testInfo = new TestInfo(authFrbs.getCurrentUser().getEmail(), new Date(),
//                            nameTest, nameImage, 1, questions);
                    questionsFirebase = new QuestionFirebase(questions);
                    db.collection("tests").document(nameTest)
//                    db.collection("themes").document("Music")
                            .set(questionsFirebase)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(AnswersCreatingActivity.this,
                                            "Возникла ошибка", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                // Если нажимал пользователь, то открыть новый intent.
                // Если вызов был искусственным, то ничего не делать.
                if (!isFromNextQuestionBtn) {
                    Toast.makeText(AnswersCreatingActivity.this, "Тест создан", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AnswersCreatingActivity.this, MainActivity.class));
                }
                isFromNextQuestionBtn = false;
            }
        });
    }

    /**
     * Обработчик события для кнопки для завершения создания текущего вопроса.
     * Если создаётся первый вопрос, то делегируем создание вопроса кнопке
     * endCreatingTestBtn (вызываем искусственное нажатие), потому что именно
     * в ней создаётся документ в БД с названием теста.
     * Если пользователь нажал на кнопку "Перейти к следующему вопросу" и уже есть
     * хотя бы один созданный вопрос, то проверяем что выбран хотя бы один ответ в
     * вопросе и переходим на QuestionsCreatingActivity для создания следующего
     * вопроса.
     */
    private void createNextQuestionBtnClickListen() {
        createNextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pair<Integer, StringBuilder> rightAns = saveRightAnswers();
                if (rightAns.first == 0) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Нужно отметить правильный ответ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Если создаётся первый вопрос, то вызвать нажатие
                // на кнопку для завершения создания теста.
                if (questionNumber == 1) {
                    isFromNextQuestionBtn = true;
                    endCreatingTestBtn.callOnClick();
                } else {
                    DocumentReference docRef = db.collection("tests").document(nameTest);
//                    DocumentReference docRef = db.collection("themes").document("Music");
                    docRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    questionsFirebase = documentSnapshot.toObject(QuestionFirebase.class);
                                    List<QuestionModel> lstQuestions = questionsFirebase.getQuestions();
                                    List<QuestionModel> question = createQuestion(rightAns.second, rightAns.first);
                                    lstQuestions.add(question.get(0));
                                    //testInfo.setQuestionsNumber(lstQuestions.size());
                                    questionsFirebase.setQuestions(lstQuestions);

                                    db.collection("tests").document(nameTest)
//                                    db.collection("themes").document("Music")
                                            .set(questionsFirebase)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                    Toast.makeText(AnswersCreatingActivity.this,
                                                            "Возникла ошибка", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error getting document", e);
                                    Toast.makeText(AnswersCreatingActivity.this,
                                            "Возникла ошибка", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                // Если нажимал пользователь, то открыть новый intent.
                // Если вызов был искусственным, то ничего не делать.
                if (!isFromEndCreatingBtn) {
                    Intent newIntent = new Intent(AnswersCreatingActivity.this, QuestionsCreatingActivity.class);
                    newIntent.putExtra("questionNumber", questionNumber + 1);
                    newIntent.putExtra("nameTestEdt", nameTest);
                    newIntent.putExtra("keyNameTestEdt", keyNameTest);
                    startActivity(newIntent);
                }
                isFromEndCreatingBtn = false;
            }

        });
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById() {
        addNewVariantBtn = findViewById(R.id.addNewVariantBtn);
        createNextQuestionBtn = findViewById(R.id.createNextQuestionBtn);
        endCreatingTestBtn = findViewById(R.id.endCreatingTestBtn);
    }
}