package it.tiwiz.qurl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.tiwiz.qurl.common.Common;
import it.tiwiz.qurl.fragment.SettingsFragment;


public class QrListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_list);


        final Context context = this;
        findViewById(R.id.clickText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showSettings = new Intent(context, QrActivity.class);
                showSettings.setAction(Common.QR_ACCEPTANCE_ACTION);
                showSettings.setType(Common.QR_ACCEPTANCE_MIME_TYPE);
                showSettings.putExtra(Intent.EXTRA_TEXT, "http://www.androidworld.it");
                startActivity(showSettings);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qr_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            loadSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSettings() {
        Intent showSettings = new Intent(this, SettingsActivity.class);
        startActivity(showSettings);
    }
}
