package com.example.testcreator.ui.searchTest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Adapter.SelectingTestAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-фрагмент для вывода названий существующих тестов.
 * Позволяет по нажатию на определённое название перейти к прохождению
 * теста, состоящего из вопросов из выбранного теста.
 */
public class SelectingTestFragment extends Fragment {

    private AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selecting_test, container, false);
        // Очищаем установленные ранее значения.
        Common.isIsShuffleAnswerMode = false;
        Common.isShuffleQuestionMode = false;

        if (Utils.hasConnection(getContext())) {
            dialog = Utils.showLoadingDialog(getContext());
            final RecyclerView testsRecycler = root.findViewById(R.id.selectingTestsRecycler);
            testsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            OnlineDBHelper.getInstance(getContext())
                    .getTestInfos(new TestInfoCallBack() {
                        @Override
                        public void setInfosToAdapter(List<TestInfo> testInfos) {
                            if (testInfos == null) {
                                testInfos = new ArrayList<>();
                                Toast.makeText(getContext(), "Нет тестов в базе!", Toast.LENGTH_LONG).show();
                            }
                            SelectingTestAdapter adapter = new SelectingTestAdapter(testInfos);
                            testsRecycler.setAdapter(adapter);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Нужно подключить интернет!", Toast.LENGTH_LONG).show();
        }

        return root;
    }
}