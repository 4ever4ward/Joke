package com.sleuprua.classes;

/**
 * Created by Alexandr on 05/03/2017.
 */

public class Category {

    public static final int CATEGORY_POPULAR_ID = 777;
    public static final int CATEGORY_FRESH_ID = 888;

    private int categoryID;
    private String categoryName;

    public Category(int categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
