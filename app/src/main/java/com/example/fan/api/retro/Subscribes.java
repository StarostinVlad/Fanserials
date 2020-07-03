package com.example.fan.api.retro;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subscribes {

    @SerializedName("serials")
    @Expose
    private List<SubscribeSerial> serials = null;
    @SerializedName("subscribes")
    @Expose
    private List<Integer> subscribes = null;

    public List<SubscribeSerial> getSerials() {
        return serials;
    }

    public void setSerials(List<SubscribeSerial> serials) {
        this.serials = serials;
    }

    public List<Integer> getSubscribes() {
        return subscribes;
    }

    public void setSubscribes(List<Integer> subscribes) {
        this.subscribes = subscribes;
    }

}

