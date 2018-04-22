package com.sanilk.whatshappening;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sanilk.whatshappening.database.News;
import com.sanilk.whatshappening.database.NewsHandler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Handler uiHandler;

    String from;
    String to;
    String q;

    private static final int PAGE_SIZE=20;

    int pageCount;
    final static String PAGE_COUNT_KEY="PAGE_COUNT";
    final static String QUERY_KEY="QUERY";

//    private static final int MESSAGE_UPDATE_LIST=1;

    NewsHandler newsHandler;
    boolean searchingFromQuery=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context=this;

        if(Build.VERSION.SDK_INT>=21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        newsHandler=new NewsHandler(context);

        if(getIntent().hasExtra(PAGE_COUNT_KEY)) {
            pageCount = getIntent().getIntExtra(PAGE_COUNT_KEY, 1);
            searchingFromQuery=true;
        }else{
            pageCount=1;
            searchingFromQuery=false;
        }

        if(getIntent().hasExtra(QUERY_KEY)){
            q=getIntent().getStringExtra(QUERY_KEY);
        }
        CardView nextCardView=findViewById(R.id.more_stories_activity_main);
        nextCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra(PAGE_COUNT_KEY, pageCount+1);
                intent.putExtra(QUERY_KEY, q);
                startActivity(intent);
            }
        });

        uiHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                NewsResponse news=(NewsResponse) msg.obj;

//                final TextView loadingTextView=findViewById(R.id.loading_activity_main);
                final VideoView loadingVideoView=findViewById(R.id.loading_activity_main);;
                loadingVideoView.stopPlayback();
                loadingVideoView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

                NewsResponse.ArticleResponse[] articleResponse=news.getArticles();
                CustomAdapter customAdapter=new CustomAdapter(context, articleResponse);

                ListView listView=findViewById(R.id.main_news_list);
                listView.setAdapter(customAdapter);
//                setListViewHeightBasedOnChildren(listView);
            }
        };

        final VideoView loadingVideoView=findViewById(R.id.loading_activity_main);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.loading);
        loadingVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        loadingVideoView.setVideoURI(video);
        loadingVideoView.start();

        ImageView bookmarksButton=findViewById(R.id.bookmarks_button_activity_main);
        bookmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BookmarkedNews.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        this.getSupportActionBar().hide();

        final LinearLayout filterLinearLayout=findViewById(R.id.filter_layout);

        ImageView filterImageView=findViewById(R.id.filter_button_activity_main);
        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterLinearLayout.getVisibility()==View.GONE){
                    filterLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    filterLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        updateNews();

        final EditText searchText=findViewById(R.id.search_activity_main);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    q=searchText.getText().toString();
                    updateNews();
                    searchText.clearFocus();
                    final VideoView loadingVideoView=findViewById(R.id.loading_activity_main);
                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                            + R.raw.loading);
                    ViewGroup.LayoutParams layoutParams=loadingVideoView.getLayoutParams();
                    layoutParams.height=80;
                    layoutParams.width=80;
                    loadingVideoView.setLayoutParams(layoutParams);
                    loadingVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                        }
                    });
                    loadingVideoView.setVideoURI(video);
                    loadingVideoView.start();
                    return true;
                }
                return false;
            }
        });

        final Calendar myCalendar=Calendar.getInstance();

        LinearLayout fromTimePicker=findViewById(R.id.from_time_picker_activity_main);
        final TextView fromTextView=findViewById(R.id.from_time_textview_activity_main);
        final DatePickerDialog.OnDateSetListener fromDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date=dayOfMonth+"-"+month+"-"+year;
                fromTextView.setText(date);

                q=searchText.getText().toString();
                from=date;
            }
        };
        fromTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, fromDateSetListener, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        LinearLayout toTimePicker=findViewById(R.id.to_time_picker_activity_main);
        final TextView toTextView=findViewById(R.id.to_time_textview_activity_main);
        final DatePickerDialog.OnDateSetListener toDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date=dayOfMonth+"-"+month+"-"+year;
                toTextView.setText(date);

                q=searchText.getText().toString();
                to=date;

            }
        };
        toTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, toDateSetListener, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()+10) * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void updateNews(){
        final Context context=this;
        if(q!=null && q!=""){
            NetworkCommunicationHandler.getEverythingNews(new NewsReceiver() {
                @Override
                public void onSuccess(NewsResponse news) {

                    Message message = new Message();
                    message.obj = news;
                    uiHandler.sendMessage(message);
                }

                @Override
                public void onFailure(final Error error) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, error.text, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }, null, q, from, to, PAGE_SIZE, pageCount);
            
        }else {
            NetworkCommunicationHandler.getTopHeadlines(new NewsReceiver() {
                @Override
                public void onSuccess(NewsResponse news) {

                    Message message = new Message();
                    message.obj = news;
                    uiHandler.sendMessage(message);
                }

                @Override
                public void onFailure(final Error error) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, error.text, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }, null, q, from, to, "in", PAGE_SIZE, pageCount);
        }
    }
}

