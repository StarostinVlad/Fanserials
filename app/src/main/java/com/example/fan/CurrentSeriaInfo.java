package com.example.fan;

import java.io.Serializable;

public class CurrentSeriaInfo implements Serializable {
    public String Title;
    public String Voice;
    public String Url;

    public CurrentSeriaInfo(String title, String voice, String url) {
        this.Title = title;
        this.Voice = voice;
        this.Url = url;
    }
}
