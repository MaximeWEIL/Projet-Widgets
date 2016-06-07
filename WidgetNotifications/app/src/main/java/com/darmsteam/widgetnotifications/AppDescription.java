package com.darmsteam.widgetnotifications;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 */
public class AppDescription implements Comparable<AppDescription>
{
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean checked;

    public AppDescription(String name, String packageName, Drawable icon, boolean checked)
    {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.checked = checked;
    }

    public AppDescription()
    {
        this.name = null;
        this.packageName = null;
        this.icon = null;
        this.checked = false;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public String getName()
    {
        return name;
    }

    public Drawable getIcon()
    {
        return icon;
    }

    public boolean isChecked()
    {
        return checked;
    }

    // Methode permettant de trier les apps dans l'ordre "coché/décoché"
    @Override
    public int compareTo(@NonNull AppDescription another)
    {
        if(this.isChecked() && !another.isChecked())
            return -1;
        else if(!this.isChecked() && another.isChecked())
            return 1;
        else
            return name.toLowerCase().compareTo(another.name.toLowerCase());
    }

    public String getPackageName()
    {
        return packageName;
    }
}
