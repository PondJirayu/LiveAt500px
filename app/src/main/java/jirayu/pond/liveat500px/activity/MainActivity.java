package jirayu.pond.liveat500px.activity;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import jirayu.pond.liveat500px.R;
import jirayu.pond.liveat500px.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstances();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MainFragment.newInstance())
                    .commit();
        }


    }

    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar); // find Toolbar ที่บรรทัดแรกเท่านั้น
        setSupportActionBar(toolbar); // บอก activity ให้ใช้ toolbar แทน actionbar

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    // การ Handle click ของที่อยู่บน Action Bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
