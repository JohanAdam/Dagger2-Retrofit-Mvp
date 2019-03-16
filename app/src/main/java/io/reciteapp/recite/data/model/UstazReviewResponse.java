package io.reciteapp.recite.data.model;

import java.util.List;

public class UstazReviewResponse {

  //User submission audio id.
  private String audioID_FK;
  //General how much cs takes time to review.
  private int reviewDuration;
  //Cs comment list
  private List<UstazReviewCommentResponse> ustazReviewComments;


  // ---- GETTER ----
  public String getAudioID_FK() {
    return audioID_FK;
  }
  public int getReviewDuration() {
    return reviewDuration;
  }
  public List<UstazReviewCommentResponse> getUstazReviewComments() {
    return ustazReviewComments;
  }

  // ---- SETTER ----
  public void setAudioID_FK(String audioID_FK) {
    this.audioID_FK = audioID_FK;
  }
  public void setReviewDuration(int reviewDuration) {
    this.reviewDuration = reviewDuration;
  }
  public void setUstazReviewComments(List<UstazReviewCommentResponse> ustazReviewComments) {
    this.ustazReviewComments = ustazReviewComments;
  }
}
