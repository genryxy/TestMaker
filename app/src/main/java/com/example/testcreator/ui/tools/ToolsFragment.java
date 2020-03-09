package com.example.testcreator.ui.tools;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Adapter.CategoryAdapter;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.R;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);

        RecyclerView categoryRecycler = root.findViewById(R.id.categoryRecycler);
        categoryRecycler.setHasFixedSize(true);

//        // Get screen height
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        // Max size of item in category
//        int height = displayMetrics.heightPixels / 8;

        CategoryAdapter adapter = new CategoryAdapter(getActivity(),
                DBHelper.getInstance(getActivity()).getAllCategories());
        int spaceInPixel = 4;
        categoryRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }
}