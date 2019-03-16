package io.reciteapp.recite.submissiondetail.user;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract.Model;

public class SubmissionDetailUDataManager implements Model {

  private SharedPreferencesManager sharedPref;
  private String id;
  private String userAudioUri;

  public SubmissionDetailUDataManager(SharedPreferencesManager sharedPreferences) {
    this.sharedPref = sharedPreferences;
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
  public void setUserAudioUri(String userAudioUri) {
    this.userAudioUri = userAudioUri;
  }

  @Override
  public String getUserAudioUri() {
    return userAudioUri;
  }
}
