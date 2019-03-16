package io.reciteapp.recite.share;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.share.ShareContract.Model;
import io.reciteapp.recite.utils.Utils;
import timber.log.Timber;

public class ShareDataManager implements Model {

  private SharedPreferencesManager sharedPref;
  private String shareCode;

  public ShareDataManager(SharedPreferencesManager sharedPreferencesManager) {
    this.sharedPref = sharedPreferencesManager;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public void setShareCode(String shareCode) {
    this.shareCode = shareCode;
  }

  @Override
  public String getShareCode() {
    return shareCode;
  }

  @Override
  public String getUserId() {
    return sharedPref.loadString(Constants.PREF_USER_ID);
  }

  @Override
  public void copyShareCode() {
    Timber.d("copyShareCode");
    final String message = App.getApp().getString(R.string.msg_share_recite_template, shareCode);
    new Utils().copyToClipboard(message);
  }

  @Override
  public boolean checkLogin() {
    return sharedPref.loadLoginStatus();
  }
}
