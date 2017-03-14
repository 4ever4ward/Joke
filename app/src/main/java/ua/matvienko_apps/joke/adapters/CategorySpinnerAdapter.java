package ua.matvienko_apps.joke.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sleuprua.R;

import java.util.ArrayList;

import ua.matvienko_apps.joke.classes.Category;

/**
 * Created by Alexandr on 26/02/2017.
 */

public class CategorySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context context;
    private ArrayList<Category> categoryList;

    public CategorySpinnerAdapter(Context context, ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }


    public int getCount() {
        return categoryList.size();
    }

    public Object getItem(int i) {
        return categoryList.get(i);
    }

    public long getItemId(int i) {
        return (long) categoryList.get(i).getCategoryID();
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(categoryList.get(position).getCategoryName());
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(32, 14, 20, 16);
        txt.setTextSize(16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ud, 0);
        txt.setText(categoryList.get(i).getCategoryName());
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

}
