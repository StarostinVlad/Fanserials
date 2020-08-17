package com.starostinvlad.fan.api.retro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FANAPI {
    public List<Datum> getDatumList() {
        return datumList;
    }

    public void setDatumList(List<Datum> datumList) {
        this.datumList = datumList;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @SerializedName("data")
    @Expose
    private List<Datum> datumList = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination = null;
}
