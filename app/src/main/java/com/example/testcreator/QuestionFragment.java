package com.example.testcreator;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Interface.IQuestion;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements IQuestion {

    private TextView questionTextTxt;
    private CheckBox checkBoxA;
    private CheckBox checkBoxB;
    private CheckBox checkBoxC;
    private CheckBox checkBoxD;
    private FrameLayout layoutImage;
    private ProgressBar progressBar;

    private QuestionModel question;
    private int questionIndex = -1;

    private boolean wasAnswered = false;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public boolean isWasAnswered() {
        return wasAnswered;
    }

    public void setWasAnswered(boolean wasAnswered) {
        this.wasAnswered = wasAnswered;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_question, container, false);


        // Get question.
        questionIndex = getArguments().getInt("index", -1);
        question = Common.questionLst.get(questionIndex);

        if (question != null) {
            progressBar = itemView.findViewById(R.id.progressBar);
            layoutImage = itemView.findViewById(R.id.layoutImage);

            // Если вопрос с картинкой, то показываем значок загрузки во время
            // загрузки изображения, иначе делаем невидимым layout с картинкой.
            if (question.isImageQuestion()) {
                ImageView imgQuestion = itemView.findViewById(R.id.imgQuestion);
                Picasso.get().load(question.getQuestionImage()).into(imgQuestion, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                layoutImage.setVisibility(View.GONE);
            }

            // Find elements.
            findElementsViewById(itemView);

            // Set the text.
            setTextToTextView();
            setOnCheckedChangeListenerToTextView();
        }
        return itemView;
    }

    private void findElementsViewById(View itemView) {
        questionTextTxt = itemView.findViewById(R.id.fragmentQuestionTextTxt);
        checkBoxA = itemView.findViewById(R.id.checkBoxA);
        checkBoxB = itemView.findViewById(R.id.checkBoxB);
        checkBoxC = itemView.findViewById(R.id.checkBoxC);
        checkBoxD = itemView.findViewById(R.id.checkBoxD);
    }

    private void setTextToTextView() {
        questionTextTxt.setText(question.getQuestionText());
        checkBoxA.setText(question.getAnswerA());
        checkBoxB.setText(question.getAnswerB());
        checkBoxC.setText(question.getAnswerC());
        checkBoxD.setText(question.getAnswerD());
    }

    private void setOnCheckedChangeListenerToTextView() {
        checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!wasAnswered) {
                    if (isChecked) {
                        Common.selectedValues.add(checkBoxA.getText().toString());
                    } else {
                        Common.selectedValues.remove(checkBoxA.getText().toString());
                    }
                }
            }
        });

        checkBoxB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!wasAnswered) {
                    if (isChecked) {
                        Common.selectedValues.add(checkBoxB.getText().toString());
                    } else {
                        Common.selectedValues.remove(checkBoxB.getText().toString());
                    }
                }
            }
        });

        checkBoxC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!wasAnswered) {
                    if (isChecked) {
                        Common.selectedValues.add(checkBoxC.getText().toString());
                    } else {
                        Common.selectedValues.remove(checkBoxC.getText().toString());
                    }
                }
            }
        });

        checkBoxD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!wasAnswered) {
                    if (isChecked) {
                        Common.selectedValues.add(checkBoxD.getText().toString());
                    } else {
                        Common.selectedValues.remove(checkBoxD.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public CurrentQuestion getSelectedAnswer() {
        // This method returns the state of answer
        // Right, wrong or normal.
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex, Common.AnswerType.NO_ANSWER);
        StringBuilder resStr = new StringBuilder();
        if (Common.selectedValues.size() > 1) {
            // If multichoice.

            // Split answer to array
            // arr[0] = A. Moscow
            // arr[1] = B. Berlin
            Object[] arrayAnswer = Common.selectedValues.toArray();
            for (int i = 0; i < arrayAnswer.length; i++) {
                if (i < arrayAnswer.length - 1) {
                    resStr.append(new StringBuilder(((String) arrayAnswer[i])).substring(0, 1)).append(",");
                } else {
                    resStr.append(new StringBuilder(((String) arrayAnswer[i])).substring(0, 1));
                }
            }
        } else if (Common.selectedValues.size() == 1) {
            // If one choice.
            Object[] arrayAnswer = Common.selectedValues.toArray();
            resStr.append(((String) arrayAnswer[0]).substring(0, 1));
        }

        if (question != null) {
            // Compare correct answer with user answer
            if (!TextUtils.isEmpty(resStr)) {
                if (resStr.toString().equals(question.getCorrectAnswer())) {
                    currentQuestion.setType(Common.AnswerType.RIGHT_ANSWER);
                } else {
                    currentQuestion.setType(Common.AnswerType.WRONG_ANSWER);
                }
            } else {
                currentQuestion.setType(Common.AnswerType.NO_ANSWER);
            }
        } else {
            Toast.makeText(getContext(), "Не удается получить вопрос", Toast.LENGTH_SHORT).show();
            currentQuestion.setType(Common.AnswerType.NO_ANSWER);
        }
        Common.selectedValues.clear();
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswers() {
        // Bold correct answer.
        // Format: A,B
        String[] correctAnswers = question.getCorrectAnswer().split(",");
        for (String answer : correctAnswers) {
            if (answer.equals("A")) {
                checkBoxA.setTypeface(null, Typeface.BOLD);
                checkBoxA.setTextColor(Color.RED);
            } else if (answer.equals("B")) {
                checkBoxB.setTypeface(null, Typeface.BOLD);
                checkBoxB.setTextColor(Color.RED);
            } else if (answer.equals("C")) {
                checkBoxC.setTypeface(null, Typeface.BOLD);
                checkBoxC.setTextColor(Color.RED);
            } else if (answer.equals("D")) {
                checkBoxD.setTypeface(null, Typeface.BOLD);
                checkBoxD.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public void disableAnswers() {
        checkBoxA.setEnabled(false);
        checkBoxB.setEnabled(false);
        checkBoxC.setEnabled(false);
        checkBoxD.setEnabled(false);
    }

    @Override
    public void resetQuestion() {
        // Enable CheckBoxes
        checkBoxA.setEnabled(true);
        checkBoxB.setEnabled(true);
        checkBoxC.setEnabled(true);
        checkBoxD.setEnabled(true);

        // Remove all selected.
        checkBoxA.setChecked(false);
        checkBoxB.setChecked(false);
        checkBoxC.setChecked(false);
        checkBoxD.setChecked(false);

        // Remove bold on all texts.
        checkBoxA.setTypeface(null, Typeface.NORMAL);
        checkBoxB.setTypeface(null, Typeface.NORMAL);
        checkBoxC.setTypeface(null, Typeface.NORMAL);
        checkBoxD.setTypeface(null, Typeface.NORMAL);

        // Set black color for all texts.
        checkBoxA.setTextColor(Color.BLACK);
        checkBoxB.setTextColor(Color.BLACK);
        checkBoxC.setTextColor(Color.BLACK);
        checkBoxD.setTextColor(Color.BLACK);
    }
}