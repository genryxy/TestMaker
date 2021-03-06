package com.example.testcreator.Interface;

import com.example.testcreator.Model.TestInfo;

import java.util.List;

/**
 * Интерфейс для реализации метода обратного вызова для установки
 * значений коллекции, полученных из базы данных. Коллекция
 * состоит из экземпляров классов с информацией о тесте.
 */
public interface TestInfoCallBack {
    void setInfosToAdapter(List<TestInfo> testInfos);
}
