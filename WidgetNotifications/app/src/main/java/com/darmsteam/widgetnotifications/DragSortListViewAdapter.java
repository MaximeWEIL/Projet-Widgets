package com.darmsteam.widgetnotifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * adapter utilisé pour changer
 */

public class DragSortListViewAdapter extends ArrayAdapter<AppDescription>
{
    public DragSortListViewAdapter(Context context, int resource, ArrayList<AppDescription> apps)
    {
        super(context, resource, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View v = convertView;

        if (v == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_sort_app, null);
        }

        AppDescription description = getItem(position);

        if (description != null)
        {
            ImageView app_icon = (ImageView)v.findViewById(R.id.app_icon);
            TextView app_name = (TextView)v.findViewById(R.id.app_name);
            app_icon.setImageDrawable(description.getIcon());
            app_name.setText(description.getName());
        }

        return v;

    }
}
