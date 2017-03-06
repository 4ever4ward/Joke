package ua.matvienko_apps.joke.classes;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ua.matvienko_apps.joke.data.AppDBContract;
import ua.matvienko_apps.joke.data.DataProvider;


public class Utility {

    private final static String TAG = Utility.class.getSimpleName();

    private final static int JOKE_LIST_SIZE = 30;

    public static Calendar getDateFromString(String dateStr) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        calendar.setTime(dateFormat.parse(dateStr));
        return calendar;
    }

    private static ArrayList<Joke> mergeSortByDate(ArrayList<Joke> jokes) {
        if (jokes.size() > 1) {
            int elementsInA1 = jokes.size() / 2;
            int elementsInA2 = jokes.size() - elementsInA1;

            ArrayList<Joke> arr1 = new ArrayList<>();
            ArrayList<Joke> arr2 = new ArrayList<>();

            for (int i = 0; i < elementsInA1; i++) {
                arr1.add(jokes.get(i));
            }
            for (int i = elementsInA1; i < elementsInA1 + elementsInA2; i++) {
                arr2.add(jokes.get(i));
            }

            arr1 = mergeSortByDate(arr1);
            arr2 = mergeSortByDate(arr2);

            int i = 0, j = 0, k = 0;
            while (elementsInA1 != j && elementsInA2 != k) {
                if (arr1.get(j).getJokeDate() > arr2.get(k).getJokeDate()) {
                    jokes.set(i, arr1.get(j));
                    i++;
                    j++;
                } else {
                    jokes.set(i, arr2.get(k));
                    i++;
                    k++;
                }
            }
            while (elementsInA1 != j) {
                jokes.set(i, arr1.get(j));
                i++;
                j++;
            }
            while (elementsInA2 != k) {
                jokes.set(i, arr2.get(k));
                i++;
                k++;
            }
        }

        return jokes;
    }

    public static ArrayList<Joke> getNewestJokes(Context context) {

        DataProvider dataProvider = new DataProvider(context);
        ArrayList<Joke> jokes = dataProvider.getAllJokesFromDB(AppDBContract.JokeEntry.TABLE_NAME);

        jokes = mergeSortByDate(jokes);

        if (jokes.size() > JOKE_LIST_SIZE) {
            for (int i = 30; i < jokes.size(); i++) {
                jokes.remove(i);
            }
        }

        for (int i = 0; i < jokes.size(); i++) {
            Log.e(TAG, "getNewestJokes: " + jokes.get(i).getJokeDate());
        }

        return jokes;
    }

    private static ArrayList<Joke> mergeSortByLikes(ArrayList<Joke> jokes) {
        if (jokes.size() > 1) {
            int elementsInA1 = jokes.size() / 2;
            int elementsInA2 = jokes.size() - elementsInA1;

            ArrayList<Joke> arr1 = new ArrayList<>();
            ArrayList<Joke> arr2 = new ArrayList<>();

            for (int i = 0; i < elementsInA1; i++) {
                arr1.add(jokes.get(i));
            }
            for (int i = elementsInA1; i < elementsInA1 + elementsInA2; i++) {
                arr2.add(jokes.get(i));
            }

            arr1 = mergeSortByLikes(arr1);
            arr2 = mergeSortByLikes(arr2);

            int i = 0, j = 0, k = 0;
            while (elementsInA1 != j && elementsInA2 != k) {
                if (arr1.get(j).getJokeLikes() > arr2.get(k).getJokeLikes()) {
                    jokes.set(i, arr1.get(j));
                    i++;
                    j++;
                } else {
                    jokes.set(i, arr2.get(k));
                    i++;
                    k++;
                }
            }
            while (elementsInA1 != j) {
                jokes.set(i, arr1.get(j));
                i++;
                j++;
            }
            while (elementsInA2 != k) {
                jokes.set(i, arr2.get(k));
                i++;
                k++;
            }
        }

        return jokes;
    }

    public static ArrayList<Joke> getPopularJokes(Context context) {

        DataProvider dataProvider = new DataProvider(context);
        ArrayList<Joke> jokes = dataProvider.getAllJokesFromDB(AppDBContract.JokeEntry.TABLE_NAME);

        jokes = mergeSortByLikes(jokes);

        if (jokes.size() > JOKE_LIST_SIZE) {
            for (int i = 30; i < jokes.size(); i++) {
                jokes.remove(i);
            }
        }
        return jokes;
    }

}