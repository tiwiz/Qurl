package it.tiwiz.qurl.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public final class QrCode extends AsyncTask<String,Void, Bitmap>{
    private final static int URL_POSITION = 0;
    private final static int BLACK_COLOR_POSITION = 1;
    private final static int WHITE_COLOR_POSITION = 2;
    private final static int QR_CODE_DIMENSION_POSITION = 3;
    private ImageCallback mCallback;
    private boolean mShowLoading;

    public interface ImageCallback {
        public void run (Bitmap result);
    }

    public static String[] buildParameters (Context context, String url, int blackColorRes, int whiteColorRes, int qrDimensionRes) {
        final int blackColor = context.getResources().getColor(blackColorRes);
        final int whiteColor = context.getResources().getColor(whiteColorRes);
        final int qrDimension = context.getResources().getInteger(qrDimensionRes);
        return buildParameters(url, blackColor, whiteColor, qrDimension);
    }

    public static String[] buildParameters (String url, int blackColor, int whiteColor, int qrDimension) {
        final String blackColorString = "#" + Integer.toHexString(blackColor);
        final String whiteColorString = "#" + Integer.toHexString(whiteColor);
        final String qrDimensionString = String.valueOf(qrDimension);
        return buildParameters(url, blackColorString, whiteColorString, qrDimensionString);
    }

    public static String[] buildParameters (String url, String blackColor, String whiteColor, String qrDimension) {
        String[] parameters = new String[] { url, blackColor, whiteColor, qrDimension};
        return parameters;
    }

    public QrCode(ImageCallback callback, boolean showLoading) {
        this.mCallback = callback;
        this.mShowLoading = showLoading;
    }

    public QrCode(ImageCallback callback) {
        this (callback, false);
    }

    @Override
    protected void onPreExecute () {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground (String... params) {
        if ((params == null) || (params.length != 4)) {
            return null;
        }

        final String url = params[URL_POSITION];
        try {
            final int blackColor = Color.parseColor(params[BLACK_COLOR_POSITION]);
            final int whiteColor = Color.parseColor(params[WHITE_COLOR_POSITION]);
            final int qrCodeDimension = Integer.parseInt(params[QR_CODE_DIMENSION_POSITION]);
            return generateQrCode(url, blackColor, whiteColor, qrCodeDimension);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Bitmap generateQrCode (String url, int blackColor, int whiteColor, int qrCodeDimension) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            final BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, qrCodeDimension, qrCodeDimension);
            final int matrixHeight = bitMatrix.getHeight();
            final int matrixWidth = bitMatrix.getWidth();
            int[] pixels = new int[matrixWidth * matrixHeight];
            int offset;
            for (int y = 0; y < matrixHeight; y++) {
                offset = y * matrixWidth;
                for (int x = 0; x < matrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? blackColor : whiteColor;
                }
            }
            Bitmap resultQrBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            resultQrBitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return resultQrBitmap;
        } catch (WriterException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute (Bitmap bitmap) {
        if ((bitmap != null) && (mCallback != null)) {
            mCallback.run(bitmap);
        }
    }
}
