package ua.matvienko_apps.joke.classes;

import android.content.Context;
import android.os.AsyncTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ua.matvienko_apps.joke.data.AppDBContract;
import ua.matvienko_apps.joke.data.DataProvider;


public class Utility {

    private final static String TAG = Utility.class.getSimpleName();

    private final static int JOKE_LIST_SIZE = 40;

    public static Calendar getDateFromString(String dateStr) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        calendar.setTime(dateFormat.parse(dateStr));
        return calendar;
    }

    public static ArrayList<Joke> deleteUsedJokes(ArrayList<Joke> jokes) {

        ArrayList<Joke> list = new ArrayList<>();

        for (int i = 0; i < jokes.size(); i++) {
            Joke joke = jokes.get(i);

            if (joke.getMyJokeVote() == 0) {
                list.add(joke);
            }

        }
        return list;
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
        ArrayList<Joke> jokes = dataProvider.getJokes();

        jokes = mergeSortByDate(jokes);

        if (jokes.size() > JOKE_LIST_SIZE) {
            for (int i = 30; i < jokes.size(); i++) {
                jokes.remove(i);
            }
        }

        return deleteUsedJokes(jokes);
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
        ArrayList<Joke> jokes = dataProvider.getJokes();

        jokes = mergeSortByLikes(jokes);

        if (jokes.size() > JOKE_LIST_SIZE) {
            for (int i = 30; i < jokes.size(); i++) {
                jokes.remove(i);
            }
        }
        return deleteUsedJokes(jokes);
    }

    public static void executeDeferredOperations(Context context) {
        DataProvider dataProvider = new DataProvider(context);
        ArrayList<DeferredOperation> deferredOperations = dataProvider.getAllDeferredOperationFromDB();

        for (int i = 0; i < deferredOperations.size(); i++) {

            new ExecuteOperation(
                    deferredOperations.get(i).getOperationID(),
                    deferredOperations.get(i).getJokeID(),
                    context).execute();
        }

    }

    public static void deleteFromFavourites(Context context, Joke joke) {
        new DeleteFromFavourites(context, joke).execute();
    }

    public static void addToFavourites(Context context, Joke joke) {
        new AddToFavourites(joke, context).execute();
    }

    private static class AddToFavourites extends AsyncTask<Void, Void, Void> {

        Joke joke;
        Context context;

        DataProvider dataProvider;

        AddToFavourites(Joke joke, Context context) {

            this.joke = joke;
            this.context = context;

            dataProvider = new DataProvider(context);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!dataProvider.addToFavourites(joke)) {
                dataProvider.addDeferredOperation(new DeferredOperation(joke.getJokeID(),
                        DeferredOperation.OPERATION_ADD_FAVORITE));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            cont.setText(Integer.toString(dataProvider.getAllJokesFromDB(AppDBContract.FavouritesEntry.TABLE_NAME).size()));
        }
    }

    private static class DeleteFromFavourites extends AsyncTask<Void, Void, Void> {

        private Context context;
        private Joke joke;

        DeleteFromFavourites(Context context, Joke joke) {
            this.context = context;
            this.joke = joke;
        }

        @Override
        protected Void doInBackground(Void... params) {
            new DataProvider(context).deleteFromFavourites(joke);
            return null;
        }
    }

    private static class ExecuteOperation extends AsyncTask<Void, Void, Void> {

        private int operationID;
        private int jokeID;
        private Context context;

        public ExecuteOperation(int operationID, int jokeID, Context context) {
            this.operationID = operationID;
            this.jokeID = jokeID;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DataProvider dataProvider = new DataProvider(context);

            switch (operationID) {
                case DeferredOperation.OPERATION_ADD_FAVORITE: {
                    if (dataProvider.addToFavourites(dataProvider.getJokeByID(jokeID, AppDBContract.JokeEntry.TABLE_NAME))) {
                        dataProvider.deleteDeferredOperation(new DeferredOperation(jokeID, operationID));
                    }
                    break;
                }
                case DeferredOperation.OPERATION_DISLIKE: {
                    if (dataProvider.setJokeVote(jokeID, Joke.DISLIKE)) {
                        dataProvider.deleteDeferredOperation(new DeferredOperation(jokeID, operationID));
                    }
                    break;
                }
                case DeferredOperation.OPERATION_LIKE: {
                    if (dataProvider.setJokeVote(jokeID, Joke.LIKE)) {
                        dataProvider.deleteDeferredOperation(new DeferredOperation(jokeID, operationID));
                    }
                    break;
                }
//                case DeferredOperation.OPERATION_DELETE_FAVORITE: {
//                    if (dataProvider.deleteFavourite(dataProvider.getJokeByID(jokeID, AppDBContract.FavouritesEntry.TABLE_NAME))) {
//                        dataProvider.deleteDeferredOperation(new DeferredOperation(jokeID, operationID));
//                    }
//                }
            }
            return null;
        }
    }
}