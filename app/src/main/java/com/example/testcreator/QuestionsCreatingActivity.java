package com.example.testcreator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionsCreatingActivity extends AppCompatActivity
{
    private TextView numberQuestionTxt;
    private RadioGroup typeAnsRadioGroup;
    private EditText questionTextEdt;
    private EditText answersNumberEdt;
    private Button startCreatingAnswersBtn;
    private TypeAnswer typeAnswer = TypeAnswer.OneOrManyAnswers;
    private int questionNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_creating);
        findElementsViewById();
        setRadioGroupCheckedChangeListener();
        setCreatingAnswersBtnClickListener();

        Intent prevIntent = getIntent();
        if (prevIntent.hasExtra("questionNumber"))
            questionNumber = prevIntent.getIntExtra("questionNumber", 1);

        String tmp = questionNumber + " вопрос";
        numberQuestionTxt.setText(tmp);
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById()
    {
        typeAnsRadioGroup = findViewById(R.id.typeAnswerRadioGroup);
        questionTextEdt = findViewById(R.id.questionTextEdt);
        answersNumberEdt = findViewById(R.id.answersNumberEdt);
        startCreatingAnswersBtn = findViewById(R.id.startCreatingAnswersBtn);
        // Номер вопроса, который создаётся.
        numberQuestionTxt = findViewById(R.id.numberQuestionTxt);
    }

    private void setRadioGroupCheckedChangeListener()
    {
        typeAnsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                answersNumberEdt.setText("4");
                switch (checkedId)
                {
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

    private void setCreatingAnswersBtnClickListener()
    {
        startCreatingAnswersBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent prevIntent = getIntent();
                String nameTest = prevIntent.getStringExtra("nameTestEdt");
                // ????
                String questionsNumber = prevIntent.getStringExtra("questionsNumberEdt");
                Intent newIntent = new Intent(QuestionsCreatingActivity.this, AnswersCreatingActivity.class);
                newIntent.putExtra("nameTestEdt", nameTest);
                // ????
                newIntent.putExtra("questionsNumberEdt", questionsNumber);
                newIntent.putExtra("typeAnswer", typeAnswer.name());
                newIntent.putExtra("questionTextEdt", questionTextEdt.getText().toString());
                newIntent.putExtra("questionNumber", questionNumber);
                if (answersNumberEdt.getText().toString().length() == 0)
                {
                    newIntent.putExtra("answersNumberEdt", "4");
                    startActivity(newIntent);
                } else if (Integer.valueOf(answersNumberEdt.getText().toString()) > 10)
                {
                    answersNumberEdt.requestFocus();
                    answersNumberEdt.setError("Не более 10 ответов");
                } else
                {
                    newIntent.putExtra("answersNumberEdt", answersNumberEdt.getText().toString());
                    startActivity(newIntent);
                }

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание");
        builder.setMessage("Изменения не будут сохранены ");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //saveResult();
                QuestionsCreatingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }
}
