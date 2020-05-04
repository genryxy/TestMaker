package com.example.testcreator.Model;

/**
 * Класс-модель для хранения результатов прохождения теста одного пользователя
 * без выбранных ответов. Нужен для вывода результатов всех пользователей по
 * указанному тесту.
 */
public class ResultAll {
    private int categoryID;
    private String Duration;
    private String finalScore;
    private int wrongAnswer;
    private String testTaker;
    private String finalPoint;

    public ResultAll() {}

    public ResultAll(int categoryID, String duration, String finalScore, int wrongAnswer, String testTaker, String finalPoint) {
        this.categoryID = categoryID;
        Duration = duration;
        this.finalScore = finalScore;
        this.wrongAnswer = wrongAnswer;
        this.testTaker = testTaker;
        this.finalPoint = finalPoint;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
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

    public String getTestTaker() {
        return testTaker;
    }

    public void setTestTaker(String testTaker) {
        this.testTaker = testTaker;
    }

    public String getFinalPoint() {
        return finalPoint;
    }

    public void setFinalPoint(String finalPoint) {
        this.finalPoint = finalPoint;
    }
}
