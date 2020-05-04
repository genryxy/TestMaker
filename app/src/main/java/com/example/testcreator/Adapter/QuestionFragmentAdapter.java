package com.example.testcreator.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.testcreator.QuestionFragment;

import java.util.List;

/**
 * Класс-адаптер для вывода различных фрагментов с вопросами внутри viewPager.
 */
public class QuestionFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<QuestionFragment> fragmentsLst;

    public QuestionFragmentAdapter(@NonNull FragmentManager fm, Context context, List<QuestionFragment> fragmentsLst) {
        super(fm);
        this.context = context;
        this.fragmentsLst = fragmentsLst;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentsLst.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsLst.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return new StringBuilder("Вопрос ").append(position + 1).toString();
    }
}
