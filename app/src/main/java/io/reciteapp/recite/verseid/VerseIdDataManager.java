package io.reciteapp.recite.verseid;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.verseid.VerseIdContract.Model;

public class VerseIdDataManager implements Model {

  private SharedPreferencesManager sharedPref;

  public VerseIdDataManager(SharedPreferencesManager sharedPreferencesManager) {
    this.sharedPref = sharedPreferencesManager;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }
}
