package com.example.kchebolu.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by kchebolu on 7/22/16.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    public String[] urls;
    public MovieAdapter(Context c,String[] urls) {
        mContext = c;
        this.urls = urls;
    }
    public int getCount() {
        return urls.length;
    }
    public String getItem(int position) {
        return urls[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        String baseUrl = "http://image.tmdb.org/t/p/w500/";
        Picasso
                .with(mContext)
                .load(baseUrl + urls[position])
                .into(imageView);
        return imageView;
    }

    public void updateList(String[] urls) {
        this.urls = urls;
    }


}

