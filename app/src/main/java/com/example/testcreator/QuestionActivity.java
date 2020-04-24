package com.example.testcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
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
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.Model.UserResults;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity implements FireBaseConnections {

    public static final String TAG = "QuestionActivity";
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
        // Получить вопросы из БД и установить их.
        getAndSetupQuestions();
    }

    /**
     * Метод для подсчёта количества правильных и неправильных ответов.
     */
    private void countCorrectAnswer() {
        // Сбрасываем имеющиеся значения.
        Common.rightAnswerCount = 0;
        Common.wrongAnswerCount = 0;
        for (CurrentQuestion item : Common.answerSheetList) {
            if (item.getType() == Common.AnswerType.RIGHT_ANSWER) {
                Common.rightAnswerCount++;
            } else if (item.getType() == Common.AnswerType.WRONG_ANSWER) {
                Common.wrongAnswerCount++;
            }
        }
    }

    /**
     * Метод для завершения прохождения теста. Может вызываться при двух разных случаях.
     * Если пользователь находится в режиме прохождения теста, то подсчитывает результаты
     * и записывает их в базу данных. Затем независимо от режима (просмотр ответов или
     * прохождение теста) запускает интент, содержащий информацию о прохождении пользователем
     * теста.
     */
    private void finishQuiz() {
        if (!isAnswerModeView) {
            int position = viewPager.getCurrentItem();
            QuestionFragment questionFragment = Common.fragmentsLst.get(position);
            CurrentQuestion questionState = questionFragment.getSelectedAnswer();
            Common.answerSheetList.set(position, questionState);
            // Оповестить об изменении цвета.
            answerSheetAdapter.notifyDataSetChanged();

            countCorrectAnswer();
            questionRightTxt.setText(getFinalResult());
            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));

            // Проходимся по всем фрагментам и устанавливаем правильные ответы,
            // чтобы корректно отображались правильные ответы при просмотре ответов.
            for (QuestionFragment frag : Common.fragmentsLst) {
                frag.showCorrectAnswers();
                frag.disableAnswers();
                frag.setWasAnswered(true);
            }
            writeResultToDatabase();
        }

        // Перейти к новому activity с ожиданием ответа от него.
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - timePlay;
        Common.noAnswerCount = Common.questionLst.size() - (Common.rightAnswerCount + Common.wrongAnswerCount);
        startActivityForResult(intent, CODE_GET_RESULT);
    }

    /**
     * Метод для записи результатов прохождения теста в БД.
     */
    private void writeResultToDatabase() {
        List<QuestionModel> questionLst = new ArrayList<>(Common.questionLst);
        List<CurrentQuestion> answerSheetList = new ArrayList<>(Common.answerSheetList);
        final ResultTest resultTest = new ResultTest(String.valueOf(Common.TOTAL_TIME - timePlay),
                questionLst, answerSheetList, Common.selectedTest.getName(),
                Common.selectedCategory.getName(), getFinalResult(), String.valueOf(Common.wrongAnswerCount));
        final String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();

        final DocumentReference docIdRef = db.collection("users").document(keyUser);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Если у пользователя ещё не было пройденных тестов,
                    // то создаём экземпляр класса UserResults и добавляем его.
                    // Иначе просто добавляем в существующую коллекцию.
                    if (document != null && document.exists()) {
                        docIdRef.update("resultTestsLst", FieldValue.arrayUnion(resultTest));
                    } else {
                        UserResults userResults = new UserResults();
                        userResults.getResultTestsLst().add(resultTest);

                        docIdRef.set(userResults)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(QuestionActivity.this,
                                                "Возникла ошибка при добавлении", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    private String getFinalResult() {
        return String.format("%d/%d", Common.rightAnswerCount, Common.questionLst.size());
    }

    /**
     * Метод для генерирования списка, содержащего фрагменты с вопросами.
     * Необходимо, чтобы потом отображать вопросы в viewPager.
     */
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
     * (шаг обновления - 1000 мс)
     */
    private void countTimer() {
        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }
        Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Переводит в формат: мм::сс
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

    /**
     * Метод для получения вопросов из БД и их вывода на экран.
     */
    private void getAndSetupQuestions() {
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
                                // Устанавливаем в коллбэке вопросы.
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

    /**
     * Метод для вывода формулировки вопроса и вариантов ответов на него.
     * Также метод отображает таймер, количество правильных/неправильных ответов.
     * Вызывает методы для создания фрагментов
     */
    private void setupQuestion() {
        if (Common.questionLst.size() > 0) {
            findElementsViewById();

            if (!isAnswerModeView) {
                // Показывать поле, отвечающее выведение значения таймера.
                timerTxt.setVisibility(View.VISIBLE);
                // Запустить отсчёт таймера.
                countTimer();
            }
            questionRightTxt.setVisibility(View.VISIBLE);
            questionRightTxt.setText(getFinalResult());

            setAnswerSheetViewAdapter();
            generateFragmentList();
            QuestionFragmentAdapter adapter = new QuestionFragmentAdapter(
                    getSupportFragmentManager(), this, Common.fragmentsLst);
            viewPager.setAdapter(adapter);
            // Добавим эффект при смене вопроса.
            viewPager.setPageTransformer(true, new ScaleInOutTransformer());
            tabLayout.setupWithViewPager(viewPager);
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

    /**
     * Метод для обработки смены страниц в viewPager.
     * Если пользователь отметил хотя бы один вариант ответа в режиме прохождения
     * теста, то при смене страниц произойдёт проверка отмеченных вариантов ответов,
     * то есть по возвращении на эту страницу будут отмечены уже правильные варианты
     * ответов и изменить свои ответы нельзя. Но если не было выбрано ни одного
     * варианта ответа, то есть возможность вернуться к вопросу и отметить ответы.
     */
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
//                    Toast.makeText(QuestionActivity.this, "prevPos: " + Common.selectedValues.toString(), Toast.LENGTH_SHORT).show();
                    if (Common.selectedValues.size() > 0 && !questionFragment.isWasAnswered()) {
                        questionFragment.showCorrectAnswers();
                        Common.fragmentsLst.get(prevPosition).setWasAnswered(true);
                        CurrentQuestion questionState = questionFragment.getSelectedAnswer();
                        Common.answerSheetList.set(prevPosition, questionState);
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

    /**
     * Метод для установки адаптера, отвечающего за вывод таблицы, которая
     * отображает правильные/неправильные ответы, а также вопросы, оставшиеся
     * без ответа.
     */
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

    /**
     * Метод для установки textView, отвечающих за количество правильных
     * и неправильных ответов, а также за вывод значения таймера, видимыми
     * или невидимыми в зависимости от переданного параметра.
     *
     * @param isVisible true - сделать видимыми, false - спрятать
     */
    private void setVisibilityOfNumberAnswers(boolean isVisible) {
        if (isVisible) {
            questionWrongTxt.setVisibility(View.VISIBLE);
            questionRightTxt.setVisibility(View.VISIBLE);
            timerTxt.setVisibility(View.VISIBLE);
        } else {
            questionWrongTxt.setVisibility(View.GONE);
            questionRightTxt.setVisibility(View.GONE);
            timerTxt.setVisibility(View.GONE);
        }
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
                    setVisibilityOfNumberAnswers(false);
                } else if (action.equals("doQuizAgain")) {
                    viewPager.setCurrentItem(0);

                    isAnswerModeView = false;
                    countTimer();
                    setVisibilityOfNumberAnswers(true);

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
                    Toast.makeText(this, "Новая попытка", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}