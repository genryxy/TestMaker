package com.example.testcreator.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Model.CurrentQuestion;
import com.example.testcreator.R;

import java.util.List;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.MyViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionsLst;

    public ResultGridAdapter(Context context, List<CurrentQuestion> answerSheetList) {
        this.context = context;
        this.currentQuestionsLst = answerSheetList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_result_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Drawable img;

        // Изменяем цвет на основании ответа.
        holder.questionBtn.setText(new StringBuilder("Question ").append(currentQuestionsLst
                .get(position).getQuestionIndex() + 1));
        if (currentQuestionsLst.get(position).getType() == Common.AnswerType.RIGHT_ANSWER) {
            holder.questionBtn.setBackgroundColor(Color.parseColor("#ff99cc00"));
            img = context.getResources().getDrawable(R.drawable.ic_check_white_24dp, null);
            holder.questionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        } else if (currentQuestionsLst.get(position).getType() == Common.AnswerType.WRONG_ANSWER) {
            holder.questionBtn.setBackgroundColor(Color.parseColor("#ffcc0000"));
            img = context.getResources().getDrawable(R.drawable.ic_clear_white_24dp, null);
            holder.questionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        } else {
            img = context.getResources().getDrawable(R.drawable.ic_error_outline_white_24dp, null);
            holder.questionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionsLst.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button questionBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            questionBtn = itemView.findViewById(R.id.questionBtn);
            questionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // При нажатии на кнопку возвращаемся к QuestionsActivity, чтобы показать вопрос.
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(new Intent(Common.KEY_BACK_FROM_RESULT).putExtra(Common.KEY_BACK_FROM_RESULT,
                                    currentQuestionsLst.get(getAdapterPosition()).getQuestionIndex()));
                }
            });
        }
    }
}
