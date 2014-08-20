package it.tiwiz.qurl.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * This class will allow you to asynchronously create a QR Code using two different callbacks in case of
 * success or failure.
 * <p/>
 * This code relies on ZXing library, that must be imported in the project.
 */
public class QrCodeAsync extends AsyncTask<String, Void, QrCodeAsync.Result> {
    private final static int URL_POSITION = 0;
    private final static int FOREGROUND_COLOR_POSITION = 1;
    private final static int BACKGROUND_COLOR_POSITION = 2;
    private final static int QR_CODE_DIMENSION_POSITION = 3;
    private final static String DEFAULT_FOREGROUND_COLOR = "#000000";
    private final static String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";
    private final static String DEFAULT_QR_SIZE_PX = "800";

    public enum ResponseCode {
        SUCCESS,
        WRONG_PARAMETERS,
        NUMBER_FORMAT_EXCEPTION,
        QR_CODE_EXCEPTION,
        TASK_INTERRUPTED
    }

    public static class Result {
        private Bitmap mBitmap;
        private ResponseCode mResponseCode;

        public Result(Bitmap bitmap) {
            mBitmap = bitmap;
            mResponseCode = ResponseCode.SUCCESS;
        }

        public Result(ResponseCode responseCode) {
            mBitmap = null;
            mResponseCode = responseCode;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public ResponseCode getResponseCode() {
            return mResponseCode;
        }
    }

    private WeakReference<ImageCallback> imageCallback;

    /**
     * This Interface, similar to {@link java.lang.Runnable}, will take care of
     * executing the actions requested when the QR Code has been generated.
     */
    public interface ImageCallback {
        /**
         * This method will be run on main thread as soon as the QR is generated successfully.
         *
         * @param result a {@link android.graphics.Bitmap} containing the generated QR Code
         */
        public void onSuccess(Bitmap result);

        /**
         * This method will be run on main thread as soon as QR Code generation fails.
         *
         * @param responseCode a {@link QrCodeAsync.ResponseCode} telling what happened during the execution of the task
         */
        public void onFailure(ResponseCode responseCode);
    }

    /**
     * Builds parameter for the AsyncTask, Those parameters shall be passed to the {@link #execute(Object[])} method in order to avoid
     * manual parsing.
     *
     * @param url                {@link java.lang.String} representing a URL (or any other text).
     * @param foregroundColorRes {@link android.content.res.Resources} ID of the foreground color of the QR Code
     * @param backgroundColorRes {@link android.content.res.Resources} ID of the background color of the QR Code
     * @param qrDimensionRes     {@link android.content.res.Resources} ID of the QR Code dimension, expressed as {@link java.lang.Integer}
     * @return a {@link java.lang.String} vector that can be passed to the {@link #execute(Object[])} method
     */
    public static String[] buildParameters(Context context, String url, int foregroundColorRes, int backgroundColorRes, int qrDimensionRes) {
        final int foregroundColor = context.getResources().getColor(foregroundColorRes);
        final int backgroundColor = context.getResources().getColor(backgroundColorRes);
        final int qrDimension = context.getResources().getInteger(qrDimensionRes);
        return buildParameters(url, foregroundColor, backgroundColor, qrDimension);
    }

    /**
     * Builds parameter for the AsyncTask, Those parameters shall be passed to the {@link #execute(Object[])} method in order to avoid
     * manual parsing.
     *
     * @param url             {@link java.lang.String} representing a URL (or any other text).
     * @param foregroundColor {@link java.lang.Integer} representation of foreground color, as from {@link Color}.<i>parseColor()</i>
     * @param backgroundColor {@link java.lang.Integer} representation of background color, as from {@link Color}.<i>parseColor()</i>
     * @param qrDimension     dimension of the QR Code represented as {@link java.lang.Integer}
     * @return a {@link java.lang.String} vector that can be passed to the {@link #execute(Object[])} method
     */
    public static String[] buildParameters(String url, int foregroundColor, int backgroundColor, int qrDimension) {
        final String foregroundColorString = "#" + Integer.toHexString(foregroundColor);
        final String backgroundColorString = "#" + Integer.toHexString(backgroundColor);
        final String qrDimensionString = String.valueOf(qrDimension);
        return buildParameters(url, foregroundColorString, backgroundColorString, qrDimensionString);
    }

    /**
     * Builds parameter for the AsyncTask, Those parameters shall be passed to the {@link #execute(Object[])} method in order to avoid
     * manual parsing.
     *
     * @param url             {@link java.lang.String} representing a URL (or any other text).
     * @param foregroundColor {@link java.lang.String} representation of the foreground color as the one accepted by {@link Color}.<i>parseColor()</i>
     * @param backgroundColor {@link java.lang.String} representation of the background color as the one accepted by {@link Color}.<i>parseColor()</i>
     * @param qrDimension     {@link java.lang.String} representation of the QR Code dimension, such as "800".
     * @return a {@link java.lang.String} vector that can be passed to the {@link #execute(Object[])} method
     */
    public static String[] buildParameters(String url, String foregroundColor, String backgroundColor, String qrDimension) {
        String[] parameters = new String[] {url, foregroundColor, backgroundColor, qrDimension};
        return parameters;
    }

    /**
     * Creates a default configuration for the QR Code and the given address.<br>
     * This means: black foreground, white background and a dimension of 800 pixel per each side.
     *
     * @param url {@link java.lang.String} containing the URL or the next that will be represented in the QR Code
     * @return a {@link java.lang.String} vector that can be passed to the {@link #execute(Object[])} method containing the default configuration for the QR Code
     */
    public static String[] getDefaultParameters(String url) {
        return buildParameters(url, DEFAULT_FOREGROUND_COLOR, DEFAULT_BACKGROUND_COLOR, DEFAULT_QR_SIZE_PX);
    }

    /**
     * Builds the structure of the QR Code. No loading indicator will be shown.
     * for setting a message and display the loading screen
     *
     * @param callback {@link QrCodeAsync.ImageCallback} that will be executed for this QR Code
     */
    public QrCodeAsync(ImageCallback callback) {
        imageCallback = new WeakReference<ImageCallback>(callback);
    }

    @Override
    protected Result doInBackground(String... params) {
        if (params == null || params.length != 4) {
            return new Result(ResponseCode.WRONG_PARAMETERS);
        }

        final String url = params[URL_POSITION];
        try {
            final int foregroundColor = Color.parseColor(params[FOREGROUND_COLOR_POSITION]);
            final int backgroundColor = Color.parseColor(params[BACKGROUND_COLOR_POSITION]);
            final int qrCodeDimension = Integer.parseInt(params[QR_CODE_DIMENSION_POSITION]);
            final Bitmap resultBitmap = Generator.generateQrCode(url, foregroundColor, backgroundColor, qrCodeDimension);
            Result result;
            if (resultBitmap == null) {
                result = new Result(ResponseCode.QR_CODE_EXCEPTION);
            } else {
               result = new Result(resultBitmap);
            }
            return result;
        } catch (NumberFormatException e) {
            return new Result(ResponseCode.NUMBER_FORMAT_EXCEPTION);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        ImageCallback callback = imageCallback.get();

        if (result.getResponseCode() == ResponseCode.SUCCESS) {
            if (callback != null) {
                callback.onSuccess(result.getBitmap());
            }
        } else {
            if (callback != null) {
                callback.onFailure(result.getResponseCode());
            }
        }
    }

    @Override
    protected void onCancelled() {
        ImageCallback callback = imageCallback.get();

        if (callback != null) {
            callback.onFailure(ResponseCode.TASK_INTERRUPTED);
        }
    }
}
