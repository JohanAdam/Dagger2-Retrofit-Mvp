package io.reciteapp.recite.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import io.reciteapp.recite.BuildConfig;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.root.App;
import java.util.Locale;
import timber.log.Timber;

public class SharedPreferencesManager implements SharedPreferencesContract {

  private Context context;

  public SharedPreferencesManager(Context context) {
    this.context = context;
  }

  //get Default SharedPrefs
  private static SharedPreferences getDefaultSharedPreferences(Context context){
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Override
  public void saveString(String key ,String data) {
    Timber.d("saveString key " + key);
    //If key is country, save as default sp instead of file sp
    SharedPreferences sharedPreferences;
    if (key.equals(Constants.PREF_COUNTRY)) {
      sharedPreferences = getDefaultSharedPreferences(context);
    } else {
      sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }
    sharedPreferences.edit().putString(key, data).apply();

    //If save country make sure called the App.onCreate again
    if (key.equals(Constants.PREF_COUNTRY)) {
      App.getApp().buildComponent();
    }
  }

  @Override
  public void saveBoolean(String key, boolean data) {
    //if key is first user, save it in another sp file to avoid being wipe when logout
    SharedPreferences sharedPreferences;
    if (key.equals(Constants.PREF_FIRST_USER)) {
      sharedPreferences = getDefaultSharedPreferences(context);
    } else {
      sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }
    sharedPreferences.edit().putBoolean(key, data).apply();
  }

  @Override
  public void saveInt(String key, int data) {
    context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
        .edit().putInt(key, data).apply();
  }

  @Override
  public String loadString(final String key) {
    SharedPreferences sharedPref;
    if (key.equals(Constants.PREF_COUNTRY)) {
      sharedPref = getDefaultSharedPreferences(context);
    } else {
      sharedPref = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }
    return sharedPref.getString(key, key.equals(Constants.PREF_COUNTRY) ? Constants.in : "");
  }

  @Override
  public boolean loadBoolean(final String key) {
    //Load another sp file when key is first user.
    SharedPreferences sharedPreferences;
    if (key.equals(Constants.PREF_FIRST_USER)) {
      sharedPreferences = getDefaultSharedPreferences(context);
    } else {
      sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }
    return sharedPreferences.getBoolean(key, key.equals(Constants.PREF_FIRST_USER));

  }

  @Override
  public int loadingInt(final String key) {
    return context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
        .getInt(key, 0);
  }

  @Override
  public boolean loadLoginStatus() {
    String token = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString(Constants.PREF_TOKEN, "");
    String uid = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString(Constants.PREF_USER_ID, "");

    return !TextUtils.isEmpty(token) && !TextUtils.isEmpty(uid);

  }

  //get app language
  public void getLanguage() {
    Timber.d("getLanguage");
    //LANGUAGE SECTION
    //loading user app preferences
    String lang = getDefaultSharedPreferences(context).getString("LANG","");
    Configuration config = context.getResources().getConfiguration();

    if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
      Locale locale = new Locale(lang);
      Timber.d("Language get is %s", lang);

      Locale.setDefault(locale);
      config.setLocale(locale);
      context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
  }

  public String getMainUrl() {
    Timber.d("getMainUrl : country is " + loadString(Constants.PREF_COUNTRY));
    if (loadString(Constants.PREF_COUNTRY).equals(Constants.in)) {
      return BuildConfig.BASEURLIN;
    } else {
      return BuildConfig.BASEURLMY;
    }
  }

  @Override
  public void clearData(String data) {
    context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().remove(data).apply();
  }

  @Override
  public void logout() {
    Timber.d("logout Sp");
    SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit();
    editor.clear();
    editor.apply();
  }

}
