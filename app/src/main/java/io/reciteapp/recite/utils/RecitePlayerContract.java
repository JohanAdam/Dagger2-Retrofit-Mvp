package io.reciteapp.recite.utils;

import android.content.Context;
import com.google.android.exoplayer2.ExoPlaybackException;

public interface RecitePlayerContract {

  void initPlayer(Context context,
      String audioUri,
      playerStateCallback playerStateCallback,
      playerErrorStateCallback playerErrorStateCallback);

  String getAudioUri();

  void setVolume(float value);

  void play();

  void pause();

  void stopAndRelease();

  void seekTo(long seekValue);

  void seekToWithoutRange(long seekValue);

  boolean checkPlaying();

  boolean checkAudioManager();

  long getDuration();

  long getCurrentDuration();

  /**
   * Interface to listen to player state change
   */
  public interface playerStateCallback {
    void state(boolean playWhenReady, int state);
  }

  /**
   * Interface to listen to player error state
   */
  public interface playerErrorStateCallback {
    void error_state(ExoPlaybackException exception);
  }

}
