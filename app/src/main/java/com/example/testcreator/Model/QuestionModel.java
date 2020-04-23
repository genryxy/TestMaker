package com.example.testcreator.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionModel {
    public static final int NUMBER_ANSWER = 10;

    private int id;
    private String questionText;
    private String questionImage;
    private List<String> allAnswer = new ArrayList<>();
    private String correctAnswer;
    private boolean isImageQuestion;
    private int categoryId;

    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private String answerF;
    private String answerG;
    private String answerH;
    private String answerI;
    private String answerJ;

    public QuestionModel() {
        allAnswer.clear();
    }

    public QuestionModel(int id, String questionText, String questionImage, List<String> allAnswer, String correctAnswer, boolean isImageQuestion, int categoryId) {
        this.id = id;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.allAnswer = allAnswer;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isImageQuestion() {
        return isImageQuestion;
    }

    public void setImageQuestion(boolean imageQuestion) {
        isImageQuestion = imageQuestion;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getAllAnswer() {
        Collections.sort(allAnswer);
        return allAnswer;
    }

//    public void setAllAnswer(List<String> allAnswer) {
//        this.allAnswer = allAnswer;
//    }

    public String getAnswerA() {
        return allAnswer.get(0);
    }

    public String getAnswerB() {
        return allAnswer.get(1);
    }

    public String getAnswerC() {
        return allAnswer.get(2);
    }

    public String getAnswerD() {
        return allAnswer.get(3);
    }

    public String getAnswerE() {
        return allAnswer.get(4);
    }

    public String getAnswerF() {
        return allAnswer.get(5);
    }

    public String getAnswerG() {
        return allAnswer.get(6);
    }

    public String getAnswerH() {
        return allAnswer.get(7);
    }

    public String getAnswerI() {
        return allAnswer.get(8);
    }

    public String getAnswerJ() {
        return allAnswer.get(9);
    }

    public void setAnswerA(String answerA) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerA == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerA);
            }
        }
        Collections.sort(allAnswer);
    }

    public void setAnswerB(String answerB) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerB == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerB);
            }
        }
    }

    public void setAnswerC(String answerC) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerC == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerC);
            }
        }
    }

    public void setAnswerD(String answerD) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerD == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerD);
            }
        }
    }

    public void setAnswerE(String answerE) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerE == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerE);
            }
        }
    }

    public void setAnswerF(String answerF) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerF == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerF);
            }
        }
    }

    public void setAnswerG(String answerG) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerG == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerG);
            }
        }
    }

    public void setAnswerH(String answerH) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerH == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerH);
            }
        }
    }

    public void setAnswerI(String answerI) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerI == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerI);
            }
        }
    }

    public void setAnswerJ(String answerJ) {
        if (allAnswer.size() < NUMBER_ANSWER) {
            if (answerJ == null) {
                allAnswer.add("Z");
            } else {
                allAnswer.add(answerJ);
            }
        }
        Collections.sort(allAnswer);
    }
}
