package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс-обёртка для хранения списка из экземпляров класса QuestionModel
 * в базе данных. Этот класс позволяет удобно сохранять информацию в
 * Cloud Firestore, а также получать при чтении.
 */
public class QuestionFirebase {
    private List<QuestionModel> questions;

    public QuestionFirebase() {}

    public QuestionFirebase(List<QuestionModel> questions) {
        this.questions = questions;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionModel> questions) {
        this.questions = questions;
    }
}
