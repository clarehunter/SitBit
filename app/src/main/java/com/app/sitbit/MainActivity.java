package com.app.sitbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private NavController navController;

    private Globals globals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        globals = Globals.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);

        globals.deleteOldData(cal.getTimeInMillis());

        globals.getAttribute("EnableNotifications", new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                if (o != null) {
                    if ((Boolean) o)
                        globals.registerNotification(MainActivity.this);
                    else
                        globals.deregisterNotification(MainActivity.this);
                }
            }
        });
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                navController.navigate(R.id.home_fragment);
                break;
            }

            case R.id.nav_goals: {
                navController.navigate(R.id.goals_fragment);
                break;
            }

            case R.id.nav_history: {
                navController.navigate(R.id.history_fragment);
                break;
            }

            case R.id.nav_export: {
                navController.navigate(R.id.export_fragment);
                break;
            }

            case R.id.nav_settings: {
                navController.navigate(R.id.settings_main_fragment);
                break;
            }
        }

        drawerLayout.closeDrawers();

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {

        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }
}
