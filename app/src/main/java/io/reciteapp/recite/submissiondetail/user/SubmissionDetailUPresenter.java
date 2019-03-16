package io.reciteapp.recite.submissiondetail.user;

import android.text.TextUtils;
import com.google.android.exoplayer2.util.Util;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.user.SubmissionDetailCallUser.GetSubmissionDetailUserCallback;
import io.reciteapp.recite.data.networking.call.submission.submissiondetail.user.SubmissionDetailCallUser.PostAudioSubmissionRatingCallback;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract.Model;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract.Presenter;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract.View;
import io.reciteapp.recite.utils.Rplayer;
import java.util.Collections;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SubmissionDetailUPresenter implements Presenter {

  private Model model;
  private NetworkCallWrapper service;
  private View view;
  private CompositeSubscription subscriptions;
  private Rplayer player = null;
  private long playerDuration = 0;

  /**
   * Set model to presenter
   * @param model
   */
  //Set model to presenter
  public SubmissionDetailUPresenter(Model model) {
    this.model = model;
  }

  /**
   * Set view, network service to presenter
   * Initialized new subscriptions
   */
  //Set view and service to presenter
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
   * Check if view is still attached or not
   * @return true if attached, false for not attached
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Get submission detail from server
   */
  @Override
  public void getSubmissionDetail() {
    view.showWait();
    view.removeErrorView();

    String token = model.getToken();
    String audioId = model.getId();
    Subscription subscription = service.getSubmissionDetailUser(token, audioId,
        new GetSubmissionDetailUserCallback() {
          @Override
          public void onSuccess(HistoryDetailResponse submissionDetailResponse) {
            if (isViewAttached()) {
              view.removeWait();
              processSubmissionDetail(submissionDetailResponse);
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removeWait();
              view.showErrorView();
            }
          }
        });

    subscriptions.add(subscription);

  }

  /**
   * Setup submission detail response from server
   */
  private void processSubmissionDetail(HistoryDetailResponse response) {
    if (response != null) {

      if (response.getUserAudioUri() != null) {
        model.setUserAudioUri(response.getUserAudioUri());
        view.initializedPlayer(response.getUserAudioUri());
      } else {
        view.hidePlayerView();
      }

      view.setCsProfile(response);

      if (response.isReviewed()) {
        //submission already reviewed
        setupReviewView(response);
      } else {
        //submission not reviewed yet
        view.showNotReviewView();
      }
    } else {
      //response is null
      view.showErrorView();
    }
  }

  /**
   * Setup submission detail comment
   */
  private void setupReviewView(HistoryDetailResponse response) {

    if (response.getReviewerComment().size() > 0) {
      for (int i = 0; i < response.getReviewerComment().size(); i++) {

        //Replace old template default text to new default text
        String comment = response.getReviewerComment().get(i).getRemark().replace(" ", "");
        //replace old default template text
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
      view.showNoComment();
    }

    //Cs already been rated?
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
   * Post audio submission rating
   * @param rating rating user input
   */
  @Override
  public void postAudioSubmissionRating(int rating) {
    view.showLoadingDialog();

    String token = model.getToken();
    String audioId = model.getId();
    Subscription subscription = service.postSubmissionRatingAudio(token, audioId, String.valueOf(rating),
        new PostAudioSubmissionRatingCallback() {
          @Override
          public void onSuccess() {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showAfterRatedCs();
              view.showSnackBar(R.string.action_rating_has_been_sent);
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showSnackBar(R.string.error_default);
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
   * Initialized player
   */
  @Override
  public void initializedPlayer(Rplayer rplayer) {
    rplayer.seekToWithoutRange(playerDuration);
    this.player = rplayer;
  }

  /**
   * Play and pause player
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
   * Seek player to current duration
   * @param value duration to seek
   */
  @Override
  public void seekTo(long value) {
    if (player != null) {
      player.seekTo(value);
    }
  }

  @Override
  public void onStart() {
    if (Util.SDK_INT > 23) {
      if (view != null && !TextUtils.isEmpty((model.getUserAudioUri()))) {
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
      if (view != null) {
        view.pauseAudioView();
      }
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    if (Util.SDK_INT > 23) {
      if (view != null) {
        view.pauseAudioView();
      }
      releasePlayer();
    }
  }

  /**
   * Unsubscribe from all network call
   * Stop all audio playing
   * Set view to null
   */
  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    releasePlayer();
    view = null;
  }

  //Stop and release player, and set the duration to resume if page not destroy
  private void releasePlayer() {
    if (player != null) {
      Timber.d("set duration %s", player.getCurrentDuration());
      playerDuration = player.getCurrentDuration();
      player.stopAndRelease();
      player = null;
    }
  }
}
