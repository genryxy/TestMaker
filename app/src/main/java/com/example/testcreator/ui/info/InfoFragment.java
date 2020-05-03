package com.example.testcreator.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.R;

public class InfoFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_info, container, false);
        final TextView devTxt = root.findViewById(R.id.developerTxt);
        String str = "О разработчике:\n" +
                "Краснов Александр, студент НИУ ВШЭ группы БПИ185";
        devTxt.setText(str);
        ImageView devImg = root.findViewById(R.id.developerImg);
        devImg.setImageResource(R.drawable.cat);
        return root;
    }
}