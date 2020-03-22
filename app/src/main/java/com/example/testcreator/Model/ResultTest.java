package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс для хранения результата за конкретный тест, пройденный пользователем.
 */
public class ResultTest {
    /**
     * Время затраченное пользователем на прохождение.
     */
    private String Duration;
    /**
     * Список вопросов, которые были в тесте.
     */
    private List<QuestionModel> questionLst;
    /**
     * Список с ответами пользователя на вопросы.
     */
    private List<CurrentQuestion> answerSheetLst;
    private String nameTest;
    private String categoryName;
    private String finalScore;
    private String wrongAnswer;

    public ResultTest() {}

    public ResultTest(String duration, List<QuestionModel> questionLst, List<CurrentQuestion> answerSheetLst,
                      String nameTest, String categoryName, String finalScore, String wrongAnswer) {
        Duration = duration;
        this.questionLst = questionLst;
        this.answerSheetLst = answerSheetLst;
        this.nameTest = nameTest;
        this.categoryName = categoryName;
        this.finalScore = finalScore;
        this.wrongAnswer = wrongAnswer;
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

    public String getNameTest() {
        return nameTest;
    }

    public void setNameTest(String nameTest) {
        this.nameTest = nameTest;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }
}