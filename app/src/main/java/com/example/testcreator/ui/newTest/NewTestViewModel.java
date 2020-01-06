package com.example.testcreator.ui.newTest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NewTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Введите уникальное название теста");
    }

    public LiveData<String> getText() {
        return mText;
    }
}