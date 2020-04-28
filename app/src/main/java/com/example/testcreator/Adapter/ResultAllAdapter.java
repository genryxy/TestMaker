package com.example.testcreator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Utils;
import com.example.testcreator.Model.ResultAll;
import com.example.testcreator.R;

import java.util.List;

/**
 * Класс-адаптер для вывода результатов прохождения тестов пользователем.
 */
public class ResultAllAdapter extends RecyclerView.Adapter<ResultAllAdapter.MyViewHolder> {

    private Context context;
    private List<ResultAll> resultAlls;

    public ResultAllAdapter(Context context, List<ResultAll> resultAlls) {
        this.context = context;
        this.resultAlls = resultAlls;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_result_database_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ResultAll userResult = resultAlls.get(position);

        holder.categoryTestViewTxt.setText(Utils.getNameCategoryByID(userResult.getCategoryID()));
        holder.timeTestTxt.setText(Utils.convertToNormalForm(Long.valueOf(userResult.getDuration())));
        holder.resultTestTxt.setText(userResult.getFinalScore());
        // Устанавливаем вместо названия теста id пользователя, проходившего тест.
        holder.nameTestViewTxt.setText(userResult.getTestTaker());
    }

    @Override
    public int getItemCount() {
        return resultAlls.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTestViewTxt;
        private TextView categoryTestViewTxt;
        private TextView resultTestTxt;
        private TextView timeTestTxt;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTestViewTxt = itemView.findViewById(R.id.nameTestViewTxt);
            categoryTestViewTxt = itemView.findViewById(R.id.categoryTestViewTxt);
            resultTestTxt = itemView.findViewById(R.id.resultTestTxt);
            timeTestTxt = itemView.findViewById(R.id.timeTestTxt);
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Нельзя посмотреть подробнее", Toast.LENGTH_SHORT).show();
        }
    }
}
