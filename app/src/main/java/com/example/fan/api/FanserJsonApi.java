package com.example.fan.api;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

@JsonObject
public class FanserJsonApi {
    @JsonField(name="data")
    public ArrayList<DataOfNewSer> dataOfSerials;
    @JsonObject
    public static class DataOfNewSer {
        @JsonField(name="episode")
        public Episode serialEpisode;
        @JsonField(name="serial")
        public Serial newSerial;

        @JsonObject
        public static class Episode{
            @JsonField(name="id")
            public int episodeId;
            @JsonField(name="images")
            public JsonImages episodeImages;
            @JsonField(name="name")
            public String episodeName;
            @JsonField(name="ordinal")
            public Ordinal episodeOrdinal;
            @JsonField(name="url")
            public String episodeUrl;
            @JsonField(name="voices")
            public ArrayList<Voices> episodeVoices;

            @JsonObject
            public static class JsonImages{
                @JsonField(name="large")
                public String largeImage;
                @JsonField(name="medium")
                public String mediumImage;
                @JsonField(name="small")
                public String smallImage;
            }
            @JsonObject
            public static class Ordinal{
                @JsonField(name="episode")
                public int ordinalEpisode;
                @JsonField(name="season")
                public int ordinalSeason;
            }
            @JsonObject
            public static class Voices{
                @JsonField(name="episode")
                public String voicesName;
                @JsonField(name="url")
                public String voicesUrl;
            }

        }
        @JsonObject
        public static class Serial{
            @JsonField(name="closed")
            public Boolean serialClosed;
            @JsonField(name="name")
            public String serialName;
        }


    }

}

