package com.example.fan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Star on 03.02.2018.
 */

public class Utils {

    private static final Map<Character, String> letters = new HashMap<Character, String>();
    public static String cookie;
    public static Map<String, String> cookies;

    static {
        letters.put('А', "A");
        letters.put('Б', "B");
        letters.put('В', "V");
        letters.put('Г', "G");
        letters.put('Д', "D");
        letters.put('Е', "E");
        letters.put('Ё', "E");
        letters.put('Ж', "Zh");
        letters.put('З', "Z");
        letters.put('И', "I");
        letters.put('Й', "I");
        letters.put('К', "K");
        letters.put('Л', "L");
        letters.put('М', "M");
        letters.put('Н', "N");
        letters.put('О', "O");
        letters.put('П', "P");
        letters.put('Р', "R");
        letters.put('С', "S");
        letters.put('Т', "T");
        letters.put('У', "U");
        letters.put('Ф', "F");
        letters.put('Х', "Kh");
        letters.put('Ц', "C");
        letters.put('Ч', "Ch");
        letters.put('Ш', "Sh");
        letters.put('Щ', "Sch");
        letters.put('Ъ', "");
        letters.put('Ы', "Y");
        letters.put('Ь', "");
        letters.put('Э', "E");
        letters.put('Ю', "Yu");
        letters.put('Я', "Ya");
        letters.put('а', "a");
        letters.put('б', "b");
        letters.put('в', "v");
        letters.put('г', "g");
        letters.put('д', "d");
        letters.put('е', "e");
        letters.put('ё', "e");
        letters.put('ж', "zh");
        letters.put('з', "z");
        letters.put('и', "i");
        letters.put('й', "i");
        letters.put('к', "k");
        letters.put('л', "l");
        letters.put('м', "m");
        letters.put('н', "n");
        letters.put('о', "o");
        letters.put('п', "p");
        letters.put('р', "r");
        letters.put('с', "s");
        letters.put('т', "t");
        letters.put('у', "u");
        letters.put('ф', "f");
        letters.put('х', "h");
        letters.put('ц', "c");
        letters.put('ч', "ch");
        letters.put('ш', "sh");
        letters.put('щ', "sch");
        letters.put('ъ', "");
        letters.put('ы', "y");
        letters.put('ь', "");
        letters.put('э', "e");
        letters.put('ю', "yu");
        letters.put('я', "ya");
    }

    SharedPreferences sPref;
    private String domain;

    public static void setCookies(Map<String, String> cookies) {
        Utils.cookies = cookies;
    }

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        Utils.cookie = cookie;
    }

    public static String decode(String decodeString) throws UnsupportedEncodingException {
        String Encode = "";
        //Log.d("tnp_cartoon", "dec: "+decodeString);
        decodeString = StringEscapeUtils.unescapeJava(decodeString);
        //Log.d("tnp_cartoon", "enc: "+decodeString);
        return decodeString;
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("clearCookie", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("clearCookie", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public boolean CheckResponceCode(String url) throws IOException {

        boolean connected = true;
        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("188.40.141.216", 3128));
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.connect();
        if (connection.getResponseCode() != 200) {
            connected = false;
        }
        connection.disconnect();
        return connected;

    }

    public void setCookie(String cookie, Context context) {
        sPref = context.getSharedPreferences("URL", MODE_PRIVATE);
        sPref.edit().putString("Cookie", cookie).apply();
        sPref.edit().putBoolean("Auth", true).apply();
        Utils.cookie = cookie;
    }

    public Map<String, String> getCookies(Context context) {
        Map<String, String> cookies = new HashMap<>();
        if (cookie == null) {
            sPref = context.getSharedPreferences("URL", MODE_PRIVATE);
            cookie = sPref.getString("Cookie", "");
        }
        Log.d("Utils", "cookie: " + cookie);
        if (StringUtils.isNotEmpty(cookie))
            for (String str : cookie.split(";")) {
                String[] kv = str.split("=");
                cookies.put(kv[0], kv[1]);
            }
        else
            cookies.put("a", "a");
        return cookies;
    }

    public boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public void saveFile(String string, Context context) throws IOException {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, "my-file-episodeName.txt");
        //Log.d("AllSerialsFragment", "path=" + path.getAbsolutePath());
        FileOutputStream stream = new FileOutputStream(file);
        try {
            String h = (string);
            stream.write(h.getBytes("UTF8"));
        } finally {
            stream.close();
        }
    }

    public String translit(String input) {
        input = input.replace(" ", "_");
        StringBuilder output = new StringBuilder();
        for (char ch : input.toCharArray()) {
//            Log.d("translit", "char: "+ch+" / "+letters.get(ch));
            if (letters.containsKey(ch))
                output.append(letters.get(ch));
            else
                output.append(ch);
        }
        return output.toString();
    }

    public String getDomainFromPreference(Context context) {
        sPref = context.getSharedPreferences("URL", MODE_PRIVATE);
        return sPref.getString("domain", "events");
    }

    public String getActualDomain(Context context) throws IOException {
        Document doc = Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/actual_adres.php").get();
        domain = "http://" + doc.getElementById("domain").text();
        sPref = context.getSharedPreferences("URL", MODE_PRIVATE);
        sPref.edit().putString("domain", domain).apply();
        return domain;
    }

    public void logout(Context context) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Jsoup.connect("http://" + domain + "/logout/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();

        sPref = context.getSharedPreferences("URL", MODE_PRIVATE);
        cookie = "";
        sPref.edit().putString("Cookie", cookie).putBoolean("Auth", false).apply();

    }

    public void mass_subscribe() {


    }

    public void subscibe(final int id, final int on_off, final Context context) {
        sPref = context.getSharedPreferences("URl", MODE_PRIVATE);
        boolean auth = sPref.getBoolean("Auth", false);
        if (auth) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Jsoup.connect("http://" + domain + "/profile/subscriptions/" + id + "/?checked=" + on_off)
                                .cookies(getCookies(context))
                                .ignoreContentType(true)
                                .post();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }
    }
}
