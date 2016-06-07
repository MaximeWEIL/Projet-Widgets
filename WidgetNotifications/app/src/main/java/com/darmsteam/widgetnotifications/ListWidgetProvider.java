package com.darmsteam.widgetnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Provider de la liste des apps dans le widget
 */
public class ListWidgetProvider implements RemoteViewsService.RemoteViewsFactory
{
    final String TAG = "ListWidgetProvider";
    private ArrayList<AppDescription> apps = new ArrayList<>();
    private Context context = null;

    public ListWidgetProvider(Context context, Intent intent) {
        this.context = context;
        populateListItem();
    }

    private void populateListItem() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> keys = preferences.getAll();
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo;
        AppDescription app;
        int nb;

        /* parcours de la liste des clés dans les SharedPreferences afin de récupérer les
           applis et le nombre de notifications associé. */
        for(Map.Entry<String, ?> entry : keys.entrySet())
        {
            try
            {
                if(entry.getValue() instanceof Integer && (Integer)entry.getValue() > -1)
                {
                    packageInfo = manager.getPackageInfo(entry.getKey(), 0);
                    nb = preferences.getInt(packageInfo.packageName, -1);
                    app = new AppDescription();
                    app.setPackageName(packageInfo.packageName);
                    app.setName(manager.getApplicationLabel(packageInfo.applicationInfo).toString());
                    app.setIcon(packageInfo.applicationInfo.loadIcon(manager));
                    app.setChecked(nb > -1);
                    apps.add(app);
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
        if(apps != null && !apps.isEmpty())
            Collections.sort(apps);
    }

    @Override
    public void onCreate()
    {
    }

    @Override
    public void onDataSetChanged()
    {

    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /*
    * met à jour une ligne du widget et change la couleur de la notif
    * selon le nombre de notifs
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AppDescription app = apps.get(position);
        int notif = preferences.getInt(app.getPackageName(), 0);
        remoteView.setTextViewText(R.id.app_name, app.getName());
        remoteView.setImageViewBitmap(R.id.app_icon, ((BitmapDrawable)app.getIcon()).getBitmap());
        remoteView.setTextViewText(R.id.count, notif + "");
        if(notif > 0)
            remoteView.setTextColor(R.id.count, Color.rgb(255, 0, 0));
        else
            remoteView.setTextColor(R.id.count, Color.rgb(92, 174, 234));


        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        return null;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }
}
