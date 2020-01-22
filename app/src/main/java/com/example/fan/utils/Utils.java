package com.example.fan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Star on 03.02.2018.
 */

public class Utils {
    private static final Map<Character, String> letters = new HashMap<Character, String>();

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

    public static String decode(String decodeString) throws UnsupportedEncodingException {
        String Encode = "";
        //Log.d("tnp_cartoon", "dec: "+decodeString);
        decodeString = StringEscapeUtils.unescapeJava(decodeString);
        //Log.d("tnp_cartoon", "enc: "+decodeString);
        return decodeString;
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

    void saveFile(String string, Context context) throws IOException {
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

}
