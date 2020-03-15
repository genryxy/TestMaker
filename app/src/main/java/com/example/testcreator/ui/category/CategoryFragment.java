package com.example.testcreator.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Adapter.CategoryAdapter;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import io.paperdb.Paper;

public class CategoryFragment extends Fragment {

    private CategoryViewModel toolsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        setHasOptionsMenu(true);

        //Init paper
        Paper.init(getContext());

        // Get value online mode
        Common.isOnlineMode = Paper.book().read(Common.KEY_SAVE_ONLINE_MODE, false);

        RecyclerView categoryRecycler = root.findViewById(R.id.categoryRecycler);
        categoryRecycler.setHasFixedSize(true);


        CategoryAdapter adapter = new CategoryAdapter(getActivity(),
                DBHelper.getInstance(getActivity()).getAllCategories());
        int spaceInPixel = 4;
        categoryRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.category_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuSettings) {
            showSettings();
        }
        return true;
    }

    private void showSettings() {
        View settingLayout = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_settings, null);
        final CheckBox checkBoxOnlineMode = settingLayout.findViewById(R.id.checkBoxOnlineMode);

        // Загружаем данные из Paper. Если не доступно, то инициализируем дефолтовым значением.
        checkBoxOnlineMode.setChecked(Paper.book().read(Common.KEY_SAVE_ONLINE_MODE, false));

        // Показываем диалог
        new MaterialStyledDialog.Builder(getContext())
                .setIcon(R.drawable.ic_settings_white_24dp)
                .setTitle("Настройки")
                .setDescription("Пожалуйста, выберите")
                .setCustomView(settingLayout)
                .setNegativeText("Закрыть")
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
                        if (checkBoxOnlineMode.isChecked()) {
                            Common.isOnlineMode = true;
                        } else {
                            Common.isOnlineMode = false;
                        }
                        // Сохраняем режим.
                        Paper.book().write(Common.KEY_SAVE_ONLINE_MODE, checkBoxOnlineMode.isChecked());

                        dialog.dismiss();
                    }
                })
                .show();
    }
}