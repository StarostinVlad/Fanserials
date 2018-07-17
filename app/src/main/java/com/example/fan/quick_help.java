package com.example.fan;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Star on 03.02.2018.
 */

public class quick_help {
    static String GiveDocFromUrl(String xml)
    {
        String content="";
        try{
            //Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("188.40.141.216",3128));
            HttpURLConnection connection = (HttpURLConnection) new URL(xml).openConnection();
            connection.connect();
            if(connection.getResponseCode()==200)
            {
                BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"cp1251"));
                String line;
                Log.d("check","start reading");
                while((line=in.readLine())!=null){
                    content=content +line;
                    //Log.d("check","read... "+line);
                }
            }else Log.d("check","not 200 when read");

    }catch (Exception e){
            Log.d("check","exep!!");
            e.printStackTrace();
        }
        Log.d("check","result: "+content);
        return content;
    }
    public static boolean CheckResponceCode(String url) {
        try {
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("188.40.141.216", 3128));
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            if (connection.getResponseCode()!=200)
                return false;
            connection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static ArrayList<MyGroup> sendReq(String url, int value) {
        String content = "";
        ArrayList<MyGroup> list = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("type=" + value + "");
            // remember to clean up
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),"cp1251"));
            String decodedString;
            list = new ArrayList<MyGroup>();
            ArrayList<Child> ch_list = new ArrayList<Child>();
            MyGroup gru = null;
            while ((decodedString = in.readLine()) != null) {
                Log.d("tnp_cartoon", decodedString);
                if (decodedString.contains("id=\"as-")) {
                    if (gru != null) {
                        gru.setItems(ch_list);
                        list.add(gru);
                    }
                        gru = new MyGroup();
                        decodedString = decodedString.substring(decodedString.indexOf("<div id=\"as-") + 12);
                        decodedString = decodedString.substring(0, decodedString.indexOf("\" class"));
                        if(decodedString.equals("NUMB"))decodedString="0-9";
                        gru.setName(decodedString);
                        ch_list = new ArrayList<Child>();
                } else if(decodedString.contains("<a href=\"")){
                    String name = decodedString;
                    decodedString = decodedString.substring(decodedString.indexOf("<a href=\"")+9);
                    decodedString = decodedString.substring(0, decodedString.indexOf("\">"));
                    name = name.substring(name.indexOf("\">")+2);
                    name = name.substring(0, name.indexOf("</a>"));
                    Log.d("tnp_cartoon", name);
                    Log.d("tnp_cartoon", decodedString);
                    Child ch = new Child();
                    ch.setName(name);
                    ch.setImage("jgh");
                    //ch.setImage(serial.select("div div.poster a img").attr("src"));
                    ch.setUri(decodedString);
                    ch_list.add(ch);
                }
                content += decodedString;
            }
            gru.setItems(ch_list);
            list.add(gru);
            Log.d("tnp_cartoon", content);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
