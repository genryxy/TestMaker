package com.example.testcreator.ui.searchTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

public class SelectingTestFragment extends Fragment
{

    private SelectingTestViewModel selectingTestViewModel;
//    private ListView testsLst;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
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

        ListView testsLst = root.findViewById(R.id.selectingTestsLstView);
        // Создаём список с ответами.
        final List<SelectingTestView> lstAnswers = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            lstAnswers.add(new SelectingTestView("nameTest" + i, "creator" + i, ""));

        final SelectingTestViewListAdapter adapter = new SelectingTestViewListAdapter
                (getContext(), R.layout.adapter_view_selecting_layout, lstAnswers);
        testsLst.setAdapter(adapter);

//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        testsLst.setMinimumWidth(metrics.widthPixels);


        testsLst.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getContext(), Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}