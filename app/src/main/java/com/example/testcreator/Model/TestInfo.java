package com.example.testcreator.Model;

import java.util.Date;
import java.util.List;

public class TestInfo {
    private String testCreator;
    private Date testCreationDate;
    private String testName;
    private String nameImage;
    private Integer questionsNumber;
    private List<Question> questionsLst;


    public TestInfo() { }

    public TestInfo(String testCreator, Date testCreationDate, String testName, String nameImage,
                    Integer questionsNumber, List<Question> questionsLst) {
        this.testCreator = testCreator;
        this.testCreationDate = testCreationDate;
        this.testName = testName;
        this.nameImage = nameImage;
        this.questionsNumber = questionsNumber;
        this.questionsLst = questionsLst;
    }

    public String getTestCreator() { return testCreator; }

    public Date getTestCreationDate() { return testCreationDate; }

    public String getTestName() { return testName; }

    public String getNameImage() { return nameImage; }

    public Integer getQuestionsNumber() { return questionsNumber; }

    public List<Question> getQuestionsLst() { return questionsLst; }

    public void setQuestionsLst(List<Question> questionsLst) { this.questionsLst = questionsLst; }

    public void setQuestionsNumber(Integer questionsNumber) { this.questionsNumber = questionsNumber; }
}