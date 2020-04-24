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
    private FrameLayout layoutImage;
    private ProgressBar progressBar;
    private List<CheckBox> allCheckbox = new ArrayList<>();

    private QuestionModel question;
    private int questionIndex = -1;

    private boolean wasAnswered = false;

    public QuestionFragment() {
        // Для работы требуется пустой конструктор
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
        View itemView = inflater.inflate(R.layout.fragment_question, container, false);


        // Получить вопрос из общего класса по индексу, который передали
        // при создании экземпляра класса.
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

            findElementsViewById(itemView);
            setTextToTextView();
            checkVisibilityCheckbox();
            setOnCheckedChangeListenerToTextView();
        }
        return itemView;
    }

    /**
     * Метод, который делает невидимыми нижние checkbox, если вариантов ответов
     * меньше максимально возможного количества (10 ответов).
     */
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
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxA));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxB));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxC));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxD));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxE));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxF));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxG));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxH));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxI));
        allCheckbox.add((CheckBox) itemView.findViewById(R.id.checkBoxJ));
    }

    private void setTextToTextView() {
        questionTextTxt.setText(question.getQuestionText());
        for (int i = 0; i < allCheckbox.size(); i++) {
            allCheckbox.get(i).setText(question.getAllAnswer().get(i));
        }
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
     * Парсится строка с эталонными ответами, а затем вызывается метод для
     * выделения текста ответа жирным шрифтом и для смены цвета текста.
     */
    @Override
    public void showCorrectAnswers() {
        // Формат: A,B
        String[] correctAnswers = question.getCorrectAnswer().split(",");
        for (String answer : correctAnswers) {
            switch (answer) {
                case "A":
                    changeTypefaceAndColor(allCheckbox.get(0));
                    break;
                case "B":
                    changeTypefaceAndColor(allCheckbox.get(1));
                    break;
                case "C":
                    changeTypefaceAndColor(allCheckbox.get(2));
                    break;
                case "D":
                    changeTypefaceAndColor(allCheckbox.get(3));
                    break;
                case "E":
                    changeTypefaceAndColor(allCheckbox.get(4));
                    break;
                case "F":
                    changeTypefaceAndColor(allCheckbox.get(5));
                    break;
                case "G":
                    changeTypefaceAndColor(allCheckbox.get(6));
                    break;
                case "H":
                    changeTypefaceAndColor(allCheckbox.get(7));
                    break;
                case "I":
                    changeTypefaceAndColor(allCheckbox.get(8));
                    break;
                case "J":
                    changeTypefaceAndColor(allCheckbox.get(9));
                    break;
            }
        }
    }

    /**
     * Выделяет правильный вариант ответа. Для этого меняет
     * цвет текста на зелёный и делает шрифт жирным, помечает
     * чекбокс.
     *
     * @param checkBox Вариант ответа, написание текста которого нужно изменить.
     */
    private void changeTypefaceAndColor(CheckBox checkBox) {
        checkBox.setTypeface(null, Typeface.BOLD);
        checkBox.setTextColor(Color.parseColor("#228B22"));
    }

    @Override
    public void disableAnswers() {
        for (CheckBox chckBox : allCheckbox) {
            chckBox.setEnabled(false);
        }
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
     * Метод для отображения ответов пользователь с помощью выделения
     * checkbox, которые отметил пользователь в качестве ответа.
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
                    allCheckbox.get(0).setChecked(true);
                    break;
                case "B":
                    allCheckbox.get(1).setChecked(true);
                    break;
                case "C":
                    allCheckbox.get(2).setChecked(true);
                    break;
                case "D":
                    allCheckbox.get(3).setChecked(true);
                    break;
                case "E":
                    allCheckbox.get(4).setChecked(true);
                    break;
                case "F":
                    allCheckbox.get(5).setChecked(true);
                    break;
                case "G":
                    allCheckbox.get(6).setChecked(true);
                    break;
                case "H":
                    allCheckbox.get(7).setChecked(true);
                    break;
                case "I":
                    allCheckbox.get(8).setChecked(true);
                    break;
                case "J":
                    allCheckbox.get(9).setChecked(true);
                    break;
            }
        }
    }
}