package io.reciteapp.recite.dashboard;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.DashboardResponse;


public class DashboardDataManager implements DashboardContract.Model {

  private DashboardResponse dashboardResponse;
  private SharedPreferencesManager sharedPreferencesManagerV;

  public DashboardDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPreferencesManagerV = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPreferencesManagerV.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public boolean getLoginStatus() {
    return sharedPreferencesManagerV.loadLoginStatus();
  }

  @Override
  public void setDashboardData(DashboardResponse dashboardResponse) {
    //save as instance
    this.dashboardResponse = dashboardResponse;
  }

  @Override
  public DashboardResponse getDashboardData() {
    return dashboardResponse;
  }

  @Override
  public void setCsState(boolean csState) {
    sharedPreferencesManagerV.saveBoolean(Constants.PREF_CS_STATE, csState);
  }

  @Override
  public boolean getCsState() {
    return sharedPreferencesManagerV.loadBoolean(Constants.PREF_CS_STATE);
  }

  @Override
  public void setEmail(String email) {
    sharedPreferencesManagerV.saveString(Constants.PREF_EMAIl, email);
  }

  @Override
  public String getCountry() {
    return sharedPreferencesManagerV.loadString(Constants.PREF_COUNTRY);
  }

  @Override
  public String getMessage() {
    return sharedPreferencesManagerV.loadString(Constants.PREF_MESSAGE);
  }

  @Override
  public int getReferralTime() {
    return sharedPreferencesManagerV.loadingInt(Constants.PREF_REFERRAL_TIME);
  }

  @Override
  public void clearData(String data) {
    sharedPreferencesManagerV.clearData(data);
  }
}
