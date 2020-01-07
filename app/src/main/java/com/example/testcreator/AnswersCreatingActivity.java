package com.example.testcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AnswersCreatingActivity extends AppCompatActivity
{
    private static final String TAG = "AnswersCreatingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);

        Log.d(TAG, "onCreate: Started.");
        final ListView lstView = findViewById(R.id.answersLstView);
        Button addNewVariantBtn = findViewById(R.id.addNewVariantBtn);

        AnswerView ans1 = new AnswerView("текст ответа 1", false);
        AnswerView ans2 = new AnswerView("текст ответа 2", false);
        AnswerView ans3 = new AnswerView("текст ответа 3 ", false);
        AnswerView ans4 = new AnswerView("текст ответа 4", false);

        final List<AnswerView> lstAnswers = new ArrayList<>();
        lstAnswers.add(ans1);
        lstAnswers.add(ans2);
        lstAnswers.add(ans3);
        lstAnswers.add(ans4);

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter(this, R.layout.adapter_view_answers_layout, lstAnswers);
        lstView.setAdapter(adapter);


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
                    lstAnswers.add(new AnswerView("новый ответ", false));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
