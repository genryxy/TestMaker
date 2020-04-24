package com.example.testcreator.Model;

import com.example.testcreator.Enum.NumberAnswerEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionModel {
    public static final int NUMBER_ANSWER = 10;

    private String questionText;
    private String questionImage;
    private List<String> allAnswer = new ArrayList<>();
    private String correctAnswer;
    private boolean isImageQuestion;
    private String categoryName;
    private NumberAnswerEnum typeAnswer;

    public QuestionModel() {
        allAnswer.clear();
    }

    public QuestionModel(String questionText, String questionImage, List<String> allAnswer, String correctAnswer,
                         boolean isImageQuestion, String categoryName, NumberAnswerEnum typeAnswer) {
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.allAnswer = allAnswer;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryName = categoryName;
        this.typeAnswer = typeAnswer;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

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
        Collections.sort(allAnswer);
        return allAnswer;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public NumberAnswerEnum getTypeAnswer() {
        return typeAnswer;
    }

    public void setTypeAnswer(NumberAnswerEnum typeAnswer) {
        this.typeAnswer = typeAnswer;
    }

    //    public void setAllAnswer(List<String> allAnswer) {
//        this.allAnswer = allAnswer;
//    }
}
