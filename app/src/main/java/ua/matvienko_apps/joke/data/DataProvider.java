package ua.matvienko_apps.joke.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ua.matvienko_apps.joke.Joke;

/**
 * Created by Alexandr on 01/03/2017.
 */

public class DataProvider {

    private AppDBHelper appDBHelper;

    public DataProvider(Context context) {
        this.appDBHelper = new AppDBHelper(context, AppDBContract.DB_NAME, null, AppDBContract.DB_VERSION);
    }


    public void addJoke(Joke joke, String tableName) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AppDBContract.JokeEntries.COLUMN_JOKE_DATE, joke.getJokeDate());
        contentValues.put(AppDBContract.JokeEntries.COLUMN_JOKE_CATEGORY, joke.getJokeCategory());
        contentValues.put(AppDBContract.JokeEntries.COLUMN_JOKE_TEXT, joke.getJokeText());
        contentValues.put(AppDBContract.JokeEntries.COLUMN_JOKE_LIKES, joke.getJokeLikes());
        contentValues.put(AppDBContract.JokeEntries.COLUMN_JOKE_DISLIKES, joke.getJokeDislikes());

        db.insert(tableName, null, contentValues);
        db.close();
    }


    public ArrayList<Joke> getAllJokes(String tableName) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        ArrayList<Joke> jokesList = new ArrayList<Joke>();

        String query = "SELECT * FROM "
                + tableName
                + " ORDER BY "
                + AppDBContract.JokeEntries.COLUMN_JOKE_DATE + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Joke joke = new Joke(cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5));

                jokesList.add(joke);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return jokesList;
    }

    public ArrayList<Joke> getAllJokesByCategory(String tableName, String category) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        ArrayList<Joke> jokesList = new ArrayList<Joke>();

        String query = "SELECT * FROM "
                + tableName
                + " WHERE "
                + AppDBContract.JokeEntries.COLUMN_JOKE_CATEGORY + " == " + category
                + " ORDER BY "
                + AppDBContract.JokeEntries.COLUMN_JOKE_DATE + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Joke joke = new Joke(cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5));

                jokesList.add(joke);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return jokesList;
    }
}
