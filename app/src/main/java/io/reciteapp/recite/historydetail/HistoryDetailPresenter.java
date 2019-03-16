package io.reciteapp.recite.historydetail;

import android.text.TextUtils;
import com.google.android.exoplayer2.util.Util;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.history.historydetail.HistoryDetailCall.GetHistoryDetailCallback;
import io.reciteapp.recite.data.networking.call.history.historydetail.RatingCsCall.PatchRatingCsCallback;
import io.reciteapp.recite.historydetail.HistoryDetailContract.Model;
import io.reciteapp.recite.historydetail.HistoryDetailContract.Presenter;
import io.reciteapp.recite.historydetail.HistoryDetailContract.View;
import io.reciteapp.recite.utils.Rplayer;
import java.util.Collections;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class HistoryDetailPresenter implements Presenter {

  private Model model;
  private NetworkCallWrapper service;
  private View view;
  private CompositeSubscription subscriptions;
  private Rplayer player = null;
  private long playerDuration = 0;

  /**
   * Set model in presenter
   */
  public HistoryDetailPresenter(Model model) {
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
  }

  @Override
  public void setId(String id) {
    model.setId(id);
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Get history detail from server
   */
  @Override
  public void getHistoryDetail() {
    view.showWait();
    view.removeErrorView();

    String token = model.getToken();
    String audioId = model.getId();
    Subscription subscription = service.getHistoryDetails(token, audioId,
        new GetHistoryDetailCallback() {
          @Override
          public void onSuccess(HistoryDetailResponse historyDetailResponse) {
            if (isViewAttached()) {
              view.removeWait();
              processHistoryDetailResponse(historyDetailResponse);
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            Timber.e("onError getHistoryDetail");
            view.removeWait();
            view.showErrorView();
          }
        });

    subscriptions.add(subscription);
  }

  /**
   * Patch Rating for cs review
   * @param rating Rating value
   * @param feedback Feedback in string
   */
  @Override
  public void patchRatingCs(int rating, String feedback) {
    view.showLoadingDialog();

    String token = model.getToken();
    String audioId = model.getId();
    Subscription subscription = service.patchRatingCs(token, audioId, feedback,
        String.valueOf(rating), new PatchRatingCsCallback() {
          @Override
          public void onSuccess() {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showAfterRatedCs();
            }
          }

          @Override
          public void onError(int responseCode) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showSnackbar(R.string.error_default);
            }
          }
        });

    subscriptions.add(subscription);

  }

  @Override
  public void openCsProfileFragment() {
    view.openCsProfileFragment(model.getId());
  }

  /**
   * Setup history detail response get from server
   * @param response
   */
  private void processHistoryDetailResponse(
      HistoryDetailResponse response) {
    Timber.v("processHistoryDetailResponse");

    if (response != null) {
      Timber.v("response != null");
      if (response.getUserAudioUri() != null) {
        model.setAudioUri(response.getUserAudioUri());
        view.initializedPlayer(response.getUserAudioUri());
      } else {
        Timber.e("No audio to user play");
        view.hidePlayerView();
      }

      view.setCsProfile(response);

      //If already reviewed, setup the comment list
      if (response.isReviewed()) {
        setupReviewView(response);
      } else {
        //Cs not review
        view.showNotReviewView();
      }
    } else {
      Timber.d("Response in null");
      //Show error view
      view.showErrorView();
    }
  }

  /**
   * Setup comment list view
   * @param response
   */
  private void setupReviewView(HistoryDetailResponse response) {

    //Set comment in rv
    Timber.v("try comment loop");
    if (response.getReviewerComment().size() > 0) {
      for (int i = 0; i < response.getReviewerComment().size(); i++) {

        //Replace old template default text to new default text
        String comment = response.getReviewerComment().get(i).getRemark();
        if (comment.trim().toLowerCase().equalsIgnoreCase("lampiranaudio") || comment.trim().toLowerCase().equalsIgnoreCase("voicenote")) {
          response.getReviewerComment().get(i).setRemark(view.getStringFromResource(R.string.msg_voice_note_template));
        }
      }

      //sort list
      Collections.sort(response.getReviewerComment(),
          (lhs, rhs) -> lhs.getAudioDuration().compareTo(rhs.getAudioDuration()));

      //attach to rv
      view.setCommentListToRv(response.getReviewerComment(), response.getUserAudioUri());
    } else {
      //remark is null
      Timber.d("remark is null");
      view.showNoComment();
    }

    //TODO enable after server make it. For now just hide it.
    //setup cs review button
//    if (response.getRating() < 1) {
//      view.showBeforeRatedCs();
//    } else {
//      view.showAfterRatedCs();
//    }
    view.showAfterRatedCs();

  }

  /**
   * Initialized player
   * @param rplayer Pass player that has been inserted context from view to control in presenter
   */
  @Override
  public void initializedPlayer(Rplayer rplayer) {
    //Set player duration if there is play session before onStop
    Timber.d("initializedPlayer %s", playerDuration);
    rplayer.seekToWithoutRange(playerDuration);
    this.player = rplayer;
  }

  /**
   * Play player if not playing, Pause player if playing
   */
  @Override
  public void playPauseToggle() {
    if (player != null) {
      if (player.checkPlaying()) {
        player.pause();
      } else {
        player.play();
      }
    }
  }

  /**
   * Seek player to value param
   * @param value value for seek to
   */
  @Override
  public void seekTo(long value) {
    if (player != null) {
      player.seekTo(value);
    }
  }

  /**
   * Pause all playing
   */
  @Override
  public void onPauseAudioOperation() {
    if (player != null) {
      if (player.checkPlaying()) {
        player.pause();
      }
    }
  }

  @Override
  public void onStart() {
    Timber.e("onStart");
    if (Util.SDK_INT > 23) {
      if (view != null && !TextUtils.isEmpty(model.getAudioUri())) {
        view.initializedPlayer(model.getAudioUri());
      }
    }
  }

  @Override
  public void onResume() {
    Timber.e("onResume");
    if (Util.SDK_INT <= 23 || player == null) {
      if (view != null && !TextUtils.isEmpty(model.getAudioUri())) {
        view.initializedPlayer(model.getAudioUri());
      }
    }
  }

  @Override
  public void onPause() {
    Timber.e("onPause");
    if (Util.SDK_INT <= 23) {
      if (view != null) {
        view.pauseAudioView();
      }
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    Timber.e("onStop");
    if (Util.SDK_INT > 23) {
      if (view != null) {
        view.pauseAudioView();
      }
      releasePlayer();
    }
  }

  private void releasePlayer() {
    if (player != null) {
     Timber.d("set duration %s", player.getCurrentDuration());
      playerDuration = player.getCurrentDuration();
      player.stopAndRelease();
      player = null;
    }
  }

  /**
   * This method is calling before destroy fragment.
   * Unsubscribe all network call
   * Stop all audio playing
   * Set view to null
   */
  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
//    onPauseAudioOperation();
    releasePlayer();
    view = null;
  }

}
