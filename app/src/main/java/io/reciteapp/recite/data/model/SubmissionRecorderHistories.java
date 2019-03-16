package io.reciteapp.recite.data.model;

import java.io.Serializable;

public class SubmissionRecorderHistories implements Serializable {

  private String submissionId;
  private int submissionTime;
  private boolean isReviewed;

  public String getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  public int getSubmissionTime() {
    return submissionTime;
  }

  public void setSubmissionTime(int submissionTime) {
    this.submissionTime = submissionTime;
  }

  public boolean isReviewed() {
    return isReviewed;
  }

  public void setReviewed(boolean reviewed) {
    isReviewed = reviewed;
  }
}
