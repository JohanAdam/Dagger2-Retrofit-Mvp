package io.reciteapp.recite.recite;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.dialog.DialogInfoOptional;
import io.reciteapp.recite.data.model.HistoryResponse;
import io.reciteapp.recite.data.model.SubmissionRecorderHistories;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.ReciteFragmentComponent;
import io.reciteapp.recite.di.module.ReciteModule;
import io.reciteapp.recite.historydetail.HistoryDetailFragment;
import io.reciteapp.recite.historylist.HistoryListFragment;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.recite.ReciteContract.Presenter;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.utils.FileManager;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class ReciteFragment extends Fragment implements ReciteContract.View,
    playerErrorStateCallback, playerStateCallback {

  public static final Utils utils = new Utils();
  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;

  @BindView(R.id.main_layout)
  ConstraintLayout main_layout;
  @BindView(R.id.btn_record)
  ImageButton btnRecord;
  @BindView(R.id.pb_circle)
  MagicProgressCircle pbCircle;
  @BindView(R.id.tv_chronometer)
  Chronometer tvChronometer;
  @BindView(R.id.tv_surah_name)
  TextView tvSurahName;
  @BindView(R.id.tv_verse)
  TextView tvVerse;
  @BindView(R.id.btn_play)
  ImageButton btnPlay;
  @BindView(R.id.btn_reset)
  ImageButton btnReset;
  @BindView(R.id.tv_history)
  TextView tvHistory;
  @BindView(R.id.btn_submit)
  Button btnSubmit;
  @BindView(R.id.tv_empty_history)
  TextView tvEmptyHistory;
  @BindView(R.id.item_one)
  View item_one;
  @BindView(R.id.item_two)
  View item_two;
  Unbinder unbinder;

  //Parent activity
  private MainActivity activity;
  //This fragment component
  private ReciteFragmentComponent component;
  //Dialog confirmation submitting
  private DialogInfoOptional dialogConfirmSubmitting;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      activity = (MainActivity) context;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_recite, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    //Inject service and presenter
    getReciteComponent().inject(this);
    //initialized and setup view
    init();
    return rootView;
  }

  /**
   * Get recite component from application component to initialized presenter, service and shared
   * prefs
   *
   * @return ReciteComponent
   */
  private ReciteFragmentComponent getReciteComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newReciteFragmentComponent(new ReciteModule());
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
  }

  /**
   * Setup view when first load this fragment
   */
  private void setupView() {
    //TODO Enter animation

    //setup button
    recordButtonToggle(true);
    resetButtonToggle(false);
    playButtonToggle(false);
    submitButtonToggle(false);

    tvChronometer.setText(getString(R.string.dummy_00_00));

    tvEmptyHistory.setVisibility(View.VISIBLE);
    item_one.setVisibility(View.INVISIBLE);
    item_two.setVisibility(View.INVISIBLE);

    pbCircle.setSmoothPercent(0.0f);

    //setup TextView
    Bundle bundle = this.getArguments();
    if (bundle != null) {

      String surahName = bundle.getString(Constants.AYAT_SURAHNAME);
      String subAyat = bundle.getString(Constants.AYAT_SUBAYAT);
      String ayatId = bundle.getString(Constants.AYAT_ID);

      if (!TextUtils.isEmpty(surahName) && !TextUtils.isEmpty(subAyat) &&!TextUtils.isEmpty(ayatId)) {
        //Set surah name
        tvSurahName.setText(surahName);

        //Set surah ayat
        tvVerse.setText(subAyat);

        //Save id in model
        presenter.setSurahId(ayatId);

        //Save sub ayat in model
        presenter.setAyat(subAyat);
      } else {
        activity.onBackPressed();
      }

      //Save surahName for output recording location
      String outputFileName  = utils.trimSurahNameToFileName(tvSurahName.getText().toString());
      presenter.setAudioFileName(outputFileName);
      presenter.setSurahName(tvSurahName.getText().toString());

      presenter.initializedRecorder();

      //Get recite time from server
      presenter.processGetReciteTime(ayatId);
    } else {
      activity.onBackPressed();
    }

    //Setup folder for recording
    //TODO Ask for permission!
    boolean folder = new FileManager().createdReciteFolder();
    if (!folder) {
      //Unsuccessful create folder
      utils.getToast(activity, getString(R.string.error_create_folder));
      //disable record button
      recordButtonToggle(false);
    }
  }

  /**
   * Enable and Disable Play button
   */
  private void playButtonToggle(boolean isEnable) {
    if (isEnable) {
      btnPlay.setEnabled(true);
      btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
    } else {
      btnPlay.setEnabled(false);
      btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_disable));
    }
  }

  /**
   * Enable and Disable Reset button
   */
  private void resetButtonToggle(boolean isEnable) {
    if (isEnable) {
      btnReset.setEnabled(true);
      btnReset.setImageDrawable(getResources().getDrawable(R.drawable.ic_reset));
    } else {
      btnReset.setEnabled(false);
      btnReset.setImageDrawable(getResources().getDrawable(R.drawable.ic_reset_disable));
    }
  }

  /**
   * Enable and Disable Record button
   */
  private void recordButtonToggle(boolean isEnable) {
    if (isEnable) {
      btnRecord.setEnabled(true);
      btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone));
    } else {
      btnRecord.setEnabled(false);
      btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_disable));
    }
  }

  /**
   * Enable and Disable Submit button
   */
  private void submitButtonToggle(boolean isEnable) {
    if (isEnable) {
      btnSubmit.setEnabled(true);
      btnSubmit.setBackground(getResources().getDrawable(R.drawable.button_background_pink));
    } else {
      btnSubmit.setEnabled(false);
      btnSubmit.setBackground(getResources().getDrawable(R.color.transparent));
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
  public void showSnackbar(int messageResource) {
    activity.showSnackBar(messageResource);
  }

  /**
   * Dialog Error
   * @param responseCode error response codedashboard/
   * @param isKick true if want backpressed function on button click
   */
  @Override
  public void showErrorDialog(int responseCode, boolean isKick) {
    activity.showErrorDialog(responseCode, isKick);
  }

  /**
   * Dialog update version
   */
  @Override
  public void showUpdateDialog() {
    activity.showUpdateDialog();
  }

  /**
   * Method for show get view ready for use. (After on success getReciteTime call)
   * @param historyList contains all data from getReciteTime call.
   */
  @Override
  public void setHistoryItem(List<SubmissionRecorderHistories> historyList) {
    if (isAdded()) {
      TextView tv_date_one = item_one.findViewById(R.id.tv_timestamp);
      TextView tv_isReview_one = item_one.findViewById(R.id.tv_is_review);
      TextView tv_date_two = item_two.findViewById(R.id.tv_timestamp);
      TextView tv_isReview_two = item_two.findViewById(R.id.tv_is_review);

      if (historyList.size() <= 0) {
        //if history list <= 0 hide history items
        //show history empty text
        tvEmptyHistory.setVisibility(View.VISIBLE);
        item_one.setVisibility(View.INVISIBLE);
        item_two .setVisibility(View.INVISIBLE);
      } else {
        //if history list > 0 show items history
        //disable history empty text
        tvEmptyHistory.setVisibility(View.GONE);

        //Setting item one
        try {
          //set item one date and color
          tv_date_one.setText(utils.secondsToTimeStamp(historyList.get(0).getSubmissionTime()));

          //set item one is submitted
          if (historyList.get(0).isReviewed()) {
            tv_isReview_one.setText(getResources().getString(R.string.msg_reviewed));
            tv_isReview_one.setTextColor(ContextCompat.getColor(activity, R.color.pale_olive_green));
          } else {
            tv_isReview_one.setText(getResources().getString(R.string.msg_unreviewed));
            tv_isReview_one.setTextColor(ContextCompat.getColor(activity, R.color.wheat));
          }

          //show item one
          item_one.setVisibility(View.VISIBLE);
        } catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
          //this is because there is no item one
          item_one.setVisibility(View.INVISIBLE);
        }

        //Setting item two
        try {
          //set item two date and color
          tv_date_two.setText(utils.secondsToTimeStamp(historyList.get(1).getSubmissionTime()));

          //set item two is submitted
          if (historyList.get(1).isReviewed()) {
            tv_isReview_two.setText(getResources().getString(R.string.msg_reviewed));
            tv_isReview_two.setTextColor(ContextCompat.getColor(activity, R.color.pale_olive_green));
          } else {
            tv_isReview_two.setText(getResources().getString(R.string.msg_unreviewed));
            tv_isReview_two.setTextColor(ContextCompat.getColor(activity, R.color.wheat));
          }

          //show item two
          item_two.setVisibility(View.VISIBLE);
        } catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
          //this is because there is no item two
          item_two.setVisibility(View.INVISIBLE);
        }
      }
    }
  }

  /**
   * Disable record due to no ReciteTime / ReciteTime < 5 seconds
   */
  @Override
  public void onInsufficientReciteTime() {
    playButtonToggle(false);
    resetButtonToggle(false);
    submitButtonToggle(false);
    btnRecord.setEnabled(true);
    btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_disable));

    showShareDialog();
  }

  /**
   * View when recording start
   */
  @Override
  public void startRecordView() {
    btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
    tvChronometer.setBase(SystemClock.elapsedRealtime());
    tvChronometer.start();
    tvChronometer.setOnChronometerTickListener(chronometer -> {
      long chronoInMillisec = SystemClock.elapsedRealtime() - chronometer.getBase();
      long seconds = TimeUnit.MILLISECONDS.toSeconds(chronoInMillisec);
      Timber.d("seconds %s", seconds);
      float value = presenter.calculateProgressRecording(seconds);
      pbCircle.setSmoothPercent(value, 1000);
    });
  }

  /**
   * Set view after recording
   */
  @Override
  public void finishRecordView() {
    recordButtonToggle(false);
    resetButtonToggle(true);
    tvChronometer.stop();
    Timber.d("check FileRecording Exist is %s", presenter.checkIfRecordingFileExist());
    if (presenter.checkIfRecordingFileExist()) {
      //exist
      playButtonToggle(true);
      submitButtonToggle(true);
    } else {
      //not exist
      playButtonToggle(false);
      submitButtonToggle(false);
    }
    //initialized after finish recording
    initializedPlayer();
  }

  /**
   * Play button Play view
   */
  @Override
  public void playingAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
  }

  /**
   * Play button Pause view
   */
  @Override
  public void pauseAudioView() {
    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
  }

  /**
   * Reset function view
   */
  @Override
  public void resetButtonFunctionView() {
    recordButtonToggle(true);
    resetButtonToggle(false);
    playButtonToggle(false);
    submitButtonToggle(false);
    tvChronometer.setText(getString(R.string.dummy_00_00));
    pbCircle.setSmoothPercent(0.0f);
  }

  @Override
  public void openHistoryDetail(SubmissionRecorderHistories submissionHistories,
      String surahName, String ayat) {

    //Because HistoryDetailFragment need HistoryResponse model onStart.
    HistoryResponse data = new HistoryResponse();
    data.setSurahName(surahName);
    data.setAyat(ayat);
    data.setId(submissionHistories.getSubmissionId());
    data.setSubmitted(String.valueOf(submissionHistories.getSubmissionTime()));

    Fragment mFragment = new HistoryDetailFragment();
    Bundle mBundle = new Bundle();
    mBundle.putSerializable("data", data);
    mFragment.setArguments(mBundle);
    activity.switchFragmentAddBackstack(mFragment, Constants. TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void showShareDialog() {
    if (utils.randomizerDialogPopup()) {
      activity.showShareDialog(getResources().getString(R.string.title_get_recitetime), getResources().getString(R.string.msg_get_recite));
    }
  }

  @Override
  public void openHistoryListFragment(String surahId) {
    Fragment fragment = new HistoryListFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString("surahName", tvSurahName.getText().toString());
    mBundle.putString("surahId", surahId);
    fragment.setArguments(mBundle);
    activity.switchFragmentAddBackstack(fragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public void initializedPlayer() {
    Rplayer player = new Rplayer();
    String file = presenter.getAudioFileURI();
    player.initPlayer(activity, file, this, this);
    presenter.initializedPlayer(player);
  }


  @OnClick({
      R.id.btn_record,
      R.id.btn_play,
      R.id.btn_reset,
      R.id.tv_history,
      R.id.btn_submit,
      R.id.item_one,
      R.id.item_two})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_record:
        presenter.recorderToggle();
        break;
      case R.id.btn_play:
        presenter.playPauseToggle();
        break;
      case R.id.btn_reset:
        presenter.reset();
        break;
      case R.id.tv_history:
        //TODO disable this when recording and playing
        presenter.openHistoryListFragment();
        break;
      case R.id.btn_submit:
        long audioDuration = utils.convertTimerStringFormatToSeconds(tvChronometer.getText().toString());
        Timber.d("Audio Duration recorder in seconds is %s", audioDuration);
        long milli = TimeUnit.SECONDS.toMillis(audioDuration);
        confirmSubmittingDialog(milli);
        break;
      case R.id.item_one:
        if (item_one.getVisibility() == View.VISIBLE) {
          presenter.openHistoryDetailItemOne();
        }
        break;
      case R.id.item_two:
        if (item_one.getVisibility() == View.VISIBLE) {
          presenter.openHistoryDetailItemTwo();
        }
        break;
    }
  }

  /**
   * Dialog ask for confimation before submit
   */
  private void confirmSubmittingDialog(long audioDuration) {
    if  (dialogConfirmSubmitting == null) {
      dialogConfirmSubmitting = new DialogInfoOptional();
      dialogConfirmSubmitting.showDialog(activity,
          getString(R.string.title_confirmation_submittion),
          getString(R.string.msg_confirmation_submittion),
          btnClick -> {
            switch (btnClick) {
              case R.id.btn_ok:
                presenter.postAudioProcess(audioDuration);
                dialogConfirmSubmitting = null;
                break;
              case R.id.btn_cancel:
                dialogConfirmSubmitting = null;
                break;
              default:
                dialogConfirmSubmitting.removeDialog();
                dialogConfirmSubmitting = null;
                break;
            }
          });
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
        Timber.d("Player state idle");
        if (isAdded()) {
          btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_24dp));
        }
        break;
      case Player.STATE_READY:
        Timber.d("Player state ready");
        if (isAdded()) {
          if (playWhenReady) {
            playingAudioView();
          } else {
            pauseAudioView();
          }
        }
        break;
      case Player.STATE_BUFFERING:
        Timber.d("Player state buffering");
        break;
      case Player.STATE_ENDED:
        Timber.d("Player state ended");
        if (isAdded()) {
          pauseAudioView();
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
        utils.getToast(activity, "Playing error , check log");
        break;
      case ExoPlaybackException.TYPE_RENDERER:
        break;
      case ExoPlaybackException.TYPE_UNEXPECTED:
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
    Timber.d("onStop stopAndRelease all recording and playing");
    presenter.onStopOperation();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    presenter.unSubscribe();
  }
}
