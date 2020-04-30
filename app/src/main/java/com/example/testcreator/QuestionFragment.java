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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcreator.Common.Common;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Enum.NumberAnswerEnum;
import com.example.testcreator.Interface.IQuestion;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
    private RadioGroup radioGroup;
    private List<CheckBox> allCheckbox = new ArrayList<>();
    private List<RadioButton> allRadioBtn = new ArrayList<>();

    private QuestionModel question;
    private int questionIndex = -1;

    private boolean wasAnswered = false;

    public List<CheckBox> getAllCheckbox() {
        return allCheckbox;
    }

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
                final ImageView imgQuestion = itemView.findViewById(R.id.imgQuestion);
                // 17, так как при сохранении в FireBase длина имени файла == 17
                if (question.getQuestionImage().length() != 17) {
                    Picasso.get().load(question.getQuestionImage()).into(imgQuestion, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getContext(), "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    OnlineDBHelper.getInstance(getContext()).getImageByName(question.getQuestionImage(), imgQuestion, progressBar);
                }
            } else {
                layoutImage.setVisibility(View.GONE);
            }

            findElementsViewById(itemView);
            setTextToTextView();
            checkVisibilityAnswerFields();
            setOnCheckedChangeListenerToCheckbox();
            setOnCheckedChangeListenerToRadioGroup();
        }
        return itemView;
    }

    /**
     * Метод, который делает невидимыми нижние checkbox, если вариантов ответов
     * меньше максимально возможного количества (10 ответов). А также скрывает
     * элементы, которые необходимы для ответов на другие типы вопросов.
     */
    private void checkVisibilityAnswerFields() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            for (int i = 0; i < QuestionModel.NUMBER_ANSWER; i++) {
                allCheckbox.get(i).setVisibility(View.GONE);
            }
            inputAnswerTextEdt.setVisibility(View.VISIBLE);
            rightAnswerTxt.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            radioGroup.setVisibility(View.VISIBLE);
            for (int i = 0; i < question.getAllAnswer().size(); i++) {
                if (question.getAllAnswer().get(i) == null) {
                    allRadioBtn.get(i).setVisibility(View.GONE);
                }
            }

            for (int i = 0; i < QuestionModel.NUMBER_ANSWER; i++) {
                allCheckbox.get(i).setVisibility(View.GONE);
            }
            inputAnswerTextEdt.setVisibility(View.GONE);
            rightAnswerTxt.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < question.getAllAnswer().size(); i++) {
                if (question.getAllAnswer().get(i) == null) {
                    allCheckbox.get(i).setVisibility(View.GONE);
                }
            }
            inputAnswerTextEdt.setVisibility(View.GONE);
            rightAnswerTxt.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
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
        radioGroup = itemView.findViewById(R.id.radioGroup);
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

        allRadioBtn.clear();
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioA));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioB));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioC));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioD));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioE));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioF));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioG));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioH));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioI));
        allRadioBtn.add((RadioButton) itemView.findViewById(R.id.radioJ));
    }

    /**
     * Устанавливает формулировки вариантов ответов в соответствующие TextView. Значения
     * берутся из списка с формулировками ответов экземпляра класса QuestionModel.
     */
    private void setTextToTextView() {
        questionTextTxt.setText(question.getQuestionText());
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
            List<String> filledAnswers = getAndShuffleAnswer();
            for (int i = 0; i < filledAnswers.size(); i++) {
                allCheckbox.get(i).setText(filledAnswers.get(i));
            }
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            List<String> filledAnswers = getAndShuffleAnswer();
            for (int i = 0; i < filledAnswers.size(); i++) {
                allRadioBtn.get(i).setText(filledAnswers.get(i));
            }
        }
        rightAnswerTxt.setText(question.getCorrectAnswer());
    }

    private List<String> getAndShuffleAnswer() {
        List<String> filledAnswers = new ArrayList<>();
        for (String ans : question.getAllAnswer()) {
            if (ans != null)
                filledAnswers.add(ans);
        }
        if (Common.isIsShuffleAnswerMode) {
            List<String> answerLetters = new ArrayList<>();
            for (int i = 0; i < filledAnswers.size(); i++) {
                answerLetters.add(String.valueOf((char) ('A' + i)));
            }
            Collections.shuffle(answerLetters);
            StringBuilder newAnsw = new StringBuilder();
            for (int i = 0; i < filledAnswers.size(); i++) {
                String currAns = filledAnswers.get(i);
                if (!String.valueOf(currAns.charAt(0)).equals(answerLetters.get(i))) {
                    if (question.getCorrectAnswer().contains(String.valueOf(currAns.charAt(0)))) {
                        newAnsw.append(answerLetters.get(i)).append(",");
                    }
                    String newStr = answerLetters.get(i) + currAns.substring(1);
                    filledAnswers.set(i, newStr);
                } else {
                    if (question.getCorrectAnswer().contains(String.valueOf(currAns.charAt(0)))) {
                        newAnsw.append(answerLetters.get(i)).append(",");
                    }
                }
            }
            question.setCorrectAnswer(newAnsw.substring(0, newAnsw.length() - 1));
            Collections.sort(filledAnswers);
        }
        return filledAnswers;
    }

    /**
     * Устанавливает обработчик событий для нажатия на checkbox. Заносит в коллекцию
     * выбранных, если элемент стал помечен. Иначе удаляет из коллекции соответствующий
     * экземпляр checkbox.
     */
    private void setOnCheckedChangeListenerToCheckbox() {
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

    private void setOnCheckedChangeListenerToRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View checkedBtn = radioGroup.findViewById(checkedId);
                for (RadioButton btn : allRadioBtn) {
                    if (btn.getId() == checkedBtn.getId()) {
                        Common.selectedValues.clear();
                        Common.selectedValues.add(btn.getText().toString());
                    }
                }
            }
        });

