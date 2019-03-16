package io.reciteapp.recite.data.model;

public class ReviewerCommentResponse {

  private String audioDuration;
  private String reviewAudioUri;
  private String remark;
  private boolean isAttachmentAvailable;

  public String getAudioDuration() {
    return audioDuration;
  }

  public void setAudioDuration(String audioDuration) {
    this.audioDuration = audioDuration;
  }

  public String getReviewAudioUri() {
    return reviewAudioUri;
  }

  public void setReviewAudioUri(String reviewAudioUri) {
    this.reviewAudioUri = reviewAudioUri;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public boolean isAttachmentAvailable() {
    return isAttachmentAvailable;
  }

  public void setAttachmentAvailable(boolean attachmentAvailable) {
    isAttachmentAvailable = attachmentAvailable;
  }
}
