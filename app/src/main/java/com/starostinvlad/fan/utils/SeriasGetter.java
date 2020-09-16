package com.starostinvlad.fan.utils;

import android.content.Context;

import com.bluelinelabs.logansquare.LoganSquare;
import com.starostinvlad.fan.api.FanserJsonApi;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.starostinvlad.fan.utils.Utils.DOMAIN;
import static com.starostinvlad.fan.utils.Utils.PROXY;

public class SeriasGetter {
    public int items = 0;
    //    public String domain = RemoteConfig.read(RemoteConfig.DOMAIN);
    public Context context;
    int page = 1;
    private ArrayList<Seria> serias;

    public ArrayList<Seria> getSerias() {
        return serias;
    }

    public void setSerias(ArrayList<Seria> serias) {
        this.serias = serias;
    }

    public ArrayList<Seria> getNewSeries() throws IOException {
        items = 30;
        Document doc = null;
        String queryUrl = DOMAIN + "/api/v1/episodes";
        Connection.Response request = Jsoup.connect(queryUrl)
                .data("limit", "30")
                .data("offset", "0")
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .execute();
        if (request.statusCode() == 200)
            doc = request.parse();
        else return null;

        serias = seriesParser(doc);

        return this.serias;
    }

    public ArrayList<Seria> addNewSeries() throws IOException {
        items = 30;
        Document doc = null;
        String queryUrl = DOMAIN + "/api/v1/episodes";
        Connection.Response request = Jsoup.connect(queryUrl)
                .data("limit", "30")
                .data("offset", "" + (page += 30))
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .execute();
        if (request.statusCode() == 200)
            doc = request.parse();
        else return null;

        serias.addAll(seriesParser(doc));
        return this.serias;
    }

    public ArrayList<Seria> seriesParser(Document doc) throws IOException {
        FanserJsonApi fanserJsonApi = LoganSquare.parse(doc.body().html(), FanserJsonApi.class);
//        Log.d("SeriaGetter", fanserJsonApi.toString());
        ArrayList<Seria> serias = new ArrayList<>();
        if (fanserJsonApi.dataOfSerials.isEmpty()) {
            return null;
        } else
            for (FanserJsonApi.DataOfNewSer item : fanserJsonApi.dataOfSerials) {
                Seria seria = new Seria(item.newSerial.serialName, item.serialEpisode.episodeUrl,
                        item.serialEpisode.episodeImages.smallImage, item.serialEpisode.episodeName);
                serias.add(seria);
            }
        return serias;
    }

    public ArrayList<Seria> getSeriesOfSerial(String uri) throws IOException {
        String queryUrl = uri.contains("http") ? uri : (DOMAIN + uri);

        items = queryUrl.contains("profile") ? 12 : 32;

        if (queryUrl.lastIndexOf("/") < queryUrl.length() - 1)
            queryUrl += "/";
//        Log.d(getClass().getSimpleName(), "query: " + queryUrl);
        Document doc = null;
        Connection.Response request = null;
        if (PROXY != null)
            request = Jsoup.connect(queryUrl)
                    .ignoreContentType(true)
                    .proxy(PROXY)
                    .cookies(Utils.COOKIE)
                    .method(Connection.Method.GET)
                    .execute();
        else
            request = Jsoup.connect(queryUrl)
                    .ignoreContentType(true)
                    .cookies(Utils.COOKIE)
                    .method(Connection.Method.GET)
                    .execute();
//        Log.d(getClass().getSimpleName(), "code: " + request.statusCode());
//        if (!request.cookies().isEmpty()) {
//            utils.setCookie(request.cookies().toString(), context);
//            Log.d(getClass().getSimpleName(), "cookies: " + request.cookies().toString());
//        }
        if (request.statusCode() == 200) {
            doc = request.parse();
        } else {
            return null;
        }

        this.serias = serialParser(doc);
        return this.serias;
    }

    public ArrayList<Seria> addSeriesOfSerial(String uri) throws IOException {

        String queryUrl = uri.contains("http") ? uri : (DOMAIN + uri);


        if (queryUrl.lastIndexOf("/") < queryUrl.length() - 1)
            queryUrl += "/";

        queryUrl += "page/" + (++page) + "/";
//        Log.d("query", "url: " + queryUrl);
        items = 32;
        Document doc = null;
        Connection.Response request = null;

        if (PROXY != null) request = Jsoup.connect(queryUrl)
                .ignoreContentType(true)
                .cookies(Utils.COOKIE)
                .method(Connection.Method.GET)
                .execute();
        else
            request = Jsoup.connect(queryUrl)
                    .ignoreContentType(true)
                    .cookies(Utils.COOKIE)
                    .method(Connection.Method.GET)
                    .execute();

        if (request.statusCode() == 200)
            doc = request.parse();
        else return null;


        this.serias.addAll(serialParser(doc));
        return this.serias;
    }

    public ArrayList<Seria> serialParser(Document doc) {
        Elements item_serials = doc.select("div div.item-serial");
        ArrayList<Seria> serias = new ArrayList<>();
        for (Element item_serial : item_serials) {
            String title = item_serial.select("div.serial-bottom div.field-title a").text();
            String description = item_serial.select("div.serial-bottom div.field-description a").text();

            String href = item_serial.select("div.serial-top div.field-img a").attr("href");
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern
                    .matcher(item_serial.select("div.serial-top div.field-img")
                            .attr("style"));
            String img = matcher.find() ? matcher.group(1) : "";


            Seria seria = new Seria(title, href, img, description);
            serias.add(seria);
        }
        return serias;
    }


}
