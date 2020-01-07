package com.example.testcreator;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FireBaseConnections
{
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_new_test, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Button addQuestionBtn = findViewById(R.id.addQuestionBtn);
        Button readDataBtn = findViewById(R.id.readDataBtn);
        final TextView readDataTxt = findViewById(R.id.readDataTxt);
        addQuestionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v)
            {
//                Map<String, Object> result = new HashMap<>();
//                result.put("название теста 1", 1);
//                result.put("название теста 2", 3);
//                result.put("название теста 3", 2);
//
//                db.collection("users").document(authFrbs.getUid()).set(result)
//                        //dbRef.child("users").push().setValue(user)
//                        .addOnSuccessListener(new OnSuccessListener<Void>()
//                        {
//                            @Override
//                            public void onSuccess(Void aVoid)
//                            {
//                                Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener()
//                        {
//                            @Override
//                            public void onFailure(@NonNull Exception e)
//                            {
//                                Log.w("FAILURE", "Error adding document", e);
//                                Toast.makeText(MainActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                List<String> answers = new ArrayList<>();
                answers.add("неверный 1");
                answers.add("неверный 2");
                answers.add("верный ответ");
                answers.add("неверный 3");
                Question question = new Question("формулировка вопроса", TypeAnswer.OneAnswer, 4,
                        answers, 1, "3");
                List<Question> questions = new ArrayList<>();
                questions.add(question);
                TestInfo testInfo = new TestInfo(authFrbs.getCurrentUser().getEmail(), new Date(), "название х", 1, questions);
                db.collection("tests").document("hereTestsName")
                        .set(testInfo)
                        //dbRef.child("users").push().setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.w("FAILURE", "Error adding document", e);
                                Toast.makeText(MainActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        readDataBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String tmp = "";
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        tmp += document.getId() + " ";
                                        readDataTxt.setText(document.getData().toString() + " " + tmp);
                                        if (document.getId().equals("VNVXKvyaXnd3RwxUum8HRLr7FOA3"))
                                        {
                                            break;
                                        }
                                        // tmp = readDataTxt.getText().toString() + " " + document.getData().toString();
                                        //readDataTxt.setText(tmp);
                                    }
                                } else
                                {
                                    Log.w("FAILURE", "Error adding document", task.getException());
                                    //Toast.makeText(MainActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
