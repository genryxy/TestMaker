package com.example.testcreator.Interface;

import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;

import java.util.List;

/**
 * Интерфейс для реализации метода обратного вызова для установки
 * значений коллекции, полученных из базы данных. Первая коллекция
 * состоит из ID вопросов, вторая - из информации о прохождении вопросов.
 */
public interface ResultCallBack {
    void setQuestionList(List<Integer> questionsIDLst, List<CurrentQuestion> answerLst);
}
