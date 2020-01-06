package com.example.testcreator.ui.newTest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.FireBaseConnections;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class NewTestFragment extends Fragment implements FireBaseConnections
{
    private Integer tests_number;
    private NewTestViewModel newTestViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        newTestViewModel = ViewModelProviders.of(this).get(NewTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_name_test, container, false);
        final TextView nameTestTxt = root.findViewById(R.id.nameTestTxt);
        newTestViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                nameTestTxt.setText(s);
            }
        });
        final EditText nameTestEdt = root.findViewById(R.id.nameTestEdt);
        Button saveNameTestBtn = root.findViewById(R.id.saveNameTestBtn);

        saveNameTestBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CountDownLatch downLatch = new CountDownLatch(1);
                new CountTests(downLatch);
                try
                {
                    downLatch.await();
                } catch (InterruptedException e)
                {
                    Log.w("FAILURE", "Error CountDownLatch", e);
                    return;
                }

                db.collection("tests_names")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    if (task.getResult().getDocuments().get(0).getData().values().contains(nameTestEdt.getText().toString()))
                                    {
                                        nameTestEdt.setError("Тест с таким названием уже существует");
                                        nameTestEdt.requestFocus();
                                    } else
                                    {
                                        Map<String, String> data = new HashMap<>();
                                        data.put("name" + tests_number, nameTestEdt.getText().toString());
                                        db.collection("tests_names").document("names").set(
                                                data, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                                {
                                                    @Override
                                                    public void onSuccess(Void aVoid)
                                                    {
                                                        Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                                        Map<String, Object> dataNumb = new HashMap<>();
                                                        dataNumb.put("tests_number", tests_number + 1);
                                                        db.collection("tests").document("tests")
                                                                .update(dataNumb);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        Log.w("FAILURE", "Error adding document", e);
                                                        //Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else
                                {
                                    Log.w("FAILURE", "Error adding document", task.getException());
                                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return root;
    }

    class CountTests implements Runnable
    {
        CountDownLatch latch;

        CountTests(CountDownLatch downLatch)
        {
            latch = downLatch;
            new Thread(this).start();
        }

        public void run()
        {
            db.collection("tests")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                tests_number = Integer.valueOf(task.getResult().getDocuments()
                                        .get(0).getData().get("tests_number").toString());
                                //Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();

                            } else
                            {
                                Log.w("FAILURE", "Error adding document", task.getException());
                                //Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            latch.countDown();
        }
    }
}

