package ua.matvienko_apps.joke.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ua.matvienko_apps.joke.classes.Category;
import ua.matvienko_apps.joke.classes.Joke;
import ua.matvienko_apps.joke.classes.Utility;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Alexandr on 01/03/2017.
 */

public class DataProvider {

    private final String PARAM_UUID = "uuid";
    private final String API_URL = "http://app1.app.trafficterminal.com/api/list";
    private final String RESPONSE = "response";
    private AppDBHelper appDBHelper;
    private Context context;

    public DataProvider(Context context) {
        this.appDBHelper = new AppDBHelper(context, AppDBContract.DB_NAME, null, AppDBContract.DB_VERSION);
        this.context = context;
    }

    private String inputStreamToString(InputStream is) throws IOException {
        String line;
        StringBuilder total = new StringBuilder();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        while ((line = rd.readLine()) != null)
            total.append(line);

        return total.toString();
    }


    public ArrayList<Joke> getJokes() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL);

        try {
            // Add parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String jokeJsonStr = inputStreamToString(response.getEntity().getContent());
            getJokeDataFromJson(jokeJsonStr);

        } catch (IOException | JSONException e) {
            Log.e(TAG, "getJokes: ", e);
        }
        return getAllJokesFromDB(AppDBContract.JokeEntry.TABLE_NAME);
    }

    public ArrayList<Joke> getJokesByCategory(int categoryID) {

        final String PARAM_CATEGORY_ID = "category_id";

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL);

        try {
            // Add parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            nameValuePairs.add(new BasicNameValuePair(PARAM_CATEGORY_ID, Integer.toString(categoryID)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String jokeJsonStr = inputStreamToString(response.getEntity().getContent());
            getJokeDataFromJson(jokeJsonStr);

        } catch (IOException | JSONException e) {
            Log.e(TAG, "getJokes: ", e);
        }
        return getAllJokesByCategoryFromDB(categoryID);
    }

    public ArrayList<Category> getCategories() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL);

        try {
            // Add parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String jsonStr = inputStreamToString(response.getEntity().getContent());

            // Put data from json to db
            getCategoriesFromJson(jsonStr);

        } catch (IOException | JSONException e) {
            Log.e(TAG, "getCategories: ERROR", e);
        }

        return getAllCategoriesFromDB();
    }

    private void getCategoriesFromJson(String jokeJsonStr) throws JSONException {
        final String CATEGORY_ID = "id";
        final String CATEGORY_NAME = "name";

        JSONObject dataJsonObj = new JSONObject(jokeJsonStr);
        JSONArray responseArrayJson = dataJsonObj.getJSONArray(RESPONSE);

        JSONArray categoriesJsonList = responseArrayJson.getJSONArray(1);

        for (int i = 0; i < categoriesJsonList.length(); i++) {
            JSONObject category = categoriesJsonList.getJSONObject(i);

            int categoryID = category.getInt(CATEGORY_ID);
            String categoryName = category.getString(CATEGORY_NAME);

            if (getCategoryByID(categoryID) == null)
                addCategory(new Category(categoryID, categoryName));
        }
    }

    private void getJokeDataFromJson(String jokeJsonStr) throws JSONException {
        final String ID = "id";
        final String CATEGORY = "category";
        final String DATE = "date_added";
        final String TEXT = "text";
        final String LIKES = "count_likes";
        final String DISLIKES = "count_dislikes";

        JSONObject dataJsonObj = new JSONObject(jokeJsonStr);
        JSONArray responseArrayJson = dataJsonObj.getJSONArray(RESPONSE);

        JSONObject jokeListJsonStr = responseArrayJson.getJSONObject(0);
        Iterator x = jokeListJsonStr.keys();
        JSONArray jokeListJsonArr = new JSONArray();

        while (x.hasNext()) {
            String key = (String) x.next();
            jokeListJsonArr.put(jokeListJsonStr.get(key));
        }

        for (int i = 0; i < jokeListJsonArr.length(); i++) {
            JSONObject joke = jokeListJsonArr.getJSONObject(i);

            String jokeDateStr = joke.getString(DATE);
            try {
                Calendar jokeDate = Utility.getDateFromString(jokeDateStr);

                int jokeID = joke.getInt(ID);
                String jokeCategory = joke.getString(CATEGORY);
                String jokeText = joke.getString(TEXT);
                int jokeLikes = joke.getInt(LIKES);
                int jokeDislikes = joke.getInt(DISLIKES);


                if (getJokeByID(jokeID, AppDBContract.JokeEntry.TABLE_NAME) == null) {

                    String[] jokeCategories = jokeCategory.substring(1, jokeCategory.length() - 1).split(",");

                    for (int j = 0; j < jokeCategories.length; j++) {
                        addJoke(new Joke(jokeID,
                                        jokeDate.getTimeInMillis(),
                                        jokeText,
                                        jokeLikes,
                                        jokeDislikes),
                                Integer.parseInt(jokeCategories[j]));
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "getJokeDataFromJson: ", e);
            }

        }


    }

    public ArrayList<Joke> getAllJokesByCategoryFromDB(int categoryID) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        ArrayList<Joke> jokesList = new ArrayList<Joke>();
        jokesList.add(new Joke(-1, 0, null, 0, 0));

        String query = "SELECT * FROM "
                + AppDBContract.JokeEntry.TABLE_NAME
                + " NATURAL JOIN " + AppDBContract.PrePackingEntry.TABLE_NAME
                + " WHERE " + AppDBContract.PrePackingEntry.COLUMN_CATEGORY_ID + " = " + categoryID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                if (jokesList.get(jokesList.size() - 1).getJokeID() != cursor.getInt(0)) {

                    Joke joke = new Joke(cursor.getInt(0),
                            cursor.getLong(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4));

                    jokesList.add(joke);
                }
            } while (cursor.moveToNext());
        }

        jokesList.remove(0);

        db.close();
        cursor.close();
        return jokesList;
    }

    public ArrayList<Joke> getAllJokesFromDB(String tableName) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        ArrayList<Joke> jokesList = new ArrayList<Joke>();

        String query = "SELECT * FROM "
                + tableName;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Joke joke = new Joke(cursor.getInt(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4));

                jokesList.add(joke);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return jokesList;
    }

    private ArrayList<Category> getAllCategoriesFromDB() {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        ArrayList<Category> categoryList = new ArrayList<Category>();

        String query = "SELECT * FROM "
                + AppDBContract.CategoryEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(cursor.getInt(0),
                        cursor.getString(1));

                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return categoryList;
    }

    public Joke getJokeByID(int id, String tableName) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();
        String query = "SELECT * FROM "
                + tableName
                + " WHERE " + AppDBContract.JokeEntry.COLUMN_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);

        Joke joke = null;

        if (cursor.moveToFirst()) {
            joke = new Joke(cursor.getInt(0),
                    cursor.getLong(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4));

        }

        db.close();
        cursor.close();
        return joke;
    }

    public Category getCategoryByID(int id) {
        SQLiteDatabase db = appDBHelper.getReadableDatabase();

        String query = "SELECT * FROM "
                + AppDBContract.CategoryEntry.TABLE_NAME
                + " WHERE " + AppDBContract.CategoryEntry._ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);

        Category category = null;

        if (cursor.moveToFirst()) {
            category = new Category(cursor.getInt(0),
                    cursor.getString(1));
        }
        db.close();
        cursor.close();
        return category;
    }

    public void addJoke(Joke joke, int categoryID) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AppDBContract.JokeEntry.COLUMN_ID, joke.getJokeID());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DATE, joke.getJokeDate());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_TEXT, joke.getJokeText());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_LIKES, joke.getJokeLikes());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DISLIKES, joke.getJokeDislikes());

        db.insert(AppDBContract.JokeEntry.TABLE_NAME, null, contentValues);
        contentValues.clear();

        contentValues.put(AppDBContract.PrePackingEntry.COLUMN_JOKE_ID, joke.getJokeID());
        contentValues.put(AppDBContract.PrePackingEntry.COLUMN_CATEGORY_ID, categoryID);

        db.insert(AppDBContract.PrePackingEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AppDBContract.CategoryEntry.COLUMN_ID, category.getCategoryID());
        contentValues.put(AppDBContract.CategoryEntry.COLUMN_CATEGORY_NAME, category.getCategoryName());

        db.insert(AppDBContract.CategoryEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    private String getUUID() {
        String device_uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String packageName = context.getPackageName();

        return packageName + "::" + device_uuid;
    }

//    public ArrayList<Joke> getAllJokesByCategory(String tableName, String category) {
//        SQLiteDatabase db = appDBHelper.getReadableDatabase();
//
//        ArrayList<Joke> jokesList = new ArrayList<Joke>();
//
//        String query = "SELECT * FROM "
//                + tableName
//                + " WHERE "
//                + AppDBContract.JokeEntry.COLUMN_JOKE_CATEGORY + " == " + category
//                + " ORDER BY "
//                + AppDBContract.JokeEntry.COLUMN_JOKE_DATE + " ASC";
//
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Joke joke = new Joke(cursor.getInt(0),
//                        cursor.getLong(1),
//                        cursor.getString(2),
//                        cursor.getString(3),
//                        cursor.getInt(4),
//                        cursor.getInt(5));
//
//                jokesList.add(joke);
//            } while (cursor.moveToNext());
//        }
//
//        db.close();
//        cursor.close();
//        return jokesList;
//    }
}
