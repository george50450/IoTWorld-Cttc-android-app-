package com.example.george.cttctry2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.george.cttctry2.adapter.SlidingMenuAdapter;
import com.example.george.cttctry2.fragment.CostFragment;
import  com.example.george.cttctry2.fragment.Fragment1;
import  com.example.george.cttctry2.fragment.Fragment2;
import  com.example.george.cttctry2.fragment.Fragment3;
import com.example.george.cttctry2.model.ItemSlideMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George Kalampokis on 6/12/2017.
 */

public class MainActivity extends ActionBarActivity {

    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    int frag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        frag =getIntent().getIntExtra("frag",0);
        //Init component
        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listSliding = new ArrayList<>();
        //Add item for sliding list
        listSliding.add(new ItemSlideMenu(R.drawable.sens_ic, "Sensors"));
        listSliding.add(new ItemSlideMenu(R.drawable.cost, "Cost"));
        listSliding.add(new ItemSlideMenu(R.drawable.maps, "Maps"));
        listSliding.add(new ItemSlideMenu(R.drawable.info, "About"));
        adapter = new SlidingMenuAdapter(this, listSliding);
        listViewSliding.setAdapter(adapter);

        //Display icon to open/ close sliding list
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Close menu
        drawerLayout.closeDrawer(listViewSliding);
        //Set title
        setTitle(listSliding.get(frag).getTitle());
        //item selected
        listViewSliding.setItemChecked(frag, true);
        //Display fragment 1 when start
        replaceFragment(frag);

        //Hanlde on item click

        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Set title
                final int pos=position;
                setTitle(listSliding.get(position).getTitle());
                //item selected
                listViewSliding.setItemChecked(position, true);
                //Replace fragment
                replaceFragment(pos);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Close menu
                        drawerLayout.closeDrawer(listViewSliding);
                    }
                }, 200);

            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); //which is empty but if you want you can add things to main_menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //Create method replace fragment
    private void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            //depending of user's options this method replaces the current fragment with the one's selected
            case 0:
                fragment = new Fragment1();
                break;
            case 1:
                fragment = new CostFragment();
                break;
            case 2:
                fragment = new Fragment2();
                break;
            case 3:
                fragment = new Fragment3();
                break;
            default:
                fragment = new Fragment1();
                break;
        }

        if(null!=fragment) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