//            // Add logic here
//
//                switch(index)
//
//            {
//                case 0: // first button
//
//                    Toast.makeText(getApplicationContext(), "Selected button number " + index, 500).show();
//                    break;
//                case 1: // secondbutton
//
//                    Toast.makeText(getApplicationContext(), "Selected button number " + index, 500).show();
//                    break;
//            }
//        }
//        for (final RadioButton btn : allRadioBtn) {
//            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (!wasAnswered) {
//                        if (isChecked) {
//                            Common.selectedValues.add(btn.getText().toString());
//                        } else {
//                            Common.selectedValues.remove(btn.getText().toString());
//                        }
//                    }
//                }
//            });
//        }
    }

    @Override
    public CurrentQuestion getAndCheckSelectedAnswer() {
        // Вопрос может быть в трёх состояниях.
        // Правильный ответ, неправильный ответ, не отвечен.
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex, Common.AnswerType.NO_ANSWER);
        StringBuilder resStr = new StringBuilder();
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)
                || question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
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
            resStr.append(inputAnswerTextEdt.getText().toString().toLowerCase().trim());
            if (resStr.toString().length() == 0) {
                resStr.append("текст ответа");
            }
        }
        if (resStr.toString().equals("текст ответа") && question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            return currentQuestion;
        }

        // Сравниваем правильные ответы с ответами пользователя.
        if (!TextUtils.isEmpty(resStr)) {
            String rightAnswer = question.getCorrectAnswer().toLowerCase();
            // Если ответ в свободной форме, то заносим значение в правильный ответ.
            if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
                rightAnswer = rightAnswer.toLowerCase().trim();
            }
            if (resStr.toString().toLowerCase().trim().equals(rightAnswer)) {
                currentQuestion.setType(Common.AnswerType.RIGHT_ANSWER);
            } else {
                currentQuestion.setType(Common.AnswerType.WRONG_ANSWER);
            }
        }
        currentQuestion.setUserAnswer(inputAnswerTextEdt.getText().toString());

        Common.selectedValues.clear();
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswers() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
            // Формат: A,B
            String[] correctAnswers = question.getCorrectAnswer().split(",");
            for (String answer : correctAnswers) {
                switch (answer) {
                    case "A":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(0));
                        break;
                    case "B":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(1));
                        break;
                    case "C":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(2));
                        break;
                    case "D":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(3));
                        break;
                    case "E":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(4));
                        break;
                    case "F":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(5));
                        break;
                    case "G":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(6));
                        break;
                    case "H":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(7));
                        break;
                    case "I":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(8));
                        break;
                    case "J":
                        changeTypefaceAndColorCheckBox(allCheckbox.get(9));
                        break;
                }
            }
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            rightAnswerTxt.setVisibility(View.VISIBLE);
            rightAnswerTxt.setTypeface(null, Typeface.BOLD);
            rightAnswerTxt.setTextColor(Color.parseColor("#228B22"));
        } else {
            // Формат: A
            String answer = question.getCorrectAnswer();
            switch (answer) {
                case "A":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(0));
                    break;
                case "B":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(1));
                    break;
                case "C":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(2));
                    break;
                case "D":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(3));
                    break;
                case "E":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(4));
                    break;
                case "F":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(5));
                    break;
                case "G":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(6));
                    break;
                case "H":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(7));
                    break;
                case "I":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(8));
                    break;
                case "J":
                    changeTypefaceAndColorRadioBtn(allRadioBtn.get(9));
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
    private void changeTypefaceAndColorCheckBox(CheckBox checkBox) {
        checkBox.setTypeface(null, Typeface.BOLD);
        checkBox.setTextColor(Color.parseColor("#228B22"));
    }

    private void changeTypefaceAndColorRadioBtn(RadioButton btn) {
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextColor(Color.parseColor("#228B22"));
    }

    @Override
    public void disableAnswers() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
            for (CheckBox chckBox : allCheckbox) {
                chckBox.setEnabled(false);
            }
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            for (RadioButton btn : allRadioBtn) {
                btn.setEnabled(false);
            }
        } else {
            inputAnswerTextEdt.setEnabled(false);
            inputAnswerTextEdt.requestFocus();
        }
    }

    @Override
    public void resetQuestion() {
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
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
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            for (RadioButton btn : allRadioBtn) {
                btn.setEnabled(true);
            }

            radioGroup.check(R.id.radioA);

            for (RadioButton btn : allRadioBtn) {
                btn.setTypeface(null, Typeface.NORMAL);
            }

            for (RadioButton btn : allRadioBtn) {
                btn.setTextColor(Color.BLACK);
            }
        } else {
            inputAnswerTextEdt.setText("");
            inputAnswerTextEdt.setEnabled(true);
            rightAnswerTxt.setVisibility(View.GONE);
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

        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
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
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            switch (userAnswer) {
                case "A":
                    allRadioBtn.get(0).setChecked(true);
                    break;
                case "B":
                    allRadioBtn.get(1).setChecked(true);
                    break;
                case "C":
                    allRadioBtn.get(2).setChecked(true);
                    break;
                case "D":
                    allRadioBtn.get(3).setChecked(true);
                    break;
                case "E":
                    allRadioBtn.get(4).setChecked(true);
                    break;
                case "F":
                    allRadioBtn.get(5).setChecked(true);
                    break;
                case "G":
                    allRadioBtn.get(6).setChecked(true);
                    break;
                case "H":
                    allRadioBtn.get(7).setChecked(true);
                    break;
                case "I":
                    allRadioBtn.get(8).setChecked(true);
                    break;
                case "J":
                    allRadioBtn.get(9).setChecked(true);
                    break;
            }
        } else {
            inputAnswerTextEdt.setText(userAnswer);
        }
    }
}