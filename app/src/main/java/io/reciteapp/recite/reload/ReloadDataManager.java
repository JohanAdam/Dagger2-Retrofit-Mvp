package io.reciteapp.recite.reload;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.PackageListResponse;
import io.reciteapp.recite.reload.ReloadContract.Model;
import java.util.ArrayList;

public class ReloadDataManager implements Model {

  private SharedPreferencesManager sharedPreferences;
  private ArrayList<PackageListResponse> packageList;

  public ReloadDataManager(SharedPreferencesManager sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override
  public String getToken() {
    return sharedPreferences.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public String getCountry() {
    return sharedPreferences.loadString(Constants.PREF_COUNTRY);
  }

}
