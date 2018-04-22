package com.sanilk.whatshappening;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sanil on 21/4/18.
 */

public class NetworkCommunicationHandler {
    private static final String TAG="NETWORK_COMMUNICATION";
    private static String TEMP_COUNTRY="us";
    private static String URL="http://newsapi.org/v2/";
    private static String API_KEY="d9157c5d499b4d8cb6138354c90253f8";

    public static String getURL(String init, String[] keys, String[] values){
        init+="?";
        for(int i=0;i<keys.length;i++){
            init+=keys[i]+"="+values[i];
            if(i!=keys.length-1){
                init+="&";
            }
        }
        return init;
    }

    public static void getEverythingNews(NewsReceiver newsReceiver, String[] keywords, String q, String from, String to, int pageSize, int page){
        try {
            ArrayList<String> keys=new ArrayList<>();
            ArrayList<String> values=new ArrayList<>();
            if (q != null && q != "") {
                keys.add("q");
                values.add(q);
            }
            if(pageSize>0){
                keys.add("pageSize");
                values.add(pageSize+"");
            }
            if(page>0){
                keys.add("page");
                values.add(page+"");
            }
            if(to!=null && to!=""){
                keys.add("to");
                values.add(to);
            }
            if(from!=null && from!=""){
                keys.add("from");
                values.add(from);
            }
            keys.add("apiKey");
            values.add(API_KEY);
            URL url = new URL(getURL(
                    URL+"everything", keys.toArray(new String[]{}), values.toArray(new String[]{})
            ));
            getNews(newsReceiver, url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getTopHeadlines(NewsReceiver newsReceiver, String[] keywords, String q, String from, String to, String country, int pageSize, int page){
        try {
            ArrayList<String> keys=new ArrayList<>();
            ArrayList<String> values=new ArrayList<>();
            if (q != null && q != "") {
                keys.add("q");
                values.add(q);
            }
            if(pageSize>0){
                keys.add("pageSize");
                values.add(pageSize+"");
            }
            if(page>0){
                keys.add("page");
                values.add(page+"");
            }
            if(to!=null && to!=""){
                keys.add("to");
                values.add(to);
            }
            if(from!=null && from!=""){
                keys.add("from");
                values.add(from);
            }
            if(country!=null && country!=""){
                keys.add("country");
                values.add(country);
            }
            keys.add("apiKey");
            values.add(API_KEY);
            URL url = new URL(getURL(
                    URL+"top-headlines", keys.toArray(new String[]{}), values.toArray(new String[]{})
            ));
            getNews(newsReceiver, url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getNews(final NewsReceiver newsReceiver, final URL url){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(2000);
                        InputStream inputStream = url.openStream();
                        int c;
                        String output = "";
                        while ((c = inputStream.read()) != -1) {
                            output += (char) c;
                        }

                        Log.d(TAG, output);
                        System.out.print(output);

                        JSONParser jsonParser = new JSONParser();
                        NewsResponse newsResponse = jsonParser.parseNewsResponse(output);
                        if(newsResponse==null){
                            newsReceiver.onFailure(
                                    new Error("No more articles")
                            );
                            break;
                        }

                        newsReceiver.onSuccess(newsResponse);
                        break;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
}
