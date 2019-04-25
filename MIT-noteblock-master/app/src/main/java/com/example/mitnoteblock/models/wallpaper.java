package com.example.mitnoteblock.models;

import com.google.firebase.database.Exclude;

public class wallpaper {


    @Exclude
    public String id;

    public String title, desc, url;
    @Exclude
    public String category;

    @Exclude
    public boolean isFavourite = false;

    public wallpaper(String id, String title, String desc, String url, String category) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.category = category;
    }
}
