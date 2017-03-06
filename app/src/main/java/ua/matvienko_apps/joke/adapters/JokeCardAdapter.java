package ua.matvienko_apps.joke.adapters;

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

import java.util.ArrayList;

import ua.matvienko_apps.joke.R;
import ua.matvienko_apps.joke.activity.FavouritesActivity;
import ua.matvienko_apps.joke.classes.CustomScrollView;
import ua.matvienko_apps.joke.classes.Joke;


/**
 * Created by Alexandr on 18/02/2017.
 */
public class JokeCardAdapter extends BaseAdapter {

    private final String TAG = JokeCardAdapter.class.getSimpleName();
    public Context context;
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

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(R.layout.item_joke_card, parent, false);

            viewHolder = new ViewHolder(rootView);

            viewHolder.myScrollView.setEnableScrolling(false);

            viewHolder.favouritesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setActivated(!v.isActivated());
                }
            });

            if (context.getClass().getSimpleName().equals(FavouritesActivity.class.getSimpleName()))
                viewHolder.favouritesView.callOnClick();

            viewHolder.clipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", viewHolder.jokeTextView.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View layout = inflater.inflate(R.layout.toast_layout, null);

                    Toast toast = new Toast(context);
                    toast.setGravity(Gravity.BOTTOM, 0, 225);
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
        viewHolder.jokeTextView.setText(jokeList.get(position).getJokeText() + " like=" + jokeList.get(position).getJokeLikes() + " dislike=" + jokeList.get(position).getJokeDislikes());

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
            favouritesView = (ImageView) view.findViewById(R.id.starredView);
            clipView = (ImageView) view.findViewById(R.id.copyToClipBoardView);
            myScrollView = (CustomScrollView) view.findViewById(R.id.myScroll);
        }

    }
}
