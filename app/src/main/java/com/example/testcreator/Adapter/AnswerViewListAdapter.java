package com.example.testcreator.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testcreator.Model.AnswerView;
import com.example.testcreator.R;

import java.util.List;

public class AnswerViewListAdapter extends ArrayAdapter<AnswerView> {
    private Context context;
    private int resource;


    public AnswerViewListAdapter(@NonNull Context context, int resource, @NonNull List<AnswerView> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String answerText = getItem(position).getAnswerText();
        Boolean isSelected = getItem(position).getSelected();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        EditText txtAnswer = convertView.findViewById(R.id.answerTextEdt);
        final CheckedTextView txtChoice = convertView.findViewById(R.id.checkTxt);
        final ImageButton deleteItemBtn = convertView.findViewById(R.id.deleteItemBtn);
        txtAnswer.setText(answerText);
        txtChoice.setChecked(isSelected);

        txtChoiceClickListen(position, txtChoice);
        deleteItemBtnClickListen(position, deleteItemBtn);
        txtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                getItem(position).setAnswerText(s.toString());
                //Toast.makeText(getContext(), getItem(position).getAnswerText(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private void txtChoiceClickListen(final int position,
                                      final CheckedTextView txtChoice) {
        txtChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtChoice.isChecked()) {
                    txtChoice.setChecked(false);
                    getItem(position).setSelected(false);
                } else {
                    txtChoice.setChecked(true);
                    getItem(position).setSelected(true);
                }
            }
        });
    }

    private void deleteItemBtnClickListen(final int position,
                                          final ImageButton deleteItemBtn) {
        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerView answer = getItem(position);
                remove(answer);
                notifyDataSetChanged();
            }
        });
    }
}
