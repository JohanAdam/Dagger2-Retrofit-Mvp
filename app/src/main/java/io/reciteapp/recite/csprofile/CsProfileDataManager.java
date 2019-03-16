package io.reciteapp.recite.csprofile;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.csprofile.CsProfileContract.Model;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;

public class CsProfileDataManager implements Model {

  private SharedPreferencesManager sharedPref;
  private String id;

  public CsProfileDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
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
}
