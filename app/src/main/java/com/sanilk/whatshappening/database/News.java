package com.sanilk.whatshappening.database;

/**
 * Created by sanil on 22/4/18.
 */

public class News {
    private int newsId;
    private String title;
    private String description;
    private String url;
    private String imageUrl;

    public News(int newsId, String title, String description, String imageUrl, String url) {
        this.newsId = newsId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public News(String title, String description, String imageUrl, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
    }


    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
