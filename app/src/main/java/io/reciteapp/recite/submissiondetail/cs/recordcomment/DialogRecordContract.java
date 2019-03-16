package io.reciteapp.recite.submissiondetail.cs.recordcomment;

import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;

public interface DialogRecordContract  {

  interface View {

    Rplayer getContextForPlayerUser(Rplayer playerUser, String userAudioUri,
        playerStateCallback playerUserStateListener,
        playerErrorStateCallback playerErrorListener);

    Rplayer getContextForPlayerComment(Rplayer playerComment, String commentAudioUri,
        playerStateCallback playerCommentStateListener,
        playerErrorStateCallback playerErrorListener);

    void setTextDurationToView(String duration);

    void setCommentToView(String comment);

    void viewAudioCsExist();

    void viewAudioCsNotExist();

    void removeDialog();

    void userPlayBtnEnable();

    void userPlayBtnDisable();

    void submitBtnEnable();

    void submitBtnDisable();

    void isRecordingView();

    void btnUserError();

    void btnUserBuffering();

    void btnUserReady(boolean playWhenReady);

    void btnUserEnded();

    void btnCsError();

    void btnCsBuffering();

    void btnCsReady(boolean playWhenReady);

    void btnCsEnded();

  }

  interface Presenter {

    void initDialog(View view, String userAudioUri, String audioFileName, UstazReviewCommentResponse ustazReviewCommentResponse);

    String getAudioDuration();

    String getComment();

    String getUserAudioUri();

    void showDialog(
        DialogRecordCommentCallback callback);

    void initializedPlayerUser();

    void initializedPlayerCs();

    void initializedRecorder();

    void playerUserToggle();

    void playerCsToggle();

    void recorderToggle();

    void deleteAudioComment();

    void submit(String comment);

    boolean checkAudioCommentFileExist();

    void checkAudioCommentExist();

    void destroy();

  }

  interface DialogRecordCommentCallback {
    void onClick(UstazReviewCommentResponse ustazReviewComment);
  }

}
