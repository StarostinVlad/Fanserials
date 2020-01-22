package com.example.fan.api;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;


import java.util.ArrayList;

@JsonObject
public class FanserialsAlphabetApi {
    @JsonField(name = "data")
    public ArrayList<DataOfSerial> dataOfSerials;
    @JsonObject
    public static class DataOfSerial {
        @JsonField(name = "literal")
        public String literalOfSerial;
        @JsonField(name = "serials")
        public ArrayList<Serial> serialsList;
        @JsonObject
        public static class Serial{
            @JsonField(name = "href")
            public String serialHref;
            @JsonField(name = "title")
            public String serialTitle;
        }
    }
}
