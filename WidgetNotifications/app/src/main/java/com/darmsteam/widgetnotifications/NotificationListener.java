package com.darmsteam.widgetnotifications;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 */
public class NotificationListener extends NotificationListenerService
{
    private final String TAG = "NotificationListener";

    String dialer = "com.android.dialer";
    String telecom = "com.android.server.telecom";
    String free = "fr.freemobile.android.vvm";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"notif reçue");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if((preferences.contains(sbn.getPackageName()) && sbn.getNotification().tickerText != null) ||
                (preferences.contains(sbn.getPackageName()) && sbn.getPackageName().equals("com.microsoft.office.outlook") && sbn.getId() == 1) )
            setNbNotif(sbn.getPackageName(), 1);

        else if(preferences.contains(dialer) && sbn.getPackageName().equals(telecom))
            setNbNotif(dialer, 1);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"notif supprimée");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if((preferences.contains(sbn.getPackageName()) && sbn.getNotification().tickerText != null) ||
                (preferences.contains(sbn.getPackageName()) && sbn.getPackageName().equals("com.microsoft.office.outlook") && sbn.getId() == 1) )
            setNbNotif(sbn.getPackageName(), -1);

        else if(preferences.contains(dialer) && sbn.getPackageName().equals(telecom))
            setNbNotif(dialer, -1);
    }

    private void setNbNotif(String packageName, int nb)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int notif = nb < 0 ? 0 : preferences.getInt(packageName, 0)+nb;
        notif = notif > 1 && packageName.equals(free) ? 1 : notif;
        if(preferences.contains(packageName))
            preferences.edit().putInt(packageName, notif).apply();

        int widgetIDs[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetNotificationsProvider.class));
        for (int id : widgetIDs)
            AppWidgetManager.getInstance(getApplication()).notifyAppWidgetViewDataChanged(id, R.id.list_apps_widget);
    }
}
