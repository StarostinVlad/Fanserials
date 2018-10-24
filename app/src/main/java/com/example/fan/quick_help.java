package com.example.fan;

import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.apache.commons.lang.StringEscapeUtils;

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
    public static String decode(String decodeString) throws UnsupportedEncodingException {
        String Encode="";
        Log.d("tnp_cartoon", "dec: "+decodeString);
        decodeString=StringEscapeUtils.unescapeJava(decodeString);
        Log.d("tnp_cartoon", "enc: "+decodeString);
        return decodeString;
    }
    public static ArrayList<MyGroup> sendReq(String url, int value) {
        String content = "";
        ArrayList<MyGroup> list = null;
        try {
            URLConnection connection = new URL(url+value).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36 OPR/54.0.2952.64");
            connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            /*OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("type=" + value + "");
            // remember to clean up
            out.flush();
            out.close();*/
            /*
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),"UTF8"));*/


            InputStreamReader r = new InputStreamReader(connection.getInputStream());
            StringBuilder buf = new StringBuilder();
            while (true) {
                int ch = r.read();
                if (ch < 0)
                    break;
                buf.append((char) ch);
            }
            String str = new String(buf);
            //Log.d("tnp_cartoon", "result 1 : "+r.getEncoding());
            String[] Htmltext=str.split("n ");
            //Log.d("tnp_cartoon", "result 1 : "+Htmltext.length);
            list = new ArrayList<MyGroup>();
            ArrayList<Child> ch_list = new ArrayList<Child>();
            MyGroup gru = null;
            boolean exist=false,end=false;
            for (String decodedString: Htmltext){
                //Log.d("tnp_cartoon", "result: "+decodedString);
                if (decodedString.contains("literal__header")) {
                    if (gru != null) {
                        gru.setItems(ch_list);
                        list.add(gru);
                    }
                        gru = new MyGroup();
                        decodedString = decodedString.substring(decodedString.indexOf("<div class=\\\"literal__header\\\">") + 31);
                        //Log.d("tnp_cartoon", decodedString);
                        decodedString = decodedString.substring(0, decodedString.indexOf("<\\/div>"));
                        decodedString=decode(decodedString);
                        if(decodedString.equals("#"))decodedString="0-9";
                        else if(decodedString.equals("?"))end=true;
                        gru.setName(decodedString);
                        ch_list = new ArrayList<Child>();
                        exist=true;

                } else if(decodedString.contains("<a href=\\\"")&&exist){
                    String name = decodedString;
                    decodedString = decodedString.substring(decodedString.indexOf("<a href=\\\"")+10);
                    decodedString = decodedString.substring(1, decodedString.indexOf("\">")).replace("\\","");
                    name = name.substring(name.indexOf("\">")+2);
                    name = name.substring(0, name.indexOf("<\\/a>\\"));
                    name=decode(name);
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
            //Log.d("tnp_cartoon", content);
            //in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
