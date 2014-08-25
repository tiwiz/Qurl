package it.tiwiz.qurl.wear;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import it.tiwiz.qurl.R;
import it.tiwiz.qurl.common.Common;
import it.tiwiz.qurl.wear.utils.PrefManager;

public class QrWearActivity extends Activity implements WatchViewStub.OnLayoutInflatedListener {

    private ImageView mImgQrCode;
    private Intent mIntroIntent;
    public static final String QR_CODE_EXTRA = QrWearActivity.class.getSimpleName() + ".EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntroIntent = getIntent();

        if (mIntroIntent == null || !mIntroIntent.hasExtra(QR_CODE_EXTRA)) {
            setOpenApp();
        } else {
            dismissNotification();
            setContentView(R.layout.activity_qr_wear);
            ((WatchViewStub) findViewById(R.id.watch_view_stub_qr)).setOnLayoutInflatedListener(this);
        }
    }

    private void setQrCode(WatchViewStub stub) {
        mImgQrCode = (ImageView) stub.findViewById(R.id.imgQrCode);
        Bitmap qrCode = mIntroIntent.getParcelableExtra(QR_CODE_EXTRA);
        if (qrCode != null) {
            mImgQrCode.setImageBitmap(qrCode);
        } else {
            Toast.makeText(this, getString(R.string.invalid_qr_code_toast_message), Toast.LENGTH_LONG).show();
        }
    }

    private void dismissNotification() {
        if (PrefManager.getDismissNotification(this)) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Common.QR_NOTIFICATION_ID);
        }
    }

    /**
     * We decided not to set a layout if the app is opened from the watch.
     * This is because the app is not intended to be used on its own
     * In case the app is opened, we will throw a Notification that will actually let the user
     * open the app on its connected phone
     */
    private void setOpenApp() {

    //TODO Logic for app opening on phone
    }

    @Override
    public void onLayoutInflated(WatchViewStub watchViewStub) {
        if (watchViewStub.getId() == R.id.watch_view_stub_qr) {
            setQrCode(watchViewStub);
        }
    }
}
