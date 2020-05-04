package com.example.testcreator.ui.compare;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.TestInfoCallBack;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-фрагмент для вывода информации о результатах прохождения определённого теста
 * всеми пользователями. Название теста можно выбирать при помощи элемента Spinner.
 */
public class CompareFragment extends Fragment {

    private Spinner nameTestSpinner;
    private List<String> nameTestLst;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compare, container, false);

        AlertDialog dialog = Utils.showLoadingDialog(getContext());
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