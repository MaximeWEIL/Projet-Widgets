package com.darmsteam.widgetnotifications;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RemoteViews;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SortAppsActivity extends AppCompatActivity
{
    private final String TAG = "SortAppsActivity";
    DragSortListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_apps);

        setTitle("Tri des applications sélectionnées");

        DragSortListView list_sort_apps = (DragSortListView)findViewById(R.id.list_sort_apps);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> keys = preferences.getAll();
        ArrayList<AppDescription> apps = new ArrayList<>();
        PackageManager manager = this.getPackageManager();
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
                    app.setOrdre(preferences.getInt("sort"+packageInfo.packageName,0));
                    apps.add(app);
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
        if(!apps.isEmpty())
            Collections.sort(apps);

        adapter = new DragSortListViewAdapter(this, android.R.layout.simple_list_item_1, apps);
        list_sort_apps.setAdapter(adapter);

        list_sort_apps.setDropListener(new DragSortListView.DragSortListener()
        {
            @Override
            public void drag(int from, int to)
            {

            }

            @Override
            public void drop(int from, int to)
            {
                if(from != to)
                {
                    AppDescription item = adapter.getItem(from);
                    adapter.remove(item);
                    adapter.insert(item, to);
                }
            }

            @Override
            public void remove(int which)
            {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int mAppWidgetId = -1;
        switch(item.getItemId())
        {
            case R.id.action_valider:
                // on enregistre la sélection des applis sur le clic du bouton valider
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                AppDescription app;

                for(int i = 0; i < adapter.getCount(); i++)
                {
                    app = adapter.getItem(i);
                    editor.putInt("sort" + app.getPackageName(), i);
                }
                editor.apply();

                RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.appwidget_layout);
                remoteViews.setOnClickPendingIntent(R.id.sort, getPendingSelfIntent(this, ACTION_SORT));
                remoteViews.setOnClickPendingIntent(R.id.edit, getPendingSelfIntent(this, ACTION_EDIT));

                int widgetIDs[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetNotificationsProvider.class));
                for (int id : widgetIDs)
                {
                    AppWidgetManager.getInstance(getApplication()).notifyAppWidgetViewDataChanged(id, R.id.list_apps_widget);
                }


                finish();
                break;

            default:
                break;
        }
        return true;
    }
    final String ACTION_SORT = "com.darmsteam.widgetnotifications.SORT";
    final String ACTION_EDIT = "com.darmsteam.widgetnotifications.EDIT";

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        Log.d("PENDING", "PENDING");
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
