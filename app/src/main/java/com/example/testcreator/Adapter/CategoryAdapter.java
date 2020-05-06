package com.example.testcreator.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.Model.Category;
import com.example.testcreator.QuestionActivity;
import com.example.testcreator.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.List;

/**
 * Класс-адаптер для вывода названия категории внутри прямоугольника.
 * Сами категории расположены в виде списка.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.categoryNameTxt.setText(categories.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardCategory;
        TextView categoryNameTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            categoryNameTxt = itemView.findViewById(R.id.categoryNameTxt);

            cardCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (Common.isOnlineMode && !Utils.hasConnection(context)) {
                            Toast.makeText(context, "Нужно подключить интернет!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        View shuffleLayout = View.inflate(context, R.layout.layout_shuffle, null);
                        final CheckBox checkboxShuffle = shuffleLayout.findViewById(R.id.checkboxShuffle);
                        final CheckBox checkboxShuffleAnswer = shuffleLayout.findViewById(R.id.checkboxShuffleAnswer);

                        Category category = categories.get(getAdapterPosition());
                        new MaterialStyledDialog.Builder(context)
                                .setIcon(R.drawable.ic_question_answer_black_24dp)
                                .setCustomView(shuffleLayout)
                                .setDescription("Будет предложено не более 30 вопросов. " +
                                        "Начать тест по категории \"" + category.getName() + "\"?")
                                .setNegativeText("Нет")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveText("Да")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Common.isShuffleQuestionMode = checkboxShuffle.isChecked();
                                        Common.isIsShuffleAnswerMode = checkboxShuffleAnswer.isChecked();
                                        Common.selectedCategory = categories.get(getAdapterPosition()).getId();
                                        Common.selectedTest = null;
                                        Common.fragmentsLst.clear();
                                        Common.answerSheetList.clear();
                                        Common.rightAnswerCount = 0;
                                        Common.userPointCount = 0;
                                        Common.wrongAnswerCount = 0;
                                        Intent intent = new Intent(context, QuestionActivity.class);
                                        context.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    }
}
