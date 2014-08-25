package it.tiwiz.qurl.wear.utils;

import android.content.Context;
import android.content.SharedPreferences;

import it.tiwiz.qurl.common.Common;

public class PrefManager {
    private static final String PREF_ROOT = PrefManager.class.getSimpleName().toUpperCase();

    private PrefManager() {}

    protected static SharedPreferences _getSP(Context context) {
        return context.getSharedPreferences(PREF_ROOT, Context.MODE_PRIVATE);
    }
    public static boolean getDismissNotification(Context context) {
        SharedPreferences preferences = _getSP(context);
        return preferences.getBoolean(Common.SETTINGS_DISMISS_NOTIFICATION, false);
    }

    public static void setDismissNotification(Context context, boolean dismissNotification) {
        SharedPreferences.Editor editor = _getSP(context).edit();
        editor.putBoolean(Common.SETTINGS_DISMISS_NOTIFICATION, dismissNotification);
        editor.apply();
    }
}
