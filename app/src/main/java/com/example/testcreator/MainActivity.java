package com.example.testcreator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.testcreator.Common.Common;
import com.example.testcreator.Common.Utils;
import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.Model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FireBaseConnections {
    public final String TAG = "FAILURE MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private FragmentTransaction transaction;
    private Fragment selectingTestFragment;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Quiz 2020");
        setSupportActionBar(toolbar);

        dialog = Utils.showLoadingDialog(this);
        OnlineDBHelper.getInstance(this).getCategories(new ThemesCallBack() {
            @Override
            public void setThemes(List<Category> themesLst) {
                Common.categoryLst = themesLst;
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        OnlineDBHelper.getInstance(this).putNamesTestToSet();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_statistic, R.id.nav_new_test, R.id.nav_selecting_test,
                R.id.nav_category, R.id.nav_compare, R.id.nav_info, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//                if (destination.getId() == R.id.drawer_layout) {
//                    Toast.makeText(MainActivity.this, "kukukukukareku", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "kvak-plak " + destination.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        selectingTestFragment = new SelectingTestFragment();
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.nav_host_fragment, selectingTestFragment);
//        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.remove(selectingTestFragment);
//        transaction.commit();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}