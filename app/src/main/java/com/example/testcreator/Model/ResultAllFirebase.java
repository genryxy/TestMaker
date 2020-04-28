package com.example.testcreator.Model;

import java.util.List;

public class ResultAllFirebase {
    List<ResultAll> resultAllList;

    public ResultAllFirebase() {}

    public ResultAllFirebase(List<ResultAll> resultAllList) {
        this.resultAllList = resultAllList;
    }

    public List<ResultAll> getResultAllList() {
        return resultAllList;
    }

    public void setResultAllList(List<ResultAll> resultAllList) {
        this.resultAllList = resultAllList;
    }
}
