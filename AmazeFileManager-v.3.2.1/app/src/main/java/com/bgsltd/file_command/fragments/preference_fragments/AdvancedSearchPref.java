package com.bgsltd.file_command.fragments.preference_fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.bgsltd.file_command.R;

/**
 * @author Emmanuel
 *         on 8/5/2017, at 11:30.
 */

public class AdvancedSearchPref extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.advancedsearch_prefs);
    }

}
