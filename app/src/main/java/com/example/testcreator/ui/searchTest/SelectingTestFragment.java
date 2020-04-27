package com.example.testcreator.ui.searchTest;

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

import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Model.SelectingTestView;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class SelectingTestFragment extends Fragment {

    private SelectingTestViewModel selectingTestViewModel;
    private AlertDialog dialog;

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

        showLoadingDialog();
        final RecyclerView testsRecycler = root.findViewById(R.id.selectingTestsRecycler);
        testsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        OnlineDBHelper.getInstance(getContext())
                .getTestInfos(new TestInfoCallBack() {
                    @Override
                    public void setInfosToAdapter(List<TestInfo> testInfos) {
                        SelectingTestAdapter adapter = new SelectingTestAdapter(testInfos);
                        testsRecycler.setAdapter(adapter);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        return root;
    }

    private void showLoadingDialog() {
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("loading ...")
                .setCancelable(false)
                .build();

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}