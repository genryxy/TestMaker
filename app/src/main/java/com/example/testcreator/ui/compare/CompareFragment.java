package com.example.testcreator.ui.compare;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.R;
import com.example.testcreator.ui.searchTest.SelectingTestAdapter;

import java.util.ArrayList;
import java.util.List;

public class CompareFragment extends Fragment {

    private CompareViewModel compareViewModel;
    private Spinner nameTestSpinner;
    private AlertDialog dialog;
    private List<String> nameTestLst;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        compareViewModel =
                ViewModelProviders.of(this).get(CompareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_compare, container, false);

        dialog = Utils.showLoadingDialog(getContext());
        addSpinnerAdapter();
        int spaceInPixel = 4;
        RecyclerView resultAllDBRecycler = root.findViewById(R.id.resultAllDBRecycler);
        resultAllDBRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        resultAllDBRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        nameTestSpinner = root.findViewById(R.id.nameTestSpinner);
        nameTestSpinner.setOnItemSelectedListener(new NamesSpinnerOnItemSelectedListener(
                getContext(), resultAllDBRecycler, dialog));

        return root;
    }

    private void addSpinnerAdapter() {
        nameTestLst = new ArrayList<>();
        OnlineDBHelper.getInstance(getContext())
                .getTestInfos(new TestInfoCallBack() {
                    @Override
                    public void setInfosToAdapter(List<TestInfo> testInfos) {
                        for (TestInfo testInfo : testInfos) {
                            nameTestLst.add(testInfo.getName());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                R.layout.layout_spinner_item, nameTestLst);
                        nameTestSpinner.setAdapter(adapter);
                    }
                });
    }
}