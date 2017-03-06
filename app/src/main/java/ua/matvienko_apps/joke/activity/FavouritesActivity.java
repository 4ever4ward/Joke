package ua.matvienko_apps.joke.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import ua.matvienko_apps.joke.R;
import ua.matvienko_apps.joke.adapters.JokeCardAdapter;
import ua.matvienko_apps.joke.classes.Joke;
import ua.matvienko_apps.joke.data.DataProvider;
import ua.matvienko_apps.joke.tindercard.SwipeFlingAdapterView;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class FavouritesActivity extends AppCompatActivity {

    private final String TAG = FavouritesActivity.class.getSimpleName();

    private AdView adView;
    private TextView jokeQuantityText;

    private DataProvider dataProvider;
    private SwipeFlingAdapterView flingContainer;
    private JokeCardAdapter jokeAdapter;
    private ArrayList<Joke> jokeBackStackList;
    private ArrayList<Joke> jokeArrayList;


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
        jokeBackStackList = new ArrayList<>();


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
                if (jokeArrayList.size() > 1) {
                    jokeBackStackList.add(jokeArrayList.get(0));
                    jokeArrayList.remove(0);
                }
                jokeAdapter.notifyDataSetChanged();

                jokeQuantityText.setText((jokeBackStackList.size() + 1) + "/" + favouritesListSize);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                if (jokeBackStackList.size() != 0) {
                    jokeArrayList.add(0, jokeBackStackList.get(jokeBackStackList.size() - 1));
                    jokeBackStackList.remove(jokeBackStackList.size() - 1);
                }
                jokeAdapter.notifyDataSetChanged();

                jokeQuantityText.setText((jokeBackStackList.size() + 1) + "/" + favouritesListSize);

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

    class InitFavourites extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            jokeArrayList = dataProvider.getFavourites();
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
        }
    }

}
