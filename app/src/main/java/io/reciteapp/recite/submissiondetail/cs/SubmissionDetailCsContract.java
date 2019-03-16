package io.reciteapp.recite.submissiondetail.cs;

import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.utils.Rplayer;
import java.util.List;

public interface SubmissionDetailCsContract {

  interface View {

    void showLoadingDialog();

    void removeLoadingDialog();

    void showPlayerLoadingView();

    void removePlayerLoadingView();

    void showSnackbar(int messageResource);

    void showErrorDialog(int responseCode, boolean isKick);

    void showUpdateDialog();

    void btnBackwardToggle(boolean isEnable);

    void btnForwardToggle(boolean isEnable);

    void btnPlayToggle(boolean isEnable);

    void btnSubmitToggle(boolean isEnable);

    void btnFreeStyleToggle(boolean isEnable);

    void initializedPlayer(String audioUri);

    void errorAudioView();

    void updateAudioDuration(long currentDuration, long totalDuration);

    void insertCommentAtRv(String userAudioUri, String audioAttachmentFileName,
        List<UstazReviewCommentResponse> comment);

    void openCommentDialog(String userAudioUri, String audioFileName,
        UstazReviewCommentResponse comment);

    void finishFragment();
  }

  interface Model {

    String getToken();

    void setSurahName(String surahName);

    String getSurahName();

    void setSubmissionId(String submissionId);

    String getSubmissionId();

    void setAudioId(String audioId);

    String getAudioId();

    void setUserAudioUri(String result);

    String getUserAudioUri();

    void setAudioFileName(String audioFileName);

    String getAudioFileName();

    void setAudioAttachmentDuration(long audioAttachmentDuration);

    long getAudioAttachmentDuration();

    void setStartReviewingDuration(long startReviewDuration);

    long getStartReviewingDuration();

    void setCommentList(List<UstazReviewCommentResponse> commentList);

    List<UstazReviewCommentResponse> getCommentList();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setSurahName(String surahName);

    void setSurahId(String surahId);

    void setAudioId(String audioId);

    void setAudioAttachmentFileName(String name);

    void setAudioAttachmentDuration(long audioAttachmentDuration);

    void setUstazReviewCommentToList(UstazReviewCommentResponse ustazReviewComment);

    void getSubmissionDetail();

    void postReport(String text);

    void initializedPlayer(Rplayer player);

    void playButtonFunction(boolean isError);

    void playPauseToggle();

    void seekTo(int value);

    void startTimerTask();

    void prePostAudioProcess();

    void openCommentFragment();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void unSubscribe();
  }

}
