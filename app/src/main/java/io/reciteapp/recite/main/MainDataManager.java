package io.reciteapp.recite.main;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;

public class MainDataManager implements MainContract.Model {

  private SharedPreferencesManager sharefPref;

  public MainDataManager(SharedPreferencesManager sharedPref) {
    this.sharefPref = sharedPref;
  }

  @Override
  public boolean getLoginStatus() {
    return sharefPref.loadLoginStatus();
  }

  @Override
  public void setReferralCode(String referralCode) {
    sharefPref.saveString(Constants.PREF_REFERRAL_CODE, referralCode);
  }

  @Override
  public String getEnrollList() {
    return sharefPref.loadString(Constants.PREF_ENROLL_LIST);
  }

  @Override
  public String getToken() {
    return sharefPref.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public boolean getCsStatus() {
    return sharefPref.loadBoolean(Constants.PREF_CS_STATE);
  }

  @Override
  public boolean getFirstUser() {
    return sharefPref.loadBoolean(Constants.PREF_FIRST_USER);
  }

  @Override
  public void logout() {
    sharefPref.logout();
  }

}

