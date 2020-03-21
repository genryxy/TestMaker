package com.example.testcreator.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения результатов всех тестов, пройденных пользователем, в БД.
 */
public class UserResults {
    /**
     * Коллекция, содержащая результаты всех тестов,
     * пройденных пользователем.
     */
    private List<ResultTest> resultTestsLst = new ArrayList<>();

    public UserResults() { }

    public UserResults(List<ResultTest> resultTestsLst) {
        this.resultTestsLst = resultTestsLst;
    }

    public List<ResultTest> getResultTestsLst() {
        return resultTestsLst;
    }

    public void setResultTestsLst(List<ResultTest> resultTestsLst) {
        this.resultTestsLst = resultTestsLst;
    }
}
