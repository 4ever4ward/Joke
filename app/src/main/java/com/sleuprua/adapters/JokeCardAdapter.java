package com.sleuprua.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sleuprua.R;
import com.sleuprua.activity.FavouritesActivity;
import com.sleuprua.classes.CustomScrollView;
import com.sleuprua.classes.Joke;
import com.sleuprua.classes.Utility;

import java.util.ArrayList;


/**
 * Created by Alexandr on 18/02/2017.
 */
public class JokeCardAdapter extends BaseAdapter {

    private final String TAG = JokeCardAdapter.class.getSimpleName();
    public Context context;
    Joke joke;
    private ViewHolder viewHolder;
    private ArrayList<Joke> jokeList;

    public JokeCardAdapter(ArrayList<Joke> jokeList, Context context) {
        this.jokeList = jokeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return jokeList.size();
    }

    @Override
    public Object getItem(int position) {
        return jokeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View rootView = convertView;


        joke = jokeList.get(position);

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(R.layout.item_joke_card, parent, false);

            viewHolder = new ViewHolder(rootView);

            viewHolder.myScrollView.setEnableScrolling(false);

            viewHolder.favouritesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setActivated(!v.isActivated());

                    if (v.isActivated()) {

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View layout = inflater.inflate(R.layout.toast_layout, null);

                        ((ImageView) layout.findViewById(R.id.toast_image)).setImageResource(R.drawable.ic_starf);
                        ((TextView) layout.findViewById(R.id.toast_text)).setText("Добавлено в избранные");

                        Toast toast = new Toast(context);
                        toast.setGravity(Gravity.BOTTOM, 0, 275);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                }
            });

//            if (joke.getJokeFavorites() == Joke.FAVOURITES_TRUE) {
//                viewHolder.favouritesView.setActivated(true);
//                viewHolder.favouritesView.setClickable(false);
//            }

            if (context.getClass().getSimpleName().equals(FavouritesActivity.class.getSimpleName())) {
                viewHolder.favouritesView.setActivated(true);

                viewHolder.favouritesView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        joke = jokeList.get(0);

                        v.setActivated(!v.isActivated());

                        if (!v.isActivated()) {
                            Utility.deleteFromFavourites(context, joke);

                        }
                    }
                });

            }

            viewHolder.clipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", viewHolder.jokeTextView.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    ((ImageView) v).setImageResource(R.drawable.ic_copyf);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View layout = inflater.inflate(R.layout.toast_layout, null);

                    Toast toast = new Toast(context);
                    toast.setGravity(Gravity.BOTTOM, 0, 275);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();


                }
            });

            viewHolder.shareView = (ImageView) rootView.findViewById(R.id.shareView);
            viewHolder.shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(createShareJokeIntent(viewHolder.jokeTextView.getText().toString(), "#Joke"));
                }
            });

            rootView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.jokeTextView.setText(joke.getJokeText());

        return rootView;
    }

    private Intent createShareJokeIntent(String text, String HASH_TAG) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + HASH_TAG);
        return shareIntent;
    }

    public class ViewHolder {
        public TextView jokeTextView;
        public ImageView favouritesView;
        public ImageView clipView;
        public ImageView shareView;
        public CustomScrollView myScrollView;

        public ViewHolder(View view) {
            jokeTextView = (TextView) view.findViewById(R.id.jokeText);
            favouritesView = (ImageView) view.findViewById(R.id.favouritesImageView);
            clipView = (ImageView) view.findViewById(R.id.copyToClipBoardView);
            myScrollView = (CustomScrollView) view.findViewById(R.id.myScroll);
        }

    }
}
