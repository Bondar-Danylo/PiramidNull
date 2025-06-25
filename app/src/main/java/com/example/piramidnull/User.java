package com.example.piramidnull;

public class User {
    public int id;
    public String username;
    public String password;
    public int voiceType;
    public String avatar;
    public String background;

    public User(int id, String username, String password, int voiceType, String avatar, String background) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.voiceType = voiceType;
        this.avatar = avatar;
        this.background = background;
    }
}

