package ua.matvienko_apps.joke.classes;

/**
 * Created by Alexandr on 15/02/2017.
 */

public class Joke {

    public static final int LIKE = 222;
    public static final int DISLIKE = 333;
    public static int FAVOURITES_TRUE = 12;
    public static int FAVOURITES_FALSE = 13;
    private int jokeID;
    private long jokeDate;
    private String jokeCategory;
    private String jokeText;
    private int jokeLikes;
    private int jokeDislikes;
    private int jokeFavorites;
    private int myJokeVote;

    public Joke(int jokeID, long jokeDate, String jokeText, int jokeLikes, int jokeDislikes, int jokeFavorites, int myJokeVote) {
        this.jokeID = jokeID;
        this.jokeDate = jokeDate;
        this.jokeText = jokeText;
        this.jokeLikes = jokeLikes;
        this.jokeDislikes = jokeDislikes;
        this.jokeFavorites = jokeFavorites;
        this.myJokeVote = myJokeVote;
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

    public int getJokeFavorites() {
        return jokeFavorites;
    }

    public void setJokeFavorites(int jokeFavorites) {
        this.jokeFavorites = jokeFavorites;
    }

    public int getMyJokeVote() {
        return myJokeVote;
    }

    public void setMyJokeVote(int myJokeVote) {
        this.myJokeVote = myJokeVote;
    }
}
