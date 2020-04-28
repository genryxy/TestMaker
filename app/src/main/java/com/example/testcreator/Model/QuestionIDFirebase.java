package com.example.testcreator.Model;

import java.util.List;

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
