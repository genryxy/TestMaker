package com.example.testcreator.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Adapter.ResultDatabaseAdapter;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.UserResults;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatisticFragment extends Fragment implements FireBaseConnections {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StatisticViewModel statisticViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticViewModel =
                ViewModelProviders.of(this).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        statisticViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        final RecyclerView resultDatabaseRecycler = root.findViewById(R.id.resultDatabaseRecycler);


        final String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(keyUser);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserResults userResults = documentSnapshot.toObject(UserResults.class);
                        if (userResults == null) {
                            Toast.makeText(getContext(), "Вы ещё не проходили тесты!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ResultDatabaseAdapter adapter = new ResultDatabaseAdapter(getActivity(), userResults.getResultTestsLst());
                        int spaceInPixel = 4;
                        resultDatabaseRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
                        resultDatabaseRecycler.setAdapter(adapter);
                        resultDatabaseRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error getting document", e);
                        Toast.makeText(getContext(), "Не удалось загрузить результаты", Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }
}