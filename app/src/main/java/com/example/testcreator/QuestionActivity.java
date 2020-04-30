package com.example.testcreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Enum.NumberAnswerEnum;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.MyCallBack;
import com.example.testcreator.Interface.QuestionIdCallBack;
import com.example.testcreator.Interface.ResultCallBack;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.Model.ResultAll;
import com.example.testcreator.Model.ResultTest;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends AppCompatActivity implements FireBaseConnections {

    public static final int NUMBER_QUESTION = 30;
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
    private QuestionFragmentAdapter adapter;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AlertDialog dialog;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(Utils.getNameCategoryByID(Common.selectedCategory));

        if (getIntent().hasExtra("isAnswerModeView")) {
            if (getIntent().getStringExtra("isAnswerModeView").equals("true")) {
                isAnswerModeView = true;
            }
        }
        // Получить вопросы из БД и установить их.
        getAndSetupQuestions();
    }

    /**
     * Метод для получения вопросов из БД и их вывода на экран.
     */
    private void getAndSetupQuestions() {
        if (isAnswerModeView) {
            getAndSetupQuestionFromUserResult();
        } else {
            getAndSetupQuestionsFromStorage();
        }
    }

    private void getAndSetupQuestionFromUserResult() {
        if (Common.isOnlineMode) {
            dialog = Utils.showLoadingDialog(this);
            OnlineDBHelper.getInstance(this)
                    .getQuestionsByResult(Common.keyGetTestByResult, new ResultCallBack() {
                        @Override
                        public void setQuestionList(List<Integer> questionsIDLst, final List<CurrentQuestion> answerLst) {
                            OnlineDBHelper.getInstance(context).getQuestionsByID(questionsIDLst, new QuestionIdCallBack() {
                                @Override
                                public void setQuestionList(List<QuestionModel> questionsLst) {
                                    Common.questionLst = questionsLst;
                                    addQuestionToCommonAnswerSheetAdapter();
                                    // Устанавливаем в коллбэке вопросы.
                                    Common.answerSheetList = answerLst;
                                    setupQuestion();
                                }
                            });
                        }
                    }, dialog);
        } else {
            dialog = Utils.showLoadingDialog(this);
            // Ответы пользователя хранятся в онлайн БД!
            OnlineDBHelper.getInstance(this)
                    .getQuestionsByResult(Common.keyGetTestByResult, new ResultCallBack() {
                        @Override
                        public void setQuestionList(List<Integer> questionsIDLst, List<CurrentQuestion> answerLst) {
                            Common.questionLst = DBHelper.getInstance(context).getQuestionsByID(Common.questionIDLst);
                            addQuestionToCommonAnswerSheetAdapter();
                            // Устанавливаем в коллбэке вопросы.
                            Common.answerSheetList = answerLst;
                            setupQuestion();
                        }
                    }, dialog);
        }
    }

    private void getAndSetupQuestionsFromStorage() {
        // Подгружаем вопросы по категории или по названию теста.
        // В зависимости от способа перехода пользователя на Activity.
        if (Common.selectedTest != null) {
            dialog = Utils.showLoadingDialog(this);
            OnlineDBHelper.getInstance(this)
                    .getQuestionsByTest(new MyCallBack() {
                        @Override
                        public void setQuestionList(List<Integer> questionsIDLst) {
                            Common.questionLst.clear();
                            if (Common.isShuffleQuestionMode) {
                                Collections.shuffle(questionsIDLst);
                            }
                            getQuestionsByIDAndSet(questionsIDLst);
                        }
                    }, Common.selectedTest, dialog);
        } else {
            // Если выключен онлайн-режим, то подгружаем из локальной БД.
            if (!Common.isOnlineMode) {
                Common.questionLst = DBHelper.getInstance(this).getQuestionsByCategory(Common.selectedCategory);
                if (Common.isShuffleQuestionMode) {
                    Collections.shuffle(Common.questionLst);
                }
                addQuestionToCommonAnswerSheetAdapter();
                setupQuestion();
            } else {
                dialog = Utils.showLoadingDialog(this);
                // Если включен онлайн-режим, то подгружаем из FireBase.
                OnlineDBHelper.getInstance(this)
                        .getQuestionsByCategory(new MyCallBack() {
                            @Override
                            public void setQuestionList(List<Integer> questionsIDLst) {
                                Common.questionLst.clear();
                                if (Common.isShuffleQuestionMode) {
                                    Collections.shuffle(questionsIDLst);
                                }
                                List<Integer> tmpIDLst = new ArrayList<>();
                                if (questionsIDLst.size() > NUMBER_QUESTION) {
                                    for (int i = 0; i < NUMBER_QUESTION; i++) {
                                        tmpIDLst.add(questionsIDLst.get(i));
                                    }
                                } else {
                                    tmpIDLst = questionsIDLst;
                                }
                                getQuestionsByIDAndSet(tmpIDLst);
                            }
                        }, Common.selectedCategory, dialog);
            }
        }
    }

    private void getQuestionsByIDAndSet(List<Integer> questionsIDLst) {
        OnlineDBHelper.getInstance(context).getQuestionsByID(questionsIDLst, new QuestionIdCallBack() {
            @Override
            public void setQuestionList(List<QuestionModel> questionsLst) {
                Common.questionLst = questionsLst;
                addQuestionToCommonAnswerSheetAdapter();
                // Устанавливаем в коллбэке вопросы.
                setupQuestion();
            }
        });
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
            CurrentQuestion questionState = questionFragment.getAndCheckSelectedAnswer();
            Common.answerSheetList.set(position, questionState);
            // Оповестить об изменении цвета.
            answerSheetAdapter.notifyDataSetChanged();

            countCorrectAnswer();
            questionRightTxt.setText(getFinalResult());
            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));

            // Устанавливаем правильные ответы, потому что если откроют этот
            // же вопрос при просмотре результатов первым, то они не выделятся,
            // потому что не произойдёт смена позиции ViewPager.
            Common.fragmentsLst.get(position).showCorrectAnswers();
            Common.fragmentsLst.get(position).disableAnswers();
            // Проходимся по всем фрагментам и устанавливаем правильные ответы,
            // чтобы корректно отображались правильные ответы при просмотре ответов.
            for (QuestionFragment frag : Common.fragmentsLst) {
                frag.setWasAnswered(true);
            }
            writeResultToDatabase();
            Common.timer = Common.TOTAL_TIME - timePlay;
            if (Common.countDownTimer != null) {
                Common.countDownTimer.cancel();
            }
        }

        // Перейти к новому activity с ожиданием ответа от него.
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.noAnswerCount = Common.questionLst.size() - (Common.rightAnswerCount + Common.wrongAnswerCount);
        startActivityForResult(intent, CODE_GET_RESULT);
    }

    /**
     * Метод для записи результатов прохождения теста в БД.
     */
    private void writeResultToDatabase() {

        List<Integer> questionsIDLst = new ArrayList<>();
        for (QuestionModel question : Common.questionLst) {
            questionsIDLst.add(question.getQuestionID());
        }

        List<CurrentQuestion> answerSheetList = new ArrayList<>(Common.answerSheetList);
        String duration = String.valueOf(Common.TOTAL_TIME - timePlay);
        if (Common.isIsShuffleAnswerMode) {
            answerSheetList = changeUserAnswerAfterShuffleAnswer();
        }

        final ResultTest resultTest = new ResultTest(duration, questionsIDLst, answerSheetList,
                Common.selectedTest, Common.selectedCategory, getFinalResult(), Common.wrongAnswerCount,
                Common.isOnlineMode || Common.selectedTest != null);

        final ResultAll resultAll = new ResultAll(Common.selectedCategory, duration, getFinalResult(),
                Common.wrongAnswerCount, authFrbs.getCurrentUser().getEmail());

        OnlineDBHelper.getInstance(this).saveResultDB(resultTest, resultAll);
    }

    /**
     * Метода для обеспечения совпадения выбранных вариантов ответов пользователя
     * с вариантами ответов на соотвествующих позициях в БД. Несовпадение может
     * возникнуть в случае перемешивания вариантов ответов при прохождении теста.
     */
    private List<CurrentQuestion> changeUserAnswerAfterShuffleAnswer() {
        List<CurrentQuestion> answerSheetList = new ArrayList<>();
        // Перекопируем состояния вопросов из Common.answerSheetList
        for (int i = 0; i < Common.answerSheetList.size(); i++) {
            CurrentQuestion curr = Common.answerSheetList.get(i);
            answerSheetList.add(new CurrentQuestion(curr.getQuestionIndex(), curr.getType(),
                    curr.getUserAnswer(), curr.getDictTransitionAns()));
        }
        // Теперь обработаем вопросы в скопированной коллекции.
        for (CurrentQuestion question : answerSheetList) {
            StringBuilder newUserAns = new StringBuilder();
            for (int i = 0; i < question.getUserAnswer().length(); i++) {
                String ch = String.valueOf(question.getUserAnswer().charAt(i));
                if (question.getDictTransitionAns().containsKey(ch)) {
                    newUserAns.append(question.getDictTransitionAns().get(ch)).append(",");
                } else {
                    newUserAns.append(ch).append(",");
                }
            }
            question.setUserAnswer(newUserAns.substring(0, newUserAns.length() - 1));
        }
        return answerSheetList;
    }

    private String getFinalResult() {
        return String.format("%d/%d", Common.rightAnswerCount, Common.questionLst.size());
    }

    /**
     * Метод для генерирования списка, содержащего фрагменты с вопросами.
     * Необходимо, чтобы потом отображать вопросы в viewPager.
     */
    private void generateFragmentList() {
        Common.fragmentsLst.clear();
        for (int i = 0; i < Common.questionLst.size(); i++) {
            QuestionFragment questionFragment = new QuestionFragment(i);
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
                timerTxt.setText(Utils.convertToNormalForm(millisUntilFinished));
                timePlay -= 1000;
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        };
        Common.countDownTimer.start();
    }

    private void addQuestionToCommonAnswerSheetAdapter() {
        if (Common.questionLst.size() == 0) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Opppps!")
                    .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                    .setDescription("Нет вопросов в выбранной категории!")
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
            adapter = new QuestionFragmentAdapter(getSupportFragmentManager(),
                    this, Common.fragmentsLst);
            viewPager.setAdapter(adapter);
            // Добавим эффект при смене вопроса.
            viewPager.setPageTransformer(true, new ScaleInOutTransformer());
            tabLayout.setupWithViewPager(viewPager);
            addViewPagerOnChangeListener();

            Common.isShuffleQuestionMode = false;
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
                    if (!questionFragment.isWasAnswered() && (Common.selectedValues.size() > 0
                            || Common.questionLst.get(prevPosition).getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer))) {
                        CurrentQuestion questionState = questionFragment.getAndCheckSelectedAnswer();
                        if (questionState.getType() != Common.AnswerType.NO_ANSWER) {
                            questionFragment.showCorrectAnswers();
                            Common.fragmentsLst.get(prevPosition).setWasAnswered(true);
                            Common.answerSheetList.set(prevPosition, questionState);
                            answerSheetAdapter.notifyDataSetChanged();
                            countCorrectAnswer();
                            questionRightTxt.setText(getFinalResult());
                            questionWrongTxt.setText(String.valueOf(Common.wrongAnswerCount));
                        }
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
            answerSheetView.setLayoutManager(new GridLayoutManager(this, (Common.questionLst.size() + 1) / 2));
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
        Common.selectedValues.clear();
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
            timerTxt.setVisibility(View.VISIBLE);
        } else {
            questionWrongTxt.setVisibility(View.GONE);
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
                    if (Common.countDownTimer != null) {
                        Common.countDownTimer.cancel();
                    }
                    Common.wrongAnswerCount = 0;
                    Common.rightAnswerCount = 0;
                    Common.noAnswerCount = 0;
                    Common.selectedValues.clear();
                    Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                    // Удаляем все активити
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(this, "Новая попытка", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}