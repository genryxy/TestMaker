package com.example.testcreator.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionModel {
    public static final int NUMBER_ANSWER = 10;

    private int id;
    private String questionText;
    private String questionImage;
    private List<String> allAnswer = new ArrayList<>();
    private String correctAnswer;
    private boolean isImageQuestion;
    private String categoryName;

    public QuestionModel() {
        allAnswer.clear();
    }

    public QuestionModel(int id, String questionText, String questionImage, List<String> allAnswer, String correctAnswer, boolean isImageQuestion, String categoryName) {
        this.id = id;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.allAnswer = allAnswer;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isImageQuestion() {
        return isImageQuestion;
    }

    public void setImageQuestion(boolean imageQuestion) {
        isImageQuestion = imageQuestion;
    }

    public String getCategoryId() {
        return categoryName;
    }

    public void setCategoryId(String categoryId) {
        this.categoryName = categoryId;
    }

    public List<String> getAllAnswer() {
        Collections.sort(allAnswer);
        return allAnswer;
    }

//    public void setAllAnswer(List<String> allAnswer) {
//        this.allAnswer = allAnswer;
//    }
}
