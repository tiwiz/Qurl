package it.tiwiz.qurl.utils;

import android.util.Log;

import it.tiwiz.qurl.BuildConfig;

/**
 * Created by roberto on 18/08/14.
 */
public final class Lg {
    public final static String DEBUG_TAG = "it.tiwiz.qurl.DEBUG";

    private static boolean isDebugBuild() {
        return BuildConfig.DEBUG;
    }

    public static void d(String message) {
        if (isDebugBuild()) {
            Log.d(DEBUG_TAG, message);
        }
    }

    public static void w(String message) {
        if (isDebugBuild()) {
            Log.w(DEBUG_TAG, message);
        }
    }

    public static void e(String message) {
        if (isDebugBuild()) {
            Log.e(DEBUG_TAG, message);
        }
    }
}
