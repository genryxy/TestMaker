package com.example.testcreator.ui.logout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogoutViewModel extends ViewModel
{
    private MutableLiveData<String> mText;

    public LogoutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Нажмите на кнопку \"Выход\" для выхода");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
