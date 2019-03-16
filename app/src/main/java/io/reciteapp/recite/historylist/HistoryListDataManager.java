package io.reciteapp.recite.historylist;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.historylist.HistoryListContract.Model;

public class HistoryListDataManager implements Model {

  private SharedPreferencesManager sharedPref;
  private String surahName;
  private String ayatId;

  public HistoryListDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPref = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public void setSurahName(String surahName) {
    this.surahName = surahName;
  }

  @Override
  public String getSurahName() {
    return surahName;
  }

  @Override
  public void setSurahId(String ayatId) {
    this.ayatId = ayatId;
  }

  @Override
  public String getSurahId() {
    return ayatId;
  }
}
