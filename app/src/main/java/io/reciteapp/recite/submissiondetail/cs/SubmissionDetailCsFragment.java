package io.reciteapp.recite.submissiondetail.cs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.animation.RecyclerViewAnimation;
import io.reciteapp.recite.customview.dialog.DialogInfoOptional;
import io.reciteapp.recite.customview.dialog.DialogReportAudio;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.SubmissionDetailCsFragmentComponent;
import io.reciteapp.recite.di.module.SubmissionDetailCsModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsContract.Presenter;
import io.reciteapp.recite.submissiondetail.cs.recordcomment.DialogRecordComment;
import io.reciteapp.recite.utils.FileManager;
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
public class SubmissionDetailCsFragment extends Fragment implements
    SubmissionDetailCsContract.View, playerErrorStateCallback, playerStateCallback {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.btn_report)
  Button btnReport;
  @BindView(R.id.progressBarPlayer)
  MagicProgressCircle progressBarPlayer;
  @BindView(R.id.tv_chronometer)
  Chronometer tvChronometer;
  @BindView(R.id.tv_SurahName)
  TextView tvSurahName;
  @BindView(R.id.btn_submit)
  Button btnSubmit;
  @BindView(R.id.btn_freeStyle)
  ImageButton btnFreeStyle;
  @BindView(R.id.btnPlay)
  ImageButton btnPlay;
  @BindView(R.id.loader_buffer)
  MagicProgressCircle loaderBuffer;
  @BindView(R.id.btnBackwards)
  ImageButton btnBackwards;
  @BindView(R.id.btnForwards)
  ImageButton btnForwards;
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;
  Unbinder unbinder;

  MainActivity activity;
  private SubmissionDetailCsFragmentComponent component;
  private boolean isPlayerError = false;
  private DialogRecordComment dialogRecordComment = null;
  private DialogInfoOptional dialogConfirmSubmit = null;
  private DialogReportAudio dialogReport = null;
  private boolean isBuffering = false;
  private Utils utils;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      activity = (MainActivity) context;
      utils = new Utils();
    }
  }

  public SubmissionDetailCsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_submission_detail_cs, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getSubmissionDetailComponent().inject(this);
    init();
    return rootView;
  }

  /**
   * Get recite component from application component to initialized presenter, service and shared
   * prefs
   *
   * @return SubmissionDetailCsFragmentComponent
   */
  private SubmissionDetailCsFragmentComponent getSubmissionDetailComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newSubmissionDetailCsComponent(new SubmissionDetailCsModule());
    }
    return component;
  }

  private void init() {
    //set view to presenter
    presenter.setView(this, service);

    setupView();
  }

  @SuppressLint("SetTextI18n")
  private void setupView() {

    tvChronometer.setText(getString(R.string.dummy_00_00));
    progressBarPlayer.setSmoothPercent(0f);
    loaderBuffer.setVisibility(View.GONE);

    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(linearLayoutManager);

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      if (bundle.getSerializable("data") != null) {
        SubmissionListResponse data = (SubmissionListResponse) bundle.getSerializable("data");
        assert data != null;
        if (data.getAyat() != null && data.getSurahName() != null) {
          tvSurahName.setText(data.getSurahName() + "\n" + data.getAyat());
        }

        //For get detail submission
        presenter.setSurahId(data.getId());
        //Set surah name
        presenter.setSurahName(data.getSurahName());
        //For reporting audio
        presenter.setAudioId(data.getId());

        //Save surahName for audio attachment audio
        String outputFileName  = utils.trimSurahNameToFileName(data.getSurahName() + data.getAyat());
        presenter.setAudioAttachmentFileName(outputFileName);

        //disable all button at first view
        btnBackwardToggle(false);
        btnForwardToggle(false);
        btnFreeStyleToggle(false);
        btnPlayToggle(false);
        btnSubmitToggle(false);

        //Get submission detail
        presenter.getSubmissionDetail();
      } else {
        //exit fragment if bundle data is null
        activity.onBackPressed();
      }
    } else {
      //Exit fragment if bundle is null
      activity.onBackPressed();
    }

    //Setup folder for recording
    boolean folder = new FileManager().createdReciteFolder();
    if (!folder) {
      //Unsuccessful create folder
      utils.getToast(activity, getString(R.string.error_create_folder));
      //disable record button

      //TODO ask for permission
    }
  }

  @Override
  public void showLoadingDialog() {
    activity.showLoadingDialog();
  }

  @Override
  public void removeLoadingDialog() {
    activity.removeLoadingDialog();
  }

  @Override
  public void showPlayerLoadingView() {
    Timber.d("showPlayerLoadingView");
    isBuffering = true;

    int duration_anim = 400;
    loaderBuffer.setVisibility(View.VISIBLE);
    loaderBuffer.setSmoothPercent(0.25f, duration_anim);
    final Handler handler = new Handler();
    handler.postDelayed(() -> {
      if (isBuffering) {
        if (isAdded()) {
          Animation animReset = AnimationUtils
              .loadAnimation(activity, R.anim.anim_rotate);
          animReset.setRepeatCount(Animation.INFINITE);
          animReset.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
              if (!isBuffering) {
                animation.cancel();
                removePlayerLoadingView();
              }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
              if (!isBuffering) {
                animation.cancel();
                removePlayerLoadingView();
              }
            }
          });
          loaderBuffer.startAnimation(animReset);
        }
      }
    }, duration_anim);
  }

  @Override
  public void removePlayerLoadingView() {
    Timber.d("removePlayerLoadingView");
    isBuffering = false;

    if (loaderBuffer.getVisibility() == View.VISIBLE) {
      final Animation animation = AnimationUtils
          .loadAnimation(activity, R.anim.alpha_disappeared);
      animation.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          loaderBuffer.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      loaderBuffer.startAnimation(animation);
    }
  }

  @Override
  public void showSnackbar(int messageResource) {
    activity.showSnackBar(messageResource);
  }

  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
  }

  @Override
  public void showUpdateDialog() {
    activity.showUpdateDialog();
  }

  @Override
  public void initializedPlayer(String audioUri) {
    isPlayerError = false;
    tvChronometer.setBase(SystemClock.elapsedRealtime());
    Rplayer player = new Rplayer();
    player.initPlayer(activity, audioUri, this, this);
    presenter.initializedPlayer(player);
  }

  @Override
  public void insertCommentAtRv(String userAudioUri,
      String audioAttachmentFileName,
      List<UstazReviewCommentResponse> comment) {
    Timber.d("commentList size is %s", comment.size());
    SubmissionDetailCommentAdapter mAdapter = new SubmissionDetailCommentAdapter(this, userAudioUri, audioAttachmentFileName, comment);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mAdapter);
    new RecyclerViewAnimation().rerunLayoutAnimation(recyclerView);
  }

  /**
   * Open fragment recording at certain point
   */
  @Override
  public void openCommentDialog(String userAudioUri,
      String audioFileName,
      UstazReviewCommentResponse ustazReviewCommentResponse) {
    if (dialogRecordComment == null) {
      dialogRecordComment = new DialogRecordComment(userAudioUri, audioFileName, ustazReviewCommentResponse);
      dialogRecordComment.showDialog(activity, ustazReviewComment -> {
        if (ustazReviewComment != null) {
          presenter.setUstazReviewCommentToList(ustazReviewComment);
        }
        dialogRecordComment = null;
      });
    }
  }

  private void openDialogReport() {
    dialogReport = new DialogReportAudio();
    dialogReport.showDialog(activity, text -> {
      if (!TextUtils.isEmpty(text)) {
        presenter.postReport(text);
      }
    });
  }

  @Override
  public void finishFragment() {
    activity.onBackPressed();
  }

  private void playingAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
  }

  private void pauseAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
  }

  @Override
  public void errorAudioView() {
    isPlayerError = true;
    btnPlay.setEnabled(true);
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_24dp));
  }

  @Override
  public void updateAudioDuration(long currentDuration, long totalDuration) {
    tvChronometer.setText(utils.millisecondsToTimerStringFormat(currentDuration));
    int percentage = getProgressPercentage(currentDuration, totalDuration);
    double x = percentage / 100.0;
    progressBarPlayer.setSmoothPercent((float) x, 300);

  }

  /**
   * Change current duration and total duration to percentage;
   */
  public int getProgressPercentage(long currentDuration, long totalDuration) {
    Double percentage;

    long currentSeconds = (int) (currentDuration / 1000);
    long totalSeconds = (int) (totalDuration / 1000);

    // calculating percentage
    percentage = (((double) currentSeconds) / totalSeconds) * 100;

    // return percentage
    return percentage.intValue();
  }

  @OnClick({R.id.btn_report, R.id.btn_submit, R.id.btn_freeStyle,
      R.id.btnPlay, R.id.btnBackwards, R.id.btnForwards})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_report:
        openDialogReport();
        break;
      case R.id.btn_submit:
        if (dialogConfirmSubmit == null) {
          dialogConfirmSubmit = new DialogInfoOptional();
          dialogConfirmSubmit.showDialog(activity,
              getString(R.string.title_confirmation_submittion),
              getString(R.string.msg_confirmation_submittion),
              btnClick -> {
                switch (btnClick) {
                  case R.id.btn_ok:
                    presenter.prePostAudioProcess();
                    dialogConfirmSubmit = null;
                    break;
                  case R.id.btn_cancel:
                    dialogConfirmSubmit.removeDialog();
                    dialogConfirmSubmit = null;
                    break;
                }
              });
        }
        break;
      case R.id.btn_freeStyle:
        presenter.openCommentFragment();
        break;
      case R.id.btnPlay:
        if (!isBuffering) {
          presenter.playButtonFunction(isPlayerError);
        }
        break;
      case R.id.btnBackwards:
        presenter.seekTo(3);
        break;
      case R.id.btnForwards:
        presenter.seekTo(2);
        break;
    }
  }

  @Override
  public void btnBackwardToggle(boolean isEnable) {
    if (isEnable) {
      btnBackwards.setEnabled(true);
      btnBackwards.setImageDrawable(getResources().getDrawable(R.drawable.ic_fast_rewind));
    } else {
      btnBackwards.setEnabled(false);
      btnBackwards.setImageDrawable(getResources().getDrawable(R.drawable.ic_fast_rewind_disable));
    }
  }

  @Override
  public void btnForwardToggle(boolean isEnable) {
    if (isEnable) {
      btnForwards.setEnabled(true);
      btnForwards.setImageDrawable(getResources().getDrawable(R.drawable.ic_fast_forward));
    } else {
      btnForwards.setEnabled(false);
      btnForwards.setImageDrawable(getResources().getDrawable(R.drawable.ic_fast_forward_disable));
    }
  }

  @Override
  public void btnPlayToggle(boolean isEnable) {
    if (isEnable) {
      btnPlay.setEnabled(true);
      btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
    } else {
      btnPlay.setEnabled(false);
      btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_disable));
    }
  }

  @Override
  public void btnSubmitToggle(boolean isEnable) {
    if (isEnable) {
      btnSubmit.setEnabled(true);
      btnSubmit.setBackground(getResources().getDrawable(R.drawable.button_background_blue));
    } else {
      btnSubmit.setEnabled(false);
      btnSubmit.setBackground(getResources().getDrawable(R.color.transparent));
    }
  }

  @Override
  public void btnFreeStyleToggle(boolean isEnable) {
    if (isEnable) {
      btnFreeStyle.setEnabled(true);
      btnFreeStyle.setVisibility(View.VISIBLE);
    } else {
      btnFreeStyle.setEnabled(false);
      btnFreeStyle.setVisibility(View.GONE);
    }
  }

  @Override
  public void state(boolean playWhenReady, int state) {
    switch (state) {
      case Player.STATE_IDLE:
        Timber.d("Player state idle");
        if (isAdded()) {
          removePlayerLoadingView();
          btnForwardToggle(false);
          btnBackwardToggle(false);
          isPlayerError = true;
          btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_24dp));
        }
        break;
      case Player.STATE_READY:
        Timber.d("Player state ready");
        if (isAdded()) {
          removePlayerLoadingView();
          btnForwardToggle(true);
          btnBackwardToggle(true);
          if (playWhenReady) {
            playingAudioView();
          } else {
            pauseAudioView();
          }
        }
        break;
      case Player.STATE_BUFFERING:
        Timber.d("Player state buffering");
        showPlayerLoadingView();
        break;
      case Player.STATE_ENDED:
        Timber.d("Player state ended");
        if (isAdded()){
          removePlayerLoadingView();
          pauseAudioView();
        }
        break;
    }
  }

  @Override
  public void error_state(ExoPlaybackException exception) {
    errorAudioView();
    switch (exception.type) {
      case ExoPlaybackException.TYPE_SOURCE:
        exception.printStackTrace();
        utils.getToast(activity, "Playing error , check log");
        break;
      default:
        utils.getToast(activity, "Playing error , check log");
        exception.printStackTrace();
        break;
    }
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

  @Override
  public void onStop() {
    super.onStop();
    presenter.onStop();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }
}
