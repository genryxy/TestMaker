package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс для хранения результата за конкретный тест, пройденный пользователем.
 * Список, состоящий из экземпляров этого класса хранится в БД.
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
    private int categoryID;
    private String finalScore;
    private int wrongAnswer;
    private int resultID;

    public ResultTest() {}

    public ResultTest(String duration, List<QuestionModel> questionLst, List<CurrentQuestion> answerSheetLst,
                      String nameTest, int categoryID, String finalScore, int wrongAnswer) {
        Duration = duration;
        this.questionLst = questionLst;
        this.answerSheetLst = answerSheetLst;
        this.nameTest = nameTest;
        this.categoryID = categoryID;
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

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public int getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(int wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }

    public int getResultID() {
        return resultID;
    }

    public void setResultID(int resultID) {
        this.resultID = resultID;
    }
}