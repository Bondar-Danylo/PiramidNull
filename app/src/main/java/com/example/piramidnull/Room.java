package com.example.piramidnull;

public class Room {
    private final String title;
    private final int imageRes;

    public Room(String title, int imageRes) {
        this.title = title;
        this.imageRes = imageRes;
    }

    public String getTitle() { return title; }
    public int getImageRes() { return imageRes; }
}