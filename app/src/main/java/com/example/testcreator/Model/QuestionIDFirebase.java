package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс-обёртка для хранения списка с индексами вопросов в базе данных.
 * Позволяет добавлять ID вопросов в новый тест, а также в соответствующую
 * категорию. Этот класс позволяет удобно сохранять информацию в
 * Cloud Firestore, а также получать при чтении.
 */
public class QuestionIDFirebase {
    private List<Integer> questionsID;

    public QuestionIDFirebase() {}

    public QuestionIDFirebase(List<Integer> questionsID) {
        this.questionsID = questionsID;
    }

    public List<Integer> getQuestionsID() {
        return questionsID;
    }

    public void setQuestionsID(List<Integer> questionsID) {
        this.questionsID = questionsID;
    }
}
