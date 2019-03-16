package io.reciteapp.recite.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class HistoryDetailResponse {

  //Submission id
  private String id;
  //Surah name
  private String surahName;
  //Ayat
  private String ayat;
  //data of the submission
  @SerializedName("submissionTime") private String submissionDate;
  //audio uri for user audio
  private String userAudioUri;
  //mark if cs already review this submission or not
  private boolean isReviewed;
  //mark if review has name or not
  private String reviewerName;
  //cs profile picture
  private String reviewerPhoto;
  //contain cs comment ( duration , comment , reviewerComment , isAttachmentAvailable)
  private ArrayList<ReviewerCommentResponse> reviewerComment;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSurahName() {
    return surahName;
  }

  public void setSurahName(String surahName) {
    this.surahName = surahName;
  }

  public String getAyat() {
    return ayat;
  }

  public void setAyat(String ayat) {
    this.ayat = ayat;
  }

  public String getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(String submissionDate) {
    this.submissionDate = submissionDate;
  }

  public String getUserAudioUri() {
    return userAudioUri;
  }

  public void setUserAudioUri(String userAudioUri) {
    this.userAudioUri = userAudioUri;
  }

  public boolean isReviewed() {
    return isReviewed;
  }

  public void setReviewed(boolean reviewed) {
    isReviewed = reviewed;
  }

  public String getReviewerName() {
    return reviewerName;
  }

  public void setReviewerName(String reviewerName) {
    this.reviewerName = reviewerName;
  }

  public String getReviewerPhoto() {
    return reviewerPhoto;
  }

  public void setReviewerPhoto(String reviewerPhoto) {
    this.reviewerPhoto = reviewerPhoto;
  }

  public ArrayList<ReviewerCommentResponse> getReviewerComment() {
    return reviewerComment;
  }

  public void setReviewerComment(ArrayList<ReviewerCommentResponse> reviewerComment) {
    this.reviewerComment = reviewerComment;
  }

}
