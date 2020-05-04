package com.example.testcreator.Model;

import java.util.List;

/**
 * Класс-обёртка для хранения списка из экземпляров класса Category
 * в базе данных. Этот класс позволяет удобно сохранять информацию в
 * Cloud Firestore, а также получать при чтении.
 */
public class CategoryFirebase {
    private List<Category> categoryLst;

    public CategoryFirebase() {
    }

    public CategoryFirebase(List<Category> categoryList) {
        this.categoryLst = categoryList;
    }

    public List<Category> getCategoryList() {
        return categoryLst;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryLst = categoryList;
    }
}
