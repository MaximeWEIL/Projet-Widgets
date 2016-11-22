package com.darmsteam.widgetnotifications;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 */

public class WidgetNotificationsProvider extends AppWidgetProvider
{
    final String ACTION_SORT = "com.darmsteam.widgetnotifications.SORT";
    final String ACTION_EDIT = "com.darmsteam.widgetnotifications.EDIT";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
        remoteViews.setOnClickPendingIntent(R.id.sort, getPendingSelfIntent(context, ACTION_SORT));
        remoteViews.setOnClickPendingIntent(R.id.edit, getPendingSelfIntent(context, ACTION_EDIT));
        ComponentName thisWidget = new ComponentName(context, WidgetNotificationsProvider.class);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        // on effectue la mise à jour pour chaque instance du widget

        for (int i=0; i<appWidgetIds.length; i++) {
            remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private RemoteViews updateWidgetListView(Context context,
                                             int appWidgetId) {

        // mise à jour de la liste des applis et des valeurs des notifications dans the widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.appwidget_layout);
        remoteViews.setOnClickPendingIntent(R.id.sort, getPendingSelfIntent(context, ACTION_SORT));
        remoteViews.setOnClickPendingIntent(R.id.edit, getPendingSelfIntent(context, ACTION_EDIT));
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.list_apps_widget, svcIntent);
        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = null;
        Log.d("COUCOU", "COUCOU");
        if (ACTION_SORT.equals(intent.getAction()))
        {
            i = new Intent(context, SortAppsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else if(ACTION_EDIT.equals(intent.getAction()))
        {
            i = new Intent(context, ConfigureActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        Log.d("PENDING", "PENDING");
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}