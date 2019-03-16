package io.reciteapp.recite.submissiondetail.cs.recordcomment;

import android.text.TextUtils;
import android.widget.Toast;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.submissiondetail.cs.recordcomment.DialogRecordContract.DialogRecordCommentCallback;
import io.reciteapp.recite.submissiondetail.cs.recordcomment.DialogRecordContract.Presenter;
import io.reciteapp.recite.submissiondetail.cs.recordcomment.DialogRecordContract.View;
import io.reciteapp.recite.utils.FileManager;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.ReciteRecorder;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class DialogRecordPresenter implements Presenter {

  private View view;
  private UstazReviewCommentResponse ustazReviewCommentResponse;
  private String audioCommentFileName, userAudioUri;
  private DialogRecordCommentCallback callback;
  //In Milliseconds
  private long mDurationAudio;

  //Player for user | Player for cs
  private Rplayer playerUser, playerComment;
  private ReciteRecorder recorder;

  @Override
  public void initDialog(View view,
      String userAudioUri,
      String audioFileName,
      UstazReviewCommentResponse ustazReviewCommentResponse) {
    this.view = view;
    this.userAudioUri = userAudioUri;
    this.ustazReviewCommentResponse = ustazReviewCommentResponse;
    this.audioCommentFileName = audioFileName + ustazReviewCommentResponse.getAudioDuration().replace(":", "P");
    this.mDurationAudio = TimeUnit.SECONDS.toMillis(new Utils().convertTimerStringFormatToSeconds(ustazReviewCommentResponse.getAudioDuration()));
  }

  @Override
  public String getAudioDuration() {
    return ustazReviewCommentResponse.getAudioDuration();
  }

  @Override
  public String getComment() {
    return ustazReviewCommentResponse.getComment();
  }

  @Override
  public String getUserAudioUri() {
    return userAudioUri;
  }

  @Override
  public void showDialog(DialogRecordCommentCallback callback) {
    this.callback = callback;

    //Set Duration to view
    view.setTextDurationToView(ustazReviewCommentResponse.getAudioDuration());
    //Set comment to view
    view.setCommentToView(ustazReviewCommentResponse.getComment());

    //Show appropriated view for audio cs
    if (ustazReviewCommentResponse.isAttachmentAvailable() && checkAudioCommentFileExist()) {
      view.viewAudioCsExist();
    } else {
      view.viewAudioCsNotExist();
    }

    //Initialized player
    initializedPlayerUser();
  }


  @Override
  public void initializedPlayerUser() {
    if (!TextUtils.isEmpty(userAudioUri)) {
      playerUser = new Rplayer();
      playerUser = view.getContextForPlayerUser(playerUser, userAudioUri, playerUserStateListener, playerErrorListener);
    }
  }

  @Override
  public void initializedPlayerCs() {
    playerComment = new Rplayer();
    String commentAudioUri = new FileManager().getFileUri(audioCommentFileName);
    playerComment = view.getContextForPlayerComment(playerComment, commentAudioUri, playerAudioCommentStateListener, playerErrorListener);
  }

  @Override
  public void initializedRecorder() {
    recorder = new ReciteRecorder();
    recorder.initialized(audioCommentFileName);
  }

  @Override
  public void playerUserToggle() {
    if (playerUser != null) {
      if (playerUser.getPlayerState() == Player.STATE_IDLE) {
        initializedPlayerUser();
      } else {
        if (playerUser.checkPlaying()) {
          playerUser.pause();
          Timber.d("Pause and seek to %s", mDurationAudio);
          playerUser.seekTo(mDurationAudio);
        } else {
          playerUser.play();
        }
      }
    } else {
      initializedPlayerUser();
    }
  }

  @Override
  public void playerCsToggle() {
    boolean isAudioCommentExist = checkAudioCommentFileExist();

    /**Audio Comment exist**/
    if (isAudioCommentExist) {
      //If audio exist and currently recording, stopAndRelease the recording first.
      if (recorder != null && recorder.isRecording()) {
        recorderToggle();
      }

      //If audio exist and not recording, play or pause the audio
      if (playerComment != null) {
        if (playerComment.getPlayerState() == Player.STATE_IDLE) {
          view.viewAudioCsExist();
        } else if (playerComment.getPlayerState() == Player.STATE_ENDED) {
          playerComment.seekTo(0);
          playerComment.play();
        } else {
          if (playerComment.checkPlaying()) {
            playerComment.pause();
          } else {
            playerComment.play();
          }
        }
      }
    }
    /**Audio Comment not exist**/
    else {
      recorderToggle();
    }
  }

  @Override
  public void recorderToggle() {
    if (recorder != null) {
      if (!recorder.isRecording()) {
        recorder.startRecording();

        view.isRecordingView();

        //disable user play button
        view.userPlayBtnDisable();
      } else {
        try {
          recorder.stopRecording();
        } catch (IOException e) {
          e.printStackTrace();
        }

        //enable user play button
        view.userPlayBtnEnable();

        view.submitBtnEnable();

        checkAudioCommentExist();
      }
    }
  }

  @Override
  public void deleteAudioComment() {
    if (playerComment != null) {
      if (playerComment.checkPlaying()) {
        playerComment.pause();
      }
    }
    new FileManager().deleteFilebyFileName(audioCommentFileName);
    checkAudioCommentExist();
  }

  @Override
  public void submit(String comment) {
    //If text not empty OR file is exist
    if (!TextUtils.isEmpty(comment) || checkAudioCommentFileExist()) {
      if (checkAudioCommentFileExist()) {
        //If text empty, insert template
        if (TextUtils.isEmpty(comment)) {
          view.setCommentToView(App.getApp().getString(R.string.msg_voice_note_template));
          comment = App.getApp().getString(R.string.msg_voice_note_template);
        }
        ustazReviewCommentResponse.setAudioUri(new FileManager().getFileUri(audioCommentFileName));
        ustazReviewCommentResponse.setAttachmentAvailable(true);
      } else {
        ustazReviewCommentResponse.setAttachmentAvailable(false);
      }

      ustazReviewCommentResponse.setComment(comment);

      callback.onClick(ustazReviewCommentResponse);
    } else {
      callback.onClick(null);
    }
    view.removeDialog();
  }

  /**
   * Check audio comment file by searching for file name exist or not.
   */
  @Override
  public boolean checkAudioCommentFileExist() {
    return new FileManager().checkFileRecordFileExist(audioCommentFileName);
  }

  /**
   * Check for audio comment file exist, and change view appropriately
   */
  @Override
  public void checkAudioCommentExist() {
    boolean isExist = checkAudioCommentFileExist();
    if (isExist) {
      view.viewAudioCsExist();
    } else {
      view.viewAudioCsNotExist();
    }
  }

  @Override
  public void destroy() {
    if (playerUser != null) {
      playerUser.stopAndRelease();
    }

    if (playerComment != null) {
      playerComment.stopAndRelease();
    }

    if (recorder != null && recorder.isRecording()) {
      try {
        recorder.stopRecording();
        new FileManager().deleteFilebyFileName(audioCommentFileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * User player state listener
   */
  private playerStateCallback playerUserStateListener = new playerStateCallback() {
    @Override
    public void state(boolean playWhenReady, int state) {
      switch (state) {
        case Player.STATE_IDLE:
          Timber.d("playerUser STATE_IDLE");
          view.btnUserError();
          break;
        case Player.STATE_BUFFERING:
          Timber.d("playerUser STATE_BUFFERING");
          view.btnUserBuffering();
          break;
        case Player.STATE_READY:
          Timber.d("playerUser STATE_READY");
          view.btnUserReady(playWhenReady);
          break;
        case Player.STATE_ENDED:
          view.btnUserEnded();
          break;
      }
    }
  };

  private playerStateCallback playerAudioCommentStateListener = new playerStateCallback() {
    @Override
    public void state(boolean playWhenReady, int state) {
      switch (state) {
        case Player.STATE_IDLE:
          Timber.d("playComment STATE IDLE");
          view.btnCsError();
          break;
        case Player.STATE_BUFFERING:
          Timber.d("playComment STATE BUFFERING");
          view.btnCsBuffering();
          break;
        case Player.STATE_READY:
          Timber.d("playComment STATE READY");
          view.btnCsReady(playWhenReady);
          break;
        case Player.STATE_ENDED:
          Timber.d("playComment STATE ENDED");
          view.btnCsEnded();
          break;
      }
    }
  };

  /**
   * Player error listener
   */
  private playerErrorStateCallback playerErrorListener = new playerErrorStateCallback() {
    @Override
    public void error_state(ExoPlaybackException exception) {
      switch (exception.type) {
        case ExoPlaybackException.TYPE_SOURCE:
          Timber.d("playerUser TYPE SOURCE");
          Toast.makeText(App.getApp(), App.getApp().getResources().getString(R.string.error_stream_audio_general), Toast.LENGTH_SHORT).show();

        case ExoPlaybackException.TYPE_RENDERER:
          Timber.d("playerUser TYPE_RENDERER: %s", exception.getRendererException().getMessage());
          break;

        case ExoPlaybackException.TYPE_UNEXPECTED:
          Timber.d("playerUser TYPE_UNEXPECTED: %s", exception.getUnexpectedException().getMessage());
          break;
      }
    }
  };
}
