package it.tiwiz.qurl.common;

import android.content.Intent;

/**
 * This class contains all the common code used for this project
 */
public class Common {

    private Common() {
        //empty constructor to prevent instantiation
    }

    public static final String SETTINGS_PATH = "/settings";
    public static final String SETTINGS_DISMISS_NOTIFICATION = "dismissNotification";
    public static final String QR_ACCEPTANCE_ACTION = Intent.ACTION_SEND;
    public static final String QR_ACCEPTANCE_MIME_TYPE = "text/plain";
    public static final int QR_WEAR_SIDE_SIZE = 280;
    public static final int QR_NOTIFICATION_ID = 1;
}
