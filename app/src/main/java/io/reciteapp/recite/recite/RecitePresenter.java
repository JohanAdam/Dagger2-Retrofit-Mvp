package io.reciteapp.recite.recite;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.Util;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.AudioV2;
import io.reciteapp.recite.data.model.SubmissionRecorderResponse;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.data.networking.NetworkError;
import io.reciteapp.recite.data.networking.call.recite.AudioUploadCall.UploadAudioUploadCallback;
import io.reciteapp.recite.data.networking.call.recite.ReciteTimeCall.GetReciteTimeCallback;
import io.reciteapp.recite.recite.ReciteContract.Model;
import io.reciteapp.recite.recite.ReciteContract.Presenter;
import io.reciteapp.recite.recite.ReciteContract.View;
import io.reciteapp.recite.data.repository.SurahRepository;
import io.reciteapp.recite.utils.FileManager;
import io.reciteapp.recite.utils.ReciteRecorder;
import io.reciteapp.recite.utils.Rplayer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RecitePresenter implements Presenter {

  private NetworkCallWrapper service;
  private View view;
  private Model model;
  private CompositeSubscription subscriptions;
  private ReciteRecorder recorder = null;
  private Rplayer player = null;
  private SurahRepository surahRepository;
  private long playerDuration = 0;

  /**
   * Set model in presenter
   */
  public RecitePresenter(Model model){
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
    surahRepository = new SurahRepository();
  }

  /**
   * Check for view after a network call to avoid view null when result return from call
   * @return true for view is attach, false for view is null
   */
  private boolean isViewAttached() {
    return view != null;
  }

  /**
   * Save surahname without edit
   */
  @Override
  public void setSurahName(String surahName) {
    model.setSurahName(surahName);
  }

  @Override
  public void setSurahId(String surahId) {
    model.setSurahId(surahId);
  }

  @Override
  public void setAyat(String ayat) {
    model.setAyat(ayat);
  }

  /**
   * Set audio file name for recording and playing.
   * @param surahName Surah Name that has been trimmed for Audio file
   */
  @Override
  public void setAudioFileName(String surahName) {
    model.setAudioFileName(surahName);
  }

  /**
   * Get audio file name and return as File URI
   * @return File Uri
   */
  @Override
  public String getAudioFileURI() {
    return new FileManager().getFileUri(model.getAudioFileName());
  }

  /**
   * Check if recordable by check the remaining time available for the user.
   * True if remaining time > 5 , False if remaining time <= 5
   * @return true if recordable , false for not recordable
   */
  private boolean checkIfRecordable() {
    return ((model.getsMaxRecordTime()) > 5);
  }

  /**
   * Check if recordingFile is Exist
   * @return true is exist, false for not exist
   */
  @Override
  public boolean checkIfRecordingFileExist() {
    return new FileManager().checkFileRecordFileExist(model.getAudioFileName());
  }

  @Override
  public float calculateProgressRecording(long currentDurationS) {

//    long remainingTime = model.getsMaxRecordTime();
    //Stop before reach this (RemainingTime - 3 seconds)
    long minusRemainingTime = model.getsMaxRecordTime() - 3;

    if (currentDurationS == minusRemainingTime) {
      Timber.d("Seconds == 30 , recorderToggle");
      recorderToggle();
    }

    String remainingTimeString = String.valueOf(minusRemainingTime) + ".0";
    Double fixedRemainingTime = Double.parseDouble(remainingTimeString);

    currentDurationS++;
    double x = currentDurationS / fixedRemainingTime;

    Timber.d("currentDurationS %s", currentDurationS);
    Timber.d("remainingTime s %s", minusRemainingTime);
    Timber.d("fixedRemainingTime %s", fixedRemainingTime);
    Timber.d("x " + String.valueOf(x) + " (float) x " + String.valueOf((float) x));

    return (float) x;
  }

  /**
   * Initialized recorder
   */
  @Override
  public void initializedRecorder() {
    recorder = new ReciteRecorder();
    String fileName = model.getAudioFileName();
    recorder.initialized(fileName);
  }

  /**
   * Start recording if not recording, Stop recording if not recording
   */
  @Override
  public void recorderToggle() {
    if (recorder != null) {
      //Check if Recite time pass to Recite
      if (checkIfRecordable()) {
        //record if pass
        if (!recorder.isRecording()) {
          //record
          recorder.startRecording();
          view.startRecordView();
        } else {
          //stopAndRelease
          try {
            recorder.stopRecording();
            view.finishRecordView();
          } catch (IOException e) {
            e.printStackTrace();
            view.finishRecordView();
          }
        }
      } else {
        //show dialog not enough credit
        Timber.e("checkIfRecordable false");
        view.showErrorDialog(Constants.RESPONSE_CODE_UNSUFFICIENT_CREDIT_AVAILABLE, false);
      }
    } else {
      Timber.e("Recorder null");
    }
  }

  /**
   * Reset all function, recorder and player
   */
  @Override
  public void reset() {
    playerDuration = 0;

    if (recorder!= null) {
      if (recorder.isRecording()) {
        try {
          recorder.stopRecording();
        } catch (IOException e) {
          e.printStackTrace();
          initializedRecorder();
        }
      }
    }

    if (player != null) {
      if (player.checkPlaying()) {
        player.stopAndRelease();
      }
    }
    initializedRecorder();
    deleteFile();
    view.resetButtonFunctionView();
  }

  /**
   * Initialized player
   * @param player Pass player that has been inserted context from view to control in presenter
   */
  @Override
  public void initializedPlayer(Rplayer player) {
    player.seekToWithoutRange(playerDuration);
    this.player = player;
  }

  /**
   * Play player if not playing, Pause player if playing
   */
  @Override
  public void playPauseToggle() {
    if (player != null) {
      if (player.getPlayerState() == Player.STATE_ENDED) {
        player.seekTo(0);
        player.play();
      } else {
        if (player.checkPlaying()) {
          player.pause();
        } else {
          player.play();
        }
      }
    }
  }

  /**
   * Check for App Version from server before proceed to get ReciteTime from server
   * - Check Version
   * - Get recite time
   */
  @Override
  public void processGetReciteTime(String surahId) {
    Timber.d("processGetReciteTime");
    view.showLoadingDialog();
    getReciteTime(surahId);
    //TODO Enable this after indonesia server have ReciteVersion check
    //Check Version
//    Subscription subscription = service.getReciteVersion(new GetReciteVersionCallback() {
//      @Override
//      public void onSuccess(ArrayList<ReciteVersionResponse> reciteVersionResponse) {
//        //Get app version from server
//        for (ReciteVersionResponse user : reciteVersionResponse) {
//          int versionServer = Integer.parseInt(user.getCurrentVersion());
//          int versionLocal = BuildConfig.VERSION_CODE;
//          Timber.d("currentVersion is " + versionServer + " versionLocal " + versionLocal);
//          if (isViewAttached()) {
//            //If app version from server is higher than local show dialog
////          if (versionServer > versionLocal) {
////            view.showUpdateDialog();
////          } else {
//            getReciteTime(surahId);
////          }
//          }
//        }
//      }
//
//      @Override
//      public void onError(int responseCode) {
//        if (isViewAttached()) {
//          view.removeLoadingDialog();
//          view.showErrorDialog(responseCode, true);
//        }
//      }
//    });
//
//    subscriptions.add(subscription);
  }

  /**
   * Get ReciteTime use from server
   * @param surahId Surah id for get current ReciteTime
   */
  private void getReciteTime(String surahId) {
    Timber.d("getReciteTime");
    String token = model.getToken();
    Timber.v("token is %s", token);
    Subscription subscription = service.getReciteTime(token,
        surahId,
        new GetReciteTimeCallback() {
          @Override
          public void onSuccess(SubmissionRecorderResponse recordPageResponse) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              //pass to setupReadyView to calculate recite time
              setupReadyView(recordPageResponse);
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            if (isViewAttached()) {
              view.removeLoadingDialog();
              view.showErrorDialog(Constants.RESPONSE_CODE_PROBLEM_CHECK_RECITE_TIME, true);
            }
          }
        });

    subscriptions.add(subscription);
  }

  /**
   * Setup view after pass App Version checking and Recite Time call
   * @param dataResponse response get after get ReciteTime call
   */
  private void setupReadyView(SubmissionRecorderResponse dataResponse) {

    //User remaining time
    long sRemainingTime = TimeUnit.MILLISECONDS.toSeconds(dataResponse.getRemainingTime());

    //Save max recording time according to get remaining time in Seconds
    model.setsMaxRecordTime(sRemainingTime);

    //If remaining time is more than 5 seconds , enable recite
    if (sRemainingTime <= 5) { //If remaining time below 5 seconds , disable recite
      view.onInsufficientReciteTime();
    }

    //Set history item view
    if (dataResponse.getSurahHistories().size() > 0) {
      view.setHistoryItem(dataResponse.getSurahHistories());
    }

    //Save item one and two if available\
    try {
      model.setHistoryItemOne(dataResponse.getSurahHistories().get(0));
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    try {
      model.setHistoryItemTwo(dataResponse.getSurahHistories().get(1));
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }

  }

  /**Step 1
   * Post audio to server
   * @param audioDuration audio file duration
   *
   * 1 - Fill item in AudioV2 (prePostAudioProcess)
   * 2 - Get Sas Query
   * 3 - Upload audio
   * 4 - Upload audio data/json
   * - Complete (  Reset all )
   */
  @Override
  public void postAudioProcess(long audioDuration) {
    Timber.d("prePostAudioProcess");
    view.showLoadingDialog();

    AudioV2 audio = new AudioV2();

    String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

    audio.setAudioUri(getAudioFileURI());
    audio.setContainerName("audioContainer");
    audio.setResourceName(model.getAudioFileName() + timeStamp);
    audio.setSurahName(model.getSurahName());
    audio.setAudioDuration(audioDuration);
    audio.setAyatFK_ID(model.getSurahId());

//    getSasQueryUser(audio);
    postAudio(audio);
  }

  private void postAudio(AudioV2 audioV2) {
    Timber.d("postAudio");
    String token = model.getToken();

    Subscription subscription = service.postAudioService(token, audioV2,
        new UploadAudioUploadCallback() {
          @Override
          public void onSuccess(AudioV2 item) {
            Timber.d("onSuccess postAudio");
            if (isViewAttached()) {
              //Increase submission in local db
              surahRepository.updateSubmissionCount(model.getSurahId());

              view.removeLoadingDialog();
              view.showSnackbar(R.string.msg_audio_submission_success);

              view.showShareDialog();

              reset();
              processGetReciteTime(model.getSurahId());
            }
          }

          @Override
          public void onError(NetworkError networkError) {
            Timber.e("onError postAudio");
            if (isViewAttached()) {
              view.removeLoadingDialog();

              switch (networkError.getResponseCode()) {
                case Constants.RESPONSE_CODE_AUDIO_FILE_SILENCE:
                  view.showErrorDialog(Constants.RESPONSE_CODE_AUDIO_FILE_SILENCE, false);
                  processGetReciteTime(audioV2.getAyatFK_ID());
                  break;
                case Constants.RESPONSE_CODE_UNSUFFICIENT_CREDIT_AVAILABLE:
                  view.showErrorDialog(Constants.RESPONSE_CODE_UNSUFFICIENT_CREDIT_AVAILABLE, false);
                  reset();
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

  @Override
  public void openHistoryDetailItemOne() {
    view.openHistoryDetail(model.getHistoryItemOne(), model.getSurahName(), model.getAyat());
  }

  @Override
  public void openHistoryDetailItemTwo() {
    view.openHistoryDetail(model.getHistoryItemTwo(), model.getSurahName(), model.getAyat());
  }

  @Override
  public void openHistoryListFragment() {
    view.openHistoryListFragment(model.getSurahId());
  }

  /**
   * Stop all recording and playing in onStop
   */
  @Override
  public void onStopOperation() {
    stopRecordingAndPlaying();
  }

  /**
   * Stop Recording and playing process
   */
  private void stopRecordingAndPlaying() {
    if (recorder != null) {
      if (recorder.isRecording()) {
        try {
          recorder.stopRecording();
          view.finishRecordView();
        } catch (IOException e) {
          e.printStackTrace();
          view.finishRecordView();
        }
      }
    }

    if (player != null) {
      player.stopAndRelease();
    }
  }

  /**
   *Delete all file in .Recite folder process
   */
  private void deleteFile() {
    new FileManager().deleteAll();
  }

  @Override
  public void onStart() {
    if (Util.SDK_INT > 23) {
      if (view != null && checkIfRecordingFileExist()) {
        view.initializedPlayer();
      }
    }
  }

  @Override
  public void onResume() {
    if (Util.SDK_INT <= 23 || player == null) {
      if (view != null && checkIfRecordingFileExist()) {
        view.initializedPlayer();
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

  private void releasePlayer() {
    if (player != null) {
      Timber.d("set duration %s", player.getCurrentDuration());
      playerDuration = player.getCurrentDuration();
      player.stopAndRelease();
      player = null;
    }
  }

  /**
   * OnStop Unsubscribe, detach view , Stop playing and recording, Delete all recording files
   */
  @Override
  public void unSubscribe() {
    subscriptions.unsubscribe();
    view.removeLoadingDialog();
    stopRecordingAndPlaying();
    deleteFile();
    view = null;
  }
}
