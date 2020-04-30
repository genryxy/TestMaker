package com.example.testcreator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Enum.NumberAnswerEnum;

public class QuestionsCreatingActivity extends AppCompatActivity {
    public final String TAG = "FAILUREQuestionActivity";

    private String nameTest;
    private String nameImage;
    private int categoryID;
    private TextView numberQuestionTxt;
    private RadioGroup typeAnsRadioGroup;
    private EditText questionTextEdt;
    private EditText answersNumberEdt;
    private Button startCreatingAnswersBtn;
    private ImageView imgViewQuestion;
    private NumberAnswerEnum typeAnswer = NumberAnswerEnum.ManyAnswers;
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

        Common.imgQuestionUri = null;
        imgViewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Common.imgQuestionUri = data.getData();
            imgViewQuestion.setImageURI(Common.imgQuestionUri);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание");
        builder.setMessage("Изменения не будут сохранены! Создаваемый тест будет удалён!");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(QuestionsCreatingActivity.this, MainActivity.class));
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
        imgViewQuestion = findViewById(R.id.imgViewQuestion);
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
                    case R.id.oneAnsRadioBtn:
                        typeAnswer = NumberAnswerEnum.OneAnswer;
                        answersNumberEdt.setEnabled(true);
                        break;
                    case R.id.manyAnsRadioBtn:
                        typeAnswer = NumberAnswerEnum.ManyAnswers;
                        answersNumberEdt.setEnabled(true);
                        break;
                    case R.id.ownAnsRadioBtn:
                        typeAnswer = NumberAnswerEnum.OwnAnswer;
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
        categoryID = prevIntent.getIntExtra("categoryID", 1);
    }

    private void setCreatingAnswersBtnClickListener() {
        startCreatingAnswersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(QuestionsCreatingActivity.this, AnswersCreatingActivity.class);
                newIntent.putExtra("nameTestEdt", nameTest);
                newIntent.putExtra("nameImage", nameImage);
                newIntent.putExtra("typeAnswer", typeAnswer.name());
                newIntent.putExtra("questionTextEdt", questionTextEdt.getText().toString());
                newIntent.putExtra("questionNumber", questionNumber);
                newIntent.putExtra("categoryID", categoryID);
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
}