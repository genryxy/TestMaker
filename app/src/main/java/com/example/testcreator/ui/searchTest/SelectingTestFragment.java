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

import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.NameTestCallBack;
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
        final List<SelectingTestView> lstTests = new ArrayList<>();
        final RecyclerView testsRecycler = root.findViewById(R.id.selectingTestsRecycler);
//        for (int i = 0; i < 4; i++)
//            lstTests.add(new SelectingTestView("nameTest" + i, "creator" + i, ""));
        OnlineDBHelper.getInstance(getContext())
                .getNamesTest(new NameTestCallBack() {
                    @Override
                    public void setNamesToAdapter(List<String> namesTestLst, List<String> categoriesLst) {
                        for (int i = 0; i < namesTestLst.size(); i++) {
                            lstTests.add(new SelectingTestView(namesTestLst.get(i), categoriesLst.get(i), ""));
                        }
                        SelectingTestAdapter adapter = new SelectingTestAdapter(lstTests);
                        testsRecycler.setAdapter(adapter);
                    }
                });
        testsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }
}