package it.tiwiz.qurl.wear;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;


import it.tiwiz.qurl.R;
import it.tiwiz.qurl.common.Common;
import it.tiwiz.qurl.wear.utils.PrefManager;

public class QrWearService extends WearableListenerService {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(Common.SETTINGS_PATH)) {
            byte[] data = messageEvent.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            sendQrCode(bitmap);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (Common.SETTINGS_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                DataMapItem mapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                DataMap map = mapItem.getDataMap();
                boolean dismissNotification = map.getBoolean(Common.SETTINGS_DISMISS_NOTIFICATION);
                PrefManager.setDismissNotification(this, dismissNotification);
            }
        }
    }

    protected void sendQrCode(Bitmap bitmap) {
        Intent openQrIntent = new Intent(this, QrWearActivity.class);
        openQrIntent.putExtra(QrWearActivity.QR_CODE_EXTRA, bitmap);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openQrIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //TODO Replace images with proper icons
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentText(getString(R.string.notification_message))
                .addAction(new Notification.Action(R.drawable.ic_open,getString(R.string.notification_action_open), pendingIntent))
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(Common.QR_NOTIFICATION_ID, notification);
    }
}
