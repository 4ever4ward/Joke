package ua.matvienko_apps.joke.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import ua.matvienko_apps.joke.R;
import ua.matvienko_apps.joke.adapters.CategorySpinnerAdapter;
import ua.matvienko_apps.joke.adapters.JokeCardAdapter;
import ua.matvienko_apps.joke.classes.Category;
import ua.matvienko_apps.joke.classes.Joke;
import ua.matvienko_apps.joke.classes.Utility;
import ua.matvienko_apps.joke.data.DataProvider;
import ua.matvienko_apps.joke.tindercard.SwipeFlingAdapterView;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static String APP_PREFERENCES = "JokePreferences";
    public static String HAS_VISITED = "HasVisited";
    public SharedPreferences sharedPreferences;
    ArrayList<Category> categories;
    private SwipeFlingAdapterView flingContainer;
    private AdView adView;
    private Spinner spinner;
    private DataProvider dataProvider;
    private JokeCardAdapter jokeAdapter;
    private ArrayList<Joke> jokeArrayList;
    private CategorySpinnerAdapter categorySpinnerAdapter;


    private boolean starred = false;

    /**
     * This function starts display tutorial arrow's and animate hiding
     */
    private void startAppTutorialAnimation() {
        final FrameLayout mainLayout = (FrameLayout) findViewById(R.id.container);
        final ImageView leftTutorialImageView = new ImageView(MainActivity.this);
        final ImageView rightTutorialImageView = new ImageView(MainActivity.this);

        rightTutorialImageView.setImageResource(R.drawable.tutorial_right);
        leftTutorialImageView.setImageResource(R.drawable.tutorial_left);

        FrameLayout.LayoutParams leftImageParams = new FrameLayout.LayoutParams(500, 900, Gravity.CENTER);
        FrameLayout.LayoutParams rightImageParams = new FrameLayout.LayoutParams(500, 900, Gravity.CENTER);

        leftImageParams.setMargins(0, 0, 0, 200);
        rightImageParams.setMargins(0, 100, 0, 0);

        rightTutorialImageView.setLayoutParams(rightImageParams);
        leftTutorialImageView.setLayoutParams(leftImageParams);

        leftTutorialImageView.setAlpha(0f);
        rightTutorialImageView.setAlpha(0f);

        mainLayout.addView(rightTutorialImageView);
        mainLayout.addView(leftTutorialImageView);


        rightTutorialImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mainLayout.removeView(rightTutorialImageView);
                        mainLayout.removeView(leftTutorialImageView);
                        break;
                    }
                }
                return true;
            }
        });

        leftTutorialImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mainLayout.removeView(rightTutorialImageView);
                        mainLayout.removeView(leftTutorialImageView);
                        break;
                    }
                }
                return true;
            }
        });


        rightTutorialImageView.animate()
                .setDuration(1200)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rightTutorialImageView.animate()
                                .setDuration(700)
                                .setInterpolator(new LinearInterpolator())
                                .alpha(0)
                                .translationX(700)
                                .setStartDelay(2000);
                    }
                });

        leftTutorialImageView.animate()
                .setDuration(1200)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        leftTutorialImageView.animate()
                                .setDuration(700)
                                .setInterpolator(new LinearInterpolator())
                                .alpha(0)
                                .translationX(-700)
                                .setStartDelay(2000);
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init all of views
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);
        ImageView favouritesImageView = (ImageView) findViewById(R.id.favouritesImage);
        adView = (AdView) findViewById(R.id.adView);
        spinner = (Spinner) findViewById(R.id.spinnerCustom);


        jokeArrayList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        // Create preferences for store data about first launch
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean(HAS_VISITED, false)) {
            /**
             * There you can do operation's when app has first launch
             * **/
            startAppTutorialAnimation();

            editor.putBoolean(HAS_VISITED, true);
            editor.apply();
        }


        // Load an ad into the AdMob banner view.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);


        favouritesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavouritesActivity.class);
                startActivity(intent);
            }
        });


        // Init DataProvider for create database connection and get data from it
        dataProvider = new DataProvider(getApplicationContext());
        // List of all jokes from standard table

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                findViewById(R.id.leftSwipeImage).setAlpha(0);

                new SetVoteToJoke(jokeArrayList.get(0), Joke.LIKE).execute();
                if (starred) {
                    new AddToFavourites(jokeArrayList.get(0)).execute();
                    starred = false;
                }

                jokeArrayList.remove(0);
                jokeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                findViewById(R.id.rightSwipeImage).setAlpha(0);

                new SetVoteToJoke(jokeArrayList.get(0), Joke.DISLIKE).execute();
                if (starred) {
                    new AddToFavourites(jokeArrayList.get(0)).execute();
                    starred = false;
                }

                jokeArrayList.remove(0);
                jokeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                findViewById(R.id.leftSwipeImage).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                findViewById(R.id.rightSwipeImage).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

                if (Math.abs(scrollProgressPercent) == 1) {
                    starred = flingContainer.getChildAt(1).findViewById(R.id.favouritesImageView).isActivated();
                }
            }
        });

        UpdateCategories updateCategories = new UpdateCategories();
        updateCategories.execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);

                jokeAdapter = new JokeCardAdapter(new ArrayList<Joke>(), MainActivity.this);
                flingContainer.setAdapter(jokeAdapter);
                jokeAdapter.notifyDataSetChanged();

                DownloadJokesForCategory download = new DownloadJokesForCategory(category.getCategoryID());
                download.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
//        // If banner not shown than hide adView
//        if(!adView.isShown()) {
//            adView.destroy();
//            adView.setVisibility(View.GONE);
//        } else {
//            adView.setVisibility(View.VISIBLE);
//        }

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

    class UpdateCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            categorySpinnerAdapter = new CategorySpinnerAdapter(MainActivity.this, categories);
            spinner.setAdapter(categorySpinnerAdapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            categories = dataProvider.getCategories();
            return null;
        }
    }

    class DownloadJokesForCategory extends AsyncTask<Void, Void, Void> {
        private int categoryID;

        public DownloadJokesForCategory(int categoryID) {
            this.categoryID = categoryID;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (categoryID == Category.CATEGORY_FRESH_ID) {
                jokeArrayList = Utility.getNewestJokes(MainActivity.this);

            } else if (categoryID == Category.CATEGORY_POPULAR_ID) {
                jokeArrayList = Utility.getPopularJokes(MainActivity.this);
            } else {
                jokeArrayList = dataProvider.getJokesByCategory(categoryID);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            jokeAdapter = new JokeCardAdapter(jokeArrayList, MainActivity.this);
            flingContainer.setAdapter(jokeAdapter);
            jokeAdapter.notifyDataSetChanged();
        }
    }

    class SetVoteToJoke extends AsyncTask<Void, Void, Void> {

        private Joke joke;
        private int jokeVote;

        SetVoteToJoke(Joke joke, int jokeVote) {
            this.joke = joke;
            this.jokeVote = jokeVote;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dataProvider.setJokeVote(joke, jokeVote);
            return null;
        }
    }

    class AddToFavourites extends AsyncTask<Void, Void, Void> {

        Joke joke;

        AddToFavourites(Joke joke) {
            this.joke = joke;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dataProvider.addToFavourites(joke);
            return null;
        }
    }
}
