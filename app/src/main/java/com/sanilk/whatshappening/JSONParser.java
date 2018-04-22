package com.sanilk.whatshappening;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sanil on 21/4/18.
 */

public class JSONParser {
    private static final int MAX_TOTAL=100;

    public NewsResponse parseNewsResponse(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            NewsResponse newsResponse = new NewsResponse();
            newsResponse.setStatus(jsonObject.getString(NewsResponse.STATUS_KEY));
            newsResponse.setTotalArticles(jsonObject.getInt(NewsResponse.TOTAL_RESULTS_KEY));


            JSONArray jsonArray=jsonObject.getJSONArray(NewsResponse.ARTICLES_KEY);
            NewsResponse.ArticleResponse[] articles=new NewsResponse.ArticleResponse[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++){
                NewsResponse.ArticleResponse article=parseArticleResponse(jsonArray.getJSONObject(i));
                articles[i]=article;
            }
            newsResponse.setArticles(articles);

            return newsResponse;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private NewsResponse.ArticleResponse parseArticleResponse(JSONObject jsonObject){
        try{
            NewsResponse.ArticleResponse articleResponse=new NewsResponse.ArticleResponse();
            articleResponse.setAuthor(jsonObject.getString(NewsResponse.ArticleResponse.AUTHOR_KEY));
            articleResponse.setDescription(jsonObject.getString(NewsResponse.ArticleResponse.DESCRIPTION_KEY));
            articleResponse.setPublishedAt(jsonObject.getString(NewsResponse.ArticleResponse.PUBLISHED_AT_KEY));
            articleResponse.setUrl(jsonObject.getString(NewsResponse.ArticleResponse.URL_KEY));
            articleResponse.setUrlToImage(jsonObject.getString(NewsResponse.ArticleResponse.URL_TO_IMAGE_KEY));
            articleResponse.setSource(jsonObject.getString(NewsResponse.ArticleResponse.SOURCE_KEY));
            articleResponse.setTitle(jsonObject.getString(NewsResponse.ArticleResponse.TITLE_KEY));

            return articleResponse;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
