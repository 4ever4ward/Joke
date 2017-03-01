package ua.matvienko_apps.joke.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandr on 28/02/2017.
 */

public class AppDBHelper extends SQLiteOpenHelper {

    public AppDBHelper(Context context, String db_name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, AppDBContract.DB_NAME, null, AppDBContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createJokesTableQuery = "CREATE TABLE "
                + AppDBContract.JokeEntries.TABLE_NAME + " ("
                + AppDBContract.JokeEntries.COLUMN_JOKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AppDBContract.JokeEntries.COLUMN_JOKE_DATE + " INTEGER NOT NULL, "
                + AppDBContract.JokeEntries.COLUMN_JOKE_CATEGORY + " TEXT NOT NULL, "
                + AppDBContract.JokeEntries.COLUMN_JOKE_TEXT + " TEXT NOT NULL, "
                + AppDBContract.JokeEntries.COLUMN_JOKE_LIKES + " INTEGER NOT NULL, "
                + AppDBContract.JokeEntries.COLUMN_JOKE_DISLIKES + " INTEGER NOT NULL " + " );";

        String createFavouritesTableQuery = "CREATE TABLE "
                + AppDBContract.FavouritesEntries.TABLE_NAME + " ("
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_DATE + " INTEGER NOT NULL, "
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_CATEGORY + " TEXT NOT NULL, "
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_TEXT + " TEXT NOT NULL, "
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_LIKES + " INTEGER NOT NULL, "
                + AppDBContract.FavouritesEntries.COLUMN_JOKE_DISLIKES + " INTEGER NOT NULL " + " );";


        db.execSQL(createJokesTableQuery);
        db.execSQL(createFavouritesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.JokeEntries.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.FavouritesEntries.TABLE_NAME);
            onCreate(db);
        }
    }

}
