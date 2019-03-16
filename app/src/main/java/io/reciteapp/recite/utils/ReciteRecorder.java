package io.reciteapp.recite.utils;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import java.io.IOException;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

public class ReciteRecorder {

  private Recorder recorder = null;
  private boolean isRecording = false;

  /**
   * Configure microphone
   */
  private PullableSource mic() {
    return new PullableSource.Default(
        new AudioRecordConfig.Default(
            MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
            AudioFormat.CHANNEL_IN_MONO, 16000
        )
    );
  }



  /**
   * Initialized microphone with output
   * @param fileName fileName for location output
   */
  public void initialized(String fileName) {
    isRecording = false;
    recorder = OmRecorder.wav(
        new PullTransport.Default(mic()), new FileManager().getFile(fileName));
  }

  public void startRecording() {
    if (recorder != null) {
      isRecording = true;
      recorder.startRecording();
    }
  }

  public void pauseRecording() {
    if (recorder != null) {
      isRecording = false;
      recorder.pauseRecording();
    }
  }

  public void resumeRecording() {
    if (recorder != null) {
      isRecording = true;
      recorder.resumeRecording();
    }
  }

  public void stopRecording() throws IOException {
    if (recorder != null) {
      isRecording = false;
      recorder.stopRecording();
    }
  }

  /**
   * Check recorder if still recording
   * @return true = still recording, false = not recording
   */
  public boolean isRecording() {
    return isRecording;
  }
}
