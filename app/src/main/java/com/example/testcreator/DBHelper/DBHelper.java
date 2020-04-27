package com.example.testcreator.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testcreator.Enum.NumberAnswerEnum;
import com.example.testcreator.Model.Category;
import com.example.testcreator.Model.QuestionModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteAssetHelper {
    private static final String DB_NAME = "Quiz.db";
    private static final int DB_VER = 1;

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    /**
     * Возвращает все категории из локальной БД. При этом в некоторых
     * категориях может не быть вопросов.
     */
    public List<Category> getAllCategories() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Category;", null);
        List<Category> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Category category = new Category(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("Name")),
                        cursor.getString(cursor.getColumnIndex("Image")));
                categories.add(category);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return categories;
    }

    /**
     * Get 30 questions from DB by category
     */
    public List<QuestionModel> getQuestionsByCategory(int categoryID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM Question WHERE CategoryId = %d ORDER BY RANDOM() LIMIT 30", categoryID), null);
        List<QuestionModel> questions = new ArrayList<>();
        String answer;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                List<String> allAnswer = new ArrayList<>(QuestionModel.NUMBER_ANSWER);
                for (int i = 0; i < QuestionModel.NUMBER_ANSWER; i++) {
                    answer = cursor.getString(cursor.getColumnIndex("Answer" + (char) ('A' + i)));
                    allAnswer.add(answer != null ? answer : "Z");
                }
                String ans = cursor.getString(cursor.getColumnIndex("TypeAnswer"));

                QuestionModel question = new QuestionModel(
                        cursor.getString(cursor.getColumnIndex("QuestionText")),
                        cursor.getString(cursor.getColumnIndex("QuestionImage")),
                        allAnswer,
                        cursor.getString(cursor.getColumnIndex("CorrectAnswer")),
                        cursor.getInt(cursor.getColumnIndex("IsImageQuestion")) == 1,
                        cursor.getInt(cursor.getColumnIndex("CategoryID")),
                        ans.equals(NumberAnswerEnum.OwnAnswer.name())
                                ? NumberAnswerEnum.OwnAnswer : NumberAnswerEnum.OneOrManyAnswers);
                questions.add(question);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return questions;
    }
}
