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
import androidx.drawerlayout.widget.DrawerLayout;
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

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter adapter;
    private RecyclerView categoryRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoryViewModel =
                ViewModelProviders.of(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        setHasOptionsMenu(true);
        // Очищаем установленные ранее значения.
        Common.isIsShuffleAnswerMode = false;
        Common.isShuffleQuestionMode = false;

        Paper.init(getContext());
        Common.isOnlineMode = Paper.book().read(Common.KEY_SAVE_ONLINE_MODE, true);
        categoryRecycler = root.findViewById(R.id.categoryRecycler);
        categoryRecycler.setHasFixedSize(true);
        int spaceInPixel = 4;
        categoryRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        categoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setAdapterDependingMode(Common.isOnlineMode);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.category_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuSettings) {
            showSettings();
        } else {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(getActivity().findViewById(R.id.nav_view));
        }
        return true;
    }

    private void setAdapterDependingMode(boolean isOnlineMode) {
        if (isOnlineMode) {
            adapter = new CategoryAdapter(getActivity(), Common.categoryLst);
            categoryRecycler.setAdapter(adapter);
        } else {
            adapter = new CategoryAdapter(getActivity(), DBHelper.getInstance(getActivity()).getAllCategories());
            categoryRecycler.setAdapter(adapter);
        }
    }

    private void showSettings() {
        View settingLayout = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_settings, null);
        final CheckBox checkBoxOnlineMode = settingLayout.findViewById(R.id.checkBoxOnlineMode);

        // Загружаем данные из Paper. Если не доступно, то инициализируем дефолтовым значением.
        checkBoxOnlineMode.setChecked(Paper.book().read(Common.KEY_SAVE_ONLINE_MODE, true));

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
                        if (Common.isOnlineMode != checkBoxOnlineMode.isChecked()) {
                            Common.isOnlineMode = checkBoxOnlineMode.isChecked();
                            setAdapterDependingMode(Common.isOnlineMode);
                        }
                        // Сохраняем режим.
                        Paper.book().write(Common.KEY_SAVE_ONLINE_MODE, checkBoxOnlineMode.isChecked());
                        dialog.dismiss();
                    }
                })
                .show();
    }
}