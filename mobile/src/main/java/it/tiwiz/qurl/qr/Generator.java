package it.tiwiz.qurl.qr;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * This class contains a single method that will generate a QR Code
 * with given text, foreground and background color, and dimension of the side
 * of the image.
 *
 * This code relies on the ZXing library, which must be added to your project
 * in order to have the QR Code generated.
 *
 * For Gradle/Android environment is enough to declare
 *      compile 'com.google.zxing:core:3.0.1'
 * in the <i>dependencies</i> section of the build.gradle file
 */
public class Generator {
    public static  Bitmap generateQrCode(String url, int foregroundColor, int backgroundColor, int qrCodeDimension) {
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
                    pixels[offset + x] = bitMatrix.get(x, y) ? foregroundColor : backgroundColor;
                }
            }
            Bitmap resultQrBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            resultQrBitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return resultQrBitmap;
        } catch (WriterException e) {
            return null;
        }
    }
}
