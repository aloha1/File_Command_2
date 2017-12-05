package com.bgsltd.file_command.utils.provider;

import com.bgsltd.file_command.utils.color.ColorPreference;
import com.bgsltd.file_command.utils.files.Futils;
import com.bgsltd.file_command.utils.theme.AppTheme;
import com.bgsltd.file_command.utils.theme.AppThemeManager;

/**
 * Created by Rémi Piotaix <remi.piotaix@gmail.com> on 2016-10-17.
 */
public interface UtilitiesProviderInterface {
    Futils getFutils();

    ColorPreference getColorPreference();

    AppTheme getAppTheme();

    AppThemeManager getThemeManager();
}
