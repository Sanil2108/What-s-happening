package com.sanilk.whatshappening;

/**
 * Created by sanil on 21/4/18.
 */

public interface NewsReceiver {
    void onSuccess(NewsResponse news);
    void onFailure(Error error);
}
