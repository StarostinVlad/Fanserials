package com.example.fan.utils;

import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Star on 03.02.2018.
 */

public class Utils {

    private static final Utils INSTANCE = new Utils();
    private static final Map<Character, String> letters = new HashMap<Character, String>();
    public static String cookie;
    public static Map<String, String> cookies;
    public static String domain;
    public static String token;
    private static Context context;

    static {
        letters.put('А', "A");
        letters.put('Б', "B");
        letters.put('В', "V");
        letters.put('Г', "G");
        letters.put('Д', "D");
        letters.put('Е', "E");
        letters.put('Ё', "E");
        letters.put('Ж', "ZH");
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
        letters.put('Х', "KH");
        letters.put('Ц', "C");
        letters.put('Ч', "CH");
        letters.put('Ш', "SH");
        letters.put('Щ', "SCH");
        letters.put('Ъ', "");
        letters.put('Ы', "Y");
        letters.put('Ь', "");
        letters.put('Э', "E");
        letters.put('Ю', "YU");
        letters.put('Я', "YA");
    }

    private Utils() {
    }

    public static void init(Context context) {
        domain = RemoteConfig.read(RemoteConfig.DOMAIN);
        Utils.context = context;

    }

    public synchronized static Utils getInstance() {

        return INSTANCE;
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

    public static boolean CheckResponceCode(String url) throws IOException {

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

    public static Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        if (cookie == null) {
            cookie = SharedPref.read(SharedPref.COOKIE);
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

    public static void setCookies(Map<String, String> cookies) {
        Utils.cookies = cookies;
    }

    public static boolean isNetworkOnline(Context context) {
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

    public static void alarm(String Title, String message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void saveFile(String string, Context context) throws IOException {
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

    public static String translit(String input) {
        StringBuilder output = new StringBuilder();
        for (char ch : input.toUpperCase().toCharArray()) {
//            Log.d("translit", "char: "+ch+" / "+letters.get(ch));
            if (letters.containsKey(ch))
                output.append(letters.get(ch));
            else
                output.append(ch);
        }
        return output.toString().replaceAll("[^a-zA-Z0-9-_.~%]", "");
    }

    public static String getDomainFromPreference() {
        domain = SharedPref.read(SharedPref.DOMAIN, "events");
        return domain;
    }

    public static String getActualDomain() throws IOException {
        Document doc = Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/actual_adres.php").get();
        domain = "http://" + doc.getElementById("domain").text();
        SharedPref.write(SharedPref.DOMAIN, domain);
        return domain;
    }

    public static void alarm(String Title, String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void logout() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Jsoup.connect(domain + "/logout/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();
        cookie = "";
        SharedPref.write(SharedPref.COOKIE, cookie);
        SharedPref.write(SharedPref.AUTH, false);

    }

    public static void subscibe(final int id, final int on_off) {
        boolean auth = SharedPref.read(SharedPref.AUTH, false);
        Log.d("unsubscribe", domain + "/profile/subscriptions/" + id + "/?checked=" + on_off);
        if (auth) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Jsoup.connect(domain + "/profile/subscriptions/" + id + "/?checked=" + on_off)
                                .cookies(getCookies())
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

    public static void putToViewed(final String id, final int check) {
//        NetworkService.getInstance()
//                .getSerials()
//                .putViewed(id, check).enqueue(new Callback<PutViewed>() {
//            @Override
//            public void onResponse(@NonNull Call<PutViewed> call, @NonNull Response<PutViewed> response) {
//                PutViewed post = response.body();
//                Log.d("retrofit", "viewed " + response.code());
//                if (post != null)
//                    Toast.makeText(context, post.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<PutViewed> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });

        boolean auth = SharedPref.read(SharedPref.AUTH, false);
        Log.d("unsubscribe", domain + "/profile/viewed/" + id + "/?checked=" + check);
        if (auth) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Jsoup.connect(domain + "/profile/viewed/" + id + "/?checked=" + check)
                                .cookies(getCookies())
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

    public void setCookie(String cookie, Context context) {
        SharedPref.write(SharedPref.COOKIE, cookie);
        SharedPref.write(SharedPref.AUTH, true);
        Utils.cookie = cookie;
    }

    public void mass_subscribe() {


    }
}
