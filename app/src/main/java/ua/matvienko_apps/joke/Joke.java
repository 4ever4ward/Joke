package ua.matvienko_apps.joke;

/**
 * Created by Alexandr on 15/02/2017.
 */

public class Joke {

    public static int STARRED_TRUE = 1;
    public static int STARRED_FALSE = 0;

    public static int LIKE = 1;
    public static int DISLIKE = 0;

    private String jokeText;
    private int jokeVotes;
    private int jokeStarred;

    public Joke(String jokeText, int jokeVotes, int jokeStarred) {
        this.jokeText = jokeText;
        this.jokeVotes = jokeVotes;
        this.jokeStarred = jokeStarred;
    }

    public void setJokeText(String jokeText) {
        this.jokeText = jokeText;
    }
    public void setJokeVotes(int jokeVotes) {
        this.jokeVotes = jokeVotes;
    }
    public void setJokeStarred(int jokeStarred) {
        this.jokeStarred = jokeStarred;
    }
    public String getJokeText() {

        return jokeText;
    }
    public int getJokeVotes() {
        return jokeVotes;
    }
    public int getJokeStarred() {
        return jokeStarred;
    }
    public boolean isStarred() {
        return jokeStarred == STARRED_TRUE;
    }
}