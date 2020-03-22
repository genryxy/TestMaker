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

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
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
import com.example.testcreator.Model.Question;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.Model.UserResults;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

public class QuestionActivity extends AppCompatActivity implements FireBaseConnections {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int CODE_GET_RESULT = 9999;
    private int timePlay = Common.TOTAL_TIME;
    private boolean isAnswerModeView = false;

    private TextView questionRightTxt;
    private TextView questionWrongTxt;
    private TextView timerTxt;

    private RecyclerView answerSheetView;
    private AnswerSheetAdapter answerSheetAdapter;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(Common.selectedCategory.getName());

        if (getIntent().hasExtra("isAnswerModeView")) {
            if (getIntent().getStringExtra("isAnswerModeView").equals("true")) {
                isAnswerModeView = true;
            }
        }
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
            questionRightTxt.setText(getFinalResult());
            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));

            if (questionState.getType() != Common.AnswerType.NO_ANSWER) {
                questionFragment.showCorrectAnswers();
                questionFragment.disableAnswers();
                Common.fragmentsLst.get(position).setWasAnswered(true);
            }
            writeResultToDatabase();
        }

        // Navigate to new result activity
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - timePlay;
        Common.noAnswerCount = Common.questionLst.size() - (Common.rightAnswerCount + Common.wrongAnswerCount);
        // Common.dataQuestion = new StringBuilder(new Gson().toJson(Common.answerSheetList));
        startActivityForResult(intent, CODE_GET_RESULT);
    }

    private void writeResultToDatabase() {
        List<QuestionModel> questionLst = new ArrayList<>(Common.questionLst);
        List<CurrentQuestion> answerSheetList = new ArrayList<>(Common.answerSheetList);
        final ResultTest resultTest = new ResultTest(String.valueOf(Common.TOTAL_TIME - timePlay),
                questionLst, answerSheetList, Common.selectedTest.getName(),
                Common.selectedCategory.getName(), getFinalResult(), String.valueOf(Common.wrongAnswerCount));
        final String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(keyUser);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserResults userResults = documentSnapshot.toObject(UserResults.class);
                        if (userResults == null) {
                            userResults = new UserResults();
                        }
                        userResults.getResultTestsLst().add(resultTest);

                        db.collection("users").document(keyUser)
                                .set(userResults)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(QuestionActivity.this,
                                                "Возникла ошибка при добавлении", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error getting document", e);
                        Toast.makeText(QuestionActivity.this,
                                "Возникла ошибка при получении", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFinalResult() {
        return String.format("%d/%d", Common.rightAnswerCount, Common.questionLst.size());
    }

    private void generateFragmentList() {
        for (int i = 0; i < Common.questionLst.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            QuestionFragment questionFragment = new QuestionFragment();
            questionFragment.setArguments(bundle);

            if (isAnswerModeView) {
                questionFragment.setWasAnswered(true);
            }
            Common.fragmentsLst.add(questionFragment);
        }
    }

    /**
     * Включить таймер с заданным шагом обновления.
     */
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

        if (!isAnswerModeView) {
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
        } else {
            setupQuestion();
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
            findElementsViewById();

            if (!isAnswerModeView) {
                // Show TextViews with right answer and Timer.
                timerTxt.setVisibility(View.VISIBLE);
                // Timer.
                countTimer();
            }
            questionRightTxt.setVisibility(View.VISIBLE);
            questionRightTxt.setText(getFinalResult());

            // Set adapter.
            setAnswerSheetViewAdapter();

            generateFragmentList();
            QuestionFragmentAdapter adapter = new QuestionFragmentAdapter(
                    getSupportFragmentManager(), this, Common.fragmentsLst);
            viewPager.setAdapter(adapter);
            // Добавим эффект при смене вопроса.
            viewPager.setPageTransformer(true, new ScaleInOutTransformer());
            tabLayout.setupWithViewPager(viewPager);

            // Add event.
            addViewPagerOnChangeListener();
        }
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById() {
        questionRightTxt = findViewById(R.id.questionRightTxt);
        timerTxt = findViewById(R.id.timerTxt);
        answerSheetView = findViewById(R.id.gridAnswer);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.slidingTabs);
    }

    private void addViewPagerOnChangeListener() {
        final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

            int prevPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                QuestionFragment questionFragment;
                if (position >= Common.fragmentsLst.size()) {
                    position = Common.fragmentsLst.size() - 1;
                }

                if (Common.fragmentsLst.get(position).isWasAnswered() || isAnswerModeView) {
                    Common.fragmentsLst.get(position).showCorrectAnswers();

                    // Установить ответы, которые дал пользователь.
                    QuestionFragment tmp = Common.fragmentsLst.get(position);
                    tmp.setUserAnswer(Common.answerSheetList.get(position).getUserAnswer());
                    tmp.disableAnswers();
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
                        questionRightTxt.setText(getFinalResult());
                        questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));
                    }
                    prevPosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        };

        viewPager.addOnPageChangeListener(pageChangeListener);
        // do this in a runnable to make sure the viewPager's views are already instantiated before triggering the onPageSelected call
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                pageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });
    }

    private void setAnswerSheetViewAdapter() {
        answerSheetView.setHasFixedSize(true);
        if (Common.questionLst.size() > 5) {
            answerSheetView.setLayoutManager(new GridLayoutManager(this, Common.questionLst.size() / 2));
        } else {
            answerSheetView.setLayoutManager(new GridLayoutManager(this, Common.questionLst.size()));
        }
        answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
        answerSheetView.setAdapter(answerSheetAdapter);
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
        if (isAnswerModeView) {
            questionWrongTxt.setVisibility(View.GONE);
        } else {
            questionWrongTxt.setVisibility(View.VISIBLE);
        }
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
                    if (Common.countDownTimer != null) {
                        Common.countDownTimer.cancel();
                    }
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
                    for (QuestionFragment fragment : Common.fragmentsLst) {
                        fragment.resetQuestion();
                        fragment.setWasAnswered(false);
                    }
                    Common.wrongAnswerCount = 0;
                    Common.rightAnswerCount = 0;
                    Common.noAnswerCount = 0;
                    Common.selectedValues.clear();
                    questionWrongTxt.setText("0");
                    questionRightTxt.setText(getFinalResult());
                    Toast.makeText(this, "new Quiz", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}