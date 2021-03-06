package com.example.testcreator.ui.newTest;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Класс для добавления конкретного действия, которое будет выполняться при нажатии на элемент
 * в Spinner, состоящем из названий категорий. В данном случае тема будет добавляться
 * в строку, содержащую название выбранной категории.
 */
public class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private StringBuilder themesName;

    public SpinnerOnItemSelectedListener(StringBuilder themesName) {
        this.themesName = themesName;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        themesName.delete(0, themesName.length());
        themesName.append(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
