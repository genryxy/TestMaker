package com.example.testcreator.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.QuestionActivity;
import com.example.testcreator.R;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Класс-адаптер для вывода результатов прохождения тестов пользователем.
 */
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

        holder.timeTestTxt.setText(convertToNormalForm(userResult.getDuration()));
        holder.resultTestTxt.setText(userResult.getFinalScore());
    }

    /**
     * Конвертирует миллисекунды в нормальное представление времени.
     * @param duration Продолжительность в миллисекундах.
     * @return чч:мм:сс (если чч == 0, то часы опускаются).
     */
    private String convertToNormalForm(String duration) {
        long millis = Long.valueOf(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder resTime = new StringBuilder();
        if (hours != 0) {
            resTime.append(String.format("%02d:", hours));
        }
        resTime.append(String.format("%02d:%02d", minutes, seconds));
        return  resTime.toString();
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
