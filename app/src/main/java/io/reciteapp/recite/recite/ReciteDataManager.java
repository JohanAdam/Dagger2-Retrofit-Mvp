package io.reciteapp.recite.recite;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.SubmissionRecorderHistories;
import io.reciteapp.recite.recite.ReciteContract.Model;

public class ReciteDataManager implements Model {

  private SharedPreferencesManager sharedPreferences;
  private long sMaxRecordTime = 0;
  private long sRemainingTime = 0;
  private String audioFileName;
  private String surahName;
  private String surahId;
  private String ayat;
  private SubmissionRecorderHistories itemOne;
  private SubmissionRecorderHistories itemTwo;

  public ReciteDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPreferences = sharedPreferencesManagerV;
  }

  @Override
  public String getToken() {
    return sharedPreferences.loadString(Constants.PREF_TOKEN);
  }

  @Override
  public void setSurahName(String surahName) {
    this.surahName = surahName;
  }

  @Override
  public String getSurahName() {
    return surahName;
  }

  @Override
  public void setSurahId(String surahId) {
    this.surahId = surahId;
  }

  @Override
  public String getSurahId() {
    return surahId;
  }

  @Override
  public void setAudioFileName(String surahName) {
    this.audioFileName = surahName;
  }

  @Override
  public String getAudioFileName() {
    return audioFileName;
  }

  /**
   * @param sMaxRecordTime User Max Recording Time in SECOND
   */
  public void setsMaxRecordTime(long sMaxRecordTime) {
    this.sMaxRecordTime = sMaxRecordTime;
  }

  @Override
  public long getsMaxRecordTime() {
    return sMaxRecordTime;
  }

  @Override
  public void setAyat(String ayat) {
    this.ayat = ayat;
  }

  @Override
  public String getAyat() {
    return ayat;
  }

  /**
   * @param sRemainingTime User Remaining Recording Time in SECOND
   */
  @Override
  public void setsRemainingTime(long sRemainingTime) {
    this.sRemainingTime = sRemainingTime;
  }

  /**
   * Get ReciteTime remainingTime
   * @return remainingTime;
   */
  @Override
  public long getsRemainingTime() {
    return sRemainingTime;
  }

  /**
   * Save history detail model data item one if available
   */
  @Override
  public void setHistoryItemOne(SubmissionRecorderHistories historyItemOne) {
    this.itemOne = historyItemOne;
  }

  /**
   * Get history detail model data item one
   */
  @Override
  public SubmissionRecorderHistories getHistoryItemOne() {
    if (itemOne != null) {
      return itemOne;
    }
    return null;
  }

  /**
   * Save history detail model data item two if available
   */
  @Override
  public void setHistoryItemTwo(SubmissionRecorderHistories historyItemTwo) {
    this.itemTwo = historyItemTwo;
  }

  /**
   * Get history detail model data item two
   */
  @Override
  public SubmissionRecorderHistories getHistoryItemTwo() {
    if (itemTwo != null) {
      return itemTwo;
    }
    return null;
  }
}
