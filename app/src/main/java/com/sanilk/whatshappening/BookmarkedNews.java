package com.sanilk.whatshappening;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sanilk.whatshappening.database.News;
import com.sanilk.whatshappening.database.NewsHandler;

import java.net.URL;

public class BookmarkedNews extends AppCompatActivity {
    NewsHandler newsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked_news);

        if(Build.VERSION.SDK_INT>=21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        getSupportActionBar().hide();

        final Context context = this;

        newsHandler = new NewsHandler(this);
        final News[] news = newsHandler.getNews();

        final LinearLayout linearLayout = findViewById(R.id.news_list_bookmarked_activity);
        for (int position = 0; position < news.length; position++) {
            final View v = getLayoutInflater().inflate(R.layout.activity_bookmarked_row_layout, null);
            linearLayout.addView(v);
            final int pos = position;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news[pos].getUrl()));
                    context.startActivity(intent);
                }
            };
            final TextView titleTextView = v.findViewById(R.id.title_bookmarked_row);
            final ImageView imageView = v.findViewById(R.id.image_bookmarked_row);
            TextView descriptionTextView = v.findViewById(R.id.description_bookmarked_row);

            titleTextView.setOnClickListener(onClickListener);
            descriptionTextView.setOnClickListener(onClickListener);
            imageView.setOnClickListener(onClickListener);

            titleTextView.setText((news[position]==null)?"":news[position].getTitle());
            descriptionTextView.setText((news[position].getDescription()==null)?"":news[position].getDescription());
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    imageView.setImageBitmap((Bitmap) msg.obj);
                }
            };
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(new URL(news[pos].getImageUrl()).openStream());
                        Message message = new Message();
                        message.obj = bmp;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

            TextView delete=v.findViewById(R.id.delete_button_bookmarked_row);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v2) {
                    CardView inner=v.findViewById(R.id.inner_layout_bookmarked_row);
                    inner.setVisibility(View.GONE);
                    newsHandler.deleteNews(news[pos]);
                    news[pos]=null;
                }
            });

            TextView share = v.findViewById(R.id.share_button_bookmarked_row);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String s = "Check out what is happening - \n" + news[pos].getTitle() + "\n Learn more - " + news[pos].getUrl()
                            + "\n\n Sent via What's happening android app - your window to the world";
                    intent.putExtra(Intent.EXTRA_SUBJECT, "What's happening");
                    intent.putExtra(Intent.EXTRA_TEXT, s);
                    context.startActivity(Intent.createChooser(intent, "Share via"));
                }
            });

//        ListView listView=findViewById(R.id.news_list_bookmarked_activity);
//        listView.setAdapter(new BookmarkedCustomAdapter(this, news));
//

        }

        CardView backCardView = findViewById(R.id.back_activity_bookmarked);
        backCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    public void removeNews(int i){
    }
}

class BookmarkedCustomAdapter extends ArrayAdapter<News>{
    Context context;
    News[] news;

    NewsHandler newsHandler;

    public BookmarkedCustomAdapter(Context context, News[] news){
        super(context, R.layout.activity_bookmarked_row_layout, news);
        this.news=news;
        this.context=context;

        newsHandler=new NewsHandler(context);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.activity_bookmarked_row_layout, parent, false);
        if(news[position]==null){
            View view=new View(context);
            return view;
        }
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(news[position].getUrl()));
                context.startActivity(intent);
            }
        };
        final TextView titleTextView=v.findViewById(R.id.title_bookmarked_row);
        final ImageView imageView=v.findViewById(R.id.image_bookmarked_row);
        TextView descriptionTextView=v.findViewById(R.id.description_bookmarked_row);

        titleTextView.setOnClickListener(onClickListener);
        descriptionTextView.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);

        titleTextView.setText(news[position].getTitle());
        descriptionTextView.setText(news[position].getDescription());
        final Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                imageView.setImageBitmap((Bitmap)msg.obj);
            }
        };
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(news[position].getImageUrl()).openStream());
                    Message message=new Message();
                    message.obj=bmp;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

        TextView share=v.findViewById(R.id.share_button_bookmarked_row);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String s="Check out what is happening - \n"+news[position].getTitle()+"\n Learn more - "+news[position].getUrl()
                        +"\n\n Sent via What's happening android app - your window to the world";
                intent.putExtra(Intent.EXTRA_SUBJECT, "What's happening");
                intent.putExtra(Intent.EXTRA_TEXT, s);
                context.startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

        TextView delete=v.findViewById(R.id.delete_button_bookmarked_row);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsHandler.deleteNews(news[position]);
                removeNews(position);
            }
        });
        return v;
    }

    public void removeNews(int i){
        news[i]=null;
        this.notifyDataSetChanged();
    }
}