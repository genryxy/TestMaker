package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Adapter.AnswerSheetAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity {

    int timePlay = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    TextView questionRightTxt;
    TextView timerTxt;

    RecyclerView answerSheetView;
    AnswerSheetAdapter answerSheetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(Common.selectedCategory.getName());

        // Get questions from DB.
        getQuestions();

        if (Common.questionLst.size() > 0) {
            // Show TextViews with right answer and Timer.
            questionRightTxt = findViewById(R.id.questionRightTxt);
            timerTxt = findViewById(R.id.timerTxt);

            timerTxt.setVisibility(View.VISIBLE);
            questionRightTxt.setVisibility(View.VISIBLE);

            questionRightTxt.setText(String.format("%d/%d", Common.rightAnswerCount, Common.questionLst.size()));

            // Timer.
            countTimer();

            // Set adapter.
            answerSheetView = findViewById(R.id.gridAnswer);
            answerSheetView.setHasFixedSize(true);
            if (Common.questionLst.size() > 5) {
                answerSheetView.setLayoutManager(new GridLayoutManager(this, Common.questionLst.size() / 2));
            }
            answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
            answerSheetView.setAdapter(answerSheetAdapter);
        }
    }

    private void countTimer() {
        if (Common.countDownTimer == null) {
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTxt.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    timePlay -= 1000;

                }

                @Override
                public void onFinish() {

                }
            }.start();
        } else {
            Common.countDownTimer.cancel();
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTxt.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    timePlay -= 1000;

                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

    private void getQuestions() {
        Common.questionLst = DBHelper.getInstance(this).getQuestionsByCategory(Common.selectedCategory.getId());
        if (Common.questionLst.size() == 0) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Opppps!")
                    .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                    .setDescription("We don't have any question in " + Common.selectedCategory.getName())
                    .setPositiveText("Ok")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        } else {
            if (Common.answerSheetList.size() > 0) {
                Common.answerSheetList.clear();
            }
            // Generate 30 answer sheet items from 30 questions
            for(int i = 0; i < Common.questionLst.size(); i++) {
                // Take index of question in List.
                Common.answerSheetList.add(new CurrentQuestion(i, Common.AnswerType.NO_ANSWER));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
