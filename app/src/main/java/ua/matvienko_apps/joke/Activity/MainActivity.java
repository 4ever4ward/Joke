package ua.matvienko_apps.joke.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import ua.matvienko_apps.joke.Joke;
import ua.matvienko_apps.joke.R;
import ua.matvienko_apps.joke.adapters.CustomSpinnerAdapter;
import ua.matvienko_apps.joke.adapters.JokeCardAdapter;
import ua.matvienko_apps.joke.data.AppDBContract;
import ua.matvienko_apps.joke.data.DataProvider;
import ua.matvienko_apps.joke.tindercard.SwipeFlingAdapterView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static String APP_PREFERENCES = "JokePreferences";
    public static String HAS_VISITED = "HasVisited";
    public SharedPreferences sharedPreferences;
    public JokeCardAdapter jokeAdapter;
    private ImageView appImageView;
    private ImageView favouritesImageView;
    private ArrayList<Joke> jokeArrayList;
    private SwipeFlingAdapterView flingContainer;
    private AdView adView;


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

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);
        appImageView = (ImageView) findViewById(R.id.appImage);
        favouritesImageView = (ImageView) findViewById(R.id.favouritesImage);


        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!sharedPreferences.getBoolean(HAS_VISITED, false)) {
            /**
             * There you can do operation's when app has first launch
             * **/
            Log.d(TAG, "onCreate: firstLaunch");

            startAppTutorialAnimation();

            editor.putBoolean(HAS_VISITED, true);
            editor.apply();
        }


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


        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);


        DataProvider dataProvider = new DataProvider(getApplicationContext());

        // fill array lie data for init jokeAdapter
        jokeArrayList = new ArrayList<>();
        jokeArrayList = dataProvider.getAllJokes(AppDBContract.JokeEntries.TABLE_NAME);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerCustom);
        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Популярные");
        categories.add("Свежие");
        categories.add("О Вовочке");
        categories.add("Автомобильные");
        categories.add("Про Тёщу");
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this, categories);

        spinner.setAdapter(customSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jokeAdapter = new JokeCardAdapter(jokeArrayList, MainActivity.this);
        flingContainer.setAdapter(jokeAdapter);

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
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.background).setAlpha(0);
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);


//                Log.d(TAG, "onScroll: " + );

                findViewById(R.id.leftSwipeImage).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                findViewById(R.id.rightSwipeImage).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

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
}
