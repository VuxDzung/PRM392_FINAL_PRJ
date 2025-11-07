package com.example.prm392_final_prj.dto.request;

import java.util.Date;

public class ReviewDisplay {
    private int userId;
    private float rate;
    private String content;
    private String timeAgo;
    private String username;

    public ReviewDisplay(int userId, float rate, String content, String timeAgo, String username) {
        this.userId = userId;
        this.rate = rate;
        this.content = content;
        this.timeAgo = timeAgo;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
