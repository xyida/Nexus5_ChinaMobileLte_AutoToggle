package cn.xyida.perfectlte;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import org.w3c.dom.Text;

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
public class SettingsActivity extends PreferenceActivity{
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        Toast.makeText(SettingsActivity.this,"点击了"+preference.getOrder()+"screen:"+preferenceScreen.getOrder(),Toast.LENGTH_SHORT).show();
        if ("关于程序".equals(preference.getTitle())){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("关于");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setView(R.layout.about_layout);
            builder.setPositiveButton("检查更新",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkUpdate();
                }
            });
            builder.show();
        }else if("捐赠".equals(preference.getTitle())){
            Intent donateIntent = new Intent();
            donateIntent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("https://shenghuo.alipay.com/send/payment/fill.htm?optEmail=522918670@qq.com");
            donateIntent.setData(content_url);
            startActivity(donateIntent);

        }else if ("Portal Server 修改".equals(preference.getTitle())){
            Intent portalIntent=new Intent();
            portalIntent.setClass(SettingsActivity.this,PortalServerActivity.class);
            startActivity(portalIntent);

        }else if("RadioInfo".equals(preference.getTitle())){
            Intent i = new Intent();
            i.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.RadioInfo"));
            i.setAction(Intent.ACTION_MAIN);
            startActivity(i);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
        findPreference("perfectlte_startService").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
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

//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_settings);




        addPreferencesFromResource(R.xml.pref_settings);

        SharedPreferences preferences= getSharedPreferences("cn.xyida.perfectlte_preferences", MODE_PRIVATE);
        if( preferences.getBoolean("isFirstRun",true)){
            final AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("注意:");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setView(R.layout.warning_layout);
            builder.setPositiveButton("同意",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //checkUpdate();
                }
            });
            builder.create().show();
            preferences.edit().putBoolean("isFirstRun",false).commit();
        }





//        bindPreferenceSummaryToValue(findPreference("perfetlte_startService"));
//        bindPreferenceSummaryToValue(findPreference("perfetlte_toggledelay"));
//        bindPreferenceSummaryToValue(findPreference("perfetlte_searchdelay"));


//        // In the simplified UI, fragments are not used at all and we instead
//        // use the older PreferenceActivity APIs.
//
//        // Add 'general' preferences.
//        addPreferencesFromResource(R.xml.pref_general);
//
//        // Add 'notifications' preferences, and a corresponding header.
//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_notifications);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_notification);
//
//        // Add 'data and sync' preferences, and a corresponding header.
//        fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_data_sync);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_data_sync);
//
//        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
//        // their values. When their values change, their summaries are updated
//        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("example_text"));
//        bindPreferenceSummaryToValue(findPreference("example_list"));
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
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
    private  Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
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
//                        (index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null));
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


              if ("开启后台服务".equals(preference.getTitle())){
                  if (!Boolean.parseBoolean(stringValue)){
                      Boolean flag=false;
                      ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                      for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                          if ("cn.xyida.perfectlte.CallEndService".equals(service.service.getClassName())) {
                              flag=true;
                              Log.e("SettingsActivity", "服务已经运行中");
                          }
                      }
                      if (flag){

                          Intent intent=new Intent();
                          intent.setAction("cn.xyida.perfectlte.intent.action.STOP_SERVICE");
                          sendBroadcast(intent);


                      }
                  }
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
    private  void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }



//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//    }
//
//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class SettingsPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_settings);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("perfetlte_startService"));
//            bindPreferenceSummaryToValue(findPreference("perfetlte_toggledelay"));
//            bindPreferenceSummaryToValue(findPreference("perfetlte_searchdelay"));
//        }
//    }

       public void toggleService(View v){
           Toast.makeText(SettingsActivity.this,"服务开启或关闭",Toast.LENGTH_SHORT).show();
       }


    public void checkUpdate() {

        Uri uri = Uri.parse("market://details?id=cn.xyida.perfectlte");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }
}
