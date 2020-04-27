package com.example.testcreator.Model;

import java.util.List;

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
