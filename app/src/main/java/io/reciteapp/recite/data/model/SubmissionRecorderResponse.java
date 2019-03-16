package io.reciteapp.recite.data.model;

import java.util.List;

/**
 * Recite page.
 * Contain ReciteTime and two latest history in recite page.
 */

public class SubmissionRecorderResponse {

  private String ayatId;
  private int totalTime;
  private int remainingTime;
  public List<SubmissionRecorderHistories> surahHistories;

  public String getAyatId() {
    return ayatId;
  }

  public void setAyatId(String ayatId) {
    this.ayatId = ayatId;
  }

  public int getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  public int getRemainingTime() {
    return remainingTime;
  }

  public void setRemainingTime(int remainingTime) {
    this.remainingTime = remainingTime;
  }

  public List<SubmissionRecorderHistories> getSurahHistories() {
    return surahHistories;
  }

  public void setSurahHistories(List<SubmissionRecorderHistories> surahHistories) {
    this.surahHistories = surahHistories;
  }
}
