package io.reciteapp.recite.data.model;

public class UstazReviewCommentResponse {

  //Cs comment
  private String comment;
  //The comment
  private int audioReviewDuration;
  //Point where cs review in user submission.
  private String audioDuration;
  //Flag audio comment exist.
  private boolean isAttachmentAvailable;
  //Resource name of audio comment in server.
  private String resourceName;
  //Audio Uri local
  private String audioUri;

  // ----  GETTER ----
  public String getComment() {
    return comment;
  }

  public int getAudioReviewDuration() {
    return audioReviewDuration;
  }

  public String getAudioDuration() {
    return audioDuration;
  }

  public boolean isAttachmentAvailable() {
    return isAttachmentAvailable;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getAudioUri() {
    return audioUri;
  }

  // ---- SETTER ----

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setAudioReviewDuration(int audioReviewDuration) {
    this.audioReviewDuration = audioReviewDuration;
  }

  public void setAudioDuration(String audioDuration) {
    this.audioDuration = audioDuration;
  }

  public void setAttachmentAvailable(boolean attachmentAvailable) {
    isAttachmentAvailable = attachmentAvailable;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
  }
}
