package com.example.fan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fan.R;
import com.example.fan.fragments.AllSerialsFragment;
import com.example.fan.fragments.AuthorizationFragment;
import com.example.fan.fragments.MainFragment;
import com.example.fan.fragments.SearchFragment;
import com.example.fan.utils.SharedPref;
import com.example.fan.utils.Utils;
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


    public static boolean newActivity = false;
    public static ArrayList<Integer> lastItem = new ArrayList<>();
    MainFragment bFragment = new MainFragment();
    AllSerialsFragment bFragment1 = new AllSerialsFragment();
    AuthorizationFragment authorizationFragment = new AuthorizationFragment();
    FragmentTransaction fTrans;
    BottomNavigationView bottomNavigationView;
    Fragment nextFrag = null;
    boolean destroed = false;
    boolean paused = false;
    String SAVED_NOTIFI_STATUS = "notification";
    int lastid = 0;
    boolean auth;
    //    private boolean isChecked;
    private boolean backward = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        Utils.init(this);

        SharedPref.init(this);


        if (savedInstanceState == null) {
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_act_id, bFragment);
            fTrans.commit();
            Log.d("MainActivity", "recreate");
            lastItem.add(R.id.new_series);
            lastid = R.id.new_series;
        }

        newActivity = false;

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("MainActivity", "bottom nav id: " + id + " last id: " + lastid);
                if (id != lastid) {
                    if (backward) {
                        backward = false;
                        return true;
                    }
                    Bundle args = new Bundle();
                    auth = SharedPref.read(SharedPref.AUTH, false);
                    Log.d("MAinActivity", "Auth : " + auth);

                    lastid = id;
                    if (id == R.id.profile) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));
                        args.putBoolean("PROFILE", true);
                        nextFrag = authorizationFragment;
                        //Log.d("MAinActivity", "bottom nav type: " + bFragment1);

                    } else if (id == R.id.viewed) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 0));
//                        args.putInt("SerialType", 1);
//                        nextFrag = bFragment1;
                        args.putBoolean("VIEWED", true);
                        nextFrag = authorizationFragment;
                        //Log.d("MAinActivity", "bottom nav type: " + bFragment1);

                    } else if (id == R.id.serials) {
                        //startActivity(new Intent(this, All_serials_activity.class).putExtra("param", 1));

                        args.putInt("SerialType", 1);
                        nextFrag = bFragment1;
                        //Log.d("MAinActivity", "bottom nav type: " + 1);

                    } else if (id == R.id.new_series) {
                        nextFrag = bFragment;
                    }


                    if (nextFrag != null) {
                        if (nextFrag.equals(bFragment)) {
                            //Log.d("MainActivity", "cur " +
                            // nextFrag.getClass().getName() + ": " + args.isEmpty());

                            nextFrag = new MainFragment();

//                            lastItem.add(id);

                            fTrans = getSupportFragmentManager().beginTransaction();
                            fTrans.replace(R.id.main_act_id, nextFrag);
//                            fTrans.addToBackStack(null);
                            fTrans.commit();

                        } else if (nextFrag.equals(bFragment1)) {
                            Log.d("MainActivity", "with args " + nextFrag.getClass().getName());
                            Bundle curType = getSupportFragmentManager()
                                    .findFragmentById(R.id.main_act_id)
                                    .getArguments();

                            if (curType == null) {
                                curType = new Bundle();
                                curType.putInt("SerialType", 0);
                            }

                            Log.d("MainActivity", "args: " + args.getInt("SerialType") + ": " + curType.getInt("SerialType"));
                            if (args.getInt("SerialType") != curType.getInt("SerialType")) {

                                nextFrag = new AllSerialsFragment();
                                nextFrag.setArguments(args);

//                                lastItem.add(id);

                                fTrans = getSupportFragmentManager().beginTransaction();
                                fTrans.replace(R.id.main_act_id, nextFrag);
//                                fTrans.addToBackStack(null);
                                fTrans.setCustomAnimations(
                                        R.anim.enter_to_right, R.anim.exit_to_right,
                                        R.anim.enter_to_right, R.anim.exit_to_right
                                );
                                fTrans.commit();
                            }

                            //Log.d("MainActivity", "cur type null: " + (curType == null));
                        } else if (nextFrag.equals(authorizationFragment)) {
//                            lastItem.add(id);
                            if (auth) {
//                                Toast.makeText(MainActivity.this, "Авторизованы!", Toast.LENGTH_SHORT).show();
                                getSupportActionBar().setTitle("Лента");
                                MainFragment bFrag = new MainFragment();
                                bFrag.setArguments(args);
                                fTrans = getSupportFragmentManager().beginTransaction();
                                fTrans.replace(R.id.main_act_id, bFrag);
//                                fTrans.addToBackStack(null);
                                fTrans.commit();
                            } else {
                                getSupportActionBar().setTitle("Авторизация");
                                fTrans = getSupportFragmentManager().beginTransaction();
                                fTrans.replace(R.id.main_act_id, nextFrag);
//                                fTrans.addToBackStack(null);
                                fTrans.commit();
                            }
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
//        if (lastItem.size() > 1) {
//            lastItem.remove(lastItem.size() - 1);
//        }
//        backward = true;
//        //Log.d("selected last episodeId", "episodeId: " + bottomNavigationView.getSelectedItemId());
////        lastid = lastItem.get(lastItem.size() - 1);
//        bottomNavigationView.setSelectedItemId(lastItem.get(lastItem.size() - 1));
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
                    if (s.length() > 0)
                        if (!getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                                .getClass().getSimpleName().equals(sFr.getClass().getSimpleName())) {
                            Log.d("retrofit", getSupportFragmentManager().findFragmentById(R.id.main_act_id)
                                    .getClass().getSimpleName() + " current");
                            sFr.setQueryString(s);
                            fTrans = getSupportFragmentManager().beginTransaction();
                            fTrans.replace(R.id.main_act_id, sFr).addToBackStack(null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}