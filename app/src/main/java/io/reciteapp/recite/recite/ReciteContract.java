package io.reciteapp.recite.recite;

import io.reciteapp.recite.data.model.SubmissionRecorderHistories;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.utils.Rplayer;
import java.util.List;

public interface ReciteContract {

  interface View {

    void showLoadingDialog();

    void removeLoadingDialog();

    void showSnackbar(int messageResource);

    void showErrorDialog(int responseCode, boolean isKick);

    void showUpdateDialog();

    void setHistoryItem(List<SubmissionRecorderHistories> historyList);

    void initializedPlayer();

    void onInsufficientReciteTime();

    void startRecordView();

    void finishRecordView();

    void playingAudioView();

    void pauseAudioView();

    void resetButtonFunctionView();

    void openHistoryListFragment(String surahId);

    void openHistoryDetail(SubmissionRecorderHistories historyResponse, String surahName,
        String ayat);

    void showShareDialog();
  }

  interface Model {

    String getToken();

    void setSurahName(String surahName);

    String getSurahName();

    void setSurahId(String surahId);

    String getSurahId();

    void setAudioFileName(String surahName);

    String getAudioFileName();

    void setsMaxRecordTime(long sMaxRecordTime);

    long getsMaxRecordTime();

    void setAyat(String ayat);

    String getAyat();

    void setsRemainingTime(long sRemainingTime);

    long getsRemainingTime();

    void setHistoryItemOne(SubmissionRecorderHistories historyItemOne);

    SubmissionRecorderHistories getHistoryItemOne();

    void setHistoryItemTwo(SubmissionRecorderHistories historyItemTwo);

    SubmissionRecorderHistories getHistoryItemTwo();

  }

  interface Presenter {

    void setView(View view, NetworkCallWrapper service);

    void setSurahName(String surahName);

    void setSurahId(String surahId);

    void setAyat(String ayat);

    void setAudioFileName(String name);

    String getAudioFileURI();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void processGetReciteTime(String surahId);

    boolean checkIfRecordingFileExist();

    float calculateProgressRecording(long currentDurationS);

    void initializedRecorder();

    void recorderToggle();

    void reset();

    void initializedPlayer(Rplayer player);

    void playPauseToggle();

    void postAudioProcess(long audioDuration);

    void openHistoryDetailItemOne();

    void openHistoryDetailItemTwo();

    void openHistoryListFragment();

    void onStopOperation();

    void unSubscribe();
  }

}
