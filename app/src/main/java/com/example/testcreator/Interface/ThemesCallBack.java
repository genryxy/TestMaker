package com.example.testcreator.Interface;

import com.example.testcreator.Model.Category;

import java.util.List;

/**
 * Интерфейс для реализации метода обратного вызова для установки
 * значений коллекции, полученных из базы данных. Коллекция
 * состоит из категорий.
 */
public interface ThemesCallBack {
    void setThemes(List<Category> themesLst);
}
