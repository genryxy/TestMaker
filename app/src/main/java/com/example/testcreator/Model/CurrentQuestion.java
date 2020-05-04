package com.example.testcreator.Model;

import com.example.testcreator.MyEnum.AnswerTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-модель для хранения информации о текущем вопросе: индекса вопроса,
 * ответа пользователя, правильности ответа пользователя.
 */
public class CurrentQuestion {
    private int questionIndex;
    private AnswerTypeEnum type = AnswerTypeEnum.NO_ANSWER;
    private String userAnswer;
    /**
     * Словарь, который позволяет восстановить ответы пользователя в перемешанных
     * вопросах относительно ответов в исходном тесте.
     * Формат: (буква в новом ответе, буква в старом ответе).
     */
    private Map<String, String> dictTransitionAns = new HashMap<>();

    public CurrentQuestion() {}

    public CurrentQuestion(int questionIndex, AnswerTypeEnum type) {
        this.questionIndex = questionIndex;
        this.type = type;
    }

    public CurrentQuestion(int questionIndex, AnswerTypeEnum type, String userAnswer,
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

    public AnswerTypeEnum getType() {
        return type;
    }

    public void setType(AnswerTypeEnum type) {
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
