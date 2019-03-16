package io.reciteapp.recite.submissionlist.user;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.submissionlist.user.SubmissionListContract.Model;
import java.util.List;

public class SubmissionListDataManager implements Model {

  private SharedPreferencesManager sharedPreferences;
  private List<SubmissionListResponse> submissionListResponses;

  public SubmissionListDataManager(SharedPreferencesManager sharedPreferencesManagerV){
    this.sharedPreferences = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPreferences.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public void setSubmissionList(List<SubmissionListResponse> submissionList) {
    this.submissionListResponses = submissionList;
  }

  @Override
  public List<SubmissionListResponse> getSubmissionList() {
    return submissionListResponses;
  }
}
