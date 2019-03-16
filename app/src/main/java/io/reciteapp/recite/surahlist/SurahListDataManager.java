package io.reciteapp.recite.surahlist;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.surahlist.SurahListContract.Model;

public class SurahListDataManager implements Model {

  private SharedPreferencesManager sharedPref;

  public SurahListDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPref = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

}
