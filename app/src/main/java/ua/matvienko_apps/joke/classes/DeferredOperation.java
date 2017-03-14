package ua.matvienko_apps.joke.classes;

/**
 * Created by alex_ on 07-Mar-17.
 */

public class DeferredOperation {

    public static final int OPERATION_LIKE = 321;
    public static final int OPERATION_DISLIKE = 322;
    public static final int OPERATION_ADD_FAVORITE = 323;
    public static final int OPERATION_DELETE_FAVORITE = 324;

    private int jokeID;
    private int operationID;

    public DeferredOperation(int jokeID, int operationID) {
        this.jokeID = jokeID;
        this.operationID = operationID;
    }

    public int getJokeID() {
        return jokeID;
    }

    public void setJokeID(int jokeID) {
        this.jokeID = jokeID;
    }

    public int getOperationID() {
        return operationID;
    }

    public void setOperationID(int operationID) {
        this.operationID = operationID;
    }
}
