package it.tiwiz.qurl;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import it.tiwiz.qurl.common.BitmapUtils;
import it.tiwiz.qurl.common.Common;
import it.tiwiz.qurl.qr.QrCodeAsync;
import it.tiwiz.qurl.utils.Web;


public class QrActivity extends Activity {

    private ImageView mImageViewQrCode;
    private GoogleApiClient mGoogleApiClient;
    private SparseArray<Object> mPreferences;

    private QrCodeAsync.ImageCallback mQrCodeCreationCallback = new QrCodeAsync.ImageCallback() {
        @Override
        public void onSuccess(Bitmap result) {
            mImageViewQrCode.setImageBitmap(result);
            boolean autoSendEnabled = (Boolean) mPreferences.get(R.string.pref_wear_auto_send);
            boolean isWearSupportEnabled = (Boolean) mPreferences.get(R.string.pref_wear_enable);

            if (autoSendEnabled && isWearSupportEnabled) {
                sendMessageToWear(BitmapUtils.scaleBitmapForWear(result));
            }
        }

        @Override
        public void onFailure(QrCodeAsync.ResponseCode responseCode) {
            //TODO add behaviour in case of failure
        }
    };

    private GoogleApiClient.ConnectionCallbacks mGoogleConnetionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            boolean dismissNotification = (Boolean) mPreferences.get(R.string.pref_wear_notification,
                    Boolean.getBoolean(getString(R.string.pref_wear_notification_default_value)));

            PutDataMapRequest dataMap = PutDataMapRequest.create(Common.SETTINGS_PATH);
            dataMap.getDataMap().putBoolean(Common.SETTINGS_DISMISS_NOTIFICATION, dismissNotification);
            PutDataRequest request = dataMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        }

        @Override
        public void onConnectionSuspended(int i) {
            //Do nothing, we need to synchronize settings only
            //When the connection is successfull
        }
    };

    private void sendMessageToWear(final Bitmap bitmap) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.wear_not_connected_toast_message), Toast.LENGTH_SHORT).show();
            return;
        }

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), Common.SETTINGS_PATH, BitmapUtils.toByteArray(bitmap));
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        setContentView(R.layout.activity_qr);
        mImageViewQrCode = (ImageView) findViewById(R.id.qr_imageview);

        initGoogleApiClient();
        final String url = Web.getURLFromIntent(getIntent());
        //final int BLACK_COLOR = Color.parseColor("#0099CC");
        //final int WHITE_COLOR = Color.parseColor("#FFBB33");
        //final int QR_DIMENSION = 800;
        //final String[] params = QrCode.buildParameters(url, BLACK_COLOR, WHITE_COLOR, QR_DIMENSION);

        final String[] params = QrCodeAsync.buildParameters(this, url, R.color.qrForegroundColor, R.color.qrBackgroundColor, R.integer.qrDimension);
        if ((url != null) && (url.length() > 0)) {
            new QrCodeAsync(mQrCodeCreationCallback).execute(params);
        } else {
            //TODO callback if error
        }

    }

    protected void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mGoogleConnetionCallbacks)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qr, menu);

        boolean isWearSupportEnabled = (Boolean) mPreferences.get(R.string.pref_wear_enable);
        if (!isWearSupportEnabled) {
            menu.findItem(R.id.action_send_to_wear).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send_to_wear) {
            Bitmap bitmap = ((BitmapDrawable) mImageViewQrCode.getDrawable()).getBitmap();
            sendMessageToWear(BitmapUtils.scaleBitmapForWear(bitmap));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPreferences() {
        mPreferences = new SparseArray<Object>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mPreferences.put(R.string.pref_wear_enable,
                sharedPreferences.getBoolean(getString(R.string.pref_wear_enable), Boolean.getBoolean(getString(R.string.pref_wear_support_default_value))));

        mPreferences.put(R.string.pref_wear_auto_send, sharedPreferences.getBoolean(getString(R.string.pref_wear_auto_send),
                Boolean.getBoolean(getString(R.string.pref_wear_auto_send_default_value))));

        mPreferences.put(R.string.pref_wear_notification, sharedPreferences.getBoolean(getString(R.string.pref_wear_notification),
                Boolean.getBoolean(getString(R.string.pref_wear_notification_default_value))));
    }
}
