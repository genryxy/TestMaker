package com.example.testcreator.Model;

import java.util.List;

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
