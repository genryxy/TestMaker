package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс-обёртка для хранения списка из экземпляров класса TestInfo
 * в базе данных. Этот класс позволяет удобно сохранять информацию в
 * Cloud Firestore, а также получать при чтении.
 */
public class TestInfoFirebase {
    private List<TestInfo> testInfos;

    public TestInfoFirebase() {}

    public TestInfoFirebase(List<TestInfo> testInfos) {
        this.testInfos = testInfos;
    }

    public List<TestInfo> getTestInfos() {
        return testInfos;
    }

    public void setTestInfos(List<TestInfo> testInfos) {
        this.testInfos = testInfos;
    }
}
