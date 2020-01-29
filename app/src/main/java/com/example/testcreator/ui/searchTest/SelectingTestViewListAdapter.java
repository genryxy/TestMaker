package com.example.testcreator.ui.searchTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testcreator.R;
import com.example.testcreator.ui.searchTest.SelectingTestView;

import java.util.List;

public class SelectingTestViewListAdapter extends ArrayAdapter<SelectingTestView>
{
    private Context context;
    private int resource;

    public SelectingTestViewListAdapter(@NonNull Context context, int resource, @NonNull List<SelectingTestView> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        String name = getItem(position).getName();
        String creator = getItem(position).getCreator();
        String pathToImage = getItem(position).getPathToImage();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        final ImageView imgTest = convertView.findViewById(R.id.testAvatarImg);
        TextView nameTestViewTxt = convertView.findViewById(R.id.nameTestViewTxt);
        final TextView infoTestTxt = convertView.findViewById(R.id.infoTestTxt);
//        LinearLayout testLayout = convertView.findViewById(R.id.testLayout);
        nameTestViewTxt.setText(name);
        imgTest.setImageResource(R.drawable.ic_menu_name_test);
        infoTestTxt.setText(creator);

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

        return convertView;
    }
}
