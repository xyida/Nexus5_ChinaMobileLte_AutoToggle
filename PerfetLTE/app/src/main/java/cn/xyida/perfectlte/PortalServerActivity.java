package cn.xyida.perfectlte;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;


import cn.xyida.perfectlte.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class PortalServerActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final int TURNON_CAPTIVE_PORTAL_DETECTION = 1;
    private static final int TURNOFF_CAPTIVE_PORTAL_DETECTION = 2;
    private static final int MODIFY_CAPTIVE_PORTAL_SERVER = 3;
    private static final int SET_TO_DEFAULT = 4;
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TURNON_CAPTIVE_PORTAL_DETECTION:
                    suCommand("settings put global captive_portal_detection_enabled 1");
                    break;
                case TURNOFF_CAPTIVE_PORTAL_DETECTION:
                    suCommand("settings put global captive_portal_detection_enabled 0");
                    break;
                case MODIFY_CAPTIVE_PORTAL_SERVER:
                    String stringValue = msg.obj.toString();
                    suCommand("settings put global captive_portal_server " + stringValue);
                    break;
                case SET_TO_DEFAULT:
                    String cmd = "settings put global captive_portal_detection_enabled 1";
                    suCommand(cmd);
                    cmd = "settings put global captive_portal_server clients3.google.com";
                    suCommand(cmd);
                    //更改preference项目为默认
//                    ArrayList<Preference> list= (ArrayList<Preference>) msg.obj;
                    Preference preference= (Preference) msg.obj;
                    preference.getEditor().putBoolean("perfectlte_captive_portal_enable",true).commit();
                    preference.getEditor().putString("perfectlte_portal_server","clients3.google.com").commit();
                    break;

            }


        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);



        setupSimplePreferencesScreen();

        findPreference("perfectlte_captive_portal_enable").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("perfectlte_portal_server").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    }

    public static Preference.OnPreferenceChangeListener getsBindPreferenceSummaryToValueListener() {

        Log.e("perfectlte", "OnPreferenceChangeListener");
        return sBindPreferenceSummaryToValueListener;
    }



    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        if ("重置默认".equals(preference.getTitle())) {
            Message msg = handler.obtainMessage();
            msg.what = SET_TO_DEFAULT;
//            ArrayList<Preference> list=new ArrayList<Preference>();
//            list.add(findPreference("perfectlte_captive_portal_enable"));
//            list.add(findPreference("perfectlte_portal_server"));

            msg.obj=preference;
            handler.sendMessage(msg);


        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);


    }

    private static void suCommand(String s) {
        String[] command = new String[]{"su"};
        DataOutputStream dataOutputStream = null;
        try {
            Process cmd = Runtime.getRuntime().exec(command);
            dataOutputStream = new DataOutputStream(cmd.getOutputStream());
            dataOutputStream.writeBytes(s + " ; " + "exit" + "\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("perfectlte", e.toString());
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("perfectlte", e.toString());
                }
            }
        }


    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_portalserver);


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
//            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

//            if (preference instanceof ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null);
//
//            } else if (preference instanceof RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                } else {
//                    Ringtone ringtone = RingtoneManager.getRingtone(
//                            preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }

            Message msg;
            if ("Captive Portal 检测".equals(preference.getTitle())) {
                Log.e("perfectlte", stringValue);

                if (Boolean.parseBoolean(stringValue)) {
                    msg = handler.obtainMessage();
                    msg.what = TURNON_CAPTIVE_PORTAL_DETECTION;
                    handler.sendMessage(msg);

                } else {
                    msg = handler.obtainMessage();
                    msg.what = TURNOFF_CAPTIVE_PORTAL_DETECTION;
                    handler.sendMessage(msg);
                }


            } else if ("自定义服务器地址".equals(preference.getTitle())) {

                msg = handler.obtainMessage();
                msg.what = MODIFY_CAPTIVE_PORTAL_SERVER;
                msg.obj = stringValue;
                handler.sendMessage(msg);
            }


            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_portalserver);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }
}
