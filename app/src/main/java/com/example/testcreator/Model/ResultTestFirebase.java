package com.example.testcreator.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для хранения результатов всех тестов, пройденных пользователем, в БД.
 */
public class ResultTestFirebase {
    /**
     * Коллекция, содержащая результаты всех тестов,
     * пройденных пользователем.
     */
    private Map<String, ResultTest> resultTestsMap = new HashMap<>();
    private int totalCount = 0;

    public ResultTestFirebase() { }

    public ResultTestFirebase(Map<String, ResultTest>  resultTestsMap) {
        this.resultTestsMap = resultTestsMap;
    }

    public Map<String, ResultTest>  getResultTestsMap() {
        return resultTestsMap;
    }

    public void setResultTestsMap(Map<String, ResultTest>  resultTestsMap) {
        this.resultTestsMap = resultTestsMap;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
