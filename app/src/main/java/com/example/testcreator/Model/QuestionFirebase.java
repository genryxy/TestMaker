package com.example.testcreator.Model;

import java.util.List;

public class QuestionFirebase {
    private List<QuestionModel> question;

    public QuestionFirebase() {}

    public QuestionFirebase(List<QuestionModel> question) {
        this.question = question;
    }

    public List<QuestionModel> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionModel> question) {
        this.question = question;
    }
}
