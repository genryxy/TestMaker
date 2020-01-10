package com.example.testcreator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnswersCreatingActivity extends AppCompatActivity
{
    private static final String TAG = "AnswersCreatingActivity";
    private final String answerText = "Текст ответа";
    private Button addNewVariantBtn;
    private Button createNextQuestionBtn;
    private Button endCreatingTest;
    private String typeAnswer;
    private int questionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);

        Log.d(TAG, "onCreate: Started.");
        ListView lstView = findViewById(R.id.answersLstView);
        findElementsViewById();


        // Получаем данные с прошлой activity.
        Intent prevIntent = getIntent();
        questionNumber = prevIntent.getIntExtra("questionNumber", 1);
        int answersNumber = Integer.valueOf(prevIntent.getStringExtra("answersNumberEdt"));
        typeAnswer = prevIntent.getStringExtra("typeAnswer");


        // Создаём список с ответами.
        final List<AnswerView> lstAnswers = new ArrayList<>();
        for (int i = 0; i < answersNumber; i++)
            lstAnswers.add(new AnswerView(answerText, false));

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter
                (this, R.layout.adapter_view_answers_layout, lstAnswers);
        lstView.setAdapter(adapter);
        addNewVariantBtnClickListen(lstAnswers, adapter);

        createNextQuestionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent newIntent = new Intent(AnswersCreatingActivity.this, QuestionsCreatingActivity.class);
                newIntent.putExtra("questionNumber", questionNumber + 1);
                startActivity(newIntent);

            }
        });
    }

    private void addNewVariantBtnClickListen(final List<AnswerView> lstAnswers,
                                             final AnswerViewListAdapter adapter)
    {
        addNewVariantBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (typeAnswer.equals(TypeAnswer.OwnAnswer.name()) && lstAnswers.size() != 0)
                {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Вы выбрали ответ в свободной форме!", Toast.LENGTH_SHORT).show();
                } else if (lstAnswers.size() >= 10)
                {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Не может быть больше 10 ответов", Toast.LENGTH_SHORT).show();

                } else
                {
                    lstAnswers.add(new AnswerView(answerText, false));
                    adapter.notifyDataSetChanged();
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
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                //saveResult();
                AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                //AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.show();
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById()
    {
        addNewVariantBtn = findViewById(R.id.addNewVariantBtn);
        createNextQuestionBtn = findViewById(R.id.createNextQuestionBtn);
        endCreatingTest = findViewById(R.id.endCreatingTest);
    }
}
