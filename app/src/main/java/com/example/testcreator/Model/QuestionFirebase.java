package com.example.testcreator.Model;

import java.util.List;

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
