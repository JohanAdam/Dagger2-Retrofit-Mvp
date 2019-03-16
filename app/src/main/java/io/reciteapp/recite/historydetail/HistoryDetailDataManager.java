package io.reciteapp.recite.historydetail;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.historydetail.HistoryDetailContract.Model;

public class HistoryDetailDataManager implements Model {

  private SharedPreferencesManager sharedPref;
  private String id;
  private String audioUri;

  public HistoryDetailDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPref = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
  }

  @Override
  public String getAudioUri() {
    return audioUri;
  }

}
