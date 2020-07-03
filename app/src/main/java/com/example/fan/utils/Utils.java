package com.example.fan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.example.fan.api.retro.SubscribeSerial;
import com.example.fan.api.retro.Subscribes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by Star on 03.02.2018.
 */

public class Utils {

    private static final Utils INSTANCE = new Utils();
    private static final Map<Character, String> letters = new HashMap<Character, String>();
    public static Map<String, String> cookies;
    public static String DOMAIN;
    public static String TOKEN;
    private static Context context;
    public static String ALL_SERIAL;
    public static boolean AUTH;

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

    public static Map<String, String> COOKIE = null;

    private Utils() {
    }

    public static void init(Context context) {
        DOMAIN = RemoteConfig.read(RemoteConfig.DOMAIN);
        ALL_SERIAL = RemoteConfig.read(RemoteConfig.ALL_SERIAL_JSON);
        Utils.context = context;
        if (SharedPref.contains(SharedPref.COOKIE))
            COOKIE = getCookies();
        else
            COOKIE = new HashMap<>();
        if (SharedPref.contains(SharedPref.AUTH))
            AUTH = SharedPref.read(SharedPref.AUTH, false);
        if (SharedPref.contains(SharedPref.TOKEN))
            TOKEN = SharedPref.read(SharedPref.TOKEN);

    }

    public synchronized static Utils getInstance() {

        return INSTANCE;
    }


    public static String decode(String decodeString) throws UnsupportedEncodingException {
        String Encode = "";
        //Log.d("tnp_cartoon", "dec: "+decodeString);
        decodeString = StringEscapeUtils.unescapeJava(decodeString);
        //Log.d("tnp_cartoon", "enc: "+decodeString);
        return decodeString;
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
        String COOKIE = SharedPref.read(SharedPref.COOKIE);
        Log.d("Utils", "getCookie: " + COOKIE);
        if (StringUtils.isNotEmpty(COOKIE))
            for (String str : COOKIE.substring(1, COOKIE.length() - 1).split(",")) {
                String[] kv = str.split("=");
                cookies.put(kv[0].trim(), kv[1].trim());
            }
        else
            cookies.put("a", "a");
        return cookies;
    }

    public static boolean isNetworkOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void aboutUs(Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(android.R.layout.select_dialog_singlechoice, null);


            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void alarm(Context context, String Title, String message) {
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
        DOMAIN = SharedPref.read(SharedPref.DOMAIN, "events");
        return DOMAIN;
    }

    public static String getActualDomain() throws IOException {
        Document doc = Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/actual_adres.php").get();
        DOMAIN = "http://" + doc.getElementById("domain").text();
        SharedPref.write(SharedPref.DOMAIN, DOMAIN);
        return DOMAIN;
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
                    Jsoup.connect(DOMAIN + "/logout/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();
        AUTH = false;
        SharedPref.remove(SharedPref.SUBSCRIBES);
        SharedPref.remove(SharedPref.COOKIE);
        SharedPref.remove(SharedPref.AUTH);

    }

    public static void subscribe(final int id, final int on_off) {
        Log.d("unsubscribe", DOMAIN + "/profile/subscriptions/" + id + "/?checked=" + on_off);
        if (AUTH) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = DOMAIN + "/profile/subscriptions/";
                        Log.d("Utils", "cookie before: " + COOKIE.toString());
                        Connection.Response res = Jsoup.connect(url + id + "/?checked=" + on_off)
                                .cookies(COOKIE)
                                .header("Accept", "application/json, text/plain, */*")
                                .header("Accept-Encoding", "gzip, deflate")
                                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                                .header("Connection", "keep-alive")
                                .header("Content-Length", "0")
                                .header("Origin", DOMAIN)
                                .header("Referer", url)
                                .userAgent("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Mobile Safari/537.36")
                                .header("host", DOMAIN.substring(DOMAIN.indexOf("://") + 3))
                                .ignoreContentType(true)
                                .method(Connection.Method.POST).execute();
//                        SharedPref.write(SharedPref.COOKIE, res.cookies().toString());
                        Log.d("Utils", "code:" + res.statusCode() + "cookie after : " + COOKIE.get("PHPSESSID"));
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

//        Log.d("unsubscribe", domain + "/profile/viewed/" + id + "/?checked=" + check);
        if (AUTH) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection.Response res = Jsoup.connect(DOMAIN + "/profile/viewed/" + id + "/?checked=" + check)
                                .cookies(COOKIE)
                                .ignoreContentType(true)
                                .method(Connection.Method.POST).execute();
//                        SharedPref.write(SharedPref.COOKIE, res.cookies().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }

    }

    public static void massSubscribe() {
//        Log.d("unsubscribe", domain + "/profile/viewed/" + id + "/?checked=" + check);
        if (AUTH) {
            Thread thread = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(DOMAIN + "/profile/subscriptions/")
                                .cookies(COOKIE)
                                .header("X-Requested-With", "XMLHttpRequest")
                                .ignoreContentType(true)
                                .get();
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Subscribes subscribes = gson.fromJson(doc.body().text(), Subscribes.class);
                        for (int id : subscribes.getSubscribes()) {
                            SubscribeSerial found = subscribes.getSerials().stream().filter(u -> u.getId().equals(id)).collect(Collectors.toList()).get(0);
                            if (found != null) {
                                String topic = translit(found.getName());
                                SharedPref.addSubscribes(topic);
                                subscribe(id, 1);
                                FirebaseMessaging.getInstance()
                                        .subscribeToTopic(topic);
                            }
                        }

                        Log.d("Utils", "body: " + doc.body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }

//        String topic = Utils.translit(viewed.getNext() != null ? viewed.getNext().getSerial().getName() :
//                viewed.getCurrent().getSerial().getName());
//        if (!SharedPref.containsSubscribe(topic)) {
////                            Log.d("retrofit", "subscribe to: " + topic);
//            SharedPref.addSubscribes(topic);
//            FirebaseMessaging.getInstance().subscribeToTopic(topic);
//        }
    }

    public static void login(final String email, final String pass) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response res = Jsoup.connect(DOMAIN + "/authorization/")
                            .data("email", email)
                            .data("password", pass)
                            .header("Connection", "keep-alive")
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .header("Accept", "application/json, text/javascript, */*; q=0.01")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("X-Requested-With", "XMLHttpRequest")
                            .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                            .userAgent("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Mobile Safari/537.36")
                            .ignoreContentType(true)
                            .method(Connection.Method.POST).execute();
                    Log.d("Utils", "code: " + res.body() + "cookie:" + res.cookies());
                    COOKIE = res.cookies();
                    SharedPref.write(SharedPref.COOKIE, COOKIE.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    public void mass_subscribe() {


    }
}
