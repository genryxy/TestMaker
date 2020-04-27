package com.example.testcreator.Interface;

import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;

import java.util.List;

public interface ResultCallBack {
    void setQuestionList(List<QuestionModel> questionLst);

    void setUserAnswerList(List<CurrentQuestion> answerLst);
}
