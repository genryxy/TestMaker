package com.example.testcreator;

public class AnswerView
{
    private String answerText;
    private Boolean isSelected;

    public AnswerView() {}

    public AnswerView(String answerText, Boolean isSelected)
    {
        this.answerText = answerText;
        this.isSelected = isSelected;
    }

    public String getAnswerText() { return answerText; }

    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public Boolean getSelected() { return isSelected; }

    public void setSelected(Boolean selected) { isSelected = selected; }
}
