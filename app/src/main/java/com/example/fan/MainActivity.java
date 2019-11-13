package com.example.fan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    static boolean newActivity = false;
    MainFragment bFragment;
    AllSerialsFragment bFragment1;
    FragmentTransaction fTrans;
    ArrayList<Integer> lastItem = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    Fragment nextFrag = null;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this,"ca-app-pub-9409630953625719~5336580212");


//        NavigationView navigationView = (NavigationView) findViewById(R.episodeId.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            bFragment = new MainFragment();
            bFragment1 = new AllSerialsFragment();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_act_id, bFragment);
            fTrans.commit();
            Log.d("MainActivity", "recreate");
            lastItem.add(R.id.new_series);
        }

        newActivity = false;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("MAinActivity", "bottom nav episodeId: " + id);
                Bundle args = new Bundle();


                if (id == R.id.serials) {
                    //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));

                    args.putInt("SerialType", 0);
                    nextFrag = bFragment1;
                    Log.d("MAinActivity", "bottom nav type: " + bFragment1);

                } else if (id == R.id.cartoon) {
                    //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 1));

                    args.putInt("SerialType", 1);
                    nextFrag = bFragment1;
                    Log.d("MAinActivity", "bottom nav type: " + 1);

                } else if (id == R.id.anime) {
                    //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 2));

                    args.putInt("SerialType", 2);
                    nextFrag = bFragment1;
                    Log.d("MAinActivity", "bottom nav type: " + 2);

                } else if (id == R.id.documental) {
                    //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 3));

                    args.putInt("SerialType", 3);
                    nextFrag = bFragment1;
                    Log.d("MAinActivity", "bottom nav type: " + 3);

                }
//                else if (episodeId == R.episodeId.tv_show) {
//                    args.putInt("SerialType", 6);
//                    nextFrag = bFragment1;
//                }
                else if (id == R.id.new_series) {
                    nextFrag = bFragment;
                }


                if (nextFrag != null) {
                    if (!getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                            .getClass().getName().equals(nextFrag.getClass().getName())) {
                        Log.d("MainActivity", "cur " + nextFrag.getClass().getName() + ": " + args.isEmpty());
                        if (!nextFrag.isVisible()) {
                            if (nextFrag.equals(bFragment1)) {
                                nextFrag = new AllSerialsFragment();
                                nextFrag.setArguments(args);
                            } else if (nextFrag.equals(bFragment)) {
                                nextFrag = new MainFragment();
                            }
                        }

                        lastItem.add(id);

                        fTrans = getSupportFragmentManager().beginTransaction();
                        fTrans.replace(R.id.main_act_id, nextFrag);
                        //fTrans.addToBackStack(null);
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

                                nextFrag = new AllSerialsFragment();
                                nextFrag.setArguments(args);

                                lastItem.add(id);

                                fTrans = getSupportFragmentManager().beginTransaction();
                                fTrans.replace(R.id.main_act_id, nextFrag);
                                //fTrans.addToBackStack(null);
                                fTrans.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right, R.anim.enter_to_right, R.anim.exit_to_right);
                                fTrans.commit();
                            }
                            Log.d("MainActivity", "cur type null: " + (curType == null));
                        }
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MainActivity", "saved: " + bFragment + " !");
        if (bFragment != null) {
            if(!newActivity) {
                outState.putSerializable("bFragment", bFragment);
                outState.putSerializable("bFragment1", bFragment1);
                outState.putIntegerArrayList("lastItem", lastItem);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bFragment = (MainFragment) savedInstanceState.getSerializable("bFragment");
        bFragment1 = (AllSerialsFragment) savedInstanceState.getSerializable("bFragment1");
        lastItem = savedInstanceState.getIntegerArrayList("lastItem");
        Log.d("MainActivity", "restore: " + bFragment);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "destroy");
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
        if (bottomNavigationView.getSelectedItemId() != R.id.new_series && !getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                .getClass().getName().equals(bFragment.getClass().getName())) {
            if (lastItem.size() > 1) {
                lastItem.remove(lastItem.size() - 1);
            }
            Log.d("selected last episodeId", "episodeId: " + bottomNavigationView.getSelectedItemId());
            bottomNavigationView.setSelectedItemId(lastItem.get(lastItem.size() - 1));
            Log.d("selected cur episodeId", "episodeId: " + bottomNavigationView.getSelectedItemId());

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        final SearchFragment sFr = new SearchFragment();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    if (s.length() > 3)
                        if (!getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                                .getClass().getSimpleName().equals(sFr.getClass().getSimpleName())) {
                            sFr.setQueryString(s);
                            fTrans = getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            fTrans.replace(R.id.main_act_id, sFr);
                            fTrans.commit();
                        } else {
                            sFr.search(s);
                        }
                } else {

                }
                return true;
            }
        });
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
            //startActivity(new Intent(this,MainActivityFrag.class));
//            startService(new Intent(this,Notification_Service.class));
//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "Пока нет, но будет!", Toast.LENGTH_SHORT);
//            toast.show();
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
        } else if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }


//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int episodeId = item.getItemId();
//
//        Fragment nextFrag = null;
//        Bundle args = new Bundle();
//
//        if (episodeId == R.episodeId.nav_camera) {
//            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));
//
//            args.putInt("SerialType", 0);
//            nextFrag = bFragment1;
//
//        } else if (episodeId == R.episodeId.nav_gallery) {
//            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 1));
//
//            args.putInt("SerialType", 1);
//            nextFrag = bFragment1;
//
//        } else if (episodeId == R.episodeId.nav_slideshow) {
//            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 2));
//
//            args.putInt("SerialType", 2);
//            nextFrag = bFragment1;
//
//        } else if (episodeId == R.episodeId.nav_manage) {
//            //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 3));
//
//            args.putInt("SerialType", 3);
//            nextFrag = bFragment1;
//
//        } else if (episodeId == R.episodeId.nav_share) {
//            //nextFrag = bFragment1;
//        } else if (episodeId == R.episodeId.nav_send) {
//            //nextFrag = bFragment;
//        }
//
//
//        if (!getSupportFragmentManager().findFragmentById(R.episodeId.main_act_id)
//                .getClass().getName().equals(nextFrag.getClass().getName())) {
//            Log.d("MainActivity", "cur " + nextFrag.getClass().getName() + ": " + args.isEmpty());
//            if (!nextFrag.isVisible()) {
//                nextFrag = new AllSerialsFragment();
//                nextFrag.setArguments(args);
//            }
//            fTrans = getSupportFragmentManager().beginTransaction();
//            fTrans.replace(R.episodeId.main_act_id, nextFrag);
//            fTrans.addToBackStack(null);
//            fTrans.commit();
//
//        } else if (nextFrag.equals(bFragment1)) {
//            Log.d("MainActivity", "with args " + nextFrag.getClass().getName());
//            Bundle curType;
//            if ((curType = getSupportFragmentManager().findFragmentById(R.episodeId.main_act_id)
//                    .getArguments()) != null) {
//                Log.d("MainActivity", "args: "
//                        + args.getInt("SerialType") +
//                        ": " + curType.getInt("SerialType"));
//                if (args.getInt("SerialType") != curType.getInt("SerialType")) {
//
//                    nextFrag = new AllSerialsFragment();
//                    nextFrag.setArguments(args);
//                    fTrans = getSupportFragmentManager().beginTransaction();
//                    fTrans.replace(R.episodeId.main_act_id, nextFrag);
//                    fTrans.addToBackStack(null);
//
//                    fTrans.commit();
//                }
//                Log.d("MainActivity", "cur type null: " + (curType == null));
//            }
//        }
//
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.episodeId.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

}