package com.example.kchebolu.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    MovieAdapter mMovieAdapter;
    private GridView gridView;
    static int index;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "Myprefs";
    public static final String Options = "popular";

    public MovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieFragment newInstance(String param1, String param2) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] urls = {};
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        if(mMovieAdapter == null) {
            mMovieAdapter = new MovieAdapter(getActivity(), urls);
        }
        gridView = (GridView) rootView.findViewById(R.id.list_item_movie);
        gridView.setAdapter(mMovieAdapter);
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.action_popular:
                index = 0;
                new FetchMovieTask().execute("popular");
                return true;
            case R.id.action_toprated:
                index = 0;
                new FetchMovieTask().execute("top_rated");
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }
    public void updateMovies() {
        if(sharedpreferences != null) {
            String input = sharedpreferences.getString(MyPREFERENCES,Options);
            if (input == "top_rated") {
                new FetchMovieTask().execute("top_rated");
            } else {
                new FetchMovieTask().execute("popular");
            }
        }
        else {
            new FetchMovieTask().execute("popular");
            }
    }


    public void onStart() {
        super.onStart();
        gridView.smoothScrollToPosition(index);
        if(index == 0) {
           updateMovies();
        }
    }

    public void onPause() {
        index = gridView.getFirstVisiblePosition();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public class FetchMovieTask extends AsyncTask<String,Void,String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        String movieJsonStr = null;

//      isOnline: Check online connectivity
        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }

        @Override
        protected String[] doInBackground(String... params) {

            if(!isOnline()) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.

            try {
                // Construct the URL for the The Movie database query
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String APPKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath(params[0])
                        .appendQueryParameter(APPKEY_PARAM, "b351e46a179aba9f982e673d415d3c2b")
                        .build();

                URL url = new URL(builtUri.toString());

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(MyPREFERENCES,params[0]);
                editor.apply();
                // Create the request to TheMovieDataBase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieData("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMovieAdapter = new MovieAdapter(getActivity(),result);
                gridView.setSelection(index);
                gridView.setAdapter(mMovieAdapter);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {

//                      Send data to the detail activity using Bundle
                        Bundle bundle = new Bundle();
                        String movie = mMovieAdapter.getItem(position);
                        try {
                            String overview[] = getMovieData("overview");
                            String title[] = getMovieData("title");
                            String release[] = getMovieData("release_date");
                            String vote[] = getMovieData("vote_average");
                            bundle.putString("MOVIE_IMAGE",movie);
                            bundle.putString("MOVIE_OVERVIEW",overview[position]);
                            bundle.putString("MOVIE_TITLE",title[position]);
                            bundle.putString("MOVIE_RELEASE",release[position]);
                            bundle.putString("MOVIE_VOTE",vote[position]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtras(bundle);
                        startActivity(intent);
                    }
                });

            }
        }

//      getMovieData: This method will return the JSON response output for the requested data

        public String[] getMovieData(String movieStr) throws JSONException {

            final String OWM_RESULTS = "results";
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray resultArray = forecastJson.getJSONArray(OWM_RESULTS);
            String[] strings = new String[resultArray.length()];
            for (int i = 0; i < resultArray.length();i++) {
                JSONObject imageObject = resultArray.getJSONObject(i);
                String path = imageObject.getString(movieStr);
                strings[i] = path;
            }
            return strings;
        }

    }


}
