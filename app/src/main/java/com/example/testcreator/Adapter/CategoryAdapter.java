package com.example.testcreator.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Model.Category;
import com.example.testcreator.QuestionActivity;
import com.example.testcreator.R;

import java.util.List;

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

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardCategory;
        TextView categoryNameTxt;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            categoryNameTxt = itemView.findViewById(R.id.categoryNameTxt);

            cardCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Click at category " + categories.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
                    Common.selectedCategory = categories.get(getAdapterPosition()).getId();
                    Common.selectedTest = null;
                    Common.fragmentsLst.clear();
                    Common.answerSheetList.clear();
                    Common.rightAnswerCount = 0;
                    Common.wrongAnswerCount = 0;
                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
