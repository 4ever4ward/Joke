package com.sleuprua.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sleuprua.R;
import com.sleuprua.adapters.JokeCardAdapter;
import com.sleuprua.classes.Joke;
import com.sleuprua.data.DataProvider;
import com.sleuprua.tindercard.SwipeFlingAdapterView;

import java.util.ArrayList;


//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class FavouritesActivity extends AppCompatActivity {

    private final String TAG = FavouritesActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    //   TODO: Uncomment private AdView adView;
    private TextView jokeQuantityText;
    private DataProvider dataProvider;
    private SwipeFlingAdapterView flingContainer;
    private JokeCardAdapter jokeAdapter;
    private ArrayList<Joke> jokeBackStackList;
    private ArrayList<Joke> jokeArrayList;
    private ArrayList<Joke> jokeTwoArrayList;
    private AdView adView;
    private int favouritesListSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        // Find all of view's
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);
        jokeQuantityText = (TextView) findViewById(R.id.jokeQuantity);
        ImageView homeImageView = (ImageView) findViewById(R.id.homeImage);
        adView = (AdView) findViewById(R.id.adView);

        // Init array's for next using
        jokeArrayList = new ArrayList<>();
        jokeTwoArrayList = new ArrayList<>();
        jokeBackStackList = new ArrayList<>();


        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setProgressStyle(android.R.style.Widget_DeviceDefault_ProgressBar_Large);
        progressDialog.setCancelable(false);


        // Load an ad into the AdMob banner view.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Init DataProvider for create database connection and get data from it
        dataProvider = new DataProvider(getApplicationContext());

        // Get and set list of all joke's from favourites table
        new InitFavourites().execute();

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                jokeArrayList.remove(0);

                int position = findJokeInList((Joke) dataObject, jokeTwoArrayList);

                Log.e(TAG, "onLeftCardExit: " + position + ((Joke) dataObject).getJokeText());

                if (jokeArrayList.size() == 1) {

                    if (position < favouritesListSize - 1) {
                        jokeArrayList.add(0, jokeTwoArrayList.get(position + 1));
                    } else {
                        jokeArrayList.add(0, jokeTwoArrayList.get(0));
                    }
                }

                if (position + 2 > favouritesListSize)
                    jokeQuantityText.setText(1 + "/" + favouritesListSize);
                else
                    jokeQuantityText.setText(position + 2 + "/" + favouritesListSize);

                jokeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                jokeArrayList.remove(0);

                int position = findJokeInList((Joke) dataObject, jokeTwoArrayList);
                if (position != 0) {
                    jokeArrayList.add(0, jokeTwoArrayList.get(position - 1));
                } else {
                    jokeArrayList.add(0, jokeTwoArrayList.get(favouritesListSize - 1));
                }

                if (position == 0)
                    jokeQuantityText.setText(favouritesListSize + "/" + favouritesListSize);
                else
                    jokeQuantityText.setText(position + "/" + favouritesListSize);

                jokeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }

    private int findJokeInList(Joke joke, ArrayList<Joke> jokes) {
        for (int i = 0; i < jokes.size(); i++) {
            if (jokes.get(i).getJokeID() == joke.getJokeID())
                return i;
        }

        return -1;
    }

    class InitFavourites extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jokeArrayList = dataProvider.getFavourites();
            jokeTwoArrayList = dataProvider.getFavourites();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            jokeAdapter = new JokeCardAdapter(jokeArrayList, FavouritesActivity.this);
            flingContainer.setAdapter(jokeAdapter);
            jokeAdapter.notifyDataSetChanged();

            favouritesListSize = jokeArrayList.size();

            // Fill TextView, where displayed number and quantity of card's
            if (jokeArrayList.size() != 0) {
                jokeQuantityText.setText((jokeBackStackList.size() + 1) + "/" + favouritesListSize);
            } else {
                jokeQuantityText.setText((jokeBackStackList.size()) + "/" + favouritesListSize);
            }

            progressDialog.dismiss();
        }
    }


}
