package com.example.testcreator.Common;

import android.content.Intent;
import android.os.CountDownTimer;

import com.example.testcreator.Model.Category;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.example.testcreator.Model.SelectingTestView;
import com.example.testcreator.QuestionFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Common {
    public static final int TOTAL_TIME = 20 * 60 * 1000;
    public static final String KEY_GO_TO_QUESTION = "GO_TO_QUESTION";
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";
    public static final String KEY_SAVE_ONLINE_MODE = "ONLINE_MODE";
    public static List<QuestionModel> questionLst = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetList = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetListFiltered = new ArrayList<>();
    public static Category selectedCategory = new Category();
    public static List<Category> categoryLst = new ArrayList<>();
    public static SelectingTestView selectedTest= new SelectingTestView();
    public static CountDownTimer countDownTimer;

    public static int timer = 0;
    public static int rightAnswerCount = 0;
    public static int wrongAnswerCount = 0;
    public static int noAnswerCount = 0;

    //public static StringBuilder dataQuestion = new StringBuilder();
    // Список с фрагментами, на которых находятся вопросы.
    public static List<QuestionFragment> fragmentsLst = new ArrayList<>();
    // Выбранные варианты ответов.
    public static TreeSet<String> selectedValues = new TreeSet<>();
    public static boolean isOnlineMode = false;

    public static String getNameCategoryByID (int categoryID) {
        for (int i = 0; i < categoryLst.size(); i++) {
            if (categoryLst.get(i).getId() == categoryID) {
                return categoryLst.get(i).getName();
            }
        }
        return "";
    }


    public enum AnswerType {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }
}
