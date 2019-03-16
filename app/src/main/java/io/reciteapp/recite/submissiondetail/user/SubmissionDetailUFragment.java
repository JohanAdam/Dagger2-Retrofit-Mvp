package io.reciteapp.recite.submissiondetail.user;


import static android.view.View.GONE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.csprofile.CsProfileFragment;
import io.reciteapp.recite.customview.ProgressBarAnimation;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.customview.dialog.DialogRating;
import io.reciteapp.recite.data.GlideApp;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.model.ReviewerCommentResponse;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.SubmissionDetailUFragmentComponent;
import io.reciteapp.recite.di.module.SubmissionDetailUModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUContract.Presenter;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

/**
 * Submission detail for user
 */
//TODO Make new model for this class, instead use the HistoryDetailResponse
public class SubmissionDetailUFragment extends Fragment implements SubmissionDetailUContract.View,
    playerErrorStateCallback, playerStateCallback {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.tv_surah)
  TextView tvSurah;
  @BindView(R.id.tv_verse)
  TextView tvVerse;
  @BindView(R.id.createdAt_tv)
  TextView createdAtTv;
  @BindView(R.id.progressBar)
  AppCompatImageView progressBar;
  @BindView(R.id.iv_profile)
  CircleImageView ivProfile;
  @BindView(R.id.tv_ustaz_name)
  TextView tvUstazName;
  @BindView(R.id.result_label)
  TextView resultLabel;
  @BindView(R.id.rc)
  RecyclerView rc;
  @BindView(R.id.no_comment_layout)
  TextView tv_no_comment;
  @BindView(R.id.middle_layout)
  RelativeLayout middleLayout;
  @BindView(R.id.group_error)
  Group groupError;
  @BindView(R.id.tv_error_message)
  TextView tvErrorMessage;
  @BindView(R.id.btn_error)
  Button btnError;
  @BindView(R.id.btn_review_cs)
  Button btnReviewCs;
  @BindView(R.id.fab_Play)
  ImageButton btnPlay;
  Unbinder unbinder;

  private MainActivity activity;
  private SubmissionDetailUFragmentComponent component;
  private DialogRating dialogRating= null;
  private boolean isError = false;
  private ProgressBarAnimation progressBarAnimation;

  public SubmissionDetailUFragment() {
    // Required empty public constructor
  }

  /**
   * Attach context to activity
   */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_submission_detail_user, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getSubmissionDetailComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  /**
   * Stop all audio playing in onStop fragment
   */
  @Override
  public void onStop() {
    super.onStop();
    presenter.onStop();
  }

  /**
   * Unsubscribe from all network call
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }

  /**
   * Get Submission Detail component to initialized presenter and model
   * @return presenter and model component
   */
  private SubmissionDetailUFragmentComponent getSubmissionDetailComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newSubmissionDetailUComponent(new SubmissionDetailUModule());
    }
    return component;
  }

  /**
   * Setup first view for this fragment
   */
  private void init() {

    presenter.setView(this, service);

    setupView();

    presenter.getSubmissionDetail();
  }

  private void setupView() {

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      if (bundle.getSerializable("data") != null) {
        SubmissionListResponse data = (SubmissionListResponse) bundle.getSerializable("data");
        assert data != null;
        if (data.getAyat() != null && data.getSurahName() != null) {
          tvVerse.setText(data.getAyat());
          tvSurah.setText(data.getSurahName());
        }

        presenter.setId(data.getId());

        createdAtTv.setText(data.getSubmitted());
      } else {
        showErrorView();
        Timber.e("Bundle data is null");
      }

      resultLabel.setVisibility(GONE);
    }

    progressBarAnimation = new ProgressBarAnimation();
    removeWait();

    tvUstazName.setText(getString(R.string.title_credible_source));
    tvUstazName.setClickable(false);
    ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp));
    ivProfile.setClickable(false);
    resultLabel.setVisibility(GONE);
    btnReviewCs.setVisibility(GONE);

    hidePlayerView();

    rc.setVisibility(GONE);
    rc.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
    rc.setLayoutManager(mLayoutManager);
  }

  /**
   * Return string by int resources
   * @param messageResource String resources
   * @return String from the string resources
   */
  @Override
  public String getStringFromResource(int messageResource) {
    return getString(messageResource);
  }

  /**
   * Show loading ui in fragment when get submission detail
   */
  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(progressBar);
    progressBar.setVisibility(View.VISIBLE);
    tv_no_comment.setVisibility(View.VISIBLE);
    tv_no_comment.setText(R.string.msg_loading);
  }

  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    progressBar.setVisibility(GONE);
    tv_no_comment.setVisibility(GONE);
  }

  /**
   * Show loading dialog when sending rating
   */
  @Override
  public void showLoadingDialog() {
    activity.showLoadingDialog();
  }

  @Override
  public void removeLoadingDialog() {
    activity.removeLoadingDialog();
  }

  /**
   * Show rating bar
   * @param messageResource Strin resources
   */
  @Override
  public void showSnackBar(int messageResource) {
    activity.showSnackBar(messageResource);
  }

  /**
   * Show error dialog
   * @param responseCode response code for showing the message appropriately
   * @param isKick true when want to kick after error dialog
   */
  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
  }

  /**
   * Show rating dialog
   */
  @Override
  public void showRatingDialog() {
    if (dialogRating == null) {
      dialogRating = new DialogRating();
      dialogRating.showDialog(activity, rating -> {
        Timber.d("rating is %s", rating);
        presenter.postAudioSubmissionRating(rating);
        dialogRating = null;
      });
    }
  }

  /**
   * Show error ui when error to get Submission detail
   */
  @Override
  public void showErrorView() {
    groupError.setVisibility(View.VISIBLE);
    hidePlayerView();
  }

  @Override
  public void removeErrorView() {
    groupError.setVisibility(GONE);
  }

  /**
   * Set Cs Profile after get Submission Detail response
   * @param response response get from server
   */
  @Override
  public void setCsProfile(HistoryDetailResponse response) {
    if (!TextUtils.isEmpty(response.getReviewerName())) {
      tvUstazName.setText(response.getReviewerName());
      tvUstazName.setClickable(true);
    } else {
      tvUstazName.setText(getString(R.string.title_credible_source));
      tvUstazName.setClickable(false);
    }

    if (!TextUtils.isEmpty(response.getReviewerPhoto()))  {
      GlideApp.with(this)
          .load(response.getReviewerPhoto())
          .placeholder(new ColorDrawable(Color.GRAY))
          .error(R.drawable.ic_account_circle_black_24dp)
          .into(ivProfile);
      if (response.isReviewed()) {
        ivProfile.setClickable(true);
      } else {
        ivProfile.setClickable(false);
        ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
      }
    }
  }

  /**
   * Show sponsor dialog
   * @param message message for agency
   * @param imageUrl imageUrl for showing image
   * @param redirectUrl Url when click on image
   * @param disableCancel true for disable cancel, otherwise false
   */
  @Override
  public void showSponsorCsDialog(String message,
      String imageUrl,
      String redirectUrl,
      boolean disableCancel) {
    activity.showSponsorDialog(message, imageUrl, redirectUrl, disableCancel);
  }

  /**
   * Setup comment list response to recyclerview
   * @param commentList comment list
   * @param userAudioUri user audio uri for playing
   */
  @Override
  public void setCommentListToRv(List<ReviewerCommentResponse> commentList,
      String userAudioUri) {
    rc.setVisibility(View.VISIBLE);
    tv_no_comment.setVisibility(GONE);
    SubmissionDetailUCommentListAdapter mAdapter = new SubmissionDetailUCommentListAdapter(activity, commentList, userAudioUri);
    rc.setItemAnimator(new DefaultItemAnimator());
    rc.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(rc);
  }

  /**
   * Show layout for there is no comment available
   */
  @Override
  public void showNoComment() {
    tv_no_comment.setVisibility(View.VISIBLE);
    tv_no_comment.setText(getResources().getString(R.string.msg_no_comment));
  }

  @Override
  public void removeNoComment() {
    tv_no_comment.setVisibility(GONE);
  }

  /**
   * Show layout button for user that not rating yet
   */
  @Override
  public void showBeforeRatedCs() {
    btnReviewCs.setVisibility(View.VISIBLE);
    btnReviewCs.setEnabled(true);
  }

  /**
   * Layout got hide button if user already rating
   */
  @Override
  public void showAfterRatedCs() {
    btnReviewCs.setVisibility(View.GONE);
    btnReviewCs.setEnabled(false);
    btnReviewCs.setText(getString(R.string.action_rating_has_been_sent));
    btnReviewCs.setTextColor(ContextCompat.getColor(activity, R.color.pale_olive_green));
    btnReviewCs.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
  }

  /**
   * Show view when this submission is not reviewed yet
   */
  @Override
  public void showNotReviewView() {
    btnReviewCs.setVisibility(GONE);
    btnReviewCs.setEnabled(false);

    rc.setVisibility(GONE);

    showNoComment();
  }

  /**
   * Hide player
   */
  @Override
  public void hidePlayerView() {
    btnPlay.setVisibility(GONE);
  }

  /**
   * Initialized player from fragment to use context and pass to presenter
   * @param audioUri Audio Uri use to initialized player
   */
  @Override
  public void initializedPlayer(String audioUri) {
    Rplayer player = new Rplayer();
    player.initPlayer(activity, audioUri, this, this);
    presenter.initializedPlayer(player);
    btnPlay.setVisibility(View.VISIBLE);
  }

  /**
   * Playing view, when playing audio
   */
  @Override
  public void playingAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
  }

  /**
   * Pause view when pause audio
   */
  @Override
  public void pauseAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
  }

  /**
   * Open cs fragment
   */
  @Override
  public void openCsProfileFragment(String id) {
    //TODO cs profile
    Fragment fragment = new CsProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString("id",id);
    fragment.setArguments(bundle);
    activity.switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @OnClick({R.id.iv_profile, R.id.tv_ustaz_name, R.id.btn_error, R.id.btn_review_cs, R.id.fab_Play})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_profile:
        if (tvUstazName.getText() != getString(R.string.title_credible_source)) {
          //Enable click if not default text
          presenter.openCsProfileFragment();
        }
        break;
      case R.id.tv_ustaz_name:
        if (tvUstazName.getText() != getString(R.string.title_credible_source)) {
          //Enable click if not default text
          presenter.openCsProfileFragment();
        }
        break;
      case R.id.btn_error:
        presenter.getSubmissionDetail();
        break;
      case R.id.btn_review_cs:
        showRatingDialog();
        break;
      case R.id.fab_Play:
        //TODO check on slow data what happen when loading and user press play and pause
        if (isError) {
          isError = false;
          presenter.getSubmissionDetail();
        } else {
          presenter.playPauseToggle();
        }
        break;
    }
  }

  /**
   * Audio player state
   * @param playWhenReady true if player is playing , false if player is paused
   * @param state Player state
   */
  @Override
  public void state(boolean playWhenReady, int state) {
    switch (state) {
      case Player.STATE_IDLE:
        if (isAdded()) {
          isError = true;
          btnPlay.clearAnimation();
          btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_24dp));
        }
        break;
      case Player.STATE_READY:
        if (isAdded()) {
          btnPlay.clearAnimation();
          if (playWhenReady) {
            playingAudioView();
          } else {
            pauseAudioView();
          }
        }
        break;
      case Player.STATE_BUFFERING:
        if (isAdded()) {
          btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh));
          Animation rotate_anim = AnimationUtils
              .loadAnimation(activity, R.anim.anim_rotate);
          rotate_anim.setRepeatCount(Animation.INFINITE);
          btnPlay.startAnimation(rotate_anim);
        }
        break;
      case Player.STATE_ENDED:
        if (isAdded()) {
          pauseAudioView();
          presenter.seekTo(0);
        }
        break;
    }  }

  /**
   * Error listener for player
   * @param exception when player get error
   */
  @Override
  public void error_state(ExoPlaybackException exception) {
    switch (exception.type) {
      case ExoPlaybackException.TYPE_SOURCE:
        exception.printStackTrace();
        new Utils().getToast(activity, "Playing error, check log");
        break;
      default:
        exception.printStackTrace();
        new Utils().getToast(activity, "Playing error, check log");
        break;
    }
  }

}
