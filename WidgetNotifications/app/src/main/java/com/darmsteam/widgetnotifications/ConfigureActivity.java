package com.darmsteam.widgetnotifications;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigureActivity extends AppCompatActivity
{
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        setTitle("Sélection des apps");
        setResult(RESULT_CANCELED);

        ListView list_apps = (ListView)findViewById(R.id.list_apps);
        PackageManager manager = getPackageManager();
        List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
        final ArrayList<AppDescription> apps = new ArrayList<>();
        AppDescription app;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        for(PackageInfo p : packageInfos)
        {
            if(manager.getLaunchIntentForPackage(p.packageName) != null)
            {
                app = new AppDescription();
                app.setName(manager.getApplicationLabel(p.applicationInfo).toString());
                app.setIcon(p.applicationInfo.loadIcon(manager));
                app.setPackageName(p.packageName);
                app.setChecked(preferences.getBoolean(p.packageName, false));
                apps.add(app);
            }
        }

        Collections.sort(apps);
        list_apps.setAdapter(new ListAppsAdapter(this, android.R.layout.simple_list_item_1, apps));

        list_apps.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Switch app_switch = (Switch) view.findViewById(R.id.app_switch);
                boolean checked = !app_switch.isChecked();
                app_switch.setChecked(checked);
                apps.get(position).setChecked(checked);
                editor.putBoolean(apps.get(position).getPackageName(), checked);
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
                editor.apply();

                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    mAppWidgetId = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_layout);
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                break;

            default:
                break;
        }
        return true;
    }
}
