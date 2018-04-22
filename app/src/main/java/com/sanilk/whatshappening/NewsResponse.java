package com.sanilk.whatshappening;

import android.graphics.Bitmap;

/**
 * Created by sanil on 21/4/18.
 */

public class NewsResponse {
    public static final String STATUS_KEY="status";
    public static final String TOTAL_RESULTS_KEY="totalResults";
    public static final String ARTICLES_KEY="articles";

    private String status;
    private int totalArticles;
    private ArticleResponse[] articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalArticles() {
        return totalArticles;
    }

    public void setTotalArticles(int totalArticles) {
        this.totalArticles = totalArticles;
    }

    public ArticleResponse[] getArticles() {
        return articles;
    }

    public void setArticles(ArticleResponse[] articles) {
        this.articles = articles;
    }

    public static class ArticleResponse {

        public static final String TITLE_KEY = "title";
        public static final String SOURCE_KEY="source";
        public static final String AUTHOR_KEY="author";
        public static final String DESCRIPTION_KEY="description";
        public static final String URL_KEY="url";
        public static final String URL_TO_IMAGE_KEY="urlToImage";
        public static final String PUBLISHED_AT_KEY="publishedAt";

        private String title;
        private String source;
        private String author;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
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

        public String getUrlToImage() {
            return urlToImage;
        }

        public void setUrlToImage(String urlToImage) {
            this.urlToImage = urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
    }
}
