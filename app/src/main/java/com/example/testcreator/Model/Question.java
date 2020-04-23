//package com.example.testcreator.Model;
//
//import com.example.testcreator.Enum.TypeAnswer;
//
//import java.util.List;
//
//public class Question {
//    private String questionText;
//    private TypeAnswer typeAnswer;
//    private Integer allAnswersNumber;
//    private List<String> answersLst;
//    private Integer correctAnswersNumber;
//    // Если OneAnswer, то только индекс.
//    // Если ManyAnswers, то только индексы через запятую.
//    // Если OwnAnswer, то строковый ответ.
//    private String correctAnswer;
//
//
//    public Question() { }
//
//    /**
//     * Конструктор класса-обёртки для вопроса.
//     *
//     * @param questionText         Формулировка вопроса (текст вопроса).
//     * @param typeAnswer           Тип ответа (один, много, собственный).
//     * @param allAnswersNumber     Количество всех ответов.
//     * @param answersLst           Список с формулировкой ответов (если typeAnswer
//     *                             - OwnAnswer, то пустой список).
//     * @param correctAnswersNumber Количество правильных ответов.
//     * @param correctAnswer        Если OneAnswer, то только индекс.
//     *                             Если ManyAnswers, то только индексы через запятую.
//     *                             Если OwnAnswer, то строковый ответ.
//     */
//    public Question(String questionText, TypeAnswer typeAnswer, Integer allAnswersNumber,
//                    List<String> answersLst, Integer correctAnswersNumber, String correctAnswer) {
//        this.questionText = questionText;
//        this.typeAnswer = typeAnswer;
//        this.allAnswersNumber = allAnswersNumber;
//        this.answersLst = answersLst;
//        this.correctAnswersNumber = correctAnswersNumber;
//        this.correctAnswer = correctAnswer;
//    }
//
//    public String getQuestionText() { return questionText; }
//
//    public TypeAnswer getTypeAnswer() { return typeAnswer; }
//
//    public Integer getAllAnswersNumber() { return allAnswersNumber; }
//
//    public List<String> getAnswersLst() { return answersLst; }
//
//    public Integer getCorrectAnswersNumber() { return correctAnswersNumber; }
//
//    public String getCorrectAnswer() { return correctAnswer; }
//}
