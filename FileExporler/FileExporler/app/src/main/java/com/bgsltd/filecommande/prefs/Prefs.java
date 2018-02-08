package com.bgsltd.filecommande.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by marinaracu on 22.11.2017.
 */

public class Prefs {

    private static final String PREFS_NAME = "com.bgsIltd.filecommand.SharedPrefUtils";
    private static final String DATE     = "user_media";
    private static final String SHOW_REWARD = "show_reward";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static long getDate(Context context) {
        return getPreferences(context).getLong(DATE, 0);
    }

    public static void putDate(Context context, long preferenceValue) {
        getPreferences(context).edit().putLong(DATE, preferenceValue).commit();
    }


    public static boolean getShowReward(Context context) {
        return getPreferences(context).getBoolean(SHOW_REWARD, false);
    }

    public static void putShowReward(Context context, boolean preferenceValue) {
        getPreferences(context).edit().putBoolean(SHOW_REWARD, preferenceValue).commit();
    }

}
