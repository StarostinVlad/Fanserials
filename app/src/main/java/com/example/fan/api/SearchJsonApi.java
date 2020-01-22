package com.example.fan.api;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

@JsonObject
public class SearchJsonApi {
    @JsonField(name = "data")
    public ArrayList<FoundSerials> foundSerialData;

    @JsonObject
    public static class FoundSerials {
        @JsonField(name = "description")
        public String serialDescription;
        @JsonField(name = "id")
        public int foundSerialId;
        @JsonField(name = "name")
        public String foundSerialName;
        @JsonField(name = "url")
        public String foundSerialUrl;

        @JsonField(name = "poster")
        public JsonImages foundSerialPoster;


        @JsonObject
        public static class JsonImages {
            @JsonField(name = "large")
            public String largeImage;
            @JsonField(name = "medium")
            public String mediumImage;
            @JsonField(name = "small")
            public String smallImage;

        }
    }

}
