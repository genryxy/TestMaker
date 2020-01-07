package com.example.testcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuestionsCreatingActivity extends AppCompatActivity
{
    private TextView numberQuestionTxt;
    private RadioGroup typeAnsRadioGroup;
    private EditText questionTextEdt;
    private EditText answersNumberEdt;
    private Button startCreatingAnswersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_creating);
        findElementsViewById();
        setRadioGroupCheckedChangeListener();
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
                    case R.id.oneAnsRadioBtn:
                    case R.id.manyAnsRadioBtn:
                        answersNumberEdt.setEnabled(true);
                        break;
                    case R.id.ownAnsRadioBtn:
                        answersNumberEdt.setEnabled(false);
                        answersNumberEdt.setText("1");
                        break;
                }
            }
        });
    }
}
