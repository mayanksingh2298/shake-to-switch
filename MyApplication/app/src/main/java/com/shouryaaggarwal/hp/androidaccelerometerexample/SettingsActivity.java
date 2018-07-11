package com.shouryaaggarwal.hp.androidaccelerometerexample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }


    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            CheckBoxPreference pref_toggle_z = (CheckBoxPreference) findPreference("toggle_z");
            CheckBoxPreference pref_toggle_x_soft = (CheckBoxPreference) findPreference("toggle_x");
            CheckBoxPreference pref_toggle_x_hard = (CheckBoxPreference) findPreference("toggle_x_hard");
            ListPreference x_soft = (ListPreference) findPreference("softXThreshold");
            ListPreference x_hard = (ListPreference) findPreference("hardXThreshold");
            ListPreference z_soft = (ListPreference) findPreference("ZThreshold");

            pref_toggle_z.setOnPreferenceChangeListener(changeValues);
            pref_toggle_x_hard.setOnPreferenceChangeListener(changeValues);
            pref_toggle_x_soft.setOnPreferenceChangeListener(changeValues);
            x_hard.setOnPreferenceChangeListener(changeValues);
            x_soft.setOnPreferenceChangeListener(changeValues);
            z_soft.setOnPreferenceChangeListener(changeValues);


            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private static Preference.OnPreferenceChangeListener changeValues = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof CheckBoxPreference) {
                boolean value = (boolean) newValue;
                String key = preference.getKey();

                switch (key) {
                    case "toggle_z":
                        MainActivity.enableZ = value;
                        break;
                    case "toggle_x":
                        MainActivity.enableSoftX = value;
                        break;
                    case "toggle_x_hard":
                        MainActivity.enableHardX = value;
                }
            }

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                String key = listPreference.getKey();
                int value = Integer.parseInt(stringValue);
                switch (key) {
                    case "softXThreshold":
                        MainActivity.softXThreshold = value;
                        break;
                    case "hardXThreshold":
                        MainActivity.hardXThreshold = value;
                        break;
                    case "ZThreshold":
                        MainActivity.ZThreshold = value;
                        break;
                }
            }

            return true;
        }
    };



    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mayanksingh2298@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
