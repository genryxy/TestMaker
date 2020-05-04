package com.example.testcreator.ui.statistic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Класс-фрагмент для вывода информации обо все результатах прохождения авторизованным
 * пользователем. По нажатию на конкретный результат можно посмотреть ответы, отмеченные
 * пользователем, а также эталонные ответы.
 */
public class StatisticFragment extends Fragment implements FireBaseConnections {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        // Очищаем установленные ранее значения.
        Common.isIsShuffleAnswerMode = false;
        Common.isShuffleQuestionMode = false;

        AlertDialog dialog = Utils.showLoadingDialog(getContext());
        int spaceInPixel = 4;
        final RecyclerView resultDBRecycler = root.findViewById(R.id.resultDatabaseRecycler);
        resultDBRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        resultDBRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        OnlineDBHelper.getInstance(getContext()).getResultByUserKey(resultDBRecycler, dialog);

        return root;
    }
}