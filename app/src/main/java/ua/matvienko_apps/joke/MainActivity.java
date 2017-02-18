package ua.matvienko_apps.joke;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.matvienko_apps.joke.tindercard.SwipeFlingAdapterView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static JokeAdapter jokeAdapter;
    public static ViewHolder viewHolder;
    private ArrayList<Joke> jokeArrayList;
    private SwipeFlingAdapterView flingContainer;
    private View rootView;

    public static void removeBackground() {


        ViewHolder.background.setVisibility(View.GONE);
        jokeAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = getLayoutInflater().inflate(R.layout.activity_main, null);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card_container);


        jokeArrayList = new ArrayList<>();
        jokeArrayList.add(new Joke("1But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("2But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("3But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("4But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("5But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("6But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));
        jokeArrayList.add(new Joke("7But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness.", Joke.LIKE, Joke.STARRED_FALSE));


        jokeAdapter = new JokeAdapter(jokeArrayList, MainActivity.this);
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

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

                findViewById(R.id.leftSwipeImage).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                findViewById(R.id.rightSwipeImage).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);

                jokeAdapter.notifyDataSetChanged();
            }
        });

    }



    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;


    }

    public class JokeAdapter extends BaseAdapter {


        public List<Joke> jokeList;
        public Context context;

        public JokeAdapter(List<Joke> jokeList, Context context) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.joke_item_card, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.jokeText);
                ViewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.DataText.setText(jokeList.get(position).getJokeText());

            return rowView;
        }
    }
}
