package com.example.testcreator.Model;

import java.util.ArrayList;
import java.util.List;

public class UserResults {
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