class CustomAdapter extends ArrayAdapter<NewsResponse.ArticleResponse>{
    NewsResponse.ArticleResponse[] articleResponses;
    boolean[] bookmarked;
    Context context;
    NewsHandler newsHandler;

    public CustomAdapter(Context context, NewsResponse.ArticleResponse[] articleResponses){
        super(context, R.layout.main_row_layout, articleResponses);

        this.context=context;
        this.articleResponses=articleResponses;
        bookmarked=new boolean[articleResponses.length];

        newsHandler=new NewsHandler(context);

        for(int i=0;i<bookmarked.length;i++){
            bookmarked[i]=newsHandler.doesNewsExist(articleResponses[i].getUrl());
        }

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.main_row_layout, parent, false);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(articleResponses[position].getUrl()));
                context.startActivity(intent);
            }
        };
        final TextView titleTextView=v.findViewById(R.id.main_row_layout_title);
        final ImageView imageView=v.findViewById(R.id.main_row_layout_image);
        TextView descriptionTextView=v.findViewById(R.id.main_row_layout_description);

        titleTextView.setOnClickListener(onClickListener);
        descriptionTextView.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);

        titleTextView.setText(articleResponses[position].getTitle());
        descriptionTextView.setText(articleResponses[position].getDescription());
        final Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                imageView.setImageBitmap((Bitmap)msg.obj);
            }
        };
        final int pos=position;
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(articleResponses[pos].getUrlToImage()).openStream());
                    Message message=new Message();
                    message.obj=bmp;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

        final ImageView bookmarkImage=v.findViewById(R.id.bookmark_button_activity_main_row);
        if(bookmarked[position]){
            Bitmap bookmarked = BitmapFactory.decodeResource(context.getResources(), R.drawable.bookmarked);
            bookmarkImage.setImageBitmap(bookmarked);
        }else {
            bookmarkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bookmarked = BitmapFactory.decodeResource(context.getResources(), R.drawable.bookmarked);
                    bookmarkImage.setImageBitmap(bookmarked);

                    newsHandler.addNews(
                            new News(
                                    articleResponses[position].getTitle(),
                                    articleResponses[position].getDescription(),
                                    articleResponses[position].getUrlToImage(),
                                    articleResponses[position].getUrl()
                            )
                    );
                }
            });
        }

        TextView share=v.findViewById(R.id.share_button_activity_main);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String s="Check out what is happening - \n"+articleResponses[position].getTitle()+"\n Learn more - "+articleResponses[position].getUrl()
                        +"\n\n Sent via What's happening android app - your window to the world";
                intent.putExtra(Intent.EXTRA_SUBJECT, "What's happening");
                intent.putExtra(Intent.EXTRA_TEXT, s);
                context.startActivity(Intent.createChooser(intent, "Share via"));
            }
        });
        return v;
    }
}
