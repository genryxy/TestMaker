package com.example.testcreator;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);

        Log.d(TAG, "onCreate: Started.");
        ListView lstView = findViewById(R.id.answersLstView);
        addNewVariantBtn = findViewById(R.id.addNewVariantBtn);


        Intent prevIntent = getIntent();
        Integer answersNumber = Integer.valueOf(prevIntent.getStringExtra("answersNumberEdt"));

        final List<AnswerView> lstAnswers = new ArrayList<>();
        for (int i = 0; i < answersNumber; i++)
            lstAnswers.add(new AnswerView(answerText, false));

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter
                (this, R.layout.adapter_view_answers_layout, lstAnswers);
        lstView.setAdapter(adapter);
        addNewVariantBtnClickListen(lstAnswers, adapter);
    }

    private void addNewVariantBtnClickListen(final List<AnswerView> lstAnswers,
                                             final AnswerViewListAdapter adapter)
    {
        addNewVariantBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lstAnswers.size() >= 10)
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
}
