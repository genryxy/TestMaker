package com.example.testcreator.Common;

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

/**
 * Класс для хранения общих данных, доступных из разных классов.
 */
public class Common {
    public static final int TOTAL_TIME = 20 * 60 * 1000;
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";
    public static final String KEY_SAVE_ONLINE_MODE = "ONLINE_MODE";

    public static String keyGetTestByResult;
    public static List<QuestionModel> questionLst = new ArrayList<>();
    public static List<Integer> questionIDLst = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetList = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetListFiltered = new ArrayList<>();
    public static int selectedCategory = 1;
    public static List<Category> categoryLst = new ArrayList<>();
    public static Set<String> namesTestSet = new HashSet<>();
    public static String selectedTest;
    public static CountDownTimer countDownTimer;

    public static int timer = 0;
    public static int rightAnswerCount = 0;
    public static int userPointCount = 0;
    public static int wrongAnswerCount = 0;
    public static int noAnswerCount = 0;

    // Список с фрагментами, на которых находятся вопросы.
    public static List<QuestionFragment> fragmentsLst = new ArrayList<>();
    // Выбранные варианты ответов.
    public static TreeSet<String> selectedValues = new TreeSet<>();
    public static boolean isOnlineMode = true;
    public static boolean isShuffleQuestionMode = false;
    public static boolean isIsShuffleAnswerMode = false;
}
