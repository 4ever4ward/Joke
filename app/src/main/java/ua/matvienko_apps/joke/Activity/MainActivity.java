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
import ua.matvienko_apps.joke.data.DataProvider;
import ua.matvienko_apps.joke.tindercard.SwipeFlingAdapterView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static String APP_PREFERENCES = "JokePreferences";
    public static String HAS_VISITED = "HasVisited";
    public SharedPreferences sharedPreferences;
    ArrayList<Category> categories;
    private JokeCardAdapter jokeAdapter;
    private ImageView appImageView;
    private ImageView favouritesImageView;
    private ArrayList<Joke> jokeArrayList;
    private SwipeFlingAdapterView flingContainer;
    private AdView adView;
    private DataProvider dataProvider;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private Spinner spinner;

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
        appImageView = (ImageView) findViewById(R.id.appImage);
        favouritesImageView = (ImageView) findViewById(R.id.favouritesImage);
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


        appImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OurAppsActivity.class);
                startActivity(intent);
            }
        });

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
                jokeArrayList.remove(0);
                jokeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                findViewById(R.id.rightSwipeImage).setAlpha(0);
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
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);

                DownloadJokesForCategory download = new DownloadJokesForCategory(category.getCategoryID());
                download.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dataProvider.getAllJokesByCategoryFromDB(1);
//        dataProvider.getAll();

        UpgradeCategories upgradeCategories = new UpgradeCategories();
        upgradeCategories.execute();

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

    class UpgradeCategories extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            categories = dataProvider.getCategories();
//            jokeArrayList = dataProvider.getJokes();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            categorySpinnerAdapter = new CategorySpinnerAdapter(MainActivity.this, categories);
            spinner.setAdapter(categorySpinnerAdapter);


        }
    }

    class DownloadJokesForCategory extends AsyncTask {
        private int categoryID;

        public DownloadJokesForCategory(int categoryID) {
            this.categoryID = categoryID;
        }

        @Override
        protected void onPreExecute() {
            jokeArrayList.clear();
            jokeAdapter = new JokeCardAdapter(jokeArrayList, MainActivity.this);
            flingContainer.setAdapter(jokeAdapter);
            jokeAdapter.notifyDataSetChanged();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            jokeArrayList = dataProvider.getJokesByCategory(categoryID);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            jokeAdapter = new JokeCardAdapter(jokeArrayList, MainActivity.this);
            flingContainer.setAdapter(jokeAdapter);
            jokeAdapter.notifyDataSetChanged();
        }
    }
}
