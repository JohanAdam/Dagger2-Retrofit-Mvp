package io.reciteapp.recite.submissionlist.cs;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.submissionlist.cs.SubmissionListCsContract.Model;

public class SubmissionListCsDataManager implements Model {

  private SharedPreferencesManager sharedPref;

  public SubmissionListCsDataManager(SharedPreferencesManager sharedPref) {
    this.sharedPref = sharedPref;
  }

  @Override
  public String getToken() {
    return sharedPref.loadString(Constants.PREF_TOKEN);
  }

  /**
   * Save tab state, Newest, Oldest or All
   * @param sortPref 0 = Newest ; 1 = Oldest
   */
  @Override
  public void saveSortPref(int sortPref) {
    sharedPref.saveInt(Constants.PREF_SORT_PREF, sortPref);
  }

  /**
   * Load tab state Newest, Oldest or All
   *  0 = Newest ; 1 = Oldest
   */
  @Override
  public int loadSortPref() {
    return sharedPref.loadingInt(Constants.PREF_SORT_PREF);
  }
}
