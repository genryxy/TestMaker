package com.example.testcreator.ui.searchTest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.TestInfo;
import com.example.testcreator.QuestionActivity;
import com.example.testcreator.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SelectingTestAdapter extends /*ArrayAdapter<SelectingTestView>*/
        //ListAdapter<SelectingTestView, SelectingTestRecyclerView.ViewHolder>
        RecyclerView.Adapter<SelectingTestAdapter.ViewHolder> implements FireBaseConnections {

    private List<TestInfo> testInfos;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgTest;
        private TextView nameTestViewTxt;
        private TextView infoTestTxt;
        private TextView creatingDateTxt;
        private Context context;

        ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            imgTest = itemView.findViewById(R.id.testAvatarImg);
            nameTestViewTxt = itemView.findViewById(R.id.nameTestViewTxt);
            infoTestTxt = itemView.findViewById(R.id.infoTestTxt);
            creatingDateTxt = itemView.findViewById(R.id.creatingDateTxt);
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                View shuffleLayout = View.inflate(context, R.layout.layout_shuffle, null);
                final CheckBox checkboxShuffle = shuffleLayout.findViewById(R.id.checkboxShuffle);
                final CheckBox checkboxShuffleAnswer= shuffleLayout.findViewById(R.id.checkboxShuffleAnswer);

                TestInfo test = testInfos.get(position);
                new MaterialStyledDialog.Builder(context)
                        .setIcon(R.drawable.ic_question_answer_black_24dp)
                        .setCustomView(shuffleLayout)
                        .setDescription("Начать тест \"" + test.getName() + "\"?")
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
                                Common.isShuffleMode = checkboxShuffle.isChecked();
                                Common.isIsShuffleAnswerMode = checkboxShuffleAnswer.isChecked();
                                Common.selectedCategory = testInfos.get(getAdapterPosition()).getCategoryID();
                                Common.selectedTest = testInfos.get(getAdapterPosition()).getName();
                                Common.fragmentsLst.clear();
                                Common.answerSheetList.clear();
                                Common.rightAnswerCount = 0;
                                Common.wrongAnswerCount = 0;
                                Intent intent = new Intent(context, QuestionActivity.class);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    public SelectingTestAdapter(@NonNull List<TestInfo> testInfos) {
        this.testInfos = testInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout.
        View view = inflater.inflate(R.layout.layout_selecting_test, parent, false);
        return new ViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position.
        TestInfo testInfo = testInfos.get(position);

        final ImageView imgTest = holder.imgTest;
        String someInfo = Utils.getNameCategoryByID(testInfo.getCategoryID()) + "\n" + testInfo.getCreator();

        Calendar.getInstance().setTime(testInfo.getDateCreation());

        holder.nameTestViewTxt.setText(testInfo.getName());
        holder.creatingDateTxt.setText(new SimpleDateFormat("HH:mm \n dd-MM-yyyy").format(testInfo.getDateCreation()));
        holder.infoTestTxt.setText(someInfo);
        imgTest.setImageResource(R.drawable.ic_launcher_foreground);

        if (testInfo.getPathToImg() != null) {
            OnlineDBHelper.getInstance(context).getImageByName(testInfo.getPathToImg(), imgTest, null);
        }
    }

    @Override
    public int getItemCount() {
        return testInfos.size();
    }

    // ----------- Реализация через ListView ---------------------
//    private Context context;
//    private int resource;
//
//    public SelectingTestViewListAdapter(@NonNull Context context, int resource, @NonNull List<SelectingTestView> objects) {
//        super(context, resource, objects);
//        this.context = context;
//        this.resource = resource;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        String name = getItem(position).getName();
//        String creator = getItem(position).getCreator();
//        String pathToImage = getItem(position).getPathToImage();
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        convertView = inflater.inflate(resource, parent, false);
//
//        final ImageView imgTest = convertView.findViewById(R.id.testAvatarImg);
//        TextView nameTestViewTxt = convertView.findViewById(R.id.nameTestViewTxt);
//        final TextView infoTestTxt = convertView.findViewById(R.id.infoTestTxt);
////        LinearLayout testLayout = convertView.findViewById(R.id.testLayout);
//        nameTestViewTxt.setText(name);
//        imgTest.setImageResource(R.drawable.ic_menu_name_test);
//        infoTestTxt.setText(creator);

//        testLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                infoTestTxt.setText("clicked from layout!!!!");
//            }
//        });
//        nameTestViewTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                infoTestTxt.setText("clicked from test!!!!");
//            }
//        });
//        infoTestTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                infoTestTxt.setText("clicked from info!!!!");
//            }
//        });
//        return convertView;
//    }
}