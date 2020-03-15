package com.example.testcreator.Model;

import java.util.List;

public class ResultTest {
    private String Duration;
    private List<QuestionModel> questionLst;
    private List<CurrentQuestion> answerSheetLst;

    public ResultTest() {}

    public ResultTest(String duration, List<QuestionModel> questionLst, List<CurrentQuestion> answerSheetLst) {
        Duration = duration;
        this.questionLst = questionLst;
        this.answerSheetLst = answerSheetLst;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public List<QuestionModel> getQuestionLst() {
        return questionLst;
    }

    public void setQuestionLst(List<QuestionModel> questionLst) {
        this.questionLst = questionLst;
    }

    public List<CurrentQuestion> getAnswerSheetLst() {
        return answerSheetLst;
    }

    public void setAnswerSheetLst(List<CurrentQuestion> answerSheetLst) {
        this.answerSheetLst = answerSheetLst;
    }
}