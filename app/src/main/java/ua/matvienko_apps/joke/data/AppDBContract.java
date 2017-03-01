package ua.matvienko_apps.joke.data;

import android.provider.BaseColumns;

/**
 * Created by Alexandr on 28/02/2017.
 */

public final class AppDBContract {

    public static String DB_NAME = "joke_app.db";
    public static int DB_VERSION = 1;

    public static abstract class JokeEntries implements BaseColumns {
        public static String TABLE_NAME = "jokes";

        public static String COLUMN_JOKE_ID = "_ID";
        public static String COLUMN_JOKE_DATE = "date";
        public static String COLUMN_JOKE_CATEGORY = "category";
        public static String COLUMN_JOKE_TEXT = "text";
        public static String COLUMN_JOKE_LIKES = "likes";
        public static String COLUMN_JOKE_DISLIKES = "dislikes";
    }

    public abstract static class FavouritesEntries extends JokeEntries implements BaseColumns {
        public static String TABLE_NAME = "favourites";
    }

}
