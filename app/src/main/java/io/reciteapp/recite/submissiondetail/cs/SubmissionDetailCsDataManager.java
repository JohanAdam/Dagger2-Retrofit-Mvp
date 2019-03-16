package io.reciteapp.recite.submissiondetail.cs;

import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract.Model;
import java.util.List;

public class SubmissionDetailCsDataManager implements Model {

  private SharedPreferencesManager sharedPreferences;
  //Surah name unedited
  private String surahName;
  //For get submission detail
  private String submissionId;
  //User Audio Submission Uri
  private String userAudioUri;
  //SurahName trim and edited for audio attachment
  private String audioFileName;
  //For get reporting audio
  private String audioId;
  //Comment list
  private List<UstazReviewCommentResponse> commentList;
  //If true show pass / fail dialog before submit review
  private boolean isAssessment;
  //If audio attachment is available , audio duration must present
  private long audioAttachmentDuration = 0;
  //Mark duration when review is start
  private long reviewDurationStart = 0;

  public SubmissionDetailCsDataManager(SharedPreferencesManager sharedPreferencesManagerV) {
    this.sharedPreferences = sharedPreferencesManagerV;
  }

  //Get token authentication from shared pref
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
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  @Override
  public String getSubmissionId() {
    return submissionId;
  }

  @Override
  public void setAudioId(String audioId) {
    this.audioId = audioId;
  }

  @Override
  public String getAudioId() {
    return audioId;
  }

  @Override
  public void setUserAudioUri(String result) {
    this.userAudioUri = result;
  }

  @Override
  public String getUserAudioUri() {
    return userAudioUri;
  }

  @Override
  public void setAudioFileName(String audioAttachmentFileName) {
    String edittedName = audioAttachmentFileName + "reviewedRecorded";
    this.audioFileName = edittedName.replaceAll("[-']", "");
  }

  @Override
  public String getAudioFileName() {
    return audioFileName;
  }

  @Override
  public void setAudioAttachmentDuration(long audioAttachmentDuration) {
    this.audioAttachmentDuration = audioAttachmentDuration;
  }

  @Override
  public long getAudioAttachmentDuration() {
    return audioAttachmentDuration;
  }

  @Override
  public void setStartReviewingDuration(long startReviewDuration) {
    this.reviewDurationStart = startReviewDuration;
  }

  @Override
  public long getStartReviewingDuration() {
    return reviewDurationStart;
  }


  @Override
  public void setCommentList(List<UstazReviewCommentResponse> commentList) {
    this.commentList = commentList;
  }

  @Override
  public List<UstazReviewCommentResponse> getCommentList() {
    return commentList;
  }
}
