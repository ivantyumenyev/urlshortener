package com.yourl.service.urlstore.dto;

import java.util.Date;

/**
 * Created by Svirinstel on 14.05.17.
 */
public class ShortUrl {

    private String url;
    private final Date date = new Date();
    private int calls;


    public ShortUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void addCall(){
        calls++;
        System.out.println("Link " + url + " calls " + calls + " times");
    }

}
