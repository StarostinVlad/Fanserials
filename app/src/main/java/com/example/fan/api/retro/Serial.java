package com.example.fan.api.retro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Serial {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("closed")
    @Expose
    private Boolean closed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

}
