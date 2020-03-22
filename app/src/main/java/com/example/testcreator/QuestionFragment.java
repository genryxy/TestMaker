package com.example.testcreator;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Класс-фрагмент для представления вопроса во ViewPager.
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements IQuestion {

    private TextView questionTextTxt;
    private CheckBox checkBoxA;
    private CheckBox checkBoxB;
    private CheckBox checkBoxC;
    private CheckBox checkBoxD;
    private CheckBox checkBoxE;
    private CheckBox checkBoxF;
    private CheckBox checkBoxG;
    private CheckBox checkBoxH;
    private CheckBox checkBoxI;
    private CheckBox checkBoxJ;
    private FrameLayout layoutImage;
    private ProgressBar progressBar;
    private List<CheckBox> allCheckbox = new ArrayList<>();

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
            checkVisibilityCheckbox();
            setOnCheckedChangeListenerToTextView();
        }
        return itemView;
    }

    private void checkVisibilityCheckbox() {
        for (int i = 0; i < question.getAllAnswer().size(); i++) {
            if (question.getAllAnswer().get(i) == null
                    || question.getAllAnswer().get(i).equals("Z")) {
                allCheckbox.get(i).setVisibility(View.GONE);
            }
        }
    }

    private void findElementsViewById(View itemView) {
        allCheckbox.clear();
        questionTextTxt = itemView.findViewById(R.id.fragmentQuestionTextTxt);
        checkBoxA = itemView.findViewById(R.id.checkBoxA);
        checkBoxB = itemView.findViewById(R.id.checkBoxB);
        checkBoxC = itemView.findViewById(R.id.checkBoxC);
        checkBoxD = itemView.findViewById(R.id.checkBoxD);
        checkBoxE = itemView.findViewById(R.id.checkBoxE);
        checkBoxF = itemView.findViewById(R.id.checkBoxF);
        checkBoxG = itemView.findViewById(R.id.checkBoxG);
        checkBoxH = itemView.findViewById(R.id.checkBoxH);
        checkBoxI = itemView.findViewById(R.id.checkBoxI);
        checkBoxJ = itemView.findViewById(R.id.checkBoxJ);
        allCheckbox.add(checkBoxA);
        allCheckbox.add(checkBoxB);
        allCheckbox.add(checkBoxC);
        allCheckbox.add(checkBoxD);
        allCheckbox.add(checkBoxE);
        allCheckbox.add(checkBoxF);
        allCheckbox.add(checkBoxG);
        allCheckbox.add(checkBoxH);
        allCheckbox.add(checkBoxI);
        allCheckbox.add(checkBoxJ);
    }

    private void setTextToTextView() {
        questionTextTxt.setText(question.getQuestionText());
        checkBoxA.setText(question.getAnswerA());
        checkBoxB.setText(question.getAnswerB());
        checkBoxC.setText(question.getAnswerC());
        checkBoxD.setText(question.getAnswerD());
        checkBoxE.setText(question.getAnswerE());
        checkBoxF.setText(question.getAnswerF());
        checkBoxG.setText(question.getAnswerG());
        checkBoxH.setText(question.getAnswerH());
        checkBoxI.setText(question.getAnswerI());
        checkBoxJ.setText(question.getAnswerJ());
    }

    private void setOnCheckedChangeListenerToTextView() {
        for (final CheckBox checkbox : allCheckbox) {
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!wasAnswered) {
                        if (isChecked) {
                            Common.selectedValues.add(checkbox.getText().toString());
                        } else {
                            Common.selectedValues.remove(checkbox.getText().toString());
                        }
                    }
                }
            });
        }
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
            currentQuestion.setUserAnswer(resStr.toString());
        } else {
            Toast.makeText(getContext(), "Не удается получить вопрос", Toast.LENGTH_SHORT).show();
            currentQuestion.setType(Common.AnswerType.NO_ANSWER);
        }
        Common.selectedValues.clear();
        return currentQuestion;
    }

    /**
     * Метод для вывода правильных ответов.
     * Парсится строка с эталонными ответами, а затем вызывается метод
     * для смены написания текста checkbox.
     */
    @Override
    public void showCorrectAnswers() {
        // Bold correct answer.
        // Format: A,B
        String[] correctAnswers = question.getCorrectAnswer().split(",");
        for (String answer : correctAnswers) {
            switch (answer) {
                case "A":
                    changeTypefaceAndColor(checkBoxA);
                    break;
                case "B":
                    changeTypefaceAndColor(checkBoxB);
                    break;
                case "C":
                    changeTypefaceAndColor(checkBoxC);
                    break;
                case "D":
                    changeTypefaceAndColor(checkBoxD);
                    break;
                case "E":
                    changeTypefaceAndColor(checkBoxE);
                    break;
                case "F":
                    changeTypefaceAndColor(checkBoxF);
                    break;
                case "G":
                    changeTypefaceAndColor(checkBoxG);
                    break;
                case "H":
                    changeTypefaceAndColor(checkBoxH);
                    break;
                case "I":
                    changeTypefaceAndColor(checkBoxI);
                    break;
                case "J":
                    changeTypefaceAndColor(checkBoxJ);
                    break;
            }
        }
    }

    /**
     * Меняет цвет текста checkbox на зелёный и делает шрифт жирным.
     *
     * @param checkBox Вариант ответа, написание текста которого нужно изменить.
     */
    private void changeTypefaceAndColor(CheckBox checkBox) {
        checkBox.setTypeface(null, Typeface.BOLD);
        checkBox.setTextColor(Color.parseColor("#228B22"));
    }

    @Override
    public void disableAnswers() {
        checkBoxA.setEnabled(false);
        checkBoxB.setEnabled(false);
        checkBoxC.setEnabled(false);
        checkBoxD.setEnabled(false);
        checkBoxE.setEnabled(false);
        checkBoxF.setEnabled(false);
        checkBoxG.setEnabled(false);
        checkBoxH.setEnabled(false);
        checkBoxI.setEnabled(false);
        checkBoxJ.setEnabled(false);
    }

    @Override
    public void resetQuestion() {
        // Enable CheckBoxes
        for (CheckBox checkbox : allCheckbox) {
            checkbox.setEnabled(true);
        }

        // Remove all selected.
        for (CheckBox checkbox : allCheckbox) {
            checkbox.setChecked(false);
        }

        // Remove bold on all texts.
        for (CheckBox checkbox : allCheckbox) {
            checkbox.setTypeface(null, Typeface.NORMAL);
        }

        // Set black color for all texts.
        for (CheckBox checkbox : allCheckbox) {
            checkbox.setTextColor(Color.BLACK);
        }
    }

    /**
     * Выделить checkbox, которые отметил пользователь в качестве ответа.
     *
     * @param userAnswer Ответы пользователя.
     */
    void setUserAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.length() == 0) {
            return;
        }

        String[] userAnswers = userAnswer.split(",");
        for (String answer : userAnswers) {
            switch (answer) {
                case "A":
                    checkBoxA.setChecked(true);
                    break;
                case "B":
                    checkBoxB.setChecked(true);
                    break;
                case "C":
                    checkBoxC.setChecked(true);
                    break;
                case "D":
                    checkBoxD.setChecked(true);
                    break;
                case "E":
                    checkBoxE.setChecked(true);
                    break;
                case "F":
                    checkBoxF.setChecked(true);
                    break;
                case "G":
                    checkBoxG.setChecked(true);
                    break;
                case "H":
                    checkBoxH.setChecked(true);
                    break;
                case "I":
                    checkBoxI.setChecked(true);
                    break;
                case "J":
                    checkBoxJ.setChecked(true);
                    break;
            }
        }
    }
}