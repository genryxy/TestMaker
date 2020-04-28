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

import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatisticFragment extends Fragment implements FireBaseConnections {

    private StatisticViewModel statisticViewModel;
    private AlertDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticViewModel =
                ViewModelProviders.of(this).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        statisticViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        dialog = Utils.showLoadingDialog(getContext());
        int spaceInPixel = 4;
        final RecyclerView resultDBRecycler = root.findViewById(R.id.resultDatabaseRecycler);
        resultDBRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        resultDBRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        OnlineDBHelper.getInstance(getContext()).getResultByUserKey(resultDBRecycler, dialog);
        return root;
    }
}