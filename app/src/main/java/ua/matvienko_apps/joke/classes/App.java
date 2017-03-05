package ua.matvienko_apps.joke.classes;

/**
 * Created by Alexandr on 03/03/2017.
 */

public class App {
    private String name;
    private String description;
    private int appImageId;

    public App(String name, String description, int appImageId) {

        this.name = name;
        this.description = description;
        this.appImageId = appImageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAppImageId() {
        return appImageId;
    }

    public void setAppImageId(int appImageId) {
        this.appImageId = appImageId;
    }


}
