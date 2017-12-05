package com.bgsltd.file_command.utils.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bgsltd.file_command.utils.color.ColorPreference;
import com.bgsltd.file_command.utils.files.Futils;
import com.bgsltd.file_command.utils.theme.AppTheme;
import com.bgsltd.file_command.utils.theme.AppThemeManager;

/**
 * Created by piotaixr on 16/01/17.
 */

public class UtilitiesProvider implements UtilitiesProviderInterface {
    private Futils futils;
    private ColorPreference colorPreference;
    private AppThemeManager appThemeManager;

    public UtilitiesProvider(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        futils = new Futils();

        colorPreference = ColorPreference.loadFromPreferences(context, sharedPreferences);
        appThemeManager = new AppThemeManager(sharedPreferences);
    }

    @Override
    public Futils getFutils() {
        return futils;
    }

    @Override
    public ColorPreference getColorPreference() {
        return colorPreference;
    }

    @Override
    public AppTheme getAppTheme() {
        return appThemeManager.getAppTheme();
    }

    @Override
    public AppThemeManager getThemeManager() {
        return appThemeManager;
    }
}
