package com.darmsteam.widgetnotifications;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Actiité utilisée pour la configuration du widget lors de son initialisation
public class ConfigureActivity extends AppCompatActivity
{
    private static final String TAG = "ConfigureActivity";
    ArrayList<AppDescription> apps;
    boolean accesNotifs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        setTitle("Sélection des apps");
        setResult(RESULT_CANCELED);

        ListView list_apps = (ListView) findViewById(R.id.list_apps);

        // Récupération de la liste des apps de l'appareil
        PackageManager manager = getPackageManager();
        List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
        apps = new ArrayList<>();
        AppDescription app;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* pour chaque appli, on affiche son icone, son nom et un switch permettant de savoir
           si l'appli est sélectionnée ou non */
        for(PackageInfo p : packageInfos)
        {
            if((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || p.packageName.equals("com.android.dialer")
                    || p.packageName.equals("com.google.android.gm")
                    || p.packageName.equals("com.google.android.calendar")
                    || p.packageName.equals("com.android.mms"))
            {
                app = new AppDescription();
                app.setName(manager.getApplicationLabel(p.applicationInfo).toString());
                app.setIcon(p.applicationInfo.loadIcon(manager));
                app.setPackageName(p.packageName);
                app.setChecked((preferences.getInt(p.packageName, -1) > -1));
                apps.add(app);
            }
        }

        // tri des apps
        Collections.sort(apps);
        final ListAppsAdapter adapter = new ListAppsAdapter(this, android.R.layout.simple_list_item_1, apps);
        list_apps.setAdapter(adapter);

        list_apps.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                boolean checked = !apps.get(position).isChecked();
                apps.get(position).setChecked(checked);
                adapter.notifyDataSetChanged();
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
                for(AppDescription app : apps)
                {
                    if(app.isChecked())
                        editor.putInt(app.getPackageName(), preferences.getInt(app.getPackageName(), 0));
                    else
                    {
                        editor.remove(app.getPackageName());
                        editor.remove("sort" + app.getPackageName());
                    }
                }
                editor.apply();


                //on met à jour les instances du widget qui sont déjà sur l'écran (s'il y en a)
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                if(extras != null)
                {
                    mAppWidgetId = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }


                intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetNotificationsProvider.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, intent);
                    sendBroadcast(intent);


                intent = new Intent(this, SortAppsActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
        return true;
    }


}
