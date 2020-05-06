package com.example.testcreator.ui.compare;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;

/**
 * Класс для добавления конкретного действия, которое будет выполняться при нажатии на элемент
 * в Spinner, состоящем из названий тестов. В данном случае будут загружаться результаты по тесту
 * с данным названием.
 */
public class NamesSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Context context;
    private RecyclerView resultAllRecycler;
    private AlertDialog dialog;

    public NamesSpinnerOnItemSelectedListener(Context context, RecyclerView resultAllRecycler, AlertDialog dialog) {
        this.context = context;
        this.resultAllRecycler = resultAllRecycler;
        this.dialog = dialog;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (Utils.hasConnection(context)) {
            String nameTest = parent.getItemAtPosition(position).toString();
            OnlineDBHelper.getInstance(context).getResultByNameTest(nameTest, resultAllRecycler, dialog);
        } else {
            Toast.makeText(context, "Нужно подключить интернет!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
