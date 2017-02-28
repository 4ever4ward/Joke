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

import java.util.List;

import ua.matvienko_apps.joke.CustomScrollView;
import ua.matvienko_apps.joke.Joke;
import ua.matvienko_apps.joke.R;


/**
 * Created by Alexandr on 18/02/2017.
 */
public class JokeCardAdapter extends BaseAdapter {

    private final String TAG = JokeCardAdapter.class.getSimpleName();
    public ViewHolder viewHolder;
    public List<Joke> jokeList;
    public Context context;

    public JokeCardAdapter(List<Joke> jokeList, Context context) {
        this.jokeList = jokeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return jokeList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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
            rootView = inflater.inflate(R.layout.joke_item_card, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.jokeTextView = (TextView) rootView.findViewById(R.id.jokeText);
            viewHolder.favouritesView = (ImageView) rootView.findViewById(R.id.starredView);
            viewHolder.clipView = (ImageView) rootView.findViewById(R.id.copyToClipBoardView);
            viewHolder.myScrollView = (CustomScrollView) rootView.findViewById(R.id.myScroll);

            viewHolder.myScrollView.setEnableScrolling(false);

            viewHolder.favouritesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setActivated(!v.isActivated());
                }
            });

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
        viewHolder.jokeTextView.setText(jokeList.get(position).getJokeText());

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

    }
}
