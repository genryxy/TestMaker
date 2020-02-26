package com.example.testcreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Adapter.AnswerSheetAdapter;
import com.example.testcreator.Adapter.QuestionFragmentAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity {

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

            viewPager = findViewById(R.id.viewpager);
            tabLayout = findViewById(R.id.slidingTabs);

            generateFragmentList();
            QuestionFragmentAdapter adapter = new QuestionFragmentAdapter(
                    getSupportFragmentManager(), this, Common.fragmentsLst);
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);

            // Add event.
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int SCROLLING_RIGHT = 0;
                int SCROLLING_LEFT = 1;
                int SCROLLING_UNDETERMINED = 2;

                int currentScrollDirection = 2;

                private void setScrollingDirection(float positionOffset) {
                    if ((1 - positionOffset) >= 0.5) {
                        currentScrollDirection = SCROLLING_RIGHT;
                    } else if ((1 - positionOffset) <= 0.5) {
                        currentScrollDirection = SCROLLING_LEFT;
                    }
                }

                private boolean isScrollDirectionUndetermined() {
                    return currentScrollDirection == SCROLLING_UNDETERMINED;
                }

                private boolean isScrollingRight() {
                    return currentScrollDirection == SCROLLING_RIGHT;
                }

                private boolean isScrollingLeft() {
                    return currentScrollDirection == SCROLLING_LEFT;
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (isScrollDirectionUndetermined()) {
                        setScrollingDirection(positionOffset);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    QuestionFragment questionFragment = new QuestionFragment();
                    int pos = 0;

                    if (position > 0) {
                        if (isScrollingRight()) {
                            // Если пользователь промотал вправо, то получаем предыдущий фрагмент
                            // для проверки ответов.
                            questionFragment = Common.fragmentsLst.get(position - 1);
                            pos = position - 1;
                        } else if (isScrollingLeft()) {
                            // Если пользователь промотал вправо, то получаем следующий фрагмент
                            // для проверки ответов.
                            questionFragment = Common.fragmentsLst.get(position + 1);
                            pos = position + 1;
                        } else {
                            questionFragment = Common.fragmentsLst.get(pos);
                        }
                    } else {
                        questionFragment = Common.fragmentsLst.get(0);
                        pos = 0;
                    }

                    // Call function in order to show correct answer.
                    CurrentQuestion questionState = questionFragment.getSelectedAnswer();
                    Common.answerSheetList.set(pos, questionState);
                    // Notify to change color.
                    answerSheetAdapter.notifyDataSetChanged();

                    countCorrectAnswer();
                    questionRightTxt.setText(new StringBuilder(String.format("%d", Common.rightAnswerCount))
                            .append("/").append(String.format("%d", Common.questionLst.size())).toString());
                    questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));

                    if (questionState.getType() != Common.AnswerType.NO_ANSWER) {
                        questionFragment.showCorrectAnswers();
                        questionFragment.disableAnswers();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        currentScrollDirection = SCROLLING_UNDETERMINED;
                    }
                }
            });
        }
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
        }

        // Navigate to new result activity
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
            for (int i = 0; i < Common.questionLst.size(); i++) {
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
            }

        }
        return super.onOptionsItemSelected(item);
    }
}