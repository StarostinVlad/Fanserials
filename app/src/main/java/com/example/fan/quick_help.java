package com.example.fan;

import android.util.Log;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
                BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String line;
                ////Log.d("check","start reading");
                while((line=in.readLine())!=null){
                    content=content +line;
                    //Log.d("check","read... "+line);
                }
            }//else Log.d("check","not 200 when read");

    }catch (Exception e){
            //Log.d("check","exep!!");
            e.printStackTrace();
        }
        //Log.d("check","result: "+content);
        return content;
    }
    public static boolean CheckResponceCode(String url) {
        try {
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("188.40.141.216", 3128));
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            if (connection.getResponseCode()!=200) {
                connection.disconnect();
                return false;
            }
            connection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String decode(String decodeString) throws UnsupportedEncodingException {
        String Encode="";
        //Log.d("tnp_cartoon", "dec: "+decodeString);
        decodeString=StringEscapeUtils.unescapeJava(decodeString);
        //Log.d("tnp_cartoon", "enc: "+decodeString);
        return decodeString;
    }

    private boolean stop;

    boolean getStop(){
        return stop;
    }

    public static ArrayList<LiteralOfSerials> sendReq(String url, int value) {
        String content = "";
        ArrayList<LiteralOfSerials> list = null;
        try {
            URLConnection connection = new URL(url+value).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Mobile Safari/537.36");
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
            String endList="\",\"countries";
            str=str.substring(1,str.indexOf(endList)+endList.length());
            //Log.d("tnp_cartoon", "result html : "+str);

            String[] Htmltext=str.split("<div class=\\\\\"literal\\\\\" id=\\\\\"as-.{0,2}\\\\\" data-id=\\\\\"[0-9]{1,5}\\\\\"");
            //Log.d("tnp_cartoon", "alfabet length : "+Htmltext.length);
            list = new ArrayList<LiteralOfSerials>();
            ArrayList<Serial> ch_list = new ArrayList<Serial>();
            LiteralOfSerials gru = null;
            boolean exist=false,end=false;
            for (String decodedString: Htmltext){
                //Log.d("tnp_cartoon", "result: "+decodedString);
                if (decodedString.contains("literal__header")) {
                    /*
                    if (gru != null) {
                        gru.setItems(ch_list);
                        list.add(gru);
                    }*/
                    gru = new LiteralOfSerials("",ch_list);
                    decodedString = decodedString.substring(decodedString.indexOf("<div class=\\\"literal__header\\\">") + 31);
                    //Log.d("tnp_cartoon", "literalOfSerial header:" + decodedString);
                    String GroupName = decodedString.substring(0, decodedString.indexOf("<\\/div>"));
                    GroupName = decode(GroupName);
                    if (GroupName.equals("#")) GroupName = "0-9";
                    else if (GroupName.equals("?")) end = true;
                    gru.setName(GroupName);
                    ch_list = new ArrayList<Serial>();
                    String[] Serials = decodedString.split("<li class=\\\\\"literal__item not-loaded\\\\\".{0,1000}\\\\\" >");
                    for (String Serial : Serials) {
                        //Log.d("tnp_cartoon", "newSerial header:" + Serial);
                        if (!Serial.contains("serialHref="))
                            continue;

                        String name = Serial;
                        Serial = Serial.substring(Serial.indexOf("=\\\"") + "=\\\"".length());
                        Serial = Serial.substring(1, Serial.indexOf("\\/\\\">"));
                        //Log.d("tnp_cartoon", "newSerial header:" + Serial);
                        //Log.d("tnp_cartoon", "episodeName header:" + name);
                        name = name.substring(name.indexOf("\">") + 2);
                        name = name.substring(0, name.indexOf("<\\/a>"));
                        name = decode(name);
                        //Log.d("tnp_cartoon", "episodeName: " + name);
                        //Log.d("tnp_cartoon", "episodeUrl: " + Serial);
                        if (!name.equals("Показать все")) {
                            com.example.fan.Serial ch = new Serial();
                            ch.setName(name);
                            ch.setImage("jgh");
                            //ch.setImage(newSerial.select("div div.foundSerialPoster a img").attr("src"));
                            ch.setUri(Serial);
                            ch_list.add(ch);
                        }
                    }
                    gru.setItems(ch_list);
                    list.add(gru);
                    exist = true;

                }
                /*else if(decodedString.contains("<a serialHref=\\\"")&&exist){
                    String episodeName = decodedString;
                    decodedString = decodedString.substring(decodedString.indexOf("<a serialHref=\\\"")+10);
                    decodedString = decodedString.substring(1, decodedString.indexOf("\">")).replace("\\","");
                    episodeName = episodeName.substring(episodeName.indexOf("\">")+2);
                    episodeName = episodeName.substring(0, episodeName.indexOf("<\\/a>\\"));
                    episodeName=decode(episodeName);
                    Log.d("tnp_cartoon", episodeName);
                    Log.d("tnp_cartoon", decodedString);
                    if(!episodeName.equals("Показать все")) {
                        Serial ch = new Serial();
                        ch.setName(episodeName);
                        ch.setImage("jgh");
                        //ch.setImage(newSerial.select("div div.foundSerialPoster a img").attr("src"));
                        ch.setUri(decodedString);
                        ch_list.add(ch);
                    }
                }
                content += decodedString;*/
            }
            //gru.setItems(ch_list);
            //list.add(gru);
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
