package io.reciteapp.recite.login;

import android.text.TextUtils;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.UserProfileResponse;
import timber.log.Timber;

public class LoginDataManager implements LoginContract.Model {

  private static String TAG = "LoginDataManager";
  private UserProfileResponse userProfileResponse;
  private SharedPreferencesManager sharedPref;

  public LoginDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPref = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public String getUserId() {
    return sharedPref.loadString(Constants.PREF_USER_ID);
  }

  //Save user id and token and login provider
  @Override
  public void setUIDAndToken(String userId,
      String token,
      String provider) {
    Timber.d("mUser token is %s", token);

    sharedPref.saveString(Constants.PREF_TOKEN, token);
    sharedPref.saveString(Constants.PREF_USER_ID, userId);
    sharedPref.saveString(Constants.PREF_LOGIN_PROVIDER, provider);

  }

  @Override
  public void setUserProfile(UserProfileResponse userProfileResponse) {
    this.userProfileResponse = userProfileResponse;

    if (!TextUtils.isEmpty(userProfileResponse.getReferralCode())) {
      sharedPref.saveString(Constants.PREF_REFERRAL_CODE, userProfileResponse.getReferralCode());
    }

    sharedPref.saveBoolean(Constants.PREF_CS_STATE, userProfileResponse.isCredible());

    if (!TextUtils.isEmpty(userProfileResponse.getName())) {
      sharedPref.saveString(Constants.PREF_USER_NAME, userProfileResponse.getName());
    }

    if (!TextUtils.isEmpty(userProfileResponse.getId())) {
      sharedPref.saveString(Constants.PREF_USER_ID, userProfileResponse.getId());
    }

    if (!TextUtils.isEmpty(userProfileResponse.getEmail())) {
      sharedPref.saveString(Constants.PREF_EMAIl, userProfileResponse.getEmail());
    }

    registerNotification();

  }

  @Override
  public void logout() {
    sharedPref.logout();
  }

  @Override
  public void setEmail(String email) {
    sharedPref.saveString(Constants.PREF_EMAIl, email);
  }

  private void registerNotification() {
    //TODO Handle notification by azure
  }

}
