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
    private List<Integer> questionsIDLst;
    /**
     * Список с ответами пользователя на вопросы.
     */
    private List<CurrentQuestion> answerSheetLst;
    private String nameTest;
    private int categoryID;
    private String finalScore;
    private int wrongAnswer;
    private int resultID;
    private boolean isOnlineMode;

    public ResultTest() {}

    public ResultTest(String duration, List<Integer> questionsIDLst, List<CurrentQuestion> answerSheetLst,
                      String nameTest, int categoryID, String finalScore, int wrongAnswer, boolean isOnlineMode) {
        Duration = duration;
        this.questionsIDLst = questionsIDLst;
        this.answerSheetLst = answerSheetLst;
        this.nameTest = nameTest;
        this.categoryID = categoryID;
        this.finalScore = finalScore;
        this.wrongAnswer = wrongAnswer;
        this.isOnlineMode = isOnlineMode;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public List<Integer> getQuestionsIDLst() {
        return questionsIDLst;
    }

    public void setQuestionsIDLst(List<Integer> questionsIDLst) {
        this.questionsIDLst = questionsIDLst;
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

    public boolean getIsOnlineMode() {
        return isOnlineMode;
    }

    public void setIsOnlineMode(boolean isOnlineMode) {
        this.isOnlineMode = isOnlineMode;
    }
}