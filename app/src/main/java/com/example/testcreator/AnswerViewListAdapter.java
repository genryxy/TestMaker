package com.example.testcreator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AnswerViewListAdapter extends ArrayAdapter<AnswerView>
{
    private Context context;
    private int resource;


    public AnswerViewListAdapter(@NonNull Context context, int resource, @NonNull List<AnswerView> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        String answerText = getItem(position).getAnswerText();
        Boolean isSelected = getItem(position).getSelected();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        EditText txtAnswer = convertView.findViewById(R.id.answerTextEdt);
        final CheckedTextView txtChoice = convertView.findViewById(R.id.checkTxt);
        txtAnswer.setText(answerText);
        txtChoice.setChecked(isSelected);


        txtChoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtChoice.isChecked())
                {
                    txtChoice.setChecked(false);
                    getItem(position).setSelected(false);
                } else
                {
                    txtChoice.setChecked(true);
                    getItem(position).setSelected(true);
                }
            }
        });

        txtAnswer.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                getItem(position).setAnswerText(s.toString());
                Toast.makeText(getContext(), getItem(position).getAnswerText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        return convertView;
    }
}
