package com.example.testcreator.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("О разработчике:\n" +
                "Краснов Александр, студент НИУ ВШЭ группы БПИ185");
    }

    public LiveData<String> getText() {
        return mText;
    }
}