package com.example.testcreator.Common;

import android.net.Uri;
import android.os.CountDownTimer;

import com.example.testcreator.Model.Category;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.QuestionFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Common {
    public static final int TOTAL_TIME = 20 * 60 * 1000;
    public static final String KEY_GO_TO_QUESTION = "GO_TO_QUESTION";
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";
    public static final String KEY_SAVE_ONLINE_MODE = "ONLINE_MODE";

    public static String keyGetTestByResult;
    public static Uri imgQuestionUri;
    public static List<QuestionModel> questionLst = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetList = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetListFiltered = new ArrayList<>();
    public static int selectedCategory = 1;
    public static List<Category> categoryLst = new ArrayList<>();
    public static Set<String> namesTestSet = new HashSet<>();
    public static String selectedTest;
    public static CountDownTimer countDownTimer;

    public static int timer = 0;
    public static int rightAnswerCount = 0;
    public static int wrongAnswerCount = 0;
    public static int noAnswerCount = 0;

    // Список с фрагментами, на которых находятся вопросы.
    public static List<QuestionFragment> fragmentsLst = new ArrayList<>();
    // Выбранные варианты ответов.
    public static TreeSet<String> selectedValues = new TreeSet<>();
    public static boolean isOnlineMode = false;
    public static boolean isShuffleMode = false;

    public enum AnswerType {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }
}
