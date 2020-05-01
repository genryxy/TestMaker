package com.example.testcreator.Model;

import com.example.testcreator.Enum.NumberAnswerEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionModel {
    public static final int NUMBER_ANSWER = 10;

    private int questionID;
    private String questionText;
    private String questionImage;
    private List<String> allAnswer = new ArrayList<>();
    private String correctAnswer;
    private boolean isImageQuestion;
    private int categoryID;
    private NumberAnswerEnum typeAnswer;
    private int questionPoint;

    public QuestionModel() {
        allAnswer.clear();
    }

    public QuestionModel(String questionText, String questionImage, List<String> allAnswer, String correctAnswer,
                         boolean isImageQuestion, int categoryID, NumberAnswerEnum typeAnswer, int questionPoint) {
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.allAnswer = allAnswer;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryID = categoryID;
        this.typeAnswer = typeAnswer;
        this.questionPoint = questionPoint;
    }

    public QuestionModel(int questionID, String questionText, String questionImage, List<String> allAnswer,
                         String correctAnswer, boolean isImageQuestion, int categoryID, NumberAnswerEnum typeAnswer, int questionPoint) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.allAnswer = allAnswer;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryID = categoryID;
        this.typeAnswer = typeAnswer;
        this.questionPoint = questionPoint;
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

    public List<String> getAllAnswer() {
        return allAnswer;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public NumberAnswerEnum getTypeAnswer() {
        return typeAnswer;
    }

    public void setTypeAnswer(NumberAnswerEnum typeAnswer) {
        this.typeAnswer = typeAnswer;
    }

    public void setAllAnswer(List<String> allAnswer) {
        this.allAnswer = allAnswer;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getQuestionPoint() {
        return questionPoint;
    }

    public void setQuestionPoint(int questionPoint) {
        this.questionPoint = questionPoint;
    }
}
