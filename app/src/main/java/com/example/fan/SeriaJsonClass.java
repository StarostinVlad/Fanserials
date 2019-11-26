package com.example.fan;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

@JsonObject
public class SeriaJsonClass {
    @JsonField(name = "uris")
    ArrayList<Uris> uris;

    @JsonObject
    static class Uris {
        @JsonField(name = "name")
        public String title;
        @JsonField(name = "player")
        public String player;
    }


}
