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
import com.example.testcreator.MyEnum.AnswerTypeEnum;
import com.example.testcreator.MyEnum.NumberAnswerEnum;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.Model.QuestionModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс-фрагмент для представления вопроса во ViewPager. Отвечает за то
 * нужно ли выводить правильные ответы, показывать ответы пользователя,
 * сделать неактивными кнопки, перемешать ответы перед тем, как вывести.
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private TextView questionTextTxt;
    private TextView fragmentPointTxt;
    private FrameLayout layoutImage;
    private ProgressBar progressBar;
    private EditText inputAnswerTextEdt;
    private TextView rightAnswerTxt;
    private RadioGroup radioGroup;
    private List<CheckBox> allCheckbox = new ArrayList<>();
    private List<RadioButton> allRadioBtn = new ArrayList<>();
    private Map<String, String> dictTransitionAns = new HashMap<>();

    private QuestionModel question;
    private int questionIndex = -1;

    private boolean wasAnswered = false;
    // Чтобы знать нужно ли перемешивать ответы при открытии страницы.
    private boolean isFirst = true;

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
     * Метод, который делает невидимыми нижние, если вариантов ответов
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
        fragmentPointTxt = itemView.findViewById(R.id.fragmentPointTxt);
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
        String weightTask = "Вопрос весит " + question.getQuestionPoint() + " балл(-а/-ов)";
        fragmentPointTxt.setText(weightTask);
        if (question.getTypeAnswer().equals(NumberAnswerEnum.ManyAnswers)) {
            List<String> filledAnswers = getAndShuffleAnswer();
            for (int i = 0; i < filledAnswers.size(); i++) {
                String str = filledAnswers.get(i);
                allCheckbox.get(i).setText(str != null ? str : "");
            }
        } else if (question.getTypeAnswer().equals(NumberAnswerEnum.OneAnswer)) {
            List<String> filledAnswers = getAndShuffleAnswer();
            for (int i = 0; i < filledAnswers.size(); i++) {
                String str = filledAnswers.get(i);
                allRadioBtn.get(i).setText(str != null ? str : "");
            }
        }
        rightAnswerTxt.setText(question.getCorrectAnswer());
    }

    /**
     * Метод для перемешивания вариантов ответов. В данном методе подменяется
     * правильный ответ на вопрос с учётом перемешанных вариантов ответов.
     * Чтобы можно было вернуть обратно, создаётся словарь переходов (замен).
     *
     * @return Список, содержащий ответы в перемешанном порядке.
     */
    private List<String> getAndShuffleAnswer() {
        List<String> filledAnswers = new ArrayList<>();
        for (String ans : question.getAllAnswer()) {
            if (ans != null)
                filledAnswers.add(ans);
        }
        if (Common.isIsShuffleAnswerMode && isFirst) {
            isFirst = false;
            List<String> answerLetters = new ArrayList<>();
            for (int i = 0; i < filledAnswers.size(); i++) {
                answerLetters.add(String.valueOf((char) ('A' + i)));
            }
            Collections.shuffle(answerLetters);
            // Нужно подогнать ответы исходной коллекции к ответам коллекции
            // с перемешанными вопросами.
            StringBuilder newAnsw = new StringBuilder();
            for (int i = 0; i < filledAnswers.size(); i++) {
                String currAns = filledAnswers.get(i);
                // Если буква, отвечающая за вариант ответа, не совпадает с буквой
                // на текущей позиции из коллекции с перемешанными буквами
                if (!String.valueOf(currAns.charAt(0)).equals(answerLetters.get(i))) {
                    // Добавляем в словарь, чтобы потом правильно показывать ответы пользователя.
                    dictTransitionAns.put(answerLetters.get(i), String.valueOf(currAns.charAt(0)));
                    // Если этот ответ является правильным, то заносим его в новый ответ.
                    if (question.getCorrectAnswer().contains(String.valueOf(currAns.charAt(0)))) {
                        newAnsw.append(answerLetters.get(i)).append(",");
                    }
                    String newStr = answerLetters.get(i) + currAns.substring(1);
                    filledAnswers.set(i, newStr);
                } else {
                    // Если совпадает с перемешанным и содержится в ответе, то просто переносим
                    // в новый ответ.
                    if (question.getCorrectAnswer().contains(String.valueOf(currAns.charAt(0)))) {
                        newAnsw.append(answerLetters.get(i)).append(",");
                    }
                }
            }
            // Нужно отсортировать правильные ответы, потому что сейчас могут быть не по алфавиту.
            String[] letters = newAnsw.toString().split(",");
            Arrays.sort(letters);
            newAnsw.delete(0, newAnsw.length());
            for (String symb : letters) {
                newAnsw.append(symb).append(",");
            }
            // (length - 1), чтобы не было висячей запятой
            question.setCorrectAnswer(newAnsw.substring(0, newAnsw.length() - 1));
            Collections.sort(filledAnswers);
            for (int i = 0; i < filledAnswers.size(); i++) {
                question.getAllAnswer().set(i, filledAnswers.get(i));
            }
        }
        return filledAnswers;
    }

    /**
     * Устанавливает обработчик событий для нажатия на checkbox. Заносит в коллекцию
     * выбранных ответов, если элемент стал помечен. Иначе удаляет из коллекции
     * соответствующий экземпляр checkbox.
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

    /**
     * Устанавливает обработчик событий для нажатия на кнопки из RadioGroup. Заносит в
     * коллекцию выбранных ответов, если элемент стал помечен. Иначе удаляет из коллекции
     * соответствующий экземпляр radioButton.
     */
    private void setOnCheckedChangeListenerToRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!wasAnswered) {
                    View checkedBtn = radioGroup.findViewById(checkedId);
                    for (RadioButton btn : allRadioBtn) {
                        if (btn.getId() == checkedBtn.getId()) {
                            Common.selectedValues.clear();
                            Common.selectedValues.add(btn.getText().toString());
                        }
                    }
                }
            }
        });
    }

    /**
     * Получает выбранные пользователем ответы. Сверяет полученные ответы с
     * эталонным. Затем возвращает экземпляр класса CurrentQuestion в
     * зависимости от правильности ответов пользователя.
     *
     * @return Возвращается экземпляр класса, который позволяет понять
     * правильно/неправильно ответил пользователь или вообще
     * не отвечал на данный вопрос.
     */
    public CurrentQuestion getAndCheckSelectedAnswer() {
        // Вопрос может быть в трёх состояниях.
        // Правильный ответ, неправильный ответ, не отвечен.
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex, AnswerTypeEnum.NO_ANSWER);
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
            if (resStr.toString().length() == 0 || resStr.toString().equals("null")) {
                resStr.append("текст ответа");
            }
        }
        if (resStr.toString().equals("текст ответа") && question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
            return currentQuestion;
        }

        // Сравниваем правильные ответы с ответами пользователя.
        if (!TextUtils.isEmpty(resStr)) {
            String rightAnswer = question.getCorrectAnswer();
            // Если ответ в свободной форме, то заносим значение в правильный ответ.
            if (question.getTypeAnswer().equals(NumberAnswerEnum.OwnAnswer)) {
                // Не учитываем регистр и пробелы между словами.
                rightAnswer = rightAnswer.toLowerCase().trim();
                rightAnswer = rightAnswer.replace(" ", "");
                resStr = new StringBuilder(resStr.toString().replace(" ", ""));
                currentQuestion.setUserAnswer(inputAnswerTextEdt.getText().toString());
            } else {
                // Устанавливаем словарь переходов одних ответов в другие.
                currentQuestion.setDictTransitionAns(dictTransitionAns);
                currentQuestion.setUserAnswer(resStr.toString());
            }
            if (resStr.toString().toLowerCase().trim().equals(rightAnswer.toLowerCase())) {
                currentQuestion.setType(AnswerTypeEnum.RIGHT_ANSWER);
            } else {
                currentQuestion.setType(AnswerTypeEnum.WRONG_ANSWER);
            }
        }

        Common.selectedValues.clear();
        return currentQuestion;
    }

    /**
     * Метод для вывода правильных ответов.
     * Парсится строка с эталонными ответами, а затем вызывается метод для
     * выделения текста ответа жирным шрифтом и для смены цвета текста.
     */
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
     * Метод для выделения правильного варианта ответа. Для этого меняет
     * цвет текста на зелёный и делает шрифт жирным, помечает
     * чекбокс.
     *
     * @param checkBox Вариант ответа, написание текста которого нужно изменить.
     */
    private void changeTypefaceAndColorCheckBox(CheckBox checkBox) {
        checkBox.setTypeface(null, Typeface.BOLD);
        checkBox.setTextColor(Color.parseColor("#228B22"));
    }

    /**
     * Метод для выделения правильного варианта ответа. Для этого меняет
     * цвет текста на зелёный и делает шрифт жирным, помечает radioButton.
     *
     * @param btn Кнопка с вариантом ответа, написание текста которого нужно изменить.
     */
    private void changeTypefaceAndColorRadioBtn(RadioButton btn) {
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextColor(Color.parseColor("#228B22"));
    }

    /**
     * Делает варианты ответов недоступными для выбора.
     */
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

    /**
     * Сбрасывает все значения (возвращает в начальное состояние).
     */
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
     * Метод для отображения ответов пользователь с помощью выделения checkbox
     * либо radioButton, которые отметил пользователь в качестве ответа.
     * В случае ответа в свободной форме отображается введённый пользователем текст.
     *
     * @param userAnswer Ответы пользователя.
     */
    public void setUserAnswer(String userAnswer) {
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