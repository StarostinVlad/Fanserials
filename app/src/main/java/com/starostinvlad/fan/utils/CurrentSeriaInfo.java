package com.starostinvlad.fan.utils;

import com.starostinvlad.fan.api.retro.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class CurrentSeriaInfo implements Serializable {
    public ArrayList<Player> players;
    public ArrayList<Seria> series;
    public String title;
    public String subTitle;
    public String url;
    public String id;
    public String description;
    public String previousSeria;
    public String nextSeria;
    public Map<String,String> cookies;

    public CurrentSeriaInfo(ArrayList<Player> players, String title, String subTitle, String url, String id, String description, String previousSeria, String nextSeria, Map<String, String> cookies, ArrayList<Seria> series) {
        this.players = players;
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.id = id;
        this.description = description;
        this.previousSeria = previousSeria;
        this.nextSeria = nextSeria;
        this.cookies = cookies;
        this.series = series;
    }
}
