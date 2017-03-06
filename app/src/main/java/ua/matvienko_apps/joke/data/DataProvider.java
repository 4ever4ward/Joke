package ua.matvienko_apps.joke.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ua.matvienko_apps.joke.classes.Category;
import ua.matvienko_apps.joke.classes.Joke;
import ua.matvienko_apps.joke.classes.Utility;


public class DataProvider {

    private static String TAG = DataProvider.class.getSimpleName();
    private final String API_URL = "http://app1.app.trafficterminal.com/api/";
    private final String RESPONSE = "response";
    private final String PARAM_UUID = "uuid";
    private final String PARAM_JOKE_ID = "id_joke";
    private final String PARAM_CATEGORY_ID = "category_id";
    private AppDBHelper appDBHelper;
    private Context context;


    public DataProvider(Context context) {
        this.appDBHelper = new AppDBHelper(context, AppDBContract.DB_NAME, null, AppDBContract.DB_VERSION);
        this.context = context;
    }


    public void setJokeVote(Joke joke, int jokeVoteID) {
        final String PARAM_JOKE_LIKE = "like";
        final String PARAM_JOKE_DISLIKE = "dislike";

        String paramJokeVote = null;

        if (jokeVoteID == Joke.LIKE) {
            paramJokeVote = PARAM_JOKE_LIKE;
        } else if (jokeVoteID == Joke.DISLIKE) {
            paramJokeVote = PARAM_JOKE_DISLIKE;
        }
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(API_URL + paramJokeVote);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            nameValuePairs.add(new BasicNameValuePair(PARAM_JOKE_ID, Integer.toString(joke.getJokeID())));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Log.e(TAG, "setJokeVote: Problem with Internet connection", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "setJokeVote: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "setJokeVote: IOException", e);
        }

    }

