package io.reciteapp.recite.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import timber.log.Timber;

public class Rplayer implements RecitePlayerContract, OnAudioFocusChangeListener {

  private AudioManager audioManager;
  private SimpleExoPlayer player;
  private String audioUri = "";

  /**
   * Initialized player
   * @param context Context Fragment/Activity
   * @param audioUri AudioUri for streaming/playing
   * @param playerStateCallback player listener (Usually in View)
   * @param playerErrorStateCallback player error listener (Usually in View)
   * @return player after initialized
   */
  @Override
  public void initPlayer(Context context, String audioUri,
      playerStateCallback playerStateCallback, playerErrorStateCallback playerErrorStateCallback) {

    //Initialized AudioManager
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    //Renderer are used to render individual components of the media.
    DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    //Provides estimates of the currently available bandwidth.
    AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(
        bandwidthMeter);
    DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

    //LoadControl is used to control when MediaSource buffers more media,
    //and how much media is buffered
    DefaultLoadControl loadControl = new DefaultLoadControl();

    player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
    player.addListener(new EventListener() {
      @Override
      public void onTimelineChanged(Timeline timeline, Object manifest) {

      }

      @Override
      public void onTracksChanged(TrackGroupArray trackGroups,
          TrackSelectionArray trackSelections) {

      }

      @Override
      public void onLoadingChanged(boolean isLoading) {
        Timber.d("onLoadingChange %s", isLoading);
      }

      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        playerStateCallback.state(playWhenReady, playbackState);
      }

      @Override
      public void onRepeatModeChanged(int repeatMode) {

      }

      @Override
      public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

      }

      @Override
      public void onPlayerError(ExoPlaybackException error) {
        Timber.d("onPlayerError ");
        error.printStackTrace();
        playerErrorStateCallback.error_state(error);
      }

      @Override
      public void onPositionDiscontinuity(int reason) {

      }

      @Override
      public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

      }

      @Override
      public void onSeekProcessed() {

      }
    });

    DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
        "ExoplayerDemo");
    DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
    Handler handler = new Handler();
    ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource(Uri.parse(audioUri),
        defaultDataSourceFactory,
        defaultExtractorsFactory,
        handler,
        null);

    player.prepare(extractorMediaSource);

    this.audioUri = audioUri;
  }

  @Override
  public String getAudioUri() {
    return audioUri;
  }

  /**
   * Change player volume
   * @param value 1f for Highest - 0.1f Lowest
   */
  @Override
  public void setVolume(float value) {
    if (player != null) {
      player.setVolume(value);
    }
  }

  /**
   * Play player
   */
  @Override
  public void play() {
    if (player != null) {
      int result = requestAudioFocus(this);
      if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
        player.setPlayWhenReady(true);
      } else {
        throw new IllegalStateException();
      }
    }
  }

  /**
   * Pause player
   */
  @Override
  public void pause() {
    if (player != null) {
      if (player.getPlayWhenReady()) {
        player.setPlayWhenReady(false);
      }
    }
  }

  /**
   * Stop player
   */
  @Override
  public void stopAndRelease() {
    if (player != null) {
      if (player.getPlayWhenReady()) {
        player.setPlayWhenReady(false);
        player.stop();
      }
      player.release();
      try {
        abandon_audio_focus(this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param valueDuration Seek to current duration - 3 seconds.
   */
  @Override
  public void seekTo(long valueDuration) {
    pause();
    long value = valueDuration - 3000;
    player.seekTo(value);
  }

  /**
   * Seek to duration without  - 3 seconds.
   * @param seekValue value to seeks.
   */
  @Override
  public void seekToWithoutRange(long seekValue) {
    pause();
    player.seekTo(seekValue);
  }


  /**
   * Seek forward plus 5 seconds
   * @param valueDuration seekDurations
   */
  public void seekForward(long valueDuration) {
    long value = valueDuration + 5000;
    player.seekTo(value);
  }

  /**
   * Seek backwards minus 5 seconds{
    long value = valueDuration + 5000;
    player.seekTo(value);
  }
   * @param valueDuration seekDurations
   */
  public void seekBackward(long valueDuration) {
    long value = valueDuration - 5000;
    player.seekTo(value);
  }

  /**
   * Get if the player still playing or not
   * @return true for playing, false for not playing
   */
  @Override
  public boolean checkPlaying() {
    if (player != null) {
      return player.getPlayWhenReady();
    }
    return false;
  }

  /**
   * For check if audioManager still available
   * @return true for != null , false for == null
   */
  @Override
  public boolean checkAudioManager() {
    if (audioManager != null) {
      return true;
    }
    return false;
  }

  /**
   * Get Audio duration.
   * @return full audio duration if player not null.
   */
  @Override
  public long getDuration() {
    if (player != null) {
      return player.getDuration();
    }
    return 0;
  }

  /**
   * Get current Audio position in milliseconds
   * @return current Audio position if player not null.
   */
  @Override
  public long getCurrentDuration() {
    if (player != null) {
      return player.getCurrentPosition();
    }
    return 0;
  }

  /**
   * Get player state
   * @return Player state
   * STATE_IDLE = The player does not have any media to play
   * STATE_BUFFERING = The player is not able to immediately play from its current position. This state typically
   *      occurs when more data needs to be loaded;
   * STATE_READY = The player is able to immediately play from its current position. The player will be playing if
   *      getPlayWhenReady() is true, and paused otherwise.
   * STATE_ENDED = The player has finished playing the media;
   */
  public int getPlayerState(){
    if (player != null) {
      return player.getPlaybackState();
    }
    return 0;
  }

  /**
   * Request for audio focus for playing audio
   * @return audio state
   */
  private int requestAudioFocus(OnAudioFocusChangeListener listener) {
    return audioManager.requestAudioFocus(
        listener,
        AudioManager.STREAM_MUSIC,
        AudioManager.AUDIOFOCUS_GAIN);
  }

  /**
   * Abandon audio focus after not use
   */
  private void abandon_audio_focus(OnAudioFocusChangeListener listener) {
    audioManager.abandonAudioFocus(listener);
  }

  /**
   * AudioManager AudioFocusChange listener
   * @param focusChange Audio Focus state
   */
  @Override
  public void onAudioFocusChange(int focusChange) {
    if (checkAudioManager()) {
      switch (focusChange) {
        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
          //lower the volume when loss audio focus for a short time and able to duck audio
          player.setVolume(0.2f);
          break;
        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
          //Stop the audio when loss audio focus for a short time and unable to duck.
          player.setPlayWhenReady(false);
          break;
        case (AudioManager.AUDIOFOCUS_LOSS):
          //stopAndRelease audio when loss the focus completely
          player.setPlayWhenReady(false);
          break;
        case (AudioManager.AUDIOFOCUS_GAIN):
          //Return audio to full volume if when get audio focus
          player.setVolume(1f);
          break;
        default:
          break;
      }
    }
  }

}
