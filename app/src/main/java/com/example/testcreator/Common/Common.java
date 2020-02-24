package com.example.testcreator.Common;

import android.os.CountDownTimer;

import com.example.testcreator.Model.Category;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static final int TOTAL_TIME = 20 * 60 * 1000;
    public static List<QuestionModel> questionLst = new ArrayList<>();
    public static List<CurrentQuestion> answerSheetList = new ArrayList<>();
    public static Category selectedCategory = new Category();
    public static CountDownTimer countDownTimer;

    public static int rightAnswerCount = 0;
    public static int wrongAnswerCount = 0;


    public enum AnswerType {
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }
}
