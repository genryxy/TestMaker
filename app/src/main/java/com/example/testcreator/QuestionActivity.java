package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Adapter.AnswerSheetAdapter;
import com.example.testcreator.Adapter.QuestionFragmentAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity implements FireBaseConnections {

    private static final int CODE_GET_RESULT = 9999;
    int timePlay = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    TextView questionRightTxt;
    TextView questionWrongTxt;
    TextView timerTxt;

    RecyclerView answerSheetView;
    AnswerSheetAdapter answerSheetAdapter;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(Common.selectedCategory.getName());

        // Get questions from DB.
        getQuestions();
    }

    private void countCorrectAnswer() {
        // Reset values.
        Common.rightAnswerCount = Common.wrongAnswerCount = 0;
        for (CurrentQuestion item : Common.answerSheetList) {
            if (item.getType() == Common.AnswerType.RIGHT_ANSWER) {
                Common.rightAnswerCount++;
            } else if (item.getType() == Common.AnswerType.WRONG_ANSWER) {
                Common.wrongAnswerCount++;
            }
        }
    }

    private void finishQuiz() {
        if (!isAnswerModeView) {
            int position = viewPager.getCurrentItem();
            QuestionFragment questionFragment = Common.fragmentsLst.get(position);
            CurrentQuestion questionState = questionFragment.getSelectedAnswer();
            Common.answerSheetList.set(position, questionState);
            // Notify to change color.
            answerSheetAdapter.notifyDataSetChanged();

            countCorrectAnswer();
            questionRightTxt.setText(new StringBuilder(String.format("%d", Common.rightAnswerCount))
                    .append("/").append(String.format("%d", Common.questionLst.size())).toString());
            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));

            if (questionState.getType() != Common.AnswerType.NO_ANSWER) {
                questionFragment.showCorrectAnswers();
                questionFragment.disableAnswers();
                Common.fragmentsLst.get(position).setWasAnswered(true);
            }
        }

        // Navigate to new result activity
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - timePlay;
        Common.noAnswerCount = Common.questionLst.size() - (Common.rightAnswerCount + Common.wrongAnswerCount);
        // Common.dataQuestion = new StringBuilder(new Gson().toJson(Common.answerSheetList));
        startActivityForResult(intent, CODE_GET_RESULT);
    }

    private void generateFragmentList() {
        for (int i = 0; i < Common.questionLst.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            QuestionFragment questionFragment = new QuestionFragment();
            questionFragment.setArguments(bundle);

            Common.fragmentsLst.add(questionFragment);
        }
    }

    private void countTimer() {
        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }
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
                finishQuiz();
            }
        }.start();
    }

    private void getQuestions() {

        if (!Common.isOnlineMode) {
            Common.questionLst = DBHelper.getInstance(this).getQuestionsByCategory(Common.selectedCategory.getId());
            addQuestionToCommonAnswerSheetAdapter();
            setupQuestion();
        } else {
            new OnlineDBHelper(this, FirebaseFirestore.getInstance())
                    .readData(new MyCallBack() {
                        @Override
                        public void setQuestionList(List<QuestionModel> questionList) {

                            Common.questionLst.clear();
                            Common.questionLst = questionList;
                            addQuestionToCommonAnswerSheetAdapter();
                            setupQuestion();
                        }
                    }, Common.selectedCategory.getName()
                            .replace(" ", "")
                            .replace("/", "_"));
        }
    }

    private void addQuestionToCommonAnswerSheetAdapter() {
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
            for (int i = 0; i < Common.questionLst.size(); i++) {
                // Take index of question in List.
                Common.answerSheetList.add(new CurrentQuestion(i, Common.AnswerType.NO_ANSWER));
            }
        }
    }

    private void setupQuestion() {
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
            } else {
                answerSheetView.setLayoutManager(new GridLayoutManager(this, Common.questionLst.size()));
            }

            answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
            answerSheetView.setAdapter(answerSheetAdapter);

            viewPager = findViewById(R.id.viewpager);
            tabLayout = findViewById(R.id.slidingTabs);

            generateFragmentList();
            QuestionFragmentAdapter adapter = new QuestionFragmentAdapter(
                    getSupportFragmentManager(), this, Common.fragmentsLst);
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);

            // Add event.
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int prevPosition = 0;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

                @Override
                public void onPageSelected(int position) {
                    QuestionFragment questionFragment;
                    if (Common.fragmentsLst.get(position).isWasAnswered()) {
                        Common.fragmentsLst.get(position).showCorrectAnswers();
                        Common.fragmentsLst.get(position).disableAnswers();
                    }
                    if (!isAnswerModeView) {
                        questionFragment = Common.fragmentsLst.get(prevPosition);
                        Toast.makeText(QuestionActivity.this, "prevPos: " + Common.selectedValues.toString(), Toast.LENGTH_SHORT).show();

                        if (Common.selectedValues.size() > 0 && !questionFragment.isWasAnswered()) {
                            questionFragment.showCorrectAnswers();
                            Common.fragmentsLst.get(prevPosition).setWasAnswered(true);
                            CurrentQuestion questionState = questionFragment.getSelectedAnswer();
                            Common.answerSheetList.set(prevPosition, questionState);
                            // Notify to change color.
                            answerSheetAdapter.notifyDataSetChanged();
                            countCorrectAnswer();
                            questionRightTxt.setText(new StringBuilder(String.format("%d", Common.rightAnswerCount))
                                    .append("/").append(String.format("%d", Common.questionLst.size())).toString());
                            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));
                        }
                        prevPosition = position;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) { }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuWrongAnswer);
        ConstraintLayout constraintLayout = (ConstraintLayout) item.getActionView();
        questionWrongTxt = constraintLayout.findViewById(R.id.wrongAnswerTxt);
        questionWrongTxt.setText(String.valueOf(0));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuFinishTest) {
            if (!isAnswerModeView) {
                new MaterialStyledDialog.Builder(this)
                        .setTitle("Завершить?")
                        .setIcon(R.drawable.ic_mood_black_24dp)
                        .setDescription("Вы действительно хотите завершить?")
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
                                finishQuiz();
                            }
                        }).show();
            } else
                finishQuiz();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_GET_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String action = data.getStringExtra("action");
                if (action == null || TextUtils.isEmpty(action)) {
                    int questionNum = data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                    viewPager.setCurrentItem(questionNum);

                    isAnswerModeView = true;
                    Common.countDownTimer.cancel();
                    questionWrongTxt.setVisibility(View.GONE);
                    questionRightTxt.setVisibility(View.GONE);
                    timerTxt.setVisibility(View.GONE);
                } else if (action.equals("doQuizAgain")) {
                    viewPager.setCurrentItem(0);

                    isAnswerModeView = false;
                    countTimer();

                    questionWrongTxt.setVisibility(View.VISIBLE);
                    questionRightTxt.setVisibility(View.VISIBLE);
                    timerTxt.setVisibility(View.VISIBLE);

                    for (CurrentQuestion question : Common.answerSheetList) {
                        question.setType(Common.AnswerType.NO_ANSWER);
                    }
                    answerSheetAdapter.notifyDataSetChanged();
//                    answerSheetHelperAdapter.notifyDataSetChanged();
                    for (QuestionFragment fragment : Common.fragmentsLst) {
                        fragment.resetQuestion();
                    }
                    Common.wrongAnswerCount = 0;
                    Common.rightAnswerCount = 0;
                    Common.noAnswerCount = 0;
                    questionWrongTxt.setText("0");
                    questionRightTxt.setText(new StringBuilder("0").append("/").append(Common.questionLst.size()));
                    Toast.makeText(this, "new Quiz", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}