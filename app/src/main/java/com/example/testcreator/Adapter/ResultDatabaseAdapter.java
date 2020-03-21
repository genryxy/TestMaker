package com.example.testcreator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.R;

import java.util.List;

public class ResultDatabaseAdapter extends RecyclerView.Adapter<ResultDatabaseAdapter.MyViewHolder> {

    private Context context;
    private List<ResultTest> resultTests;

    public ResultDatabaseAdapter(Context context, List<ResultTest> resultTests) {
        this.context = context;
        this.resultTests = resultTests;
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
        ResultTest userResult = resultTests.get(position);

        // Если пользователь выбирал категорию для прохождения,
        // то название теста будет null. Если выбирал тест по названию,
        // то категория == null.
        if (userResult.getCategoryName() != null && userResult.getCategoryName().length() > 0) {
            holder.categoryTestViewTxt.setText(userResult.getCategoryName());
        } else {
            holder.nameTestViewTxt.setText(userResult.getNameTest());
        }
        holder.timeTestTxt.setText(userResult.getDuration());
        holder.resultTestTxt.setText(userResult.getFinalScore());
    }

    @Override
    public int getItemCount() {
        return resultTests.size();
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
            int position = getAdapterPosition();
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                ResultTest resultTest = resultTests.get(position);
                // We can access the data within the views
                Toast.makeText(context, resultTest.getFinalScore(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
