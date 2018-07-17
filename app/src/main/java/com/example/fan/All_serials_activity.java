package com.example.fan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class All_serials_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListView elvMain;
    private ProgressBar pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_serials_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pr=(ProgressBar)findViewById(R.id.progressBar4);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        pr.setVisibility(View.VISIBLE);
        getHref gt=new getHref();
        gt.execute();
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
        getMenuInflater().inflate(R.menu.all_serials_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",0));
            finish();
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",1));
            finish();
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",2));
            finish();
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",3));
            finish();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ArrayList<MyGroup> list;
    void fill(){
        MyExpandableAdapter adapter = new MyExpandableAdapter(this,list);
        elvMain = (ExpandableListView) findViewById(R.id.ExpandableListOfAllSerials);
        elvMain.setAdapter(adapter);
        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("not_main", list.get(groupPosition).getItems().get(childPosition).getName());
                Log.d("not_main", list.get(groupPosition).getItems().get(childPosition).getUri());
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("Uri",list.get(groupPosition).getItems().get(childPosition).getUri()).putExtra("Name",list.get(groupPosition).getItems().get(childPosition).getName()));
                return false;
            }
        });
    }

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";

    class getHref extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            list = new ArrayList<MyGroup>();
            ArrayList<Child> ch_list;

            sPref = getSharedPreferences("URL",MODE_PRIVATE);
            String queryUrl = sPref.getString(SAVED_TEXT, "");
            Log.d("tnp", "queryUrl " + queryUrl);

            String agent="Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
            String s, img = "";
            if(quick_help.CheckResponceCode(queryUrl)){
                list=quick_help.sendReq(queryUrl+"/alfavit/",getIntent().getIntExtra("param",0));
                /*
                doc = Jsoup.connect("http://fanserials.biz").userAgent(agent).timeout(10000).get();
                Elements src = doc.select("form div div div#alfavit-content div");
                int i = 0;
                for (Element ss : src)
                {

                    String alph=ss.select("div").attr("id");
                    if(alph!="") {
                        Log.d("tnp", "id= " + ss.select("div.literal").attr("id"));
                        // заполняем список атрибутов для каждой группы
                        MyGroup gru = new MyGroup();
                        gru.setName(alph);
                        ch_list = new ArrayList<Child>();
                        Elements alphabet = ss.getElementsByAttributeValue("id", alph);
                        for (Element letter : alphabet) {
                        Elements serials=letter.select("ul li");
                            for(Element serial:serials) {
                                Log.d("tnp", "   name= " + serial.select("a").text());
                                Log.d("tnp", "  uri= " + serial.select("a").attr("href"));
                                Log.d("tnp", "  img= " + serial);
                                Child ch = new Child();
                                ch.setName(serial.select("a").text());
                                ch.setImage(serial.select("div div.poster a img").attr("src"));
                                ch.setUri(serial.select("div div a").attr("href"));
                                ch_list.add(ch);
                            }
                        }
                        gru.setItems(ch_list);
                        list.add(gru);
                    }
                }*/
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            pr.setVisibility(View.INVISIBLE);
            fill();
            super.onPostExecute(result);
        }

    }



}
