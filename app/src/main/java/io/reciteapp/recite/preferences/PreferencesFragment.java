package io.reciteapp.recite.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.utils.Utils;
import java.util.Locale;
import timber.log.Timber;

public class PreferencesFragment extends PreferenceFragment {

  PreferencesActivity activity;
  SharedPreferencesManager sharedPreferences;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof PreferencesActivity) {
      activity = (PreferencesActivity) context;
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof PreferencesActivity) {
      this.activity = (PreferencesActivity) activity;
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      sharedPreferences = new SharedPreferencesManager(activity);

//    if (sharedPrefs.loadBoolean(Constants.PREF_CS_STATE)) {
      addPreferencesFromResource(R.xml.prefs_layout_country);

      ListPreference countryPreferences = (ListPreference) getPreferenceScreen().findPreference("country");
      countryPreferences.setOnPreferenceChangeListener(countryChangeListener);
      countryPreferences.setValue(sharedPreferences.loadString(Constants.PREF_COUNTRY));
      Timber.d("Country is %s", sharedPreferences.loadString(Constants.PREF_COUNTRY));
//    } else {
//      addPreferencesFromResource(R.xml.prefs_layout);
//    }

    Preference langPreferences = getPreferenceScreen().findPreference("language");
    langPreferences.setOnPreferenceChangeListener(languageChangeListener);

  }

  private Preference.OnPreferenceChangeListener countryChangeListener = new OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
      Timber.d("Country selected %s", newValue.toString());
      sharedPreferences.saveString(Constants.PREF_COUNTRY, newValue.toString());
      new Utils().getToast(activity, "It will automatically logout to take effect.").show();
      activity.logout();
//      App.getApp().onCreate();
      return true;
    }
  };

  private Preference.OnPreferenceChangeListener languageChangeListener = new OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
      Timber.d("Language change to %s", newValue.toString());
      switch (newValue.toString()) {
        case "en": //English
          PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
              .putString("LANG", "en").apply();
          setLangRecreate("en");
          break;
        case "ms":
          PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
              .putString("LANG", "ms").apply();
          setLangRecreate("ms");
          break;
        case "in":
          PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
              .putString("LANG", "in").apply();
          setLangRecreate("in");
          break;
        default:
          PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
              .putString("LANG", "en").apply();
          setLangRecreate("en");
          break;
      }

      return true;
    }
  };

  private void setLangRecreate(String lang) {
    Configuration config = getResources().getConfiguration();
    Locale locale = new Locale(lang);
    Locale.setDefault(locale);
    config.locale = locale;
    getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.setLocale(locale);
    res.updateConfiguration(conf, dm);

//    activity.finish();
    Intent intent = new Intent(getActivity(), MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }

}
