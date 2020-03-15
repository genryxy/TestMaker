package com.example.testcreator.ui.searchTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Model.SelectingTestView;
import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

public class SelectingTestFragment extends Fragment {

    private SelectingTestViewModel selectingTestViewModel;
//    private ListView testsLst;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectingTestViewModel =
                ViewModelProviders.of(this).get(SelectingTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_selecting_test, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        ListView testsLstView = root.findViewById(R.id.selectingTestsRecView);
        // Создаём список с ответами.
        List<SelectingTestView> lstTests = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            lstTests.add(new SelectingTestView("nameTest" + i, "creator" + i, ""));

        RecyclerView testsRecycler = root.findViewById(R.id.selectingTestsRecycler);
        SelectingTestAdapter adapter = new SelectingTestAdapter(lstTests);
        testsRecycler.setAdapter(adapter);
        testsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }
}