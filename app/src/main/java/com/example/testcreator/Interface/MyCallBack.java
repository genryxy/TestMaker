package com.example.testcreator.Interface;


import java.util.List;

/**
 * Интерфейс для реализации метода обратного вызова для установки
 * значений коллекции, полученных из базы данных. Коллекция
 * состоит из ID вопросов.
 */
public interface MyCallBack {
    void setQuestionList(List<Integer> questionsIDLst);
}
