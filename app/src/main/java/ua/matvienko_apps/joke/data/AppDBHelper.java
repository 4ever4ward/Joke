package ua.matvienko_apps.joke.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandr on 28/02/2017.
 */

public class AppDBHelper extends SQLiteOpenHelper {

    public AppDBHelper(Context context, String db_name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createCategoryTableQuery = "CREATE TABLE "
                + AppDBContract.CategoryEntry.TABLE_NAME + " ("
                + AppDBContract.CategoryEntry.COLUMN_ID + " INTEGER NOT NULL, "
                + AppDBContract.CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL " + " );";

        String createJokesTableQuery = "CREATE TABLE "
                + AppDBContract.JokeEntry.TABLE_NAME + " ("
                + AppDBContract.JokeEntry.COLUMN_ID + " INTEGER NOT NULL, "
                + AppDBContract.JokeEntry.COLUMN_JOKE_DATE + " INTEGER NOT NULL, "
                + AppDBContract.JokeEntry.COLUMN_JOKE_TEXT + " TEXT NOT NULL, "
                + AppDBContract.JokeEntry.COLUMN_JOKE_LIKES + " INTEGER NOT NULL, "
                + AppDBContract.JokeEntry.COLUMN_JOKE_DISLIKES + " INTEGER NOT NULL " + " );";

        String createFavouritesTableQuery = "CREATE TABLE "
                + AppDBContract.FavouritesEntry.TABLE_NAME + " ("
                + AppDBContract.FavouritesEntry.COLUMN_ID + " INTEGER NOT NULL, "
                + AppDBContract.FavouritesEntry.COLUMN_JOKE_DATE + " INTEGER NOT NULL, "
                + AppDBContract.FavouritesEntry.COLUMN_JOKE_TEXT + " TEXT NOT NULL, "
                + AppDBContract.FavouritesEntry.COLUMN_JOKE_LIKES + " INTEGER NOT NULL, "
                + AppDBContract.FavouritesEntry.COLUMN_JOKE_DISLIKES + " INTEGER NOT NULL " + " );";

        String createPrePackingTableQuery = " CREATE TABLE "
                + AppDBContract.PrePackingEntry.TABLE_NAME + " ("
                + AppDBContract.PrePackingEntry.COLUMN_JOKE_ID + " INTEGER NOT NULL, "
                + AppDBContract.PrePackingEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "


                + " FOREIGN KEY (" + AppDBContract.PrePackingEntry.COLUMN_JOKE_ID + ") REFERENCES " +
                AppDBContract.JokeEntry.TABLE_NAME + " (" + AppDBContract.JokeEntry.COLUMN_ID + ") "
//                + " FOREIGN KEY (" + AppDBContract.PrePackingEntry.COLUMN_ID + ") REFERENCES " +
//                AppDBContract.CategoryEntry.TABLE_NAME + " (" + AppDBContract.CategoryEntry.COLUMN_ID + ") "

                + ");";

        db.execSQL(createCategoryTableQuery);
        db.execSQL(createJokesTableQuery);
        db.execSQL(createFavouritesTableQuery);
        db.execSQL(createPrePackingTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.CategoryEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.JokeEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.FavouritesEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AppDBContract.PrePackingEntry.TABLE_NAME);
            onCreate(db);
        }
    }

}
