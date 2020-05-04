package com.example.testcreator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.R;
import com.example.testcreator.MyEnum.AnswerTypeEnum;

import java.util.List;


/**
 * Класс-адаптер для удобной работы с квадратиками, цвет которых меняется
 * в зависимости от правильности ответа на вопрос.
 */
public class AnswerSheetAdapter extends RecyclerView.Adapter<AnswerSheetAdapter.MyViewHolder> {

    private Context context;
    private List<CurrentQuestion> currentQuestionsLst;

    public AnswerSheetAdapter(Context context, List<CurrentQuestion> currentQuestionsLst) {
        this.context = context;
        this.currentQuestionsLst = currentQuestionsLst;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_sheet_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (currentQuestionsLst.get(position).getType() == AnswerTypeEnum.RIGHT_ANSWER) {
            holder.questionItem.setBackgroundResource(R.drawable.grid_question_right_answer);
        } else if (currentQuestionsLst.get(position).getType() == AnswerTypeEnum.WRONG_ANSWER) {
            holder.questionItem.setBackgroundResource(R.drawable.grid_question_wrong_answer);
        } else {
            holder.questionItem.setBackgroundResource(R.drawable.grid_question_no_answer);
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionsLst.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View questionItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionItem = itemView.findViewById(R.id.questionItem);
        }
    }
}
