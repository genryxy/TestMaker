package com.example.testcreator;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcreator.Adapter.CategoryAdapter;
import com.example.testcreator.Common.SpaceDecoration;
import com.example.testcreator.DBHelper.DBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements FireBaseConnections
{
    private final String TAG = "FAILURE MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private FragmentTransaction transaction;
    private Fragment selectingTestFragment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Quiz 2020");
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
                R.id.nav_home, R.id.nav_new_test, R.id.nav_selecting_test,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        selectingTestFragment = new SelectingTestFragment();
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.select_test_fragment, selectingTestFragment);
//        transaction.commit();

        Button readDataBtn = findViewById(R.id.readDataBtn);
        //final TextView readDataTxt = findViewById(R.id.readDataTxt);

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
                                        //readDataTxt.setText(document.getData().toString() + " " + tmp);
                                        if (document.getId().equals("VNVXKvyaXnd3RwxUum8HRLr7FOA3"))
                                        {
                                            break;
                                        }
                                        // tmp = readDataTxt.getText().toString() + " " + document.getData().toString();
                                        //readDataTxt.setText(tmp);
                                    }
                                } else
                                {
                                    Log.w(TAG, "Error adding document", task.getException());
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
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.remove(selectingTestFragment);
//        transaction.commit();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
