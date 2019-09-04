package com.example.fan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    MyFrag bFragment;
    MyFrag1 bFragment1;
    FragmentTransaction fTrans;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            bFragment = new MyFrag();
            bFragment1 = new MyFrag1();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_act_id, bFragment);
            fTrans.commit();
            Log.d("MainActivity", "recreate");
        }


    }

    @Override
    protected void onDestroy() {
       /*
        sPref = getSharedPreferences("URL",MODE_PRIVATE);
        if(sPref.getBoolean("Notify",true)) {
            Intent NotifyService = new Intent(this, Notification_Service.class);
            NotifyService.putExtra("Series", Series);
            startService(NotifyService);
        }*/
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.action_notifications);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //startService(new Intent(this,Notification_Service.class));
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Пока нет, но будет!", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else if (id == R.id.action_notifications) {
            Intent NotifyService = new Intent(this, Notification_Service.class);
            if (isChecked) {
                //NotifyService.putExtra("Series", Series);
                startService(NotifyService);
            } else {
                stopService(NotifyService);
            }
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            Log.d("MainActivity", "queryUrl=" + isChecked);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment nextFrag = null;
        Bundle args = new Bundle();

        if (id == R.id.nav_camera) {
            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));

            args.putInt("SerialType", 0);
            nextFrag = bFragment1;

        } else if (id == R.id.nav_gallery) {
            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 1));

            args.putInt("SerialType", 1);
            nextFrag = bFragment1;

        } else if (id == R.id.nav_slideshow) {
            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 2));

            args.putInt("SerialType", 2);
            nextFrag = bFragment1;

        } else if (id == R.id.nav_manage) {
            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 3));

            args.putInt("SerialType", 3);
            nextFrag = bFragment1;

        } else if (id == R.id.nav_share) {
            //nextFrag = bFragment1;
        } else if (id == R.id.nav_send) {
            //nextFrag = bFragment;
        }


        if (!getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                .getClass().getName().equals(nextFrag.getClass().getName())) {
            Log.d("MainActivity", "cur " + nextFrag.getClass().getName() + ": " + args.isEmpty());
            if (!nextFrag.isVisible()) {
                nextFrag = new MyFrag1();
                nextFrag.setArguments(args);
            }
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_act_id, nextFrag);
            fTrans.addToBackStack(null);
            fTrans.commit();

        } else if (nextFrag.equals(bFragment1)) {
            Log.d("MainActivity", "with args " + nextFrag.getClass().getName());
            Bundle curType;
            if ((curType = getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                    .getArguments()) != null) {
                Log.d("MainActivity", "args: "
                        + args.getInt("SerialType") +
                        ": " + curType.getInt("SerialType"));
                if (args.getInt("SerialType") != curType.getInt("SerialType")) {

                    nextFrag = new MyFrag1();
                    nextFrag.setArguments(args);
                    fTrans = getSupportFragmentManager().beginTransaction();
                    fTrans.replace(R.id.main_act_id, nextFrag);
                    fTrans.addToBackStack(null);

                    fTrans.commit();
                }
                Log.d("MainActivity", "cur type null: " + (curType == null));
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}