package it.tiwiz.qurl.common;

import android.graphics.Bitmap;
;

import java.io.ByteArrayOutputStream;

/**
 * This class contains the helper methods to manage a Bitmap data and scale it to fit a Wear device's screen
 */
public class BitmapUtils {

    private BitmapUtils() {
        //empty constructor to prevent instantiation
    }

    public static byte[] toByteArray(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return byteStream.toByteArray();
    }

    public static Bitmap scaleBitmapForWear(Bitmap bitmap) {
        return bitmap.createScaledBitmap(bitmap, Common.QR_WEAR_SIDE_SIZE, Common.QR_WEAR_SIDE_SIZE, false);
    }

}
