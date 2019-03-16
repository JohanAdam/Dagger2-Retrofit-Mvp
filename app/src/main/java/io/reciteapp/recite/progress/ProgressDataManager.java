package io.reciteapp.recite.progress;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.progress.ProgressContract.Model;

public class ProgressDataManager implements Model{

  private SharedPreferencesManager sharedPreferences;

  public ProgressDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPreferences = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPreferences.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public boolean getLoginStatus() {
    return sharedPreferences.loadLoginStatus();
  }
}
