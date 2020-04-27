package com.example.testcreator.ui.statistic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Adapter.ResultDatabaseAdapter;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Model.ResultTest;
import com.example.testcreator.Model.ResultTestFirebase;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static java.util.Arrays.asList;

public class StatisticFragment extends Fragment implements FireBaseConnections {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StatisticViewModel statisticViewModel;
    private AlertDialog dialog;

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

        showLoadingDialog();
        int spaceInPixel = 4;
        final RecyclerView resultDBRecycler = root.findViewById(R.id.resultDatabaseRecycler);
        resultDBRecycler.addItemDecoration(new SpaceDecoration(spaceInPixel));
        resultDBRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        final String keyUser = authFrbs.getCurrentUser().getEmail() + authFrbs.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(keyUser);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ResultTestFirebase resultTestFirebase = documentSnapshot.toObject(ResultTestFirebase.class);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (resultTestFirebase == null) {
                            Toast.makeText(getContext(), "Вы ещё не проходили тесты!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ResultDatabaseAdapter adapter = new ResultDatabaseAdapter(getActivity(),
                                new ArrayList<>(resultTestFirebase.getResultTestsMap().values()));
                        resultDBRecycler.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error getting document", e);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getContext(), "Не удалось загрузить результаты", Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void showLoadingDialog() {
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("loading ...")
                .setCancelable(false)
                .build();

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}