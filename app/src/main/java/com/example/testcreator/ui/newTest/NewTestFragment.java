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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.Category;
import com.example.testcreator.QuestionsCreatingActivity;
import com.example.testcreator.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class NewTestFragment extends Fragment implements FireBaseConnections {
    private final String TAG = "FAILURE NewTestFragment";

    private Button saveNameTestBtn;
    private Button inputNewCategoryBtn;
    private EditText nameTestEdt;
    private ImageView imgViewLogo;
    private Uri imgUri;
    private String nameImage;
    private StringBuilder categoryName = new StringBuilder();
    private Spinner themesSpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_name_test, container, false);
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
        inputCategoryBtnOnClickListen();
        return root;
    }

    private void addSpinnerAdapter() {
        List<String> themesLstStr = new ArrayList<>();
        for (int i = 0; i < Common.categoryLst.size(); i++) {
            themesLstStr.add(Common.categoryLst.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.layout_spinner_item, themesLstStr);
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
        inputNewCategoryBtn = root.findViewById(R.id.inputNewCategoryBtn);
        imgViewLogo = root.findViewById(R.id.imgViewLogo);
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
                    nameImage = System.currentTimeMillis() + "." + Utils.getExtension(imgUri, getContext());
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

    private void inputCategoryBtnOnClickListen() {
        inputNewCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View newCategoryLayout = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_input_new_category, null);
                final EditText inputNewCategoryEdtTxt = newCategoryLayout.findViewById(R.id.inputNewCategoryEdtTxt);

                // Показываем диалог
                new MaterialStyledDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_add_black_24dp)
                        .autoDismiss(false)
                        .setDescription("Пожалуйста, введите новую тему")
                        .setCustomView(newCategoryLayout)
                        .setNegativeText("Отменить")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Сохранить")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                checkExistingAndAdd(dialog, inputNewCategoryEdtTxt);
                            }
                        })
                        .show();
            }
        });
    }

    private void checkExistingAndAdd(MaterialDialog dialog, EditText inputNewCategoryEdtTxt) {
        boolean isExists = false;
        String input = inputNewCategoryEdtTxt.getText().toString();
        if (input.isEmpty()) {
            inputNewCategoryEdtTxt.requestFocus();
            inputNewCategoryEdtTxt.setError("Введите название");
        } else {
            for (Category category : Common.categoryLst) {
                if (category.getName().equals(input)) {
                    isExists = true;
                    break;
                }
            }
            if (isExists) {
                inputNewCategoryEdtTxt.requestFocus();
                inputNewCategoryEdtTxt.setError("Такая уже существует");
            } else {
                Category category = new Category(Common.categoryLst.size() + 1,
                        inputNewCategoryEdtTxt.getText().toString(), null);
                Common.categoryLst.add(category);
                addSpinnerAdapter();
                OnlineDBHelper.getInstance(getContext()).saveCategoryDB(category);
                dialog.dismiss();
            }
        }
    }
}