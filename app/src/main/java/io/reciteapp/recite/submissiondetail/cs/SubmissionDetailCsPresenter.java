package io.reciteapp.recite.submissiondetail.cs;

import android.os.Handler;
import android.text.TextUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.Util;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.data.model.UstazReviewResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.ReportAudioCall.PostReportCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.CsReviewRepository.CsReviewRepoCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.cs.SubmissionDetailCallCs.GetSubmissionDetailCsCallback;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract.Model;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract.Presenter;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract.View;
import io.reciteapp.recite.utils.FileManager;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SubmissionDetailCsPresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;
  private Rplayer player = null;
  private long playerDuration = 0;

  //Timer handler
  private static final Handler mHandler = new Handler();

  /**
   * Set model to presenter
   */
  public SubmissionDetailCsPresenter(Model model) {
    this.model = model;
  }

  /**
   * Set view and service to presenter
   */
  @Override
  public void setView(View view, NetworkCallWrapper service) {
    this.subscriptions = new CompositeSubscription();
    this.view = view;
    this.service = service;

    //Delete all audio recorded in local if available
    new FileManager().deleteAll();
  }

  /**
   * Check for view if still attached or not
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Save surah name without edited
   */
  @Override
  public void setSurahName(String surahName) {
    model.setSurahName(surahName);
  }

  @Override
  public void setSurahId(String surahId) {
    model.setSubmissionId(surahId);
  }

  @Override
  public void setAudioId(String audioId) {
    model.setAudioId(audioId);
  }

  /**
   * Set audio file name for recording and playing
   * @param name Should be surahname that has been trimmed for Audio file
   */
  @Override
  public void setAudioAttachmentFileName(String name) {
    model.setAudioFileName(name);
  }

  @Override
  public void setAudioAttachmentDuration(long audioAttachmentDuration) {
    model.setAudioAttachmentDuration(audioAttachmentDuration);
  }

  @Override
  public void setUstazReviewCommentToList(UstazReviewCommentResponse ustazReviewComment) {
    List<UstazReviewCommentResponse> commentList = model.getCommentList();
    Timber.d("UstazReviewCommentList size %s", commentList.size());

    boolean getIt = false;
    for (int i = 0; i < commentList.size(); i++) {
      if (commentList.get(i).getAudioDuration().equals(ustazReviewComment.getAudioDuration())) {
        commentList.set(i, ustazReviewComment);
        Timber.d("GetIt is true, set at position %s", i);
        getIt = true;
      }
    }

    if (!getIt) {
      Timber.d("GetIt is false , add");
      commentList.add(ustazReviewComment);
    }

    //sort
    Collections.sort(model.getCommentList(),
        (lhs, rhs) -> lhs.getAudioDuration().compareTo(rhs.getAudioDuration()));

    model.setCommentList(commentList);
    view.insertCommentAtRv(model.getUserAudioUri(), model.getAudioFileName() ,model.getCommentList());
  }

  @Override
  public void getSubmissionDetail() {
    view.showPlayerLoadingView();
    String token = model.getToken();
    String id = model.getSubmissionId();
    Subscription subscription = service.getSubmissionDetailCs(token, id,
        new GetSubmissionDetailCsCallback() {
          @Override
          public void onSuccess(String response) {
            Timber.d("onSuccess %s", response);
            if (isViewAttached()) {
              view.removePlayerLoadingView();
              response = response.replace("\"", "");
              model.setUserAudioUri(response);
              view.initializedPlayer(response);
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removePlayerLoadingView();
              view.errorAudioView();

              switch (networkError.getResponseCode()) {
                case 404:
                  //TODO error get because of submission locked by other reviewer/submission has been reviewed
                  view.showErrorDialog(Constants.RESPONSE_CODE_LOCKED_NOT_FOUND, true);
                  break;
              }
            }
          }
        });

    subscriptions.add(subscription);
  }

  @Override
  public void postReport(String text) {
    Timber.d("postReport");
    view.showLoadingDialog();

    String token = model.getToken();
    String id = model.getAudioId();
    Subscription subscription = service.postReportAudio(token, id, text, new PostReportCallback() {
      @Override
      public void onSuccess() {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          view.showSnackbar(R.string.msg_feedback_success);
        }
      }

      @Override
      public void onError(NetworkError networkError) {
        if (isViewAttached()) {
          view.removeLoadingDialog();
          view.showErrorDialog(networkError.getResponseCode(), false);
        }
      }
    });

    subscriptions.add(subscription);
  }

  /**
   * Initialized player
   * @param player Pass player that has been inserted context from view to control in presenter
   */
  @Override
  public void initializedPlayer(Rplayer player) {
    player.seekToWithoutRange(playerDuration);
    this.player = player;
    view.btnPlayToggle(true);
    //Start timer
    model.setStartReviewingDuration(System.currentTimeMillis());

    //Set new list
    List<UstazReviewCommentResponse> ustazCommentList = new ArrayList<>();
    model.setCommentList(ustazCommentList);

    //TODO disable attachAudio
    view.btnFreeStyleToggle(true);
    view.btnSubmitToggle(true);

    //Start handler to update audio duration appropriately
    startTimerTask();
  }

  @Override
  public void playButtonFunction(boolean isPlayerError) {
    Timber.d("playButtonFunction %s", isPlayerError);
    if (isPlayerError) {
      //get submission detail if audiouri not available
      //if audioUri available, just initialized player
      getSubmissionDetail();
    } else {
      playPauseToggle();
    }
  }

  @Override
  public void playPauseToggle() {
    Timber.d("playPauseToggle");
    if (player != null) {
      if (player.getPlayerState() == Player.STATE_ENDED) {
        Timber.d("player state is ended, seek to 0 and play");
        player.seekTo(0);
        player.play();
      } else {
        Timber.d("player state not ended");
        if (player.checkPlaying()) {
          player.pause();
          openCommentFragment();
        } else {
          player.play();
        }
      }
    }
  }

  /**
   *
   * @param value 1 = seek to 0 ; 2 = seek forwards ; 3 = seek backwards
   */
  @Override
  public void seekTo(int value) {
    if (player != null) {
      //Update progress bar accordingly
      Timber.d("before %s", player.getCurrentDuration());
      int currentPosition = (int) player.getCurrentDuration();
      int seekTime = 5000;

      if (value == 1) {
        //Seek to 0
        player.seekTo(0);
      } else if (value == 2) {
        //Seek forwards
        //If seekForward + current duration is less than total audio duration itself , seek forward
        if (currentPosition + seekTime <= player.getDuration()) {
          player.seekForward(currentPosition);
        }
      } else if (value == 3) {
        //Seek backwards
        //If seek backwards - current duration is greater than 0 sec, seek backwards
        if (currentPosition - seekTime >= 0) {
          player.seekBackward(currentPosition);
        } else {
          //unseekable backward because already near 0
          player.seekTo(0);
        }
      }

    }
  }

  @Override
  public void startTimerTask() {
    mHandler.post(mUpdateTimeTask);
  }

  /**Handler running to update textView appropriately**/
  private final Runnable mUpdateTimeTask = new Runnable() {
    @Override
    public void run() {

      long audioTotalDuration = player.getDuration();
      long audioCurrentDuration = player.getCurrentDuration();

      view.updateAudioDuration(audioCurrentDuration, audioTotalDuration);
      mHandler.post(this);
    }
  };

  /**
   * Step 1
   * Post audio to server*
   * 1 - Fill item in UstazReviewResponse (prePostAudioProcess)
   * 2 - Get Sas Query
   * 3 - Upload audio
   * 4 - Upload audio data/json
   * - Complete (  Reset all )
   */
  @Override
  public void prePostAudioProcess() {
    Timber.d("prePostAudioProcess");

    Timber.d("Comment list is size %s", model.getCommentList().size());
    //Set review comment list
    if (model.getCommentList().size() == 0) {
      view.showSnackbar(R.string.error_please_insert_comment);
    } else {
      view.showLoadingDialog();

      //Fill the item
      UstazReviewResponse ustazReview = new UstazReviewResponse();
      //Set submission Id
      ustazReview.setAudioID_FK(model.getSubmissionId());
      //Set general Review time
      ustazReview.setReviewDuration((int) (System.currentTimeMillis() - model.getStartReviewingDuration()));
      //Set review comment list
      ustazReview.setUstazReviewComments(model.getCommentList());

      // ---- PASS for uploading ----
      postAudioAttachment(ustazReview);
    }
  }

  private void postAudioAttachment(UstazReviewResponse ustazReview) {
    Timber.d("postAudioAttachment");
    String token = model.getToken();

    Subscription subscription = service.postCsReviewService(token,
        ustazReview,
        model.getSurahName(),
        new CsReviewRepoCallback() {
          @Override
          public void onSuccess(UstazReviewResponse ustazReviewResponse) {
            Timber.d("onSuccess postAudioAttachment");
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showSnackbar(R.string.msg_audio_submission_success);
              view.finishFragment();
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            Timber.e("onError postAudioAttachment");
            if (isViewAttached()) {
              view.removeLoadingDialog();

              switch (networkError.getResponseCode()) {
                case 404:
                  //Locked not found
                  view.showErrorDialog(Constants.RESPONSE_CODE_LOCKED_NOT_FOUND, true);
                  break;
                case 400:
                  //Bad request
                  view.showErrorDialog(Constants.RESPONSE_CODE_BAD_REQUEST, false);
                  break;
                default:
                  view.showErrorDialog(Constants.RESPONSE_CODE_FAILED, false);
                  break;
              }

            }
          }
        });

    subscriptions.add(subscription);
  }

  /**
   * Check if there is comment already in this point or not
   * And open the fragment record comment
   */
  @Override
  public void openCommentFragment() {
    if (player != null) {
      if (player.checkPlaying()) {
        player.pause();
      }

      long currentDuration = player.getCurrentDuration();
      String currentTime = new Utils().millisecondsToTimerStringFormat(currentDuration);

      UstazReviewCommentResponse ustazComment = null;

      //Find if duration exist in list
      List<UstazReviewCommentResponse> commentList = model.getCommentList();
      for (int i = 0; i< commentList.size(); i++) {
        if (commentList.get(i).getAudioDuration().equals(String.valueOf(currentTime))) {
          Timber.d("Sending existing comment list at position %s", i);
          ustazComment = commentList.get(i);
        }
      }

      if (ustazComment == null) {
        Timber.d("Sending ustazComment item null");
        ustazComment = new UstazReviewCommentResponse();
      }
      ustazComment.setAudioDuration(currentTime);
      view.openCommentDialog(model.getUserAudioUri(), model.getAudioFileName(), ustazComment);
    } else {
      Timber.d("getSubmissionDetail back and initialized player");
      getSubmissionDetail();
    }
  }

  @Override
  public void onStart() {
    if (Util.SDK_INT > 23) {
      if (view != null && !TextUtils.isEmpty(model.getUserAudioUri())) {
        view.initializedPlayer(model.getUserAudioUri());
      }
    }
  }

  @Override
  public void onResume() {
    if (Util.SDK_INT <= 23 || player == null) {
      if (view != null && !TextUtils.isEmpty(model.getUserAudioUri())) {
        view.initializedPlayer(model.getUserAudioUri());
      }
    }
  }

  @Override
  public void onPause() {
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  private void releasePlayer() {
    if (player != null) {
      mHandler.removeCallbacks(mUpdateTimeTask);
      playerDuration = player.getCurrentDuration();
      player.stopAndRelease();
    }
  }

  /**
   * Unsubscribe from all network call and delete all recording if file
   */
  @Override
  public void unSubscribe() {
    releasePlayer();
    subscriptions.unsubscribe();
    new FileManager().deleteAll();
    view = null;
  }
}