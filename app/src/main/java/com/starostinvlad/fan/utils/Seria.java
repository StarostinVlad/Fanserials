package com.starostinvlad.fan.utils;

import java.io.Serializable;

/**
 * Created by Star on 17.10.2018.
 */

public class Seria implements Serializable {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getImage() {
        return img;
    }

    public void setImage(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Seria(String name, String uri, String img, String description) {
        this.name = name.isEmpty()?"":name;
        this.uri = uri.isEmpty()?"":uri;
        this.img = img.isEmpty()?"":img;
        this.description = description.isEmpty()?"":description;
    }

    String name;
    public String uri;
    String img;
    String description;
}