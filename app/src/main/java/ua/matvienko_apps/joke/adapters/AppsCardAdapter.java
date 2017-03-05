package ua.matvienko_apps.joke.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.matvienko_apps.joke.R;
import ua.matvienko_apps.joke.classes.App;

/**
 * Created by Alexandr on 03/03/2017.
 */

public class AppsCardAdapter extends BaseAdapter {

    private final String TAG = AppsCardAdapter.class.getSimpleName();
    public Context context;
    private ViewHolder viewHolder;
    private ArrayList<App> appList;

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {

            rootView = LayoutInflater.from(context).inflate(R.layout.item_app_card, parent, false);
            viewHolder = new ViewHolder(rootView);


            rootView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.appName.setText(appList.get(position).getName());
        viewHolder.appDescription.setText(appList.get(position).getDescription());

        return rootView;
    }

    public class ViewHolder {
        public TextView appName;
        public TextView appDescription;
        public ImageView appImageIcon;
        public Button installButton;

        public ViewHolder(View view) {
            appName = (TextView) view.findViewById(R.id.appNameText);
            appDescription = (TextView) view.findViewById(R.id.appDescriptionText);
            appImageIcon = (ImageView) view.findViewById(R.id.appIconImage);
            installButton = (Button) view.findViewById(R.id.installBtn);

        }
    }
}
