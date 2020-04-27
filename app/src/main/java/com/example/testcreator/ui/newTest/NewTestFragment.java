package com.example.testcreator.ui.newTest;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.Common.Common;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.QuestionsCreatingActivity;
import com.example.testcreator.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class NewTestFragment extends Fragment implements FireBaseConnections {
    private final String TAG = "FAILURE NewTestFragment";

    private Button saveNameTestBtn;
    private EditText nameTestEdt;
    private ImageView imgViewLogo;
    private Uri imgUri;
    private String nameImage;
    private StringBuilder categoryName = new StringBuilder();
    private Spinner themesSpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewTestViewModel newTestViewModel = ViewModelProviders
                .of(this).get(NewTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_name_test, container, false);
        final TextView nameTestTxt = root.findViewById(R.id.nameTestTxt);
        newTestViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                nameTestTxt.setText(s);
            }
        });
        findElementsViewById(root);
        themesSpinner = root.findViewById(R.id.themesSpinner);

        addSpinnerAdapter();
        saveNameTestBtnOnClickListen();
        themesSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener(categoryName));
        imgViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        return root;
    }

    private void addSpinnerAdapter() {
        List<String> themesLstStr = new ArrayList<>();
        for (int i = 0; i < Common.categoryLst.size(); i++) {
            themesLstStr.add(Common.categoryLst.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, themesLstStr);
        themesSpinner.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            imgViewLogo.setImageURI(imgUri);
        }
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     *
     * @param root Представление для фрагмента пользовательского интерфейса.
     */
    private void findElementsViewById(View root) {
        nameTestEdt = root.findViewById(R.id.nameTestEdt);
        saveNameTestBtn = root.findViewById(R.id.saveNameTestBtn);
        imgViewLogo = root.findViewById(R.id.imgViewLogo);
    }

    private String getExtension(Uri imgUri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(imgUri));
    }

    /**
     * Обработчик события нажатие на кнопку для сохранения названия теста. Если
     * пользователь ввёл уникальное название теста, то добавляет название
     * в БД, иначе просит повторить ввод.
     */
    private void saveNameTestBtnOnClickListen() {
        saveNameTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если пользователь выбрал фотографию.
                if (imgUri != null) {
                    nameImage = System.currentTimeMillis() + "." + getExtension(imgUri);
                    OnlineDBHelper.getInstance(getContext()).uploadImage(nameImage, imgUri);
                }

                if (nameTestEdt.getText().toString().length() == 0) {
                    nameTestEdt.setError("Небходимо заполнить это поле");
                    nameTestEdt.requestFocus();
                } else if (Common.namesTestSet.contains(nameTestEdt.getText().toString())) {
                    nameTestEdt.setError("Тест с таким названием уже существует");
                    nameTestEdt.requestFocus();
                } else {
                    int categoryID = 1;
                    for (int i = 0; i < Common.categoryLst.size(); i++) {
                        if (Common.categoryLst.get(i).getName().equals(categoryName.toString())) {
                            categoryID = Common.categoryLst.get(i).getId();
                            break;
                        }
                    }
                    Intent newIntent = new Intent(getActivity(), QuestionsCreatingActivity.class);
                    newIntent.putExtra("nameTestEdt", nameTestEdt.getText().toString());
                    newIntent.putExtra("categoryID", categoryID);
                    newIntent.putExtra("nameImage", nameImage);
                    startActivity(newIntent);
                }
            }
        });
    }
}