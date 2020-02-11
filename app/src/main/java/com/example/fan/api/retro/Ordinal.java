package com.example.fan.api.retro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ordinal {

    @SerializedName("season")
    @Expose
    private Integer season;
    @SerializedName("episode")
    @Expose
    private Integer episode;

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

}
