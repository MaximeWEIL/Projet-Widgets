package com.darmsteam.widgetnotifications;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Cette classe est le service nécessaire pour mettre à jour la liste des apps dans le widget
 */
public class WidgetService extends RemoteViewsService
{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        int appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new ListWidgetProvider(this.getApplicationContext(), intent));
    }
}
