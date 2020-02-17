package com.example.fan.api.retro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viewed {

    @SerializedName("current")
    @Expose
    private Datum current;
    @SerializedName("next")
    @Expose
    private Datum next;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;

    public Datum getCurrent() {
        return current;
    }

    public void setCurrent(Datum current) {
        this.current = current;
    }

    public Datum getNext() {
        return next;
    }

    public void setNext(Datum next) {
        this.next = next;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }
}
