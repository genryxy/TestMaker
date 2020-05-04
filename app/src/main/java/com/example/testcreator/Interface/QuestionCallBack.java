package com.example.testcreator.Interface;

import com.example.testcreator.Model.QuestionModel;

import java.util.List;

/**
 * Интерфейс для реализации метода обратного вызова для установки
 * значений коллекции, полученных из базы данных. Коллекция
 * состоит из вопросов.
 */
public interface QuestionCallBack {
    void setQuestionList(List<QuestionModel> questionsLst);
}
