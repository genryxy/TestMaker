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
import com.example.testcreator.Model.Question;
import com.example.testcreator.Model.TestInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TestInfo testInfo;
    // Если не из другой кнопки вызов, то открыть
    // intent из обработчика события.
    private boolean isFromEndCreatingBtn;
    private boolean isFromNextQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);
        ListView lstView = findViewById(R.id.answersLstView);
        findElementsViewById();
        getInfoFromPreviousIntent();

        // Создаём список с ответами.
        final List<AnswerView> lstAnswers = new ArrayList<>();
        for (int i = 0; i < answersNumber; i++)
            lstAnswers.add(new AnswerView(answerText, false));

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter
                (this, R.layout.layout_creating_answer, lstAnswers);
        lstView.setAdapter(adapter);

        addNewVariantBtnClickListen(lstAnswers, adapter);
        createNextQuestionBtnClickListen(lstAnswers);
        endCreatingTestBtnClickListen(lstAnswers);
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
     * @param lstAnswers      Список ответов на создаваемый вопрос.
     * @param rightAnsBuilder Правильный ответ в строковом представлении.
     * @param rightAnsNumber  Количество правильных ответов.
     * @return Список вопросов
     */
    private List<Question> createQuestion(List<AnswerView> lstAnswers,
                                          StringBuilder rightAnsBuilder, int rightAnsNumber) {
        // (Длина - 1), чтобы не было висячей запятой.
        String correctAnswersStr = rightAnsBuilder.substring(0, rightAnsBuilder.length() - 1);
        Question question = new Question(questionText, typeAnswer.equals(TypeAnswer.OwnAnswer.name())
                ? TypeAnswer.OwnAnswer : TypeAnswer.OneOrManyAnswers, lstAnswers.size(),
                lstAnswers, rightAnsNumber, correctAnswersStr);
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        return questions;
    }

    /**
     * Метод для подсчёта количества правильных ответов и запоминания
     * номеров правильных ответов.
     *
     * @param lstAnswers Список со всеми ответами.
     * @return Возвращается пара (количество верных ответов,
     * строка с номерами верных ответов)
     */
    private Pair<Integer, StringBuilder> countRightAnswers(List<AnswerView> lstAnswers) {
        int rightAnsNumber = 0;
        StringBuilder rightAnsBuilder = new StringBuilder();
        for (int i = 0; i < lstAnswers.size(); i++) {
            if (lstAnswers.get(i).getSelected()) {
                rightAnsNumber++;
                rightAnsBuilder.append(i);
                rightAnsBuilder.append(",");
            }
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

    private void addNewVariantBtnClickListen(final List<AnswerView> lstAnswers,
                                             final AnswerViewListAdapter adapter) {
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

    private void endCreatingTestBtnClickListen(final List<AnswerView> lstAnswers) {
        endCreatingTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если создаётся не первый вопрос, то вызвать нажатие
                // на кнопку для создания следующего вопроса.
                if (questionNumber > 1) {
                    isFromEndCreatingBtn = true;
                    createNextQuestionBtn.callOnClick();
                } else {
                    Pair<Integer, StringBuilder> rightAns = countRightAnswers(lstAnswers);
                    if (rightAns.first == 0) {
                        Toast.makeText(AnswersCreatingActivity.this,
                                "Нужно отметить правильный ответ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Question> questions = createQuestion(lstAnswers, rightAns.second, rightAns.first);
                    testInfo = new TestInfo(authFrbs.getCurrentUser().getEmail(), new Date(),
                            nameTest, nameImage, 1, questions);
                    db.collection("tests").document(nameTest)
                            .set(testInfo)
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

    private void createNextQuestionBtnClickListen(final List<AnswerView> lstAnswers) {
        createNextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pair<Integer, StringBuilder> rightAns = countRightAnswers(lstAnswers);
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
                    docRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    testInfo = documentSnapshot.toObject(TestInfo.class);
                                    List<Question> lstQuestions = testInfo.getQuestionsLst();
                                    List<Question> question = createQuestion(lstAnswers, rightAns.second, rightAns.first);
                                    lstQuestions.add(question.get(0));
                                    testInfo.setQuestionsNumber(lstQuestions.size());
                                    testInfo.setQuestionsLst(lstQuestions);

                                    db.collection("tests").document(nameTest)
                                            .set(testInfo)
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