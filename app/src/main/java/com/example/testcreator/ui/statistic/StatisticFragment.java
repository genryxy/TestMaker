package com.example.testcreator.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.R;

public class StatisticFragment extends Fragment {

    private StatisticViewModel statisticViewModel;

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

        return root;
    }
}