package it.tiwiz.qurl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import it.tiwiz.qurl.qr.QrCode;
import it.tiwiz.qurl.utils.Web;


public class QrActivity extends Activity {

    private ImageView imageViewQrCode;

    private QrCode.ImageCallback simpleCallback = new QrCode.ImageCallback() {
        @Override
        public void onSuccess (Bitmap result) {
            imageViewQrCode.setImageBitmap(result);
        }

        @Override
        public void onFailure (QrCode.ResponseCode responseCode) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        imageViewQrCode = (ImageView) findViewById(R.id.qr_imageview);

        final String url = Web.getURLFromIntent(getIntent());
        //final int BLACK_COLOR = Color.parseColor("#0099CC");
        //final int WHITE_COLOR = Color.parseColor("#FFBB33");
        //final int QR_DIMENSION = 800;
        //final String[] params = QrCode.buildParameters(url, BLACK_COLOR, WHITE_COLOR, QR_DIMENSION);

        final String[] params = QrCode.buildParameters(this, url, R.color.qrForegroundColor, R.color.qrBackgroundColor, R.integer.qrDimension);
        if ((url != null) && (url.length() > 0)) {
            new QrCode(simpleCallback).execute(params);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
