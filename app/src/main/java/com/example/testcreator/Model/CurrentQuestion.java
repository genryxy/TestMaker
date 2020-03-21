package com.example.testcreator.Model;

import com.example.testcreator.Common.Common;

public class CurrentQuestion {
    private int questionIndex;
    private Common.AnswerType type;
    private String userAnswer;

    public CurrentQuestion() {}

    public CurrentQuestion(int questionIndex, Common.AnswerType type) {
        this.questionIndex = questionIndex;
        this.type = type;
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
}
