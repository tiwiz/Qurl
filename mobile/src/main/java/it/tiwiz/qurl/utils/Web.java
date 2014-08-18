package it.tiwiz.qurl.utils;

import android.content.Intent;

import java.net.MalformedURLException;
import java.net.URL;

import it.tiwiz.qurl.BuildConfig;

/**
 * Created by roberto on 18/08/14.
 */
public final class Web {

    private static boolean isValidUrl(String urlToVerify) {
        try {
            new URL(urlToVerify);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String getURLFromIntent(Intent inputIntent) {
        String resultUrl = "";

        if (inputIntent != null) {
            if (inputIntent.getAction().equals(C.QR_ACCEPTANCE_ACTION) && (inputIntent.getType() != null) && inputIntent.getType().equals(C.QR_ACCEPTANCE_MIME_TYPE)) {
                final String extraString = inputIntent.getExtras().getString(Intent.EXTRA_TEXT);
                if (isValidUrl(extraString)) {
                    resultUrl = extraString;
                }
            }
        }
        return resultUrl;
    }
}