    public void addToFavourites(Joke joke) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL + "favourites");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            nameValuePairs.add(new BasicNameValuePair(PARAM_JOKE_ID, Integer.toString(joke.getJokeID())));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            String favouritesJsonStr = inputStreamToString(response.getEntity().getContent());
            Log.e(TAG, "addToFavourites: favouritesJsonStr" + favouritesJsonStr);


        } catch (ClientProtocolException e) {
            Log.e(TAG, "addToFavourites: Problem with Internet connection", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "addToFavourites: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "addToFavourites: IOException", e);
        }

        if (getJokeByID(joke.getJokeID(), AppDBContract.FavouritesEntry.TABLE_NAME) == null)
            addFavourites(joke);

    }

    public ArrayList<Joke> getJokes() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL + "list");

        try {
            // Add parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String jokeJsonStr = inputStreamToString(response.getEntity().getContent());
            Log.e(TAG, "getJokes: " + jokeJsonStr);
            getJokeDataFromJson(jokeJsonStr);

        } catch (ClientProtocolException e) {
            Log.e(TAG, "getJokes: Problem with Internet connection", e);
        } catch (JSONException e) {
            Log.e(TAG, "getJokes: Invalid JSON", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "getJokes: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "getJokes: IOException", e);
        }
        return getAllJokesFromDB(AppDBContract.JokeEntry.TABLE_NAME);
    }

    public ArrayList<Joke> getJokesByCategory(int categoryID) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL + "list");

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

        } catch (ClientProtocolException e) {
            Log.e(TAG, "getJokesByCategory: Problem with Internet connection", e);
        } catch (JSONException e) {
            Log.e(TAG, "getJokesByCategory: Invalid JSON", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "getJokesByCategory: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "getJokesByCategory: IOException", e);
        }
        return getAllJokesByCategoryFromDB(categoryID);
    }

    public ArrayList<Category> getCategories() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL + "list");

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

        } catch (ClientProtocolException e) {
            Log.e(TAG, "getCategories: Problem with Internet connection", e);
        } catch (JSONException e) {
            Log.e(TAG, "getCategories: Invalid JSON", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "getCategories: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "getCategories: IOException", e);
        }

        return getAllCategoriesFromDB();
    }

    public ArrayList<Joke> getFavourites() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL + "favourites");

        try {
            // Add parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(PARAM_UUID, getUUID()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String jokeJsonStr = inputStreamToString(response.getEntity().getContent());
            Log.e(TAG, "getFavourites: " + jokeJsonStr);
            getFavouriteDataFromJsom(jokeJsonStr);

        } catch (ClientProtocolException e) {
            Log.e(TAG, "getFavourites: Problem with Internet connection", e);
        } catch (JSONException e) {
            Log.e(TAG, "getFavourites: Invalid JSON", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "getFavourites: UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "getFavourites: IOException", e);
        }
        return getAllJokesFromDB(AppDBContract.FavouritesEntry.TABLE_NAME);
    }


    private void getCategoriesFromJson(String jokeJsonStr) throws JSONException {
        final String CATEGORY_ID = "id";
        final String CATEGORY_NAME = "name";

        JSONObject dataJsonObj = new JSONObject(jokeJsonStr);
        JSONArray responseArrayJson = dataJsonObj.getJSONArray(RESPONSE);

        JSONArray categoriesJsonList = responseArrayJson.getJSONArray(1);

        // Add category Popular and category Fresh on first create
        if (getCategoryByID(Category.CATEGORY_FRESH_ID) == null && getCategoryByID(Category.CATEGORY_POPULAR_ID) == null) {
            addCategory(new Category(Category.CATEGORY_POPULAR_ID, "Популярные"));
            addCategory(new Category(Category.CATEGORY_FRESH_ID, "Свежие"));
        }

        // Add categories from json to table
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

        // Part of code where we transform JSONObject to JSONArray
        while (x.hasNext()) {
            String key = (String) x.next();
            jokeListJsonArr.put(jokeListJsonStr.get(key));
        }

        // Find all jokes in JSON
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

                // If not exists joke with jokeID then add new joke
                if (getJokeByID(jokeID, AppDBContract.JokeEntry.TABLE_NAME) == null) {

                    // jokeCategory may have inside more than 1 categories
                    addJoke(new Joke(jokeID,
                                    jokeDate.getTimeInMillis(),
                                    jokeText,
                                    jokeLikes,
                                    jokeDislikes),
                            jokeCategory);
                    // If exists then upgrade joke with this id
                } else {
                    updateJoke(new Joke(jokeID,
                            jokeDate.getTimeInMillis(),
                            jokeText,
                            jokeLikes,
                            jokeDislikes));
                }

            } catch (ParseException e) {
                Log.e(TAG, "getJokeDataFromJson: ParseException", e);
            }

        }


    }

    private void getFavouriteDataFromJsom(String favouritesJsonStr) throws JSONException {

    }

    private ArrayList<Joke> getAllJokesByCategoryFromDB(int categoryID) {
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


    private Joke getJokeByID(int id, String tableName) {
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

    private Category getCategoryByID(int id) {
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


    private void updateJoke(Joke joke) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(AppDBContract.JokeEntry.COLUMN_ID, joke.getJokeID());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DATE, joke.getJokeDate());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_TEXT, joke.getJokeText());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_LIKES, joke.getJokeLikes());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DISLIKES, joke.getJokeDislikes());

        db.update(AppDBContract.JokeEntry.TABLE_NAME, contentValues,
                AppDBContract.JokeEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(joke.getJokeID())});
        db.close();
    }

    private void addJoke(Joke joke, String categoryIDs) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add joke to jokes
        contentValues.put(AppDBContract.JokeEntry.COLUMN_ID, joke.getJokeID());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DATE, joke.getJokeDate());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_TEXT, joke.getJokeText());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_LIKES, joke.getJokeLikes());
        contentValues.put(AppDBContract.JokeEntry.COLUMN_JOKE_DISLIKES, joke.getJokeDislikes());

        db.insert(AppDBContract.JokeEntry.TABLE_NAME, null, contentValues);
        contentValues.clear();

        String[] jokeCategories = categoryIDs.substring(1, categoryIDs.length() - 1).split(",");

        // Add all relations for joke and it categories
        for (int j = 0; j < jokeCategories.length; j++) {

            contentValues.put(AppDBContract.PrePackingEntry.COLUMN_JOKE_ID, joke.getJokeID());
            contentValues.put(AppDBContract.PrePackingEntry.COLUMN_CATEGORY_ID, Integer.parseInt(jokeCategories[j]));

            db.insert(AppDBContract.PrePackingEntry.TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    private void addFavourites(Joke joke) {
        SQLiteDatabase db = appDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AppDBContract.FavouritesEntry.COLUMN_ID, joke.getJokeID());
        contentValues.put(AppDBContract.FavouritesEntry.COLUMN_JOKE_DATE, joke.getJokeDate());
        contentValues.put(AppDBContract.FavouritesEntry.COLUMN_JOKE_TEXT, joke.getJokeText());
        contentValues.put(AppDBContract.FavouritesEntry.COLUMN_JOKE_LIKES, joke.getJokeLikes());
        contentValues.put(AppDBContract.FavouritesEntry.COLUMN_JOKE_DISLIKES, joke.getJokeDislikes());

        db.insert(AppDBContract.FavouritesEntry.TABLE_NAME, null, contentValues);
        db.close();

    }

    private void addCategory(Category category) {
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

        //TODO: change to device_uuid
        return packageName + "::" + "gsdfgsdfgdfs";//device_uuid;
    }

    private String inputStreamToString(InputStream is) throws IOException {
        String line;
        StringBuilder total = new StringBuilder();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        while ((line = rd.readLine()) != null)
            total.append(line);

        return total.toString();
    }

}
