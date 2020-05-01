package com.example.testcreator.Model;

import com.example.testcreator.Common.Common;

import java.util.HashMap;
import java.util.Map;

public class CurrentQuestion {
    private int questionIndex;
    private Common.AnswerType type = Common.AnswerType.NO_ANSWER;
    private String userAnswer;
    /**
     * Словарь, который позволяет восстановить ответы пользователя в перемешанных
     * вопросах относительно ответов в исходном тесте.
     * Формат: (буква в новом ответе, буква в старом ответе).
     */
    private Map<String, String> dictTransitionAns = new HashMap<>();

    public CurrentQuestion() {}

    public CurrentQuestion(int questionIndex, Common.AnswerType type) {
        this.questionIndex = questionIndex;
        this.type = type;
    }

    public CurrentQuestion(int questionIndex, Common.AnswerType type, String userAnswer,
                           Map<String, String> dictTransitionAns) {
        this.questionIndex = questionIndex;
        this.type = type;
        this.userAnswer = userAnswer;
        this.dictTransitionAns = dictTransitionAns;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public Common.AnswerType getType() {
        return type;
    }

    public void setType(Common.AnswerType type) {
        this.type = type;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Map<String, String> getDictTransitionAns() {
        return dictTransitionAns;
    }

    public void setDictTransitionAns(Map<String, String> dictTransitionAns) {
        this.dictTransitionAns = dictTransitionAns;
    }
}
