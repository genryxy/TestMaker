package com.example.testcreator.ui.searchTest;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.SelectingTestView;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class SelectingTestAdapter extends /*ArrayAdapter<SelectingTestView>*/
        //ListAdapter<SelectingTestView, SelectingTestRecyclerView.ViewHolder>
        RecyclerView.Adapter<SelectingTestAdapter.ViewHolder> implements FireBaseConnections {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private ImageView imgTest;
        private TextView nameTestViewTxt;
        private TextView infoTestTxt;
        private Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.context = context;
            imgTest = itemView.findViewById(R.id.testAvatarImg);
            nameTestViewTxt = itemView.findViewById(R.id.nameTestViewTxt);
            infoTestTxt = itemView.findViewById(R.id.infoTestTxt);
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        private ImageView getImgTest() {
            return imgTest;
        }

        private TextView getNameTestViewTxt() {
            return nameTestViewTxt;
        }

        private TextView getInfoTestTxt() {
            return infoTestTxt;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                SelectingTestView test = testsLst.get(position);
                // We can access the data within the views
                Toast.makeText(context, test.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<SelectingTestView> testsLst;
    private Context context;

     SelectingTestAdapter(@NonNull List<SelectingTestView> testsLst) {
        this.testsLst = testsLst;
    }

    // Involves inflating a layout from XML and returning the holder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout.
        View view = inflater.inflate(R.layout.layout_selecting_test, parent, false);
        // Return a new holder instance.
        ViewHolder viewHolder = new ViewHolder(context, view);
        return viewHolder;
    }

    // Involves populating data into the item through holder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position.
        SelectingTestView testView = testsLst.get(position);

        // Set item views based on your views and data models.
        TextView nameTestViewTxt = holder.getNameTestViewTxt();
        final TextView infoTestTxt = holder.getInfoTestTxt();
        final ImageView imgTest = holder.getImgTest();

        nameTestViewTxt.setText(testView.getName());
        infoTestTxt.setText(testView.getCreator());
        imgTest.setImageResource(R.drawable.ic_launcher_foreground);
        // TODO: !!!!!!!!! Брать ссылку из клааса.
        //imgTest.setImageResource(R.drawable.ic_menu_name_test);


        storageRef.child("myImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                Glide.with(context)
                        .load(uri)
                        .into(imgTest);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
//                imgTest.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });
    }

//    public void addMoreTests(List<SelectingTestView> newContacts) {
//        testsLst.addAll(newContacts);
//        submitList(testsLst); // DiffUtil takes care of the check
//    }

    @Override
    public int getItemCount() {
        return testsLst.size();
    }

//    private void uploadImage(String nameImage)
//    {
//        StorageReference childRef = storageRef.child(nameImage);
//        childRef.putFile(imgUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
//                {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
//                    {
////                        Toast.makeText(get(), "Изображение загружено", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener()
//                {
//                    @Override
//                    public void onFailure(@NonNull Exception exception)
//                    {
//                        Log.w(TAG, "Error CountDownLatch", exception);
//                        Toast.makeText(getActivity(), "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }

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