package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Adapter.ResultGridAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Model.CurrentQuestion;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.concurrent.TimeUnit;

public class ResultActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView timeTxt;
    TextView resultTxt;
    TextView rightAnswerTxt;
    Button filterTotalBtn;
    Button filterRightAnswerBtn;
    Button filterWrongAnswerBtn;
    Button filterNoAnswerBtn;
    RecyclerView resultRecycler;
    ResultGridAdapter gridAdapter, filteredGridAdapter;

    BroadcastReceiver backToQuestion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Common.KEY_BACK_FROM_RESULT)) {
                int question = intent.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                goBackActivityWithQuestion(question);
            }
        }
    };

    private void goBackActivityWithQuestion(int question) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Common.KEY_BACK_FROM_RESULT, question);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        LocalBroadcastManager.getInstance(this).registerReceiver(backToQuestion, new IntentFilter(Common.KEY_BACK_FROM_RESULT));

        findElementsViewById();
        toolbar.setTitle("Результаты");
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        resultRecycler.setHasFixedSize(true);
        resultRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        // Устанавливаем адаптер.
        gridAdapter = new ResultGridAdapter(this, Common.answerSheetList);
        resultRecycler.addItemDecoration(new SpaceDecoration(4));
        resultRecycler.setAdapter(gridAdapter);

        timeTxt.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Common.timer),
                TimeUnit.MILLISECONDS.toSeconds(Common.timer) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Common.timer))));
        rightAnswerTxt.setText(new StringBuilder().append(Common.rightAnswerCount)
                .append("/").append(Common.questionLst.size()));
        filterTotalBtn.setText(new StringBuilder().append(Common.questionLst.size()));
        filterRightAnswerBtn.setText(new StringBuilder().append(Common.rightAnswerCount));
        filterWrongAnswerBtn.setText(new StringBuilder().append(Common.wrongAnswerCount));
        filterNoAnswerBtn.setText(new StringBuilder().append(Common.noAnswerCount));

        // Считаем результат.
        int percent = (Common.rightAnswerCount * 100 / Common.questionLst.size());
        if (percent > 80) {
            resultTxt.setText("Отлично");
        } else if (percent > 65) {
            resultTxt.setText("Хорошо");
        } else if (percent > 50) {
            resultTxt.setText("Удовлетворительно");
        } else {
            resultTxt.setText("Неудовлетворительно");
        }

        // События фильтра.
        filterTotalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridAdapter == null) {
                    gridAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetList);
                    resultRecycler.setAdapter(gridAdapter);
                } else {
                    resultRecycler.setAdapter(gridAdapter);
                }
            }
        });

        filterNoAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.answerSheetListFiltered.clear();
                for (CurrentQuestion question : Common.answerSheetList) {
                    if (question.getType() == Common.AnswerType.NO_ANSWER) {
                        Common.answerSheetListFiltered.add(question);
                    }
                }
                filteredGridAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                resultRecycler.setAdapter(filteredGridAdapter);
            }
        });

        filterWrongAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.answerSheetListFiltered.clear();
                for (CurrentQuestion question : Common.answerSheetList) {
                    if (question.getType() == Common.AnswerType.WRONG_ANSWER) {
                        Common.answerSheetListFiltered.add(question);
                    }
                }
                filteredGridAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                resultRecycler.setAdapter(filteredGridAdapter);
            }
        });

        filterRightAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.answerSheetListFiltered.clear();
                for (CurrentQuestion question : Common.answerSheetList) {
                    if (question.getType() == Common.AnswerType.RIGHT_ANSWER) {
                        Common.answerSheetListFiltered.add(question);
                    }
                }
                filteredGridAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                resultRecycler.setAdapter(filteredGridAdapter);
            }
        });
    }

    private void findElementsViewById() {
        toolbar = findViewById(R.id.toolbarResult);
        timeTxt = findViewById(R.id.timeTxt);
        resultTxt = findViewById(R.id.resultTxt);
        rightAnswerTxt = findViewById(R.id.rightAnswerTxt);
        filterTotalBtn = findViewById(R.id.filterTotalBtn);
        filterRightAnswerBtn = findViewById(R.id.filterRightAnswerBtn);
        filterWrongAnswerBtn = findViewById(R.id.filterWrongAnswerBtn);
        filterNoAnswerBtn = findViewById(R.id.filterNoAnswerBtn);
        resultRecycler = findViewById(R.id.resultRecycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDoQuizAgain:
                doQuizAgain();
                break;
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Удаляем все активити
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void doQuizAgain() {
        new MaterialStyledDialog.Builder(this)
                .setTitle("Пройти тест снова?")
                .setIcon(R.drawable.ic_mood_black_24dp)
                .setDescription("Результаты данной попытки сохранены")
                .setNegativeText("Нет")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Да")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("action", "doQuizAgain");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }).show();
    }
}
