package com.example.testcreator.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.QuestionActivity;
import com.example.testcreator.R;

import java.util.List;


/**
 * Класс-адаптер для вывода результатов прохождения тестов пользователем.
 */
public class ResultDBAdapter extends RecyclerView.Adapter<ResultDBAdapter.MyViewHolder> {

    private Context context;
    private List<ResultTest> resultTests;

    public ResultDBAdapter(Context context, List<ResultTest> resultTests) {
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

        // Если пользователь выбирал тест для прохождения, то у
        // теста будет название, которое можно вывести.
        if (userResult.getNameTest() != null && userResult.getNameTest().length() > 0) {
            holder.nameTestViewTxt.setText(userResult.getNameTest());
        }

        holder.categoryTestViewTxt.setText(Utils.getNameCategoryByID(userResult.getCategoryID()));
        holder.timeTestTxt.setText(Utils.convertToNormalForm(Long.valueOf(userResult.getDuration())));
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
            if (position != RecyclerView.NO_POSITION) {
                ResultTest resultTest = resultTests.get(position);
                // Чтобы понимать какие вопросы загружать
                Common.selectedTest = resultTest.getNameTest();
                Common.selectedCategory = resultTest.getCategoryID();
                Common.keyGetTestByResult = String.valueOf(resultTest.getResultID());

                Common.fragmentsLst.clear();
                Common.questionLst = resultTest.getQuestionLst();
                Common.answerSheetList = resultTest.getAnswerSheetLst();
                int rightAnswer = Integer.valueOf(resultTest.getFinalScore().split("/")[0]);
                int wrongAnswer = Integer.valueOf(resultTest.getWrongAnswer());
                Common.rightAnswerCount = rightAnswer;
                Common.wrongAnswerCount = wrongAnswer;
                Common.noAnswerCount = Common.questionLst.size() - (rightAnswer + wrongAnswer);
                Intent intent = new Intent(context, QuestionActivity.class);
                intent.putExtra("isAnswerModeView", "true");
                context.startActivity(intent);
            }
        }
    }
}
