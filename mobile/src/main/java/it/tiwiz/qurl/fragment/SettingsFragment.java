package it.tiwiz.qurl.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import it.tiwiz.qurl.R;

public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
