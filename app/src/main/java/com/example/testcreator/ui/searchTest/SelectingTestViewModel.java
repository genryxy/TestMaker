package com.example.testcreator.ui.searchTest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SelectingTestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SelectingTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}