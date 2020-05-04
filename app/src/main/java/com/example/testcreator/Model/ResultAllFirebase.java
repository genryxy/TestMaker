package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс-обёртка для хранения списка из экземпляров класса ResultAll
 * в базе данных. Этот класс позволяет удобно сохранять информацию в
 * Cloud Firestore, а также получать при чтении.
 */
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
