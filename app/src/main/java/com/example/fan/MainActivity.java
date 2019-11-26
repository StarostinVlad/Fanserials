package com.example.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    static boolean newActivity = false;
    MainFragment bFragment = new MainFragment();
    AllSerialsFragment bFragment1 = new AllSerialsFragment();
    FragmentTransaction fTrans;
    static ArrayList<Integer> lastItem = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    Fragment nextFrag = null;
    boolean destroed = false;
    boolean paused = false;
    String SAVED_NOTIFI_STATUS = "notification";
    int lastid = 0;
//    private boolean isChecked;
    private SharedPreferences sPref;
    private boolean backward = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        sPref = getSharedPreferences("URL", MODE_PRIVATE);
//        isChecked = sPref.getBoolean(SAVED_NOTIFI_STATUS, false);
//        Log.d("MainActivity", "ischeck: " + isChecked);
//        if (isChecked) {
//            Intent service_intent = new Intent(this, Notification_Service.class);
//            startService(service_intent);
//        }

        if (savedInstanceState == null) {
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_act_id, bFragment);
            fTrans.commit();
            //Log.d("MainActivity", "recreate");
            lastItem.add(R.id.new_series);
            lastid = R.id.new_series;
        }

        newActivity = false;

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                //Log.d("MainActivity", "bottom nav id: " + id);
                if(backward)
                {
                   backward = false;
                   return true;
                }
                Bundle args = new Bundle();

                if (id != lastid) {
                    lastid = id;
                    if (id == R.id.serials) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));

                        args.putInt("SerialType", 0);
                        nextFrag = bFragment1;
                        //Log.d("MAinActivity", "bottom nav type: " + bFragment1);

                    } else if (id == R.id.cartoon) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 1));

                        args.putInt("SerialType", 1);
                        nextFrag = bFragment1;
                        //Log.d("MAinActivity", "bottom nav type: " + 1);

                    } else if (id == R.id.anime) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 2));

                        args.putInt("SerialType", 2);
                        nextFrag = bFragment1;
                        //Log.d("MAinActivity", "bottom nav type: " + 2);

                    } else if (id == R.id.documental) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 3));

                        args.putInt("SerialType", 3);
                        nextFrag = bFragment1;
                        //Log.d("MAinActivity", "bottom nav type: " + 3);

                    }
//                else if (episodeId == R.episodeId.tv_show) {
//                    args.putInt("SerialType", 6);
//                    nextFrag = bFragment1;
//                }
                    else if (id == R.id.new_series) {
                        nextFrag = bFragment;
                    }


                    if (nextFrag != null) {
                        if (nextFrag.equals(bFragment)) {
                            //Log.d("MainActivity", "cur " +
                                   // nextFrag.getClass().getName() + ": " + args.isEmpty());

                            nextFrag = new MainFragment();

                            lastItem.add(id);

                            fTrans = getSupportFragmentManager().beginTransaction();
                            fTrans.replace(R.id.main_act_id, nextFrag);
                            fTrans.addToBackStack(null);
                            fTrans.commit();

                        } else if (nextFrag.equals(bFragment1)) {
                            //Log.d("MainActivity", "with args " + nextFrag.getClass().getName());
                            Bundle curType = getSupportFragmentManager()
                                    .findFragmentById(R.id.main_act_id)
                                    .getArguments();
                            if (curType == null) {
                                curType = new Bundle();
                                curType.putInt("SerialType", -1);
                            }

                            //Log.d("MainActivity", "args: "
                                   // + args.getInt("SerialType") +
                                   // ": " + curType.getInt("SerialType"));
                            if (args.getInt("SerialType") != curType.getInt("SerialType")) {

                                nextFrag = new AllSerialsFragment();
                                nextFrag.setArguments(args);

                                lastItem.add(id);

                                fTrans = getSupportFragmentManager().beginTransaction();
                                fTrans.replace(R.id.main_act_id, nextFrag);
                                fTrans.addToBackStack(null);
                                fTrans.setCustomAnimations(
                                        R.anim.enter_to_right, R.anim.exit_to_right,
                                        R.anim.enter_to_right, R.anim.exit_to_right
                                );
                                fTrans.commit();
                            }

                            //Log.d("MainActivity", "cur type null: " + (curType == null));
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("MainActivity", "resumed: ");
        destroed = false;
        paused = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d("MainActivity", "saved: " + lastItem + " !");
//        if (bFragment != null && bFragment1 != null && lastItem != null) {
//            if (!newActivity) {
//                outState.putIntegerArrayList("lastItem", lastItem);
//                outState.putSerializable("bFragment", bFragment);
//                outState.putSerializable("bFragment1", bFragment1);
//            }
//        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        bFragment = (MainFragment) savedInstanceState.getSerializable("bFragment");
//        bFragment1 = (AllSerialsFragment) savedInstanceState.getSerializable("bFragment1");
//        lastItem = savedInstanceState.getIntegerArrayList("lastItem");
        //Log.d("MainActivity", "restore: " + bFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroed = true;
        //Log.d("MainActivity", "destroy");
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
        //Log.d("MainActivity", "pause");
    }

    @Override
    public void onBackPressed() {
            if (lastItem.size() > 1) {
                lastItem.remove(lastItem.size() - 1);
            }
            backward = true;
            //Log.d("selected last episodeId", "episodeId: " + bottomNavigationView.getSelectedItemId());
            bottomNavigationView.setSelectedItemId(lastItem.get(lastItem.size() - 1));
            //Log.d("selected cur episodeId", "episodeId: " + bottomNavigationView.getSelectedItemId());
            super.onBackPressed();
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
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem checkable = menu.findItem(R.id.action_notifications);
//        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_notifications) {
//            Intent NotifyService = new Intent(this, Notification_Service.class);
//
//
//            item.setChecked(!isChecked);
//            //NotifyService.putExtra("Series", Series);
//            if (!isChecked) {
//                startService(NotifyService);
//                Log.d("MainActivity", "notify on: " + isChecked);
//            } else {
//                stopService(NotifyService);
//                Log.d("MainActivity", "notify on: " + isChecked);
//            }
//            isChecked = !isChecked;
//
//            sPref.edit().putBoolean(SAVED_NOTIFI_STATUS, isChecked).apply();
//
//
//            return true;
//        } else
            if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}