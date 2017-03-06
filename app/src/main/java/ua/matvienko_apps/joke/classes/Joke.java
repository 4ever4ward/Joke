package ua.matvienko_apps.joke.classes;

/**
 * Created by Alexandr on 15/02/2017.
 */

public class Joke {

    public static int FAVOURITES_TRUE = 1;
    public static int FAVOURITES_FALSE = 0;

    public static final int LIKE = 1;
    public static final int DISLIKE = 0;

    private int jokeID;
    private long jokeDate;


    private String jokeCategory;
    private String jokeText;
    private int jokeLikes;
    private int jokeDislikes;

    public Joke(int jokeID, long jokeDate, String jokeText, int jokeLikes, int jokeDislikes) {
        this.jokeID = jokeID;
        this.jokeDate = jokeDate;
        this.jokeText = jokeText;
        this.jokeLikes = jokeLikes;
        this.jokeDislikes = jokeDislikes;
    }

    // TODO: delete this constructor
    public Joke(String jokeText) {
        this.jokeText = jokeText;
        this.jokeLikes = jokeLikes;
    }

    public String getJokeText() {

        return jokeText;
    }

    public void setJokeText(String jokeText) {
        this.jokeText = jokeText;
    }

    public String getJokeCategory() {
        return jokeCategory;
    }

    public void setJokeCategory(String jokeCategory) {
        this.jokeCategory = jokeCategory;
    }

    public int getJokeDislikes() {
        return jokeDislikes;
    }

    public void setJokeDislikes(int jokeDislikes) {
        this.jokeDislikes = jokeDislikes;
    }

    public int getJokeLikes() {
        return jokeLikes;
    }

    public void setJokeLikes(int jokeLikes) {
        this.jokeLikes = jokeLikes;
    }

    public long getJokeDate() {
        return jokeDate;
    }

    public void setJokeDate(long jokeDate) {
        this.jokeDate = jokeDate;
    }

    public int getJokeID() {
        return jokeID;
    }

    public void setJokeID(int jokeID) {
        this.jokeID = jokeID;
    }
}
