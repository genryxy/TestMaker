package com.example.testcreator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testcreator.Adapter.AnswerViewListAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Enum.NumberAnswerEnum;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.AnswerView;
import com.example.testcreator.Model.QuestionModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersCreatingActivity extends AppCompatActivity implements FireBaseConnections {

    public static final String TAG = "AnswersCreatingActivity";

    private final String answerText = "Текст ответа";

    private Button addNewVariantBtn;
    private Button createNextQuestionBtn;
    private Button endCreatingTestBtn;
    private String typeAnswer;
    private String nameTest;
    private String nameImage;
    private int categoryID;
    private String questionText;
    private int questionNumber;
    private int answersNumber;
    private Context context = this;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Словарь, состоящий из пар (номер вопроса, вариант ответа).
    private Map<Integer, String> lettersMap = new HashMap<>();
    // Список с вариантами ответов на вопросы. Варианты ответов вводит пользователь.
    private List<AnswerView> lstAnswers;
    // Список с вариантами ответов на вопросы. Позволяет хранить коллекцию
    // в удобном формате в базе данных.
    private List<String> lstAnswersToDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_creating);
        ListView lstView = findViewById(R.id.answersLstView);
        findElementsViewById();
        getInfoFromPreviousIntent();

        // Создаём список с ответами.
        lstAnswers = new ArrayList<>();
        for (int i = 0; i < answersNumber; i++) {
            lstAnswers.add(new AnswerView(null, false));
        }
        // Заполняем словарь.
        for (int i = 0; i < QuestionModel.NUMBER_ANSWER; i++) {
            lettersMap.put(i, String.valueOf((char) ('A' + i)));
        }

        final AnswerViewListAdapter adapter = new AnswerViewListAdapter
                (this, R.layout.layout_creating_answer, lstAnswers);
        lstView.setAdapter(adapter);

        addNewVariantBtnClickListen(adapter);
        createNextQuestionBtnClickListen();
        endCreatingTestBtnClickListen();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание");
        builder.setMessage("Изменения не будут сохранены ");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //AnswersCreatingActivity.super.onBackPressed();
            }
        });
        builder.show();
    }

    /**
     * Создаёт экземпляр класса QuestionModel с заданными параметрами.
     *
     * @param rightAnsBuilder Правильный ответ в строковом представлении.
     * @return Созданный вопрос QuestionModel.
     */
    private QuestionModel createQuestion(StringBuilder rightAnsBuilder) {
        processLstAnswers();
        return new QuestionModel(questionText, null, lstAnswersToDatabase,
                rightAnsBuilder.toString(), false, categoryID, typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name())
                ? NumberAnswerEnum.OwnAnswer : NumberAnswerEnum.OneOrManyAnswers);
    }

    /**
     * Метод для обработки вариантов ответов.
     * Для тестовых вопрос каждый ответ должен начинаться с
     * заглавной буквы латинского алфавита, которая означает номер ответа.
     * Потом следует точка. Ещё в каждом вопросе должно быть 10 вариантов ответа
     * для правильного вывода возможных ответов на вопрос, поэтому дозаполняем
     * оставшиеся варианты ответов строкой "Z".
     * Для отображения ответов при заполнении используется класс AnswerView, но
     * в базе данных достаточно только формулировки ответа, поэтому переносим
     * все ответы в другой список, использующий встроенный класс String.
     * <p>
     * Если ответ подразумевает ввод ответа на клавиатуре, то оставляем
     * пустую коллекцию.
     */
    private void processLstAnswers() {
        lstAnswersToDatabase = new ArrayList<>();
        if (typeAnswer.equals(NumberAnswerEnum.OneOrManyAnswers.name())) {
            String tmp;
            int size = lstAnswers.size();
            for (int i = 0; i < size; i++) {
                tmp = lettersMap.get(i) + ". " + lstAnswers.get(i).getAnswerText();
                lstAnswersToDatabase.add(tmp);
            }

            // Теперь дозаполним оставшиеся ответы.
            for (int i = size; i < lettersMap.size(); i++) {
                lstAnswersToDatabase.add("Z");
            }
        }
    }

    /**
     * Метод для подсчёта количества правильных ответов и запоминания
     * номеров правильных ответов.
     *
     * @return Возвращается пара (количество верных ответов,
     * строка с номерами верных ответов)
     */
    private Pair<Integer, StringBuilder> saveRightAnswers() {
        int rightAnsNumber = 0;
        StringBuilder rightAnsBuilder = new StringBuilder();
        if (typeAnswer.equals(NumberAnswerEnum.OneOrManyAnswers.name())) {
            for (int i = 0; i < lstAnswers.size(); i++) {
                if (lstAnswers.get(i).getSelected()) {
                    rightAnsNumber++;
                    rightAnsBuilder.append(lettersMap.get(i));
                    rightAnsBuilder.append(",");
                }
            }
            // (Длина - 1), чтобы не было висячей запятой.
            if (rightAnsBuilder.length() > 0) {
                rightAnsBuilder.deleteCharAt(rightAnsBuilder.length() - 1);
            }
        } else {
            if (lstAnswers.size() > 0) {
                rightAnsBuilder.append(lstAnswers.get(0).getAnswerText());
            }
            if (rightAnsBuilder.toString().length() > 0
                    && !rightAnsBuilder.toString().toLowerCase().equals("текст ответа")) {
                rightAnsNumber++;
            }
        }
        return new Pair<>(rightAnsNumber, rightAnsBuilder);
    }

    /**
     * Метод для получения значений, переданных с предыдущей страницы.
     */
    private void getInfoFromPreviousIntent() {
        Intent prevIntent = getIntent();
        questionNumber = prevIntent.getIntExtra("questionNumber", 1);
        answersNumber = Integer.valueOf(prevIntent.getStringExtra("answersNumberEdt"));
        typeAnswer = prevIntent.getStringExtra("typeAnswer");
        questionText = prevIntent.getStringExtra("questionTextEdt");
        nameTest = prevIntent.getStringExtra("nameTestEdt");
        nameImage = prevIntent.getStringExtra("nameImage");
        categoryID = prevIntent.getIntExtra("categoryID", 1);
    }

    /**
     * Добавляет в адаптер новое поле для записи ответа. Добавляемый элемент
     * включает checkbox, textBox для ввода ответа, кнопку для удаления ответа.
     *
     * @param adapter Адаптер для вывода коллекции на экране.
     */
    private void addNewVariantBtnClickListen(final AnswerViewListAdapter adapter) {
        addNewVariantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name()) && lstAnswers.size() != 0) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Вы выбрали ответ в свободной форме!", Toast.LENGTH_SHORT).show();
                } else if (lstAnswers.size() >= QuestionModel.NUMBER_ANSWER) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Не может быть больше 10 ответов", Toast.LENGTH_SHORT).show();

                } else {
                    lstAnswers.add(new AnswerView(null, false));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Метод для сохранения созданных вопросов в БД. Вопросы добавляются к уже
     * существующим вопросам в выбранной категории, а
     * <p>
     * также создаётся новый тест с указанным названием и составленными вопросами.
     *
     * @param rightAns Пара вида (количество правильных ответов, правильные ответы).
     */
    private void saveQuestionToDB(final Pair<Integer, StringBuilder> rightAns) {
        final QuestionModel questionModel = createQuestion(rightAns.second);
        String nameImage;
        if (Common.imgQuestionUri != null) {
            nameImage = System.currentTimeMillis() + "." + Utils.getExtension(Common.imgQuestionUri, context);
            questionModel.setImageQuestion(true);
            questionModel.setQuestionImage(nameImage);
            OnlineDBHelper.getInstance(this).uploadImage(nameImage, Common.imgQuestionUri);
        }

        final DocumentReference docRef = db.collection("questions").document("questionsAll");
        OnlineDBHelper.getInstance(this).saveQuestionModelDB(docRef, questionModel, nameTest);
    }

    /**
     * Обработчик события для кнопки для завершения создания теста.
     * Если создаётся первый вопрос, то необходимо отметить хотя бы один
     * правильный ответ в вопросе.
     * Если пользователь нажал на кнопку "Завершить создание" и уже есть хотя
     * бы один созданный вопрос, то переходим на MainActivity.
     */
    private void endCreatingTestBtnClickListen() {
        endCreatingTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<Integer, StringBuilder> rightAns = saveRightAnswers();
                // Если создаётся не первый вопрос, то вызвать нажатие
                // на кнопку для создания следующего вопроса.
                if (questionNumber == 1 && rightAns.first == 0 && !typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name())) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Нужно отметить правильный ответ, чтобы добавить тест " +
                                    "из одного вопроса", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isFilledAns = rightAns.second.toString().length() == 0
                        || rightAns.second.toString().equals("null");

                if (questionNumber == 1 && isFilledAns && typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name())) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Нужно заполнить правильный ответ, чтобы добавить тест " +
                                    "из одного вопроса", Toast.LENGTH_LONG).show();
                    return;
                }

                if (typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name()) && isFilledAns) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Последний вопрос не был сохранён, так как для него не введен правильный ответ!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (rightAns.first == 0 && !typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name())) {
                    Toast.makeText(context, "Последний вопрос не был сохранён, так как для него не отмечены ответы!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                saveQuestionToDB(rightAns);
                OnlineDBHelper.getInstance(null).saveTestInfoDB(nameTest, nameImage, categoryID);
                Toast.makeText(AnswersCreatingActivity.this, "Тест создан", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnswersCreatingActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * Обработчик события для кнопки для завершения создания текущего вопроса.
     * Если пользователь нажал на кнопку "Перейти к следующему вопросу", то
     * проверяем что выбран хотя бы один ответ в вопросе и переходим на
     * QuestionsCreatingActivity для создания следующего вопроса.
     */
    private void createNextQuestionBtnClickListen() {
        createNextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pair<Integer, StringBuilder> rightAns = saveRightAnswers();
                if (rightAns.first == 0 && !typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name())) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Нужно отметить правильный ответ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (typeAnswer.equals(NumberAnswerEnum.OwnAnswer.name()) && (rightAns.second.toString().length() == 0
                        || rightAns.second.toString().equals("null"))) {
                    Toast.makeText(AnswersCreatingActivity.this,
                            "Нужно заполнить поле с ответом", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveQuestionToDB(rightAns);

                Intent newIntent = new Intent(AnswersCreatingActivity.this, QuestionsCreatingActivity.class);
                newIntent.putExtra("questionNumber", questionNumber + 1);
                newIntent.putExtra("nameTestEdt", nameTest);
                newIntent.putExtra("nameImage", nameImage);
                newIntent.putExtra("categoryID", categoryID);
                startActivity(newIntent);
            }
        });
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     */
    private void findElementsViewById() {
        addNewVariantBtn = findViewById(R.id.addNewVariantBtn);
        createNextQuestionBtn = findViewById(R.id.createNextQuestionBtn);
        endCreatingTestBtn = findViewById(R.id.endCreatingTestBtn);
    }
}