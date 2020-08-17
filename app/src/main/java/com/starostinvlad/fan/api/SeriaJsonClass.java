package com.starostinvlad.fan.api;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

@JsonObject
public class SeriaJsonClass {
    @JsonField(name = "uris")
    public
    ArrayList<Uris> uris;

    @JsonObject
    public static class Uris {
        @JsonField(name = "name")
        public String title;
        @JsonField(name = "player")
        public String player;
    }


}
