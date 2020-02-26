package com.example.testcreator.Interface;

import com.example.testcreator.Model.CurrentQuestion;

public interface IQuestion {
    /**
     * Получает выбранные пользователем ответы.
     * @return
     */
    CurrentQuestion getSelectedAnswer();

    /**
     * Выделяет правильные ответы жирным шрифтом.
     */
    void showCorrectAnswers();

    /**
     * Делает ответы недоступными для выбора.
     */
    void disableAnswers();

    /**
     * Сбрасывает все значения (вовзращает в начальное состояние).
     */
    void resetQuestion();
}
