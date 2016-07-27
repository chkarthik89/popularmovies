package com.example.kchebolu.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

public static class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Resources res = getResources();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        String movie= bundle.getString("MOVIE_IMAGE");
        String title = bundle.getString("MOVIE_TITLE");
        String description = bundle.getString("MOVIE_OVERVIEW");
        String release = bundle.getString("MOVIE_RELEASE");
        String vote = bundle.getString("MOVIE_VOTE");
        TextView titleText = (TextView) rootView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail);
        TextView release_text = (TextView) rootView.findViewById(R.id.release);
        TextView vote_text = (TextView) rootView.findViewById(R.id.vote);
        TextView overviewText = (TextView) rootView.findViewById(R.id.text);
        String baseUrl = "http://image.tmdb.org/t/p/w185/";
        titleText.setText(title);
        String release_data = res.getString(R.string.RELEASE) + release;
        String rating_data = res.getString(R.string.RATING) + vote;
        release_text.setText(release_data);
        vote_text.setText(rating_data);
        overviewText.setText(description);
        Picasso
                .with(getActivity())
                .load(baseUrl + movie)
                .into(imageView);

        return rootView;
    }
}
}
