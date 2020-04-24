package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcreator.Enum.TypeAnswer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuestionsCreatingActivity extends AppCompatActivity {
    private final String TAG = "FAILUREQuestionActivity";

    private String nameTest;
    private String nameImage;
    private String categoryName;
    private int keyNameTest;
    private TextView numberQuestionTxt;
    private RadioGroup typeAnsRadioGroup;
    private EditText questionTextEdt;
    private EditText answersNumberEdt;
    private Button startCreatingAnswersBtn;
    private TypeAnswer typeAnswer = TypeAnswer.OneOrManyAnswers;
    private int questionNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_creating);
        getInfoFromPreviousIntent();
        findElementsViewById();
        setRadioGroupCheckedChangeListener();
        setCreatingAnswersBtnClickListener();

        Intent prevIntent = getIntent();
        if (prevIntent.hasExtra("questionNumber"))
            questionNumber = prevIntent.getIntExtra("questionNumber", 1);
        String tmp = questionNumber + " вопрос";
        numberQuestionTxt.setText(tmp);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание");
        builder.setMessage("Изменения не будут сохранены! Создаваемый тест будет удалён!");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeNameTestFromDataBase();
                startActivity(new Intent(QuestionsCreatingActivity.this, MainActivity.class));
                //QuestionsCreatingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById() {
        typeAnsRadioGroup = findViewById(R.id.typeAnswerRadioGroup);
        questionTextEdt = findViewById(R.id.questionTextEdt);
        answersNumberEdt = findViewById(R.id.answersNumberEdt);
        startCreatingAnswersBtn = findViewById(R.id.startCreatingAnswersBtn);
        // Номер вопроса, который создаётся.
        numberQuestionTxt = findViewById(R.id.numberQuestionTxt);
    }

    /**
     * Меняет значение в текстовом поле в зависимости от
     * выбранного варианта.
     */
    private void setRadioGroupCheckedChangeListener() {
        typeAnsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                answersNumberEdt.setText("4");
                switch (checkedId) {
                    case R.id.oneOrManyAnsRadioBtn:
                        typeAnswer = TypeAnswer.OneOrManyAnswers;
                        answersNumberEdt.setEnabled(true);
                        break;
                    case R.id.ownAnsRadioBtn:
                        typeAnswer = TypeAnswer.OwnAnswer;
                        answersNumberEdt.setEnabled(false);
                        answersNumberEdt.setText("1");
                        break;
                }
            }
        });
    }

    /**
     * Метод для получения значений, переданных с предыдущей страницы.
     */
    private void getInfoFromPreviousIntent() {
        Intent prevIntent = getIntent();
        nameTest = prevIntent.getStringExtra("nameTestEdt");
        nameImage = prevIntent.getStringExtra("nameImage");
        keyNameTest = prevIntent.getIntExtra("keyNameTestEdt", 1);
        categoryName = prevIntent.getStringExtra("categoryName");
    }

    private void setCreatingAnswersBtnClickListener() {
        startCreatingAnswersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(QuestionsCreatingActivity.this, AnswersCreatingActivity.class);
                newIntent.putExtra("nameTestEdt", nameTest);
                newIntent.putExtra("nameImage", nameImage);
                newIntent.putExtra("keyNameTestEdt", keyNameTest);
                newIntent.putExtra("typeAnswer", typeAnswer.name());
                newIntent.putExtra("questionTextEdt", questionTextEdt.getText().toString());
                newIntent.putExtra("questionNumber", questionNumber);
                newIntent.putExtra("categoryName", categoryName);
                if (answersNumberEdt.getText().toString().length() == 0) {
                    newIntent.putExtra("answersNumberEdt", "4");
                    startActivity(newIntent);
                } else if (Integer.valueOf(answersNumberEdt.getText().toString()) > 10) {
                    answersNumberEdt.requestFocus();
                    answersNumberEdt.setError("Не более 10 ответов");
                } else {
                    newIntent.putExtra("answersNumberEdt", answersNumberEdt.getText().toString());
                    startActivity(newIntent);
                }

            }
        });
    }

    /**
     * Если пользователь решил вернуться назад во время создания вопроса,
     * то необходимо удалить название из БД и поменять количество тестов,
     * содержащихся в БД.
     */
    private void removeNameTestFromDataBase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("tests").document("tests_names");

        Map<String, Object> updates = new HashMap<>();
        updates.put("name" + Integer.valueOf(keyNameTest - 1).toString(), FieldValue.delete());
        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(QuestionsCreatingActivity.this, "удалили название", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error removing nameTest", e);
                        //Toast.makeText(QuestionsCreatingActivity.this, "не удалось удалить", Toast.LENGTH_SHORT).show();
                    }
                });

        Map<String, Object> dataNumb = new HashMap<>();
        dataNumb.put("testsNumber", keyNameTest - 1);
        db.collection("tests").document("tests")
                .update(dataNumb)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating testsNumber", e);
                    }
                });

        db.collection("tests").document(nameTest)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(QuestionsCreatingActivity.this, "Тест был успешно удален",
                                Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsCreatingActivity.this, "Не удалось удалить тест",
                                Toast.LENGTH_SHORT).show();
//                                Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}