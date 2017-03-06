package ua.matvienko_apps.joke.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import ua.matvienko_apps.joke.R;

public class OurAppsActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_apps);

        // Load an ad into the AdMob banner view.
//        AdView adView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .setRequestAgent("android_studio:ad_template").build();
//        adView.loadAd(adRequest);

    }

}
