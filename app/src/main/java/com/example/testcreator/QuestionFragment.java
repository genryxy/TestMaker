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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Enum.NumberAnswerEnum;
import com.example.testcreator.Interface.IQuestion;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
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
    private EditText inputAnswerTextEdt;
    private TextView rightAnswerTxt;
    private List<CheckBox> allCheckbox = new ArrayList<>();

    private QuestionModel question;
    private int questionIndex = -1;

    private boolean wasAnswered = false;

    public QuestionFragment() {
        // Для работы требуется пустой конструктор
    }

    public QuestionFragment(int questionIndex) {
        this.questionIndex = questionIndex;
        question = Common.questionLst.get(questionIndex);
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
            checkVisibilityAnswerFields();
            setOnCheckedChangeListenerToTextView();
        }
        return itemView;
    }

    /**
     * Метод, который делает невидимыми нижние checkbox, если вариантов ответов
     * меньше максимально возможного количества (10 ответов).
     */
    private void checkVisibilityAnswerFields() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            for (int i = 0; i < QuestionModel.NUMBER_ANSWER; i++) {
                allCheckbox.get(i).setVisibility(View.GONE);
            }
            inputAnswerTextEdt.setVisibility(View.VISIBLE);
            rightAnswerTxt.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < question.getAllAnswer().size(); i++) {
                if (question.getAllAnswer().get(i) == null
                        || question.getAllAnswer().get(i).equals("Z")) {
                    allCheckbox.get(i).setVisibility(View.GONE);
                }
            }
            inputAnswerTextEdt.setVisibility(View.GONE);
            rightAnswerTxt.setVisibility(View.GONE);
        }
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     *
     * @param itemView Представление для фрагмента пользовательского интерфейса.
     */
    private void findElementsViewById(View itemView) {
        allCheckbox.clear();
        questionTextTxt = itemView.findViewById(R.id.fragmentQuestionTextTxt);
        inputAnswerTextEdt = itemView.findViewById(R.id.inputAnswerTextEdt);
        rightAnswerTxt = itemView.findViewById(R.id.rightAnswerTxt);
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

    /**
     * Устанавливает формулировки вариантов ответов в соответствующие TextView. Значения
     * берутся из списка с формулировками ответов экземпляра класса QuestionModel.
     */
    private void setTextToTextView() {
        questionTextTxt.setText(question.getQuestionText());
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
            for (int i = 0; i < allCheckbox.size(); i++) {
                allCheckbox.get(i).setText(question.getAllAnswer().get(i));
            }
        }
        rightAnswerTxt.setText(question.getCorrectAnswer());
    }

    /**
     * Устанавливает обработчик событий для нажатия на checkbox. Заносит в коллекцию
     * выбранных, если элемент стал помечен. Иначе удаляет из коллекции соответствующий
     * экземпляр checkbox.
     */
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
    public CurrentQuestion getAndCheckSelectedAnswer() {
        // Вопрос может быть в трёх состояниях.
        // Правильный ответ, неправильный ответ, не отвечен.
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex, Common.AnswerType.NO_ANSWER);
        StringBuilder resStr = new StringBuilder();
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
            if (Common.selectedValues.size() >= 1) {
                // Выделяем в выбранных ответах первый символ (большую латинскую букву),
                // отвечающий за вариант ответа, и добавляем к итоговой строке.
                // arr[0] = A. Москва
                // arr[1] = B. Париж
                Object[] arrayAnswer = Common.selectedValues.toArray();
                for (int i = 0; i < arrayAnswer.length; i++) {
                    if (i < arrayAnswer.length - 1) {
                        resStr.append(new StringBuilder(((String) arrayAnswer[i])).substring(0, 1)).append(",");
                    } else {
                        resStr.append(new StringBuilder(((String) arrayAnswer[i])).substring(0, 1));
                    }
                }
            }
        } else {
            resStr.append(inputAnswerTextEdt.getText().toString().toLowerCase());
            if (resStr.toString().length() == 0) {
                resStr.append("текст ответа");
            }
        }
        if (resStr.toString().equals("текст ответа") && question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            return currentQuestion;
        }

        // Сравниваем правильные ответы с ответами пользователя.
        if (!TextUtils.isEmpty(resStr)) {
            String rightAnswer = question.getCorrectAnswer();
            if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
                rightAnswer = rightAnswer.toLowerCase();
            }
            if (resStr.toString().equals(rightAnswer)) {
                currentQuestion.setType(Common.AnswerType.RIGHT_ANSWER);
            } else {
                currentQuestion.setType(Common.AnswerType.WRONG_ANSWER);
            }
        }
        currentQuestion.setUserAnswer(resStr.toString());

        Common.selectedValues.clear();
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswers() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
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
        } else {
            rightAnswerTxt.setVisibility(View.VISIBLE);
            rightAnswerTxt.setTypeface(null, Typeface.BOLD);
            rightAnswerTxt.setTextColor(Color.parseColor("#228B22"));
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
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
            for (CheckBox chckBox : allCheckbox) {
                chckBox.setEnabled(false);
            }
        } else {
            inputAnswerTextEdt.setEnabled(false);
            inputAnswerTextEdt.requestFocus();
        }
    }

    @Override
    public void resetQuestion() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
            for (CheckBox checkbox : allCheckbox) {
                checkbox.setEnabled(true);
            }

            // Убираем все пометки с выбранных вариантов.
            for (CheckBox checkbox : allCheckbox) {
                checkbox.setChecked(false);
            }

            for (CheckBox checkbox : allCheckbox) {
                checkbox.setTypeface(null, Typeface.NORMAL);
            }

            for (CheckBox checkbox : allCheckbox) {
                checkbox.setTextColor(Color.BLACK);
            }
        } else {
            inputAnswerTextEdt.setText("текст ответа");
        }
    }

    /**
     * Метод для отображения ответов пользователь с помощью выделения
     * checkbox, которые отметил пользователь в качестве ответа.
     *
     * @param userAnswer Ответы пользователя.
     */
    void setUserAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.length() == 0
                || userAnswer.toLowerCase().equals("текст ответа")) {
            return;
        }

        if (question.getTypeAnswer().equals(NumberAnswerEnum.OneOrManyAnswers)) {
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
        } else {
            inputAnswerTextEdt.setText(userAnswer);
        }
    }
}