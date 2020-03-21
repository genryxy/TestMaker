package com.example.testcreator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    @Override
    public int getItemCount() {
        return resultTests.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
