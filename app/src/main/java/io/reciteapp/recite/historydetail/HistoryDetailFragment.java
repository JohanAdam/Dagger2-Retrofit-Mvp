package io.reciteapp.recite.historydetail;


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
import io.reciteapp.recite.customview.dialog.DialogRatingCs;
import io.reciteapp.recite.data.GlideApp;
import io.reciteapp.recite.data.model.HistoryDetailResponse;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.data.model.ReviewerCommentResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.HistoryDetailFragmentComponent;
import io.reciteapp.recite.di.module.HistoryDetailModule;
import io.reciteapp.recite.historydetail.HistoryDetailContract.Presenter;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryDetailFragment extends Fragment implements HistoryDetailContract.View,
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
  @BindView(R.id.rc)
  RecyclerView rc;
  @BindView(R.id.no_comment_layout)
  TextView tv_no_comment;
  @BindView(R.id.middle_layout)
  RelativeLayout middleLayout;
  @BindView(R.id.btn_error)
  Button btnError;
  @BindView(R.id.btn_review_cs)
  Button btnReviewCs;
  @BindView(R.id.fab_Play)
  ImageButton btnPlay;
  @BindView(R.id.group_error)
  Group group_error;
  Unbinder unbinder;

  private MainActivity activity;
  private HistoryDetailFragmentComponent component;
  private DialogRatingCs dialogRating = null;
  private boolean isError = false;
  private Utils utils;
  private ProgressBarAnimation progressBarAnimation;

  public HistoryDetailFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
      this.utils = new Utils();
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_history_detail, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    //Inject service and presenter
    getHistoryDetailComponent().inject(this);
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
   * Stop all playing process
   */
  @Override
  public void onStop() {
    super.onStop();
//    presenter.onPauseAudioOperation();
    presenter.onStop();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }

  /**
   * Get HistoryDetail component from application component to initialized presenter, service and shared
   * prefs
   *
   * @return HistoryDetailComponent
   */
  private HistoryDetailFragmentComponent getHistoryDetailComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newHistoryDetailFragmentComponent(new HistoryDetailModule());
    }
    return component;
  }

  /**
   * Initialized and setup view
   */
  private void init() {
    //set view to presenter
    presenter.setView(this, service);

    setupView();

    //get recite time from server
    presenter.getHistoryDetail();
  }

  /**
   * Setup first view
   */
  private void setupView() {

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      if (bundle.getSerializable("data") != null) {
        HistoryResponse data = (HistoryResponse) bundle.getSerializable("data");
        assert data != null;
        if (data.getAyat() != null && data.getSurahName() != null) {
          tvVerse.setText(data.getAyat());
          tvSurah.setText(data.getSurahName());
        }

        Timber.d("Id is %s", data.getId());
        presenter.setId(data.getId());

        createdAtTv.setText(data.getSubmitted());
      } else {
        showErrorView();
        Timber.e("Bundle data is null");
      }
    }

    progressBarAnimation = new ProgressBarAnimation();
    removeWait();

    tvUstazName.setText(getString(R.string.title_credible_source));
    tvUstazName.setClickable(false);
    ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_48dp));
    ivProfile.setClickable(false);
    btnReviewCs.setVisibility(GONE);

    hidePlayerView();

    rc.setVisibility(GONE);
    rc.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
    rc.setLayoutManager(mLayoutManager);
  }

  @Override
  public String getStringFromResource(int resource) {
    return getString(resource);
  }

  /**
   * Show loading
   */
  @Override
  public void showWait() {
    progressBarAnimation.startAnimation(progressBar);
    progressBar.setVisibility(View.VISIBLE);
    tv_no_comment.setVisibility(View.VISIBLE);
    tv_no_comment.setText(getString(R.string.msg_loading));
  }

  /**
   * Remove loading
   */
  @Override
  public void removeWait() {
    progressBarAnimation.stopAnimation();
    progressBar.setVisibility(GONE);
    tv_no_comment.setVisibility(GONE);
  }

  @Override
  public void showLoadingDialog() {
    activity.showLoadingDialog();
  }

  @Override
  public void removeLoadingDialog() {
    activity.removeLoadingDialog();
  }

  /**
   * Show snackbar
   */
  @Override
  public void showSnackbar(int messageResource) {
    activity.showSnackBar(messageResource);
  }

  /**
   * Show error dialog
   * @param responseCode response code return from call
   * @param isKick true if want to kick after click, false for plain error
   */
  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
  }

  /**
   * Show rating cs dialog
   */
  @Override
  public void showRatingDialog() {
    if (dialogRating == null) {
      dialogRating = new DialogRatingCs();
      dialogRating.showDialog(activity, (rating, feedback) -> {
        Timber.d("sending rating %s", rating);
        Timber.d("Sending comment %s", feedback);
        presenter.patchRatingCs(rating, feedback);
        dialogRating = null;
      });
    }
  }

  /**
   * Show error view
   */
  @Override
  public void showErrorView() {
    group_error.setVisibility(View.VISIBLE);
    hidePlayerView();
  }

  /**
   * Hide error view
   */
  @Override
  public void removeErrorView() {
    group_error.setVisibility(GONE);
  }

  /**
   * Set cs name and picture
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

    if (!TextUtils.isEmpty(response.getReviewerPhoto())) {
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
   * Set comment list to recyclerView if available
   * @param commentList comment list data
   * @param userAudioUri  user audio uri for comment dialog
   */
  @Override
  public void setCommentListToRv(List<ReviewerCommentResponse> commentList, String userAudioUri) {
    //show recyclerview
    rc.setVisibility(View.VISIBLE);
    tv_no_comment.setVisibility(GONE);
    //attach response to recyclerView adapter
    HistoryDetailCommentListAdapter mAdapter = new HistoryDetailCommentListAdapter(activity, commentList, userAudioUri);
    rc.setItemAnimator(new DefaultItemAnimator());
    rc.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(rc);
  }

  /**
   * Show no comment text if there is no comment from cs or not reviewed yet
   */
  @Override
  public void showNoComment() {
    tv_no_comment.setVisibility(View.VISIBLE);
    tv_no_comment.setText(getResources().getString(R.string.msg_no_comment));
  }

  /**
   * Remove no comment text
   */
  @Override
  public void removeNoComment() {
    tv_no_comment.setVisibility(GONE);
  }

  /**
   * Show button for rated cs if user not rating cs yet
   */
  @Override
  public void showBeforeRatedCs() {
    btnReviewCs.setVisibility(View.VISIBLE);
    btnReviewCs.setEnabled(true);
  }

  /**
   * Disable rated button cs if user already rated
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
   * Show view if user not being reviewed yet
   */
  @Override
  public void showNotReviewView() {
    btnReviewCs.setVisibility(GONE);
    btnReviewCs.setEnabled(false);

    rc.setVisibility(GONE);

    showNoComment();
  }

  /**
   * hide user player
   */
  @Override
  public void hidePlayerView() {
    btnPlay.setVisibility(GONE);
  }

  /**
   * Initialized player and show
   * @param audioUri user audio uri
   */
  @Override
  public void initializedPlayer(String audioUri) {
    Rplayer player = new Rplayer();
    player.initPlayer(activity, audioUri, this, this);
    presenter.initializedPlayer(player);
    btnPlay.setVisibility(View.VISIBLE);
  }

  @Override
  public void playingAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
  }

  @Override
  public void pauseAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
  }

  @Override
  public void openCsProfileFragment(String id) {
    //TODO cs profile
    Fragment fragment = new CsProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString("id", id);
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
        presenter.getHistoryDetail();
        break;
      case R.id.btn_review_cs:
        showRatingDialog();
        break;
      case R.id.fab_Play:
        //TODO check on slow data what happen when loading and user press play and pause
        if (isError) {
          isError = false;
          presenter.getHistoryDetail();
        } else {
          presenter.playPauseToggle();
        }
        break;
    }
  }

  /**
   * Player state listener
   * @param playWhenReady true when is playing, false is not playing
   * @param state STATE_IDLE, STATE_READY, STATE_BUFFERING, STATE_ENDED
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
    }
  }

  /**
   * Player error listener
   * @param exception TYPE_SOURCE - Error no audio file found
   */
  @Override
  public void error_state(ExoPlaybackException exception) {
    switch (exception.type) {
      case ExoPlaybackException.TYPE_SOURCE:
        exception.printStackTrace();
        utils.getToast(activity, "Playing error, check log");
        break;
      default:
        exception.printStackTrace();
        utils.getToast(activity, "Playing error, check log");
        break;
    }
  }
}
